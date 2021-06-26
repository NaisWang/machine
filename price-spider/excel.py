# coding=utf-8
import requests
import copy
import json
import threading
import time
from xlwt import *
import product
import paijiContrast
from app import app
from flask import request, Response, make_response, jsonify
from xlutils.copy import copy
import io
import xlrd
import xlwt
import access
import log

count = 1
already_search = {}
comparison_price = {}
select_default_color = {}
select_logs = []
column_name_number = {}
exclude_desc = []

use_contrast = {}
use_field = {}


def init():
	global count
	global already_search
	global select_default_color
	global select_logs
	global exclude_desc

	log.log_error = []
	log.log_success = []

	select_logs = []
	select_default_color = {}
	already_search = {}
	exclude_desc = []
	product.product_log = []
	count = 1
	access.init_user()
	paijiContrast.init()


def judge_excel_legal(column_name_number_corr, fileName):
	column_name = ["机型", "sku", "成色", "机况描述", "单台出价"]
	for name in column_name:
		if name not in column_name_number_corr.keys():
			log.log_error.append(fileName + "文件中没有" + name + "列")
			return -1
	return 0


def dealComparisonExcel(files):
	global comparison_price
	for f in files:
		comparison_price[f.filename] = {}
		file = f.read()
		oldWb = xlrd.open_workbook(file_contents=file)
		xlrd_worksheet = oldWb.sheets()[0]

		column_name_number1 = get_column_name_number(xlrd_worksheet)
		if judge_excel_legal(column_name_number1, f.filename) == -1:
			break
		sku_number = column_name_number1['sku']
		quality_number = column_name_number1['成色']
		desc_number = column_name_number1['机况描述']
		price_number = column_name_number1['单台出价']

		for i in range(1, xlrd_worksheet.nrows):
			sku = remove_space(str(xlrd_worksheet.row_values(i)[sku_number])).lower()
			quality = remove_space(str(xlrd_worksheet.row_values(i)[quality_number])).lower()
			desc = remove_space(str(xlrd_worksheet.row_values(i)[desc_number])).lower()
			if str(xlrd_worksheet.row_values(i)[price_number]).replace(' ', '') != '' and sku + desc + quality not in \
					comparison_price[
						f.filename].keys():
				comparison_price[f.filename][sku + desc + quality] = xlrd_worksheet.row_values(i)[price_number]


def get_column_name_number(xlrd_worksheet):
	column_name_number1 = {}
	for i in range(len(xlrd_worksheet.row_values(0))):
		column_name = remove_space(str(xlrd_worksheet.row_values(0)[i])).lower()
		column_name_number1[column_name] = i
	column_name_number1["第一个空白"] = len(xlrd_worksheet.row_values(0))
	return column_name_number1


@app.route("/price_excel/import", methods=['POST', 'GET'])
def import_excel():
	global use_contrast
	global use_field

	files = request.files.getlist("files")
	dealComparisonExcel(files)

	file = request.files.get("file")

	searchPriceMethod = request.form["searchPriceMethod"]
	if searchPriceMethod == 1:
		use_contrast = paijiContrast.xd_contrast
		use_field = paijiContrast.xd_field
	else:
		use_contrast = paijiContrast.ahs_contrast
		use_field = paijiContrast.ahs_field

	f = file.read()
	oldWb = xlrd.open_workbook(file_contents=f)
	oldws = oldWb.sheets()[0]

	newWb = copy(oldWb)
	newWs = newWb.get_sheet(0)

	init()
	traverse_excel(oldws, newWs)

	output = io.BytesIO()
	newWb.save(output)
	response = make_response(output.getvalue())

	filename = "export.xls"

	response.headers["Content-Type"] = "application/octet-stream; charset=UTF-8"
	response.headers["Content-Disposition"] = "attachment; filename={}".format(filename)

	time.sleep(1)
	return response


@app.route("/log", methods=['POST', 'GET'])
def get_log():
	return jsonify({"log_success": log.log_success, "log_error": log.log_error})


class userThread(threading.Thread):
	def __init__(self, userIndex, threadLock, xlrd_worksheet, xlwt_worksheet):
		threading.Thread.__init__(self)
		self.username = access.user[userIndex]['userName']
		self.userIndex = userIndex
		self.threadLock = threadLock
		self.token = access.user[userIndex]['token']
		self.xlrd_worksheet = xlrd_worksheet
		self.xlwt_worksheet = xlwt_worksheet

	def run(self):
		global count
		while count < 50:
			# while count < self.xlrd_worksheet.nrows:
			self.threadLock.acquire()
			temp = 0
			if count < 50:
				# if count < self.xlrd_worksheet.nrows:
				temp = count
				count += 1
			self.threadLock.release()
			get_price(temp, self.xlrd_worksheet, self.xlwt_worksheet, self.userIndex)


# 在机身颜色没有完全匹配的情况下，进行机身颜色的选择
def get_color_pricePropertyValue(paiji_colors, model, sku, colors):
	sku = str(sku).replace(model, '')
	color = ""
	for item in paijiContrast.color:
		if item in sku:
			color = item
	for item1 in paiji_colors:
		if color in item1['value']:
			colors.append(item1)


def get_guarantee_propertyValue(model, sku):
	guarantee_desc = ""
	for item in sku.split("、"):
		if "保修" in item:
			guarantee_desc = item
	if guarantee_desc == "":
		for item in paijiContrast.mode_guarantee_battery:
			if str(item['model']).replace(' ', '').lower() == model and int(item['guaranteeDefaultEnable']) == 1:
				return {'id': int(item['guaranteeDefaultId']), 'value': paijiContrast.guarantee_corr[str(item[
																																																	 'guaranteeDefaultId'])]}
	else:
		for item in paijiContrast.mode_guarantee_battery:
			if str(item['model']).replace(' ', '').lower() == model and str(item['excelGuarantee']).replace(' ',
																																																			'').lower() in \
					sku:
				return {'id': int(item['paijiGuaranteeId']), 'value': paijiContrast.guarantee_corr[str(item[
																																																 'paijiGuaranteeId'])]}
	return -3


def get_battery_propertyValue(model):
	for item in paijiContrast.mode_guarantee_battery:
		if str(item['model']).replace(' ', '').lower() == model:
			return {'id': int(item['batteryId']), 'value': paijiContrast.battery_corr[str(item[
																																											'batteryId'])]}
	return -3


def get_pricePropertyValue(category_name, paiji_category_desc, model, sku, sku_desc, select_log, show_default):
	if len(paiji_category_desc) == 1:
		select_log[category_name] = paiji_category_desc[0]['value']
		return paiji_category_desc[0]['id']
	if '保修' in category_name:
		resp = get_guarantee_propertyValue(model, sku)
		if resp != -3:
			select_log[category_name] = resp['value']
			return resp['id']
		select_log[category_name] = "没有选出"
		return -3
	if '电池健康' in category_name:
		resp = get_battery_propertyValue(model)
		if resp != -3:
			select_log[category_name] = resp['value']
			return resp['id']
		return -3
	if category_name in paijiContrast.excludeField:
		for item in paiji_category_desc:
			if remove_space(item["value"]).lower() in remove_space(sku_desc):
				select_log[category_name] = item['value']
				return int(item['id'])
	else:
		for item in paiji_category_desc:
			if remove_space(str(item['value'])).lower() not in use_contrast.keys():
				break
			else:
				detectionFields = str(use_contrast.get(item['value'])).split('、')
				if detectionFields != None:
					for detectionFiled in detectionFields:
						if detectionFiled != '' and remove_space(str(detectionFiled)).lower() in remove_space(sku_desc).split("、"):
							select_log[category_name] = item['value']
							return int(item['id'])
	if category_name == "机身颜色":
		return -2
	if category_name == "内存":
		if "oppoa53" in remove_space(sku_desc).lower():
			select_log[category_name] = "2G+16G"
			return 4482
		if "oppoa57" in remove_space(sku_desc).lower():
			select_log[category_name] = "3G+32G"
			return 3964
		if "opporeno2" in remove_space(sku_desc).lower():
			select_log[category_name] = "8G+128G"
			return 5032
		if "oppoa11x" in remove_space(sku_desc).lower():
			select_log[category_name] = "8G+128G"
			return 5032
		if "oppor15x" in remove_space(sku_desc).lower():
			select_log[category_name] = "6G+128G"
			return 4068
		if "vivoy67" in remove_space(sku_desc).lower():
			select_log[category_name] = "4G+32G"
			return 3965
	if category_name in paijiContrast.musthaveField:
		select_log[category_name] = "没有选出"
		return -1
	if category_name in show_default.keys():
		show_default[category_name] = paiji_category_desc[0]['value']
	select_log[category_name] = paiji_category_desc[0]['value']
	return paiji_category_desc[0]['id']


def get_pricePropertyValues(paijiDesc, model, sku, sku_desc, number, show_default, colors):
	pricePropertyList = []
	pricePropertyValue = -1
	select_log = {}
	for parent_category in paijiDesc.keys():
		for children_category in paijiDesc[parent_category]:
			pricePropertyValue = get_pricePropertyValue(children_category["name"],
																									children_category["pricePropertyValueVos"], model, sku, sku_desc,
																									select_log, show_default)
			if pricePropertyValue == -1:
				select_logs.append({str(number) + "行": select_log})
				return -1
			# 机身颜色没有完全匹配
			if pricePropertyValue == -3:
				select_logs.append({str(number) + "行": select_log})
				return -3
			if pricePropertyValue == -2:
				get_color_pricePropertyValue(children_category["pricePropertyValueVos"], model, sku, colors)
			else:
				pricePropertyList.append(pricePropertyValue)
	color_desc = ""
	if len(colors) != 0:
		for color in colors:
			color_desc += color['value'] + "、"
		select_log["机身颜色"] = color_desc
	select_logs.append({str(number) + "行": select_log})
	return pricePropertyList


def get_pricePropertyValues_one(quality, desc, pricePropertyList):
	pricePropertyLists = []
	for item in paijiContrast.price_combination:
		if quality == remove_space(item['grade']).lower():
			flag = 0
			for item1 in remove_space(str(item['excelDesc'])).lower().split(','):
				if item1 not in desc:
					flag = 1
					break
			if flag == 0:
				for i in range(3):
					if remove_space(item['price' + str(i + 1)]) != "":
						tempPricePropertyList = pricePropertyList.copy()
						for paijiDesc in str(item['price' + str(i + 1)]).split(','):
							categoryCorr = getCategoryCorrByField(paijiDesc)
							for category in categoryCorr:
								if category['id'] in tempPricePropertyList:
									tempPricePropertyList.remove(category['id'])
								if category['value'] == paijiDesc:
									tempPricePropertyList.append(category['id'])
						pricePropertyLists.append(tempPricePropertyList)
	return pricePropertyLists


# 透图与色差
def get_pricePropertyValues_two(pricePropertyList):
	pricePropertyLists = []
	pricePropertyLists.append(pricePropertyList.copy())
	# 如果包含透图
	if 8565 in pricePropertyList:
		pricePropertyList.remove(8565)
		pricePropertyList.append(2115)
		pricePropertyLists.append(pricePropertyList)
	# 如果包含色差
	elif 2115 in pricePropertyList:
		pricePropertyList.remove(2115)
		pricePropertyList.append(2114)
		pricePropertyLists.append(pricePropertyList)
	return pricePropertyLists


def getCategoryCorrByField(field):
	url = "http://127.0.0.1:8085/machine/price/getCategoryCorrByField?field=" + field
	resp = json.loads(requests.get(url).text)
	return resp['obj']


def excel_fill(xlwt_worksheet, number, method, price, show_default, index):
	# 创建一个样式----------------------------
	style = XFStyle()
	pattern = Pattern()
	pattern.pattern = Pattern.SOLID_PATTERN
	# 设置单元格背景色为黄色, 表示表格中缺少必须要有的字段描述, 或该字段搭配没有价格
	pattern.pattern_fore_colour = Style.colour_map['yellow']

	style1 = XFStyle()
	pattern1 = Pattern()
	pattern1.pattern = Pattern.SOLID_PATTERN
	# 设置单元格背景色为红色, 表示机型搜索不出来
	pattern1.pattern_fore_colour = Style.colour_map['red']

	style2 = XFStyle()
	pattern2 = Pattern()
	pattern2.pattern = Pattern.SOLID_PATTERN
	# 设置单元格背景色为蓝色, 表示没有对应的保修或电池效率
	pattern2.pattern_fore_colour = Style.colour_map['blue']

	style3 = XFStyle()
	pattern3 = Pattern()
	pattern3.pattern = Pattern.SOLID_PATTERN
	# 设置单元格背景色为黑色, 表示excel表的机况描述字段在对照表中没有
	pattern3.pattern_fore_colour = Style.colour_map['black']

	style.pattern = pattern
	style1.pattern = pattern1
	style2.pattern = pattern2
	style3.pattern = pattern3

	price_column_number = column_name_number["单台出价"]

	if method == 1:
		xlwt_worksheet.write(number, price_column_number + index, label="", style=style)
	elif method == 3:
		xlwt_worksheet.write(number, price_column_number + index, label="", style=style1)
	elif method == 4:
		xlwt_worksheet.write(number, price_column_number + index, label="", style=style2)
	elif method == 5:
		xlwt_worksheet.write(number, price_column_number + index, label="", style=style3)
	elif method == 2:
		xlwt_worksheet.write(number, price_column_number + index, label=str(price))
		default_desc = ""
		for item in show_default.keys():
			if str(show_default[item]) != "":
				default_desc = default_desc + str(show_default[item]) + "、"
		xlwt_worksheet.write(number, price_column_number + 4, label=default_desc)


# 判断机况描述中的字段是否在对照表中全部包含，只有当全部包含时，才能查询价格
def judge_contain_desc(desc):
	for item in desc.split('、'):
		if item != "" and item not in use_field:
			if item not in exclude_desc:
				log.log_error.append("当前对照表描述中没有:" + item)
				exclude_desc.append(item)
			return -1
	return 0


# 在列上填上文件名
def fill_comparsion_column(xlwt_worksheet):
	i = 0
	for fileName in comparison_price.keys():
		xlwt_worksheet.write(0, column_name_number["单台出价"] + 5 + i, label=str(fileName))
		i += 1


# 填上对比价格
def fill_comparison_price(number, key, xlwt_worksheet):
	i = 0
	for fileName in comparison_price.keys():
		if key in comparison_price[fileName].keys():
			xlwt_worksheet.write(number, column_name_number["单台出价"] + i, label=str(comparison_price[fileName][key]))
		i += 1


def get_price(number, xlrd_worksheet, xlwt_worksheet, userIndex):
	global already_search

	type = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["机型"]])).lower()
	sku = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["sku"]])).lower()
	quality = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["成色"]])).lower()
	desc = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["机况描述"]])).lower()
	priceCell = str(xlrd_worksheet.row_values(number)[column_name_number["单台出价"]])

	number += 1

	if judge_contain_desc(desc) == -1:
		excel_fill(xlwt_worksheet, number - 1, 5, -1, "", 0)
		return

	if priceCell.replace(' ', '') != "":
		return

	show_default = {"机身颜色": "", "电池健康度": "", "网络制式": "", "购买渠道": ""}

	fill_comparison_price(number - 1, sku + desc + quality, xlwt_worksheet)

	if sku + desc + quality not in already_search.keys():
		already_search[sku + desc + quality] = {}
		productId = product.get_product_id(type, userIndex)
		if productId == -2:
			return
		if productId != -1:
			paijiDesc = product.get_desc(productId, userIndex)
			if paijiDesc == -2:
				return
			if paijiDesc != -1:
				colors = []
				pricePropertyList = get_pricePropertyValues(paijiDesc, type, sku, sku + "、" + desc, number, show_default,
																										colors)
				if pricePropertyList == -1:
					already_search[sku + desc + quality][0] = -1
					excel_fill(xlwt_worksheet, number - 1, 1, -1, show_default, 0)
					log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
					return

				if pricePropertyList == -3:
					already_search[sku + desc + quality][0] = -3
					log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行对应的保修或电池情况")
					excel_fill(xlwt_worksheet, number - 1, 4, -1, show_default, 0)
					return

				pricePropertyLists = get_pricePropertyValues_one(quality, desc, pricePropertyList.copy())
				if len(pricePropertyLists) == 0:
					pricePropertyLists = get_pricePropertyValues_two(pricePropertyList.copy())
				for index in range(len(pricePropertyLists)):
					price = 9999999
					if len(colors) != 0:
						choiceColor = {}
						for color in colors:
							pricePropertyLists[index].append(color['id'])
							resp = product.get_report_no(productId, pricePropertyLists[index], userIndex)
							if resp == -2:
								return
							if resp != -1:
								tempPrice = product.get_price(resp, userIndex)
								if tempPrice == -2:
									return
								if tempPrice != -1 and tempPrice < price:
									choiceColor = color
									price = tempPrice
							pricePropertyLists[index].pop()
					else:
						resp = product.get_report_no(productId, pricePropertyLists[index], userIndex)
						if resp == -2:
							return
						if resp != -1:
							price = product.get_price(resp, userIndex)
							if price == -2:
								return
					if price != 9999999 and price != -1:
						already_search[sku + desc + quality][index] = price
						log.log_success.append(
							"用户：" + str(access.user[userIndex]['userName'] + "查出了" + str(number) + "行的价格为" + str(price) +
													"元"))
						if len(colors) != 0:
							default_colors_desc = ""
							for color in colors:
								if color['value'] == choiceColor['value']:
									default_colors_desc += color['value'] + "*" + "、"
								else:
									default_colors_desc += color['value'] + "、"
							show_default["机身颜色"] = default_colors_desc
						excel_fill(xlwt_worksheet, number - 1, 2, price, show_default, index)
						time.sleep(1)
					else:
						already_search[sku + desc + quality][index] = -1
						log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
						excel_fill(xlwt_worksheet, number - 1, 1, -1, show_default, index)
		else:
			already_search[sku + desc + quality][0] = -2
			log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "查出" + str(number) + "行的机型搜索不到")
			excel_fill(xlwt_worksheet, number - 1, 3, -1, show_default, 0)
	else:
		prices = already_search[sku + desc + quality]
		for index in range(len(prices)):
			price = prices[index]
			if price == -1:
				log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
				excel_fill(xlwt_worksheet, number - 1, 1, -1, show_default, index)
			elif price == -2:
				log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "查出" + str(number) + "行的机型搜索不到")
				excel_fill(xlwt_worksheet, number - 1, 3, -1, show_default, index)
			else:
				log.log_error.append("用户：" + str(
					access.user[userIndex]['userName'] + "查出了" + str(number) + "行的价格为" + str(price) +
					"元"))
				excel_fill(xlwt_worksheet, number - 1, 2, price, show_default, index)


# 去掉字符串中的空格
def remove_space(s):
	return s.replace(' ', '').encode('utf-8').replace(b'\xc2\xa0', b'').decode('utf-8')


def traverse_excel(xlrd_worksheet, xlwt_worksheet):
	global column_name_number
	userNum = len(access.user)
	threads = []
	threadLock = threading.Lock()
	fill_comparsion_column(xlwt_worksheet)
	column_name_number = get_column_name_number(xlrd_worksheet)
	if judge_excel_legal(column_name_number, "询价表") == -1:
		return
	if userNum == 0:
		log.log_error.append("没有可用用户了!!!")
	for i in range(userNum):
		thread = userThread(i, threadLock, xlrd_worksheet, xlwt_worksheet)
		thread.start()
		threads.append(thread)
	for t in threads:
		t.join()

# for item in select_logs:
#	print(item)

#	print(log)
#	for item in product.product_log:
#		print(item)
#
