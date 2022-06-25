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
import sellPrice
import numpy as np

count = 1
already_search = {}
comparison_price = {}
select_default_color = {}
select_logs = []
column_name_number = {}
exclude_desc = []

search_price_flag = 1
search_price_method = 1

use_contrast = {}
use_excel_field = []
use_paiji_field = []


def init():
	global count
	global already_search
	global select_default_color
	global select_logs
	global exclude_desc
	global search_price_flag

	log.log_error = []
	log.log_success = []

	select_logs = []
	select_default_color = {}
	already_search = {}
	exclude_desc = []
	product.product_log = []
	count = 1
	search_price_flag = 1
	access.init_user()
	paijiContrast.init()


def judge_excel_legal(column_name_number_corr, fileName):
	column_name = ["机型", "sku", "成色", "机况描述", "单台出价1"]
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
		price_number = column_name_number1['单台出价1']

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


def get_use_paiji_and_excel_field(pai_desc):
	global use_paiji_field
	global use_excel_field
	use_paiji_field = []
	use_excel_field = []
	keys = ["qualityInfos", "functionInfos"]
	for key in keys:
		for item in pai_desc[key]:
			for item1 in item["pricePropertyValueVos"]:
				use_paiji_field.append(remove_space(item1['value']))
				if remove_space(item1['value']) not in use_contrast.keys():
					log.log_error.append("缺少拍机堂中:" + remove_space(item1['value']) + "字段")
					return item1['value']
				if use_contrast[remove_space(item1['value'])] != "" and use_contrast[remove_space(item1['value'])] != None:
					item2 = use_contrast[remove_space(item1['value'])]
					if item2 != "" and item2 != None:
						for item3 in item2.split("、"):
							if item3 != "" and item3 != None:
								use_excel_field.append(remove_space(item3))
	if use_contrast["账号已注销"] != "" and use_contrast["账号已注销"] != None:
		item2 = use_contrast["账号已注销"]
		if item2 != "" and item2 != None:
			for item3 in item2.split("、"):
				if item3 != "" and item3 != None and remove_space(item3) not in use_excel_field:
					use_excel_field.append(remove_space(item3))
	return 1


@app.route("/price_excel/import", methods=['POST', 'GET'])
def import_excel():
	init()

	global use_contrast
	global use_excel_field
	global search_price_method

	files = request.files.getlist("files")
	dealComparisonExcel(files)

	file = request.files.get("file")

	searchPriceMethod = request.form["searchPriceMethod"]
	if searchPriceMethod == "1":
		use_contrast = paijiContrast.xd_contrast
		search_price_method = 1
	else:
		use_contrast = paijiContrast.ahs_contrast
		search_price_method = 0

	f = file.read()
	oldWb = xlrd.open_workbook(file_contents=f, formatting_info=True)
	oldws = oldWb.sheets()[0]

	newWb = copy(oldWb)
	newWs = newWb.get_sheet(0)

	ans = traverse_excel(oldws, newWs)
	if ans != 1:
		return "出现错误!!!"

	output = io.BytesIO()
	newWb.save(output)
	response = make_response(output.getvalue())

	filename = "export.xls"

	response.headers["Content-Type"] = "application/octet-stream; charset=UTF-8"
	response.headers["Content-Disposition"] = "attachment; filename={}".format(filename)

	time.sleep(1)
	return response


@app.route("/stop-search-price", methods=['POST', 'GET'])
def stop_search_price():
	global search_price_flag
	search_price_flag = 0


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
		while count < self.xlrd_worksheet.nrows and search_price_flag == 1:
			# time.sleep(1)
			#print(time.strftime('%H:%M:%S'),'hahaha')
			# while count < self.xlrd_worksheet.nrows:
			self.threadLock.acquire()
			temp = 0
			if count < self.xlrd_worksheet.nrows and search_price_flag == 1:
				# if count < self.xlrd_worksheet.nrows:
				temp = count
				count += 1
				if count % 80 == 0:
					if access.update_token(0) == False:
						log.log_error.append("更新token失败")
						return
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


def apple_model_match(str1, str2):
	str1 = str(str1).replace(' ', '').lower().replace('苹果', '').replace('(5g版)', '').replace('（5g版）', '').replace('（',
																																																								'').replace(
		'）', '').replace('(', '').replace(')', '')
	str2 = str(str2).replace(' ', '').lower().replace('苹果', '').replace('(5g版)', '').replace('（5g版）', '').replace('（',
																																																								'').replace(
		'）', '').replace('(', '').replace(')', '')
	if str1 == str2:
		return 1
	return 0


def get_guarantee_propertyValue(model, sku):
	for item in paijiContrast.mode_guarantee_battery:
		for item1 in str(item['model']).split('、'):
			if apple_model_match(item1, model) and str(item['excelGuarantee']).replace(' ', '').lower() in sku:
				return {'id': int(item['paijiGuaranteeId']), 'value': paijiContrast.guarantee_corr[str(item[
																																																 'paijiGuaranteeId'])]}
	for item in paijiContrast.mode_guarantee_battery:
		for item1 in str(item['model']).split('、'):
			if apple_model_match(item1, model) == 1 and int(item['guaranteeDefaultEnable']) == 1:
				return {'id': int(item['guaranteeDefaultId']), 'value': paijiContrast.guarantee_corr[str(item[
																																																	 'guaranteeDefaultId'])]}
	return -3


def get_battery_propertyValue(model):
	for item in paijiContrast.mode_guarantee_battery:
		for item1 in str(item['model']).split('、'):
			if apple_model_match(item1, model) == 1:
				return {'id': int(item['batteryId']), 'value': paijiContrast.battery_corr[str(item[
																																												'batteryId'])]}
	return -3


def get_pricePropertyValue(category_name, paiji_category_desc, model, sku, sku_desc, select_log, show_default):
	if len(paiji_category_desc) == 1:
		select_log[category_name] = paiji_category_desc[0]['value']
		return paiji_category_desc[0]['id']
	if '保修' in category_name:
		if '华为' in category_name:
			select_log[category_name] = "保修时长<30天"
			return 14072
		resp = get_guarantee_propertyValue(model, sku)
		if resp != -3:
			select_log[category_name] = resp['value']
			return resp['id']
		select_log[category_name] = "没有选出"
		return -3

	if category_name in paijiContrast.excludeField:
		temp_paiji_category_desc = sorted(paiji_category_desc, key=lambda e: len(e.__getitem__('value')), reverse=True)
		for item in temp_paiji_category_desc:
			if remove_space(item["value"]).lower() in remove_space(sku):
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
	if '电池健康' in category_name:
		resp = get_battery_propertyValue(model)
		if resp != -3:
			select_log[category_name] = resp['value']
			return resp['id']
		return -3
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
	if category_name == "购买渠道":
		if "非国行" in sku and "有锁" not in sku:
			select_log[category_name] = "非大陆国行"
			return 3445
	if category_name == "网络制式":
		if "移动版" in sku:
			select_log[category_name] = "运营商版全网通"
			return 12114
	if category_name in paijiContrast.default_choice.keys():
		for item in paijiContrast.default_choice[category_name]:
			if item['value'] in use_paiji_field:
				# show_default[category_name] = item['value']
				select_log[category_name] = item['value']
				return item['id']
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
			if 'pricePropertyValueVos' in children_category: 
				pricePropertyValue = get_pricePropertyValue(children_category["name"],
																										children_category["pricePropertyValueVos"], model, sku, sku_desc,
																										select_log, show_default)
				if pricePropertyValue == -1:
					select_logs.append({str(number) + "行": select_log})
					return {-1: children_category["name"]}
				if pricePropertyValue == -3:
					select_logs.append({str(number) + "行": select_log})
					return -3
				# 机身颜色没有完全匹配
				if pricePropertyValue == -2:
					get_color_pricePropertyValue(children_category["pricePropertyValueVos"], model, sku, colors)
					if len(colors) == 0:
						return {-1: children_category["name"]}
				else:
					pricePropertyList.append(pricePropertyValue)
	color_desc = ""
	if len(colors) != 0:
		for color in colors:
			color_desc += color['value'] + "、"
		select_log["机身颜色"] = color_desc
	select_logs.append({str(number) + "行": select_log})
	return pricePropertyList


def judge_price_combination(desc, item):
	column_names = ["screenAppearance", "iframeBack", "screenDisplay"]
	for column_name in column_names:
		flag = 0
		if remove_space(str(item[column_name])) == "":
			continue
		for item1 in remove_space(str(item[column_name])).lower().split('、'):
			if item1 in desc:
				flag = 1
				break
		if flag == 0:
			return -1
	return 1


# 根据询价规则页面中的设置来价格查询规则
def get_pricePropertyValues_one(quality, desc, pricePropertyList, paijiDescProertyIds):
	pricePropertyLists = []
	print("paijiCon")
	print(str(paijiContrast.price_combination))
	for item in paijiContrast.price_combination:
		if quality in remove_space(item['grade']).lower().split("、"):
			flag = judge_price_combination(desc, item)
			if flag == 1:
				for i in range(3):
					if remove_space(item['price' + str(i + 1)]) != "":
						tempPricePropertyList = pricePropertyList.copy()
						for paijiDesc in str(item['price' + str(i + 1)]).split('、'):
							categoryCorr = getCategoryCorrByField(paijiDesc)
							for category in categoryCorr:
								if category['id'] in tempPricePropertyList:
									tempPricePropertyList.remove(category['id'])
						for paijiDesc in str(item['price' + str(i + 1)]).split('、'):
							categoryCorr = getCategoryCorrByField(paijiDesc)
							for category in categoryCorr:
								if category['value'] == paijiDesc and category['id'] in paijiDescProertyIds:
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
	url = "http://127.0.0.1:3001/machine/price/getCategoryCorrByField?field=" + field
	resp = json.loads(requests.get(url).text)
	return resp['obj']


def excel_fill(xlwt_worksheet, number, method, content, show_default, index):
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
	# 设置单元格背景色为绿色, 表示excel表的机况描述字段在对照表中没有
	pattern3.pattern_fore_colour = Style.colour_map['green']

	style4 = XFStyle()
	pattern4 = Pattern()
	pattern4.pattern = Pattern.SOLID_PATTERN
	# 设置单元格背景色为灰色, 表示缺少拍机堂中的某个字段描述
	pattern4.pattern_fore_colour = Style.colour_map['gray25']

	style.pattern = pattern
	style1.pattern = pattern1
	style2.pattern = pattern2
	style3.pattern = pattern3
	style4.pattern = pattern4

	price_column_number = column_name_number["单台出价1"]

	if method == 1:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style)
	elif method == 3:
		xlwt_worksheet.write(number, price_column_number + index, label="", style=style1)
	elif method == 4:
		xlwt_worksheet.write(number, price_column_number + index, label="", style=style2)
	elif method == 5:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style3)
	elif method == 6:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style4)
	elif method == 2:
		xlwt_worksheet.write(number, price_column_number + index, label=str(content))
		default_desc = ""
		for item in show_default.keys():
			if str(show_default[item]) != "":
				default_desc = default_desc + str(show_default[item]) + "、"
		xlwt_worksheet.write(number, price_column_number + 4, label=default_desc)
	elif method >= 7:
		xlwt_worksheet.write(number, price_column_number + method - 2, label=str(content))


# 判断机况描述中的字段是否在对照表中全部包含，只有当全部包含时，才能查询价格
def judge_contain_desc(desc):
	ans = ""
	for item in desc.split('、'):
		item = remove_space(item)
		if item != "" and item not in use_excel_field:
			if item not in exclude_desc:
				log.log_error.append("当前对照表描述中没有:" + item)
			exclude_desc.append(item)
			ans += item + "、"
			return ans
	return ans


# 在列上填上文件名
def fill_comparsion_column(xlwt_worksheet):
	i = 0
	for fileName in comparison_price.keys():
		xlwt_worksheet.write(0, column_name_number["单台出价1"] + 10 + i, label=str(fileName))
		i += 1


# 填上对比价格
def fill_comparison_price(number, key, xlwt_worksheet):
	i = 0
	for fileName in comparison_price.keys():
		if key in comparison_price[fileName].keys():
			excel_fill(xlwt_worksheet, number, 12 + i, str(comparison_price[fileName][key]), "", 0)
		i += 1


def deal_sell_price(skuId, xlwt_worksheet, number):
	if skuId in sellPrice.sell_product.keys():
		excel_fill(xlwt_worksheet, number, 7, sellPrice.sell_product[skuId]['early_create_date'][0:10], "", 0)
		excel_fill(xlwt_worksheet, number, 8, int(round(np.mean(sellPrice.sell_product[skuId]['day']))), "", 0)
		excel_fill(xlwt_worksheet, number, 9, len(sellPrice.sell_product[skuId]['day']), "", 0)
	if skuId in sellPrice.sold_product.keys():
		excel_fill(xlwt_worksheet, number, 10, int(round(np.mean(sellPrice.sold_product[skuId]['day']))), "", 0)
		excel_fill(xlwt_worksheet, number, 11, len(sellPrice.sold_product[skuId]['day']), "", 0)


def get_desc_property_ids(paijiDesc):
	paijiDescProertyIds = []
	for key in paijiDesc.keys():
		for propertyValueVos in paijiDesc[key]:
			if 'pricePropertyValueVos' in propertyValueVos:
				for propertyValueVo in propertyValueVos["pricePropertyValueVos"]:
					paijiDescProertyIds.append(propertyValueVo["id"])
	return paijiDescProertyIds


def get_price(number, xlrd_worksheet, xlwt_worksheet, userIndex):
	global already_search

	model = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["机型"]])).lower()
	sku = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["sku"]])).lower()
	quality = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["成色"]])).lower()
	desc = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["机况描述"]])).lower()
	priceCell = str(xlrd_worksheet.row_values(number)[column_name_number["单台出价1"]])

	number += 1

	fill_comparison_price(number - 1, sku + desc + quality, xlwt_worksheet)

	if desc == "":
		return

	if priceCell.replace(' ', '') != "" and priceCell.replace(' ', '')[0] != "*":
		return

	show_default = {"机身颜色": "", "电池健康度": "", "网络制式": "", "购买渠道": ""}

	if sku + desc + quality not in already_search.keys() or "flag" not in already_search.keys():
		already_search[sku + desc + quality] = {}
		productId = product.get_product_id(model, userIndex)
		if productId == -2:
			return
		if productId != -1:

			paijiDesc = product.get_desc(productId, userIndex)
			resp = get_use_paiji_and_excel_field(paijiDesc)
			if resp != 1:
				excel_fill(xlwt_worksheet, number - 1, 6, resp, "", 0)
				return

			exclusive_desc = judge_contain_desc(desc)
			if exclusive_desc != "":
				excel_fill(xlwt_worksheet, number - 1, 5, exclusive_desc, "", 0)
				return

			if paijiDesc == -2:
				return
			if paijiDesc != -1:
				colors = []
				pricePropertyList = get_pricePropertyValues(paijiDesc, model, sku, sku + "、" + desc, number, show_default,
																										colors)
				if type(pricePropertyList) == dict:
					if -1 in pricePropertyList.keys():
						already_search[sku + desc + quality][0] = -1
						excel_fill(xlwt_worksheet, number - 1, 1, pricePropertyList[-1], show_default, 0)
						log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
						return

				if pricePropertyList == -3:
					already_search[sku + desc + quality][0] = -3
					log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行对应的保修或电池情况")
					excel_fill(xlwt_worksheet, number - 1, 4, -1, show_default, 0)
					return

				# 判断是查小当还是采货侠
				pricePropertyLists = []
				if search_price_method == 1:
					paijiDescProertyIds = get_desc_property_ids(paijiDesc)
					pricePropertyLists = get_pricePropertyValues_one(quality, desc, pricePropertyList.copy(),
																													 paijiDescProertyIds)

					if len(pricePropertyLists) == 0:
						pricePropertyLists = get_pricePropertyValues_two(pricePropertyList.copy())
				else:
					pricePropertyLists.append(pricePropertyList)

				choiceColor = {}
				colorId = -1
				for index in range(len(pricePropertyLists)):
					price = 9999999
					skuId = -1
					if len(colors) != 0:
						if index == 0 or colorId == -1:
							for color in colors:
								pricePropertyLists[index].append(color['id'])
								resp1 = product.get_price_new(productId, pricePropertyLists[index], userIndex)
								if resp1 == -2:
									return
								tempPrice = resp1['price']
								if tempPrice != -1 and tempPrice < price:
									choiceColor = color
									price = tempPrice
									skuId = resp1['skuId']
									colorId = color['id']
								pricePropertyLists[index].pop()
						else:
							pricePropertyLists[index].append(colorId)
							resp1 = product.get_price_new(productId, pricePropertyLists[index], userIndex)
							if resp1 == -2:
								return
							price = resp1['price']
							skuId = resp1['skuId']
					else:
						resp1 = product.get_price_new(productId, pricePropertyLists[index], userIndex)
						if resp1 == -2:
							return
						price = resp1['price']
						skuId = resp1['skuId']
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
						already_search[sku + desc + quality]["show_default"] = show_default
						already_search[sku + desc + quality]["skuId"] = skuId
						deal_sell_price(skuId, xlwt_worksheet, number - 1)
						excel_fill(xlwt_worksheet, number - 1, 2, price, show_default, index)
						#time.sleep(1)
					else:
						already_search[sku + desc + quality][index] = -1
						log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
						excel_fill(xlwt_worksheet, number - 1, 1, -1, show_default, index)
					if index == len(pricePropertyLists) - 1:
						already_search[sku + desc + quality]["flag"] = 1
		else:
			already_search[sku + desc + quality][0] = -2
			log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "查出" + str(number) + "行的机型搜索不到")
			excel_fill(xlwt_worksheet, number - 1, 3, -1, show_default, 0)
	else:
		prices = already_search[sku + desc + quality]
		show_default = already_search[sku + desc + quality]["show_default"]
		for index in range(len(prices)):
			if index not in prices.keys():
				break
			price = prices[index]
			if price == -1:
				log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
				excel_fill(xlwt_worksheet, number - 1, 1, -1, show_default, index)
			elif price == -2:
				log.log_error.append("用户：" + str(access.user[userIndex]['userName']) + "查出" + str(number) + "行的机型搜索不到")
				excel_fill(xlwt_worksheet, number - 1, 3, -1, show_default, index)
			else:
				log.log_success.append("用户：" + str(
					access.user[userIndex]['userName'] + "查出了" + str(number) + "行的价格为" + str(price) +
					"元"))
				excel_fill(xlwt_worksheet, number - 1, 2, price, show_default, index)
		deal_sell_price(already_search[sku + desc + quality]["skuId"], xlwt_worksheet, number - 1)


# 去掉字符串中的空格
def remove_space(s):
	return s.replace(' ', '').encode('utf-8').replace(b'\xc2\xa0', b'').decode('utf-8')


# 初始化列名
def init_first_row(xlwt_worksheet):
	price_column_number = column_name_number["单台出价1"]
	xlwt_worksheet.write(0, price_column_number + 0, label="单台出价1")
	xlwt_worksheet.write(0, price_column_number + 1, label="单台出价2")
	xlwt_worksheet.write(0, price_column_number + 2, label="单台出价3")
	xlwt_worksheet.write(0, price_column_number + 4, label="备注")
	xlwt_worksheet.write(0, price_column_number + 5, label="销售中最早上架")
	xlwt_worksheet.write(0, price_column_number + 6, label="销售中平均天数")
	xlwt_worksheet.write(0, price_column_number + 7, label="销售中的数量")
	xlwt_worksheet.write(0, price_column_number + 8, label="售出平均天数")
	xlwt_worksheet.write(0, price_column_number + 9, label="售出中的数量")
	fill_comparsion_column(xlwt_worksheet)


def traverse_excel(xlrd_worksheet, xlwt_worksheet):
	global column_name_number
	userNum = len(access.user)
	threads = []
	threadLock = threading.Lock()
	column_name_number = get_column_name_number(xlrd_worksheet)
	if judge_excel_legal(column_name_number, "询价表") == -1:
		return -1
	if userNum == 0:
		log.log_error.append("没有可用用户了!!!")
		return -1
	init_first_row(xlwt_worksheet)
	for i in range(userNum):
		thread = userThread(i, threadLock, xlrd_worksheet, xlwt_worksheet)
		time.sleep(5)
		print(time.strftime('%H:%M:%S'),'hahaha')
		thread.start()
		threads.append(thread)
	for t in threads:
		t.join()

	# for item in select_logs:
	#	print(item)

	return 1
