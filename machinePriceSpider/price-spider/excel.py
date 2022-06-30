# coding=utf-8
import re
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

# 当前处理表格的行数
count = 1
# 用来存储已经查询了的机器，每次查一个机器时，会先看起其是否有相同的机器已查询过
already_search = {}
# 当前对照表中没有包含的描述
exclude_desc = []

'''
存储对照表中的价格

comparison_price = {
	"文件名1": {
		"sku1 + desc1 + quality1" : 价格1,
		"sku2 + desc2 + quality2" : 价格2
	},
	"文件名2": {
		"sku1 + desc1 + quality1" : 价格1,
		"sku2 + desc2 + quality2" : 价格2
	}
}
'''
comparison_price = {}

'''
excel文件中列号与列名的对应关系。 key为列名，value为列号

例如：column_name_number = {
	'sku': 2,
	'机况描述': 3
}
'''
column_name_number = {}

# 是否继续搜索价格的标记，当为0时，表示暂停搜索
search_price_flag = 1
# 查询价格的方式：1：小当；2：拍机堂
search_price_method = 1

'''
存储机器的拍机堂的属性名与对应的机况描述中可能出现的的对应关系

例如：use_contrast = {
	'iCloud已注销': ['密码/icloud已解除', '密码/云账号已解除']
	'iCloud无法注销': ['密码/云账号无法全部解除']
}
'''
use_contrast = {}

'''
该机器所有拍机堂的属性名对应的机况描述中可能出现的描述的集合

例如: 
use_excel_field = ['密码/icloud已解除', '密码/云账号已解除', '密码/云账号无法全部解除', '按键/卡槽/耳机孔失灵或缺失', '音量键下陷', '开机键按不动', '开机键按压手感欠佳', '开机不灵敏','振动键异常', '振动键失灵']
'''
use_excel_field = []

'''
拍机堂中一个机器的所有属性

例如：use_paiji_field = ["大陆国行", "港澳机", "256G", ...]
'''
use_paiji_field = []


def init():
	global count
	global already_search
	global exclude_desc
	global search_price_flag

	log.log_error = []
	log.log_success = []

	already_search = {}
	exclude_desc = []
	product.product_log = []
	count = 1
	search_price_flag = 1

	# 初始化用户
	access.init_user()
	# 初始化拍机堂一些信息
	paijiContrast.init()


def judge_excel_legal(column_name_number_corr, fileName):
	"""
	判断excel文件格式是否合法

	:param column_name_number_corr: 列号与列名对应关系
	:param fileName: 文件名
	:return: -1: 文件不合法; 0: 文件合法
	"""
	column_name = ["机型", "sku", "成色", "机况描述", "单台出价1"]
	for name in column_name:
		if name not in column_name_number_corr.keys():
			log.log_error.insert(0, fileName + "文件中没有" + name + "列")
			return -1
	return 0


def get_column_name_number(xlrd_worksheet):
	"""
	获取excel文件中列号与列名的对应关系。 key为列名，value为列号
	"""
	column_name_number1 = {}
	for i in range(len(xlrd_worksheet.row_values(0))):
		column_name = remove_space(str(xlrd_worksheet.row_values(0)[i])).lower()
		column_name_number1[column_name] = i
	column_name_number1["第一个空白"] = len(xlrd_worksheet.row_values(0))
	return column_name_number1


def dealComparisonExcel(files):
	"""
	获取对照excel表中的价格对象comparison_price

	:param files: 所有对照excel表
	"""
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
			if str(xlrd_worksheet.row_values(i)[price_number]).replace(' ', '') != '' and sku + desc + quality not in comparison_price[f.filename].keys():
				comparison_price[f.filename][sku + desc + quality] = xlrd_worksheet.row_values(i)[price_number]


def get_use_paiji_and_excel_field(pai_desc):
	"""
	获取该机器在拍机堂上的所有属性, 即设置use_paiji_field对象

	获取该机器所有拍机堂的属性名对应的机况描述中可能出现的描述的集合, 即设置use_excel_field对象
	"""
	global use_paiji_field
	global use_excel_field
	use_paiji_field = []
	use_excel_field = []
	keys = ["qualityInfos", "functionInfos"]
	for key in keys:
		# item对应一组属性的归类，例如item为受潮情况；受潮情况下面会有很多属性对象，例如{"id": 5226,"propertyName": 1120,"value": "机身进水"}, {"id": 5227,"propertyName": 1120,"value": "进水无进水"}
		for item in pai_desc[key]:
			# item1对应机器的属性对象，例如{"id": 5226,"propertyName": 1120,"value": "机身进水"}
			for item1 in item["pricePropertyValueVos"]:
				# property_name为机器的属性，例如机身进水
				property_name = remove_space(item1['value'])
				use_paiji_field.append(property_name)
				if property_name not in use_contrast.keys():
					log.log_error.insert(0, "缺少拍机堂中:" + property_name + "字段")
					return property_name

				if use_contrast[property_name] != "" and use_contrast[property_name] != None:
					item2 = use_contrast[property_name]
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

	# 获取对照表
	files = request.files.getlist("files")
	# 处理对照表，获取comparison_price对象
	dealComparisonExcel(files)

	# 获取询价表
	file = request.files.get("file")

	# 获取询价方式
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

	# 询价
	ans = traverse_excel(oldws, newWs)
	if ans != 1:
		return "出现错误!!!"

	output = io.BytesIO()
	newWb.save(output)
	response = make_response(output.getvalue())

	filename = "export.xls"

	response.headers["Content-Type"] = "application/octet-stream; charset=UTF-8"
	response.headers["Content-Disposition"] = "attachment; filename={}".format(filename)
	response.headers["Access-Control-Allow-Origin"] = '*'  # 允许使用响应数据的域。也可以利用请求header中的host字段做一个过滤器。
	response.headers["Access-Control-Allow-Methods"] = 'POST, GET, OPTIONS'  # 允许的请求方法
	response.headers["Access-Control-Allow-Headers"] = "x-requested-with,content-type"  # 允许的请求header

	access.delay(1)
	access.logout_all()
	return response


@app.route("/stop-search-price", methods=['POST', 'GET'])
def stop_search_price():
	global search_price_flag
	access.logout_all()
	search_price_flag = 0


@app.route("/log", methods=['POST', 'GET'])
def get_log():
	response = make_response(jsonify({"log_success": log.log_success, "log_error": log.log_error}))

	response.headers["Access-Control-Allow-Origin"] = '*'  # 允许使用响应数据的域。也可以利用请求header中的host字段做一个过滤器。
	response.headers["Access-Control-Allow-Methods"] = 'POST, GET, OPTIONS'  # 允许的请求方法
	response.headers["Access-Control-Allow-Headers"] = "x-requested-with,content-type"  # 允许的请求header

	return response


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
			self.threadLock.acquire()
			temp = 0
			if count < self.xlrd_worksheet.nrows and search_price_flag == 1:
				temp = count
				count += 1
			self.threadLock.release()
			get_price(temp, self.xlrd_worksheet, self.xlwt_worksheet, self.userIndex)


def get_color_pricePropertyValue(paiji_colors, model, sku, colors):
	"""
	在机身颜色没有完全匹配的情况下，进行机身颜色的选择
	"""
	sku = str(sku).replace(model, '')
	color = ""
	for item in paijiContrast.color:
		if item in sku:
			color = item
	for item1 in paiji_colors:
		if color in item1['value']:
			colors.append(item1)


def apple_model_match(str1, str2):
	str1 = str(str1).replace(' ', '').lower().replace('苹果', '').replace('(5g版)', '').replace('（5g版）', '').replace('（', '').replace(
		'）', '').replace('(', '').replace(')', '')
	str2 = str(str2).replace(' ', '').lower().replace('苹果', '').replace('(5g版)', '').replace('（5g版）', '').replace('（', '').replace(
		'）', '').replace('(', '').replace(')', '')
	if str1 == str2:
		return 1
	return 0


def get_guarantee_propertyValue(model, sku):
	"""
	根据机型-保修-电池容量对照表选出对应的保修属性
	"""
	for item in paijiContrast.mode_guarantee_battery:
		for item1 in str(item['model']).split('、'):
			if apple_model_match(item1, model) and str(item['excelGuarantee']).replace(' ', '').lower() in sku:
				return {'id': int(item['paijiGuaranteeId']), 'value': paijiContrast.guarantee_corr[str(item['paijiGuaranteeId'])]}
	for item in paijiContrast.mode_guarantee_battery:
		for item1 in str(item['model']).split('、'):
			if apple_model_match(item1, model) == 1 and int(item['guaranteeDefaultEnable']) == 1:
				return {'id': int(item['guaranteeDefaultId']), 'value': paijiContrast.guarantee_corr[str(item[
																											 'guaranteeDefaultId'])]}
	return -3


def get_battery_propertyValue(model):
	"""
	根据机型-保修-电池容量对照表选出对应的电池健康属性
	"""
	for item in paijiContrast.mode_guarantee_battery:
		for item1 in str(item['model']).split('、'):
			if apple_model_match(item1, model) == 1:
				return {'id': int(item['batteryId']), 'value': paijiContrast.battery_corr[str(item[
																								  'batteryId'])]}
	return -3


def get_pricePropertyValue(category_name, paiji_category_desc, model, sku, sku_desc, select_log, show_default):
	"""
	选出category_name属性组paiji_category_desc中的某个属性
	例如category_name为iCloud是否可注销, paiji_category_desc为[{"id": 5226,"propertyName": 1120,"value": "iCloud已注销"}, {"id": 5226,"propertyName": 1120,"value": "iCloud无法注销"}]
	:return -3: 表示没有选出电池健康与保修情况
	"""
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

	if category_name == '机身颜色':
		if "oppoa91" in remove_space(sku_desc).lower() and "黑色" in remove_space(sku).lower():
			sku = sku + "、暗夜星晨"
		elif "oppok5" in remove_space(sku_desc).lower() and "白色" in remove_space(sku).lower():
			sku = sku + "、极地阳光"
		elif "oppok5" in remove_space(sku_desc).lower() and "蓝色" in remove_space(sku).lower():
			sku = sku + "、赛博金属"
		elif "oppok5" in remove_space(sku_desc).lower() and "绿色" in remove_space(sku).lower():
			sku = sku + "、奇妙森林"
		elif "oppor9splus" in remove_space(sku_desc).lower() and "粉色" in remove_space(sku).lower():
			sku = sku + "、玫瑰金"
		elif "realme真我x50pro（5g版）" in remove_space(sku_desc).lower() and "青苔" in remove_space(sku).lower():
			sku = sku + "、绿色"
		elif "vivoxplay6" in remove_space(sku_desc).lower() and "粉色" in remove_space(sku).lower():
			sku = sku + "、玫瑰金"
		elif "vivoz5" in remove_space(sku_desc).lower() and "浅蓝" in remove_space(sku).lower():
			sku = sku + "、全息幻彩"
		elif "vivoz5" in remove_space(sku_desc).lower() and "紫色" in remove_space(sku).lower():
			sku = sku + "、极光幻境"
		elif "vivoz5" in remove_space(sku_desc).lower() and "绿色" in remove_space(sku).lower():
			sku = sku + "、极速幻影"
		elif "vivoz5" in remove_space(sku_desc).lower() and "黑色" in remove_space(sku).lower():
			sku = sku + "、竹林幻夜"
		elif "vivoz5" in remove_space(sku_desc).lower() and "深蓝" in remove_space(sku).lower():
			sku = sku + "、湖光幻月"
		elif "华为mate9pro" in remove_space(sku_desc).lower() and "粉色" in remove_space(sku).lower():
			sku = sku + "、玫瑰金"
		elif "华为nova5ipro（华为nova5z）" in remove_space(sku_desc).lower() and "绿色" in remove_space(sku).lower():
			sku = sku + "、翡冷翠"
		elif "华为畅享10" in remove_space(sku_desc).lower() and "浅蓝" in remove_space(sku).lower():
			sku = sku + "、天空之境"
		elif "华为畅享10" in remove_space(sku_desc).lower() and "深蓝" in remove_space(sku).lower():
			sku = sku + "、极光色"
		elif "华为畅享10plus" in remove_space(sku_desc).lower() and "蓝色" in remove_space(sku).lower():
			sku = sku + "、天空之境"
		elif "华为畅享10s" in remove_space(sku_desc).lower() and "蓝色" in remove_space(sku).lower():
			sku = sku + "、天空之境"
		elif "魅族17pro（5g版）" in remove_space(sku_desc).lower() and "乌金" in remove_space(sku).lower():
			sku = sku + "、黑色"
		elif "荣耀畅玩9a" in remove_space(sku_desc).lower() and "蓝水翡翠" in remove_space(sku).lower():
			sku = sku + "、绿色"
		elif "oppoa91" in remove_space(sku_desc).lower() and "蓝色" in remove_space(sku).lower():
			sku = sku + "、融雪之光"
		elif "opporeno3pro（5g版）" in remove_space(sku_desc).lower() and "红蓝" in remove_space(sku).lower():
			sku = sku + "、日出印象"
		elif "opporeno3（5g版）" in remove_space(sku_desc).lower() and "日出印象" in remove_space(sku).lower():
			sku = sku + "、红蓝"
		elif "opporeno4pro（5g版）" in remove_space(sku_desc).lower() and "仲夏荧光" in remove_space(sku).lower():
			sku = sku + "、绿色"
		elif "realmex2" in remove_space(sku_desc).lower() and "牛油果" in remove_space(sku).lower():
			sku = sku + "、绿色"
		elif "redmik30" in remove_space(sku_desc).lower() and "花影惊鸿" in remove_space(sku).lower():
			sku = sku + "、粉色"
		elif "redmik30" in remove_space(sku_desc).lower() and "深海微光" in remove_space(sku).lower():
			sku = sku + "、蓝色"
		elif "vivox30（5g版）" in remove_space(sku_desc).lower() and "曜石" in remove_space(sku).lower():
			sku = sku + "、黑色"
		elif "vivoy9s" in remove_space(sku_desc).lower() and "幻彩" in remove_space(sku).lower():
			sku = sku + "、幻彩晴空"
		elif "华为nova7（5g版）" in remove_space(sku_desc).lower() and "绮境森林" in remove_space(sku).lower():
			sku = sku + "、绿色"
		elif "华为p30pro" in remove_space(sku_desc).lower() and "赤茶橘" in remove_space(sku).lower():
			sku = sku + "、橙色"
		elif "华为p30" in remove_space(sku_desc).lower() and "赤茶橘" in remove_space(sku).lower():
			sku = sku + "、橙色"
		elif "华为畅享10plus" in remove_space(sku_desc).lower() and "赤茶橘" in remove_space(sku).lower():
			sku = sku + "、橙色"
		elif "荣耀20pro" in remove_space(sku_desc).lower() and "幻夜星河" in remove_space(sku).lower():
			sku = sku + "、蓝色"
		elif "荣耀30pro（5g版）" in remove_space(sku_desc).lower() and "流光幻境" in remove_space(sku).lower():
			sku = sku + "、流光幻镜"
		elif "荣耀30（5g版）" in remove_space(sku_desc).lower() and "流光幻境" in remove_space(sku).lower():
			sku = sku + "、流光幻境"
		elif "荣耀50（5g版）" in remove_space(sku_desc).lower() and "水晶初雪" in remove_space(sku).lower():
			sku = sku + "、初雪水晶"
		elif "荣耀play4t" in remove_space(sku_desc).lower() and "极光蓝" in remove_space(sku).lower():
			sku = sku + "、极光色"
		elif "荣耀v30（5g版）" in remove_space(sku_desc).lower() and "幻夜星河" in remove_space(sku).lower():
			sku = sku + "、黑色"
		elif "小米mix3" in remove_space(sku_desc).lower() and "翡翠色" in remove_space(sku).lower():
			sku = sku + "、绿色"

	if category_name in paijiContrast.excludeField:
		temp_paiji_category_desc = sorted(paiji_category_desc, key=lambda e: len(e.__getitem__('value')), reverse=True)
		for item in temp_paiji_category_desc:
			if remove_space(item["value"]).lower() in remove_space(sku):
				select_log[category_name] = item['value']
				return int(item['id'])
	else:
		cnt = 0
		res = 0
		for item in paiji_category_desc:
			if remove_space(str(item['value'])).lower() not in use_contrast.keys():
				break
			else:
				detectionFields = str(use_contrast.get(item['value'])).split('、')
				if detectionFields != None:
					for detectionFiled in detectionFields:
						if detectionFiled != '' and remove_space(str(detectionFiled)).lower() in remove_space(sku_desc).split("、"):
							cnt += 1
							select_log[category_name] = item['value']
							res = int(item['id'])
		if cnt == 1:
			return res
		elif cnt > 1:
			# 说明根据excel表中的机况描述中能匹配多种
			return -4
	if '电池健康' in category_name:
		resp = get_battery_propertyValue(model)
		if resp != -3:
			select_log[category_name] = resp['value']
			return resp['id']
		return -3
	if category_name == "机身颜色":
		return -2
	if category_name == "内存":
		temp1 = ["oppoa53", "oppoa37", "红米4a", "红米5a", "荣耀畅玩5a"]
		for item in temp1:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "2G+16G"
				return 4482

		temp2 = ["oppoa57", "三星galaxys6"]
		for item in temp2:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "3G+32G"
				return 3964

		temp3 = ["opporeno2", "小米mix2全陶瓷尊享版", "华为nova6（4g版）", "oppoa11x"]
		for item in temp3:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "8G+128G"
				return 5032

		temp4 = ["oppoa9x", "oppor15x", "redmik30极速版（5g版）", "红米note7pro", "华为mate20pro无屏幕指纹版"]
		for item in temp4:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "6G+128G"
				return 4068

		temp5 = ["oppoa59s", "vivoy67", "vivoy75"]
		for item in temp5:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "4G+32G"
				return 3965

		temp6 = ["美图m8", "三星galaxys8"]
		for item in temp6:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "4G+64G"
				return 4066

		temp7 = ["vivox27pro"]
		for item in temp7:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "8G+256G"
				return 5835

		temp8 = ["小米mix3故宫特别版"]
		for item in temp8:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "10G+256G"
				return 8582

		temp9 = ["oppor11plus", "oppor9splus"]
		for item in temp9:
			if item in remove_space(sku_desc).lower():
				select_log[category_name] = "6G+64G"
				return 4067

		return -1
	if category_name == "购买渠道":
		if "非国行" in sku and "有锁" not in sku:
			select_log[category_name] = "非大陆国行"
			return 3445
	if category_name == "网络制式":
		if "移动版" in sku:
			select_log[category_name] = "运营商版全网通"
			return 12114
	if category_name in paijiContrast.musthaveField:
		select_log[category_name] = "没有选出"
		return -1
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


def get_pricePropertyValues(paijiDesc, model, sku, sku_desc, number, show_default, colors, colNumOfSelectLog, xlwt_worksheet):
	"""
	根据excel表中的信息选出PropertyValues
	"""
	pricePropertyList = []
	pricePropertyValue = -1
	select_log = {}
	for parent_category in paijiDesc.keys():
		for children_category in paijiDesc[parent_category]:
			if 'pricePropertyValueVos' in children_category:
				pricePropertyValue = get_pricePropertyValue(children_category["name"],
															children_category["pricePropertyValueVos"], model, sku,
															sku_desc,
															select_log, show_default)

				if pricePropertyValue == -1:
					return {-1: children_category["name"]}
				if pricePropertyValue == -3:
					return -3
				if pricePropertyValue == -4:
					return {-4: children_category["name"]}
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
	excel_fill(xlwt_worksheet, number - 1, colNumOfSelectLog, '、'.join(('%s' % item for item in select_log.values())), "", 0)
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


def get_pricePropertyValues_one(quality, desc, pricePropertyList, paijiDescProertyIds):
	"""
	根据询价规则页面中的设置来价格查询规则
	"""
	pricePropertyLists = []
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


def get_pricePropertyValues_two(pricePropertyList):
	"""
	透图与色差
	"""
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

	style0 = XFStyle()
	pattern0 = Pattern()
	pattern0.pattern = Pattern.SOLID_PATTERN
	# 设置单元格背景色为紫色, 表示当前用户查此行价格时出错
	pattern0.pattern_fore_colour = Style.colour_map['dark_purple']

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

	style5 = XFStyle()
	pattern5 = Pattern()
	pattern5.pattern = Pattern.SOLID_PATTERN
	# 设置单元格背景色为粉色，表示出现不选查改机器价格的关键词
	pattern5.pattern_fore_colour = Style.colour_map['pink']

	style6 = XFStyle()
	pattern6 = Pattern()
	pattern6.pattern = Pattern.SOLID_PATTERN
	# 设置单元格背景色为棕色，表示机况描述中存在多个对同一属性组的描述
	pattern6.pattern_fore_colour = Style.colour_map['brown']

	style.pattern = pattern
	style0.pattern = pattern0
	style1.pattern = pattern1
	style2.pattern = pattern2
	style3.pattern = pattern3
	style4.pattern = pattern4
	style5.pattern = pattern5
	style6.pattern = pattern6

	price_column_number = column_name_number["单台出价1"]

	if method == 1:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style)
	elif method == 0:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style0)
	elif method == -1:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style6)
	elif method == 3:
		xlwt_worksheet.write(number, price_column_number + index, label="", style=style1)
	elif method == 4:
		xlwt_worksheet.write(number, price_column_number + index, label="", style=style2)
	elif method == 5:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style3)
	elif method == 6:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style4)
	elif method == 7:
		xlwt_worksheet.write(number, price_column_number + index, label="*" + str(content), style=style5)
	elif method == 2:
		xlwt_worksheet.write(number, price_column_number + index, label=str(content))
		default_desc = ""
		for item in show_default.keys():
			if str(show_default[item]) != "":
				default_desc = default_desc + str(show_default[item]) + "、"
		xlwt_worksheet.write(number, price_column_number + 4, label=default_desc)
	elif method in [8, 9, 10, 11, 12, 13, 14, 15]:
		xlwt_worksheet.write(number, price_column_number + method - 3, label=str(content))


def judge_contain_desc(desc):
	"""
	判断机况描述中的字段是否在对照表中全部包含，只有当全部包含时，才能查询价格
	:param desc:
	:return:
	"""
	ans = ""
	for item in desc.split('、'):
		item = remove_space(item)
		if item != "" and item not in use_excel_field:
			if item not in exclude_desc:
				log.log_error.insert(0, "当前对照表描述中没有:" + item)
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


def fill_comparison_price(number, key, xlwt_worksheet):
	"""
	填上对比价格
	:param number: 行数
	"""
	i = 0
	for fileName in comparison_price.keys():
		if key in comparison_price[fileName].keys():
			excel_fill(xlwt_worksheet, number, 12 + i, str(comparison_price[fileName][key]), "", 0)
		i += 1


def deal_sell_price(skuId, xlwt_worksheet, number):
	if skuId in sellPrice.sell_product.keys():
		excel_fill(xlwt_worksheet, number, 8, sellPrice.sell_product[skuId]['early_create_date'][0:10], "", 0)
		excel_fill(xlwt_worksheet, number, 9, int(round(np.mean(sellPrice.sell_product[skuId]['day']))), "", 0)
		excel_fill(xlwt_worksheet, number, 10, len(sellPrice.sell_product[skuId]['day']), "", 0)
	if skuId in sellPrice.sold_product.keys():
		excel_fill(xlwt_worksheet, number, 11, int(round(np.mean(sellPrice.sold_product[skuId]['day']))), "", 0)
		excel_fill(xlwt_worksheet, number, 12, len(sellPrice.sold_product[skuId]['day']), "", 0)


def get_desc_property_ids(paijiDesc):
	paijiDescProertyIds = []
	for key in paijiDesc.keys():
		for propertyValueVos in paijiDesc[key]:
			if 'pricePropertyValueVos' in propertyValueVos:
				for propertyValueVo in propertyValueVos["pricePropertyValueVos"]:
					paijiDescProertyIds.append(propertyValueVo["id"])
	return paijiDescProertyIds


def get_price(number, xlrd_worksheet, xlwt_worksheet, userIndex):
	"""
	查询价格

	:param number: 行数
	"""
	global already_search

	model = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["机型"]])).lower()
	sku = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["sku"]])).lower()
	quality = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["成色"]])).lower()

	desc = remove_space(str(xlrd_worksheet.row_values(number)[column_name_number["机况描述"]])).lower()
	pattern = re.compile(r'、+')
	pattern1 = re.compile(r'、$')
	desc = re.sub(pattern1, '', re.sub(pattern, '、', desc))

	priceCell = str(xlrd_worksheet.row_values(number)[column_name_number["单台出价1"]])

	number += 1

	fill_comparison_price(number - 1, sku + desc + quality, xlwt_worksheet)

	if desc == "":
		return

	if priceCell.replace(' ', '') != "" and priceCell.replace(' ', '')[0] != "*":
		return

	show_default = {"机身颜色": "", "电池健康度": "", "网络制式": "", "购买渠道": ""}

	temp = ["触摸失灵/延迟", "密码/云账号无法全部解除", "屏幕无法正常显示", "无法正常进入桌面或开机异常"]
	for item in temp:
		if item in desc:
			excel_fill(xlwt_worksheet, number - 1, 7, item, "", 0)
			return

	if sku + desc + quality not in already_search.keys() or "flag" not in already_search.keys():
		already_search[sku + desc + quality] = {}
		productId = product.get_product_id(model, userIndex)
		if productId == -2:
			excel_fill(xlwt_worksheet, number - 1, 0, "productId", "", 0)
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
				excel_fill(xlwt_worksheet, number - 1, 0, "paijiDesc", "", 0)
				return
			if paijiDesc != -1:
				colors = []
				desc_list = [desc, desc[:desc.rfind("、")]]
				pricePropertyLists = []
				for i in range(2):
					pricePropertyList = get_pricePropertyValues(paijiDesc, model, sku, sku + "、" + desc_list[i], number,
																show_default,
																colors, 13 + i, xlwt_worksheet)
					if type(pricePropertyList) == dict:
						if -1 in pricePropertyList.keys():
							already_search[sku + desc + quality][0] = -1
							excel_fill(xlwt_worksheet, number - 1, 1, pricePropertyList[-1], show_default, 0)
							log.log_error.insert(0, "用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
							return
						if -4 in pricePropertyList.keys():
							already_search[sku + desc + quality][0] = -4
							excel_fill(xlwt_worksheet, number - 1, -1, pricePropertyList[-4], show_default, 0)
							log.log_error.insert(0, "用户：" + str(access.user[userIndex]['userName']) + "查出" + str(number) + "行中的" + pricePropertyList[-4] + "属性存在多个选择")
							return

					# 没有选出保修或电池容量
					if pricePropertyList == -3:
						already_search[sku + desc + quality][0] = -3
						log.log_error.insert(0, "用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行对应的保修或电池情况")
						excel_fill(xlwt_worksheet, number - 1, 4, -1, show_default, 0)
						return

					pricePropertyLists.append(pricePropertyList)

				# 判断是查小当还是采货侠
				# pricePropertyLists = []
				#	if search_price_method == 1:
				#		paijiDescProertyIds = get_desc_property_ids(paijiDesc)
				#		pricePropertyLists = get_pricePropertyValues_one(quality, desc, pricePropertyList.copy(),
				#														 paijiDescProertyIds)

				#		if len(pricePropertyLists) == 0:
				#			pricePropertyLists = get_pricePropertyValues_two(pricePropertyList.copy())
				#	else:
				#		pricePropertyLists.append(pricePropertyList)

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
									excel_fill(xlwt_worksheet, number - 1, 0, "price", "", 0)
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
								excel_fill(xlwt_worksheet, number - 1, 0, "price", "", 0)
								return
							price = resp1['price']
							skuId = resp1['skuId']
					else:
						resp1 = product.get_price_new(productId, pricePropertyLists[index], userIndex)
						if resp1 == -2:
							excel_fill(xlwt_worksheet, number - 1, 0, "price", "", 0)
							return
						price = resp1['price']
						skuId = resp1['skuId']
					if price != 9999999 and price != -1:
						already_search[sku + desc + quality][index] = price
						log.log_success.insert(0,
											   "用户：" + str(
												   access.user[userIndex]['userName'] + "查出了" + str(number) + "行的价格为" + str(price) +
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
					# time.sleep(1)
					else:
						already_search[sku + desc + quality][index] = -1
						log.log_error.insert(0,
											 "用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
						excel_fill(xlwt_worksheet, number - 1, 1, -1, show_default, index)
					if index == len(pricePropertyLists) - 1:
						already_search[sku + desc + quality]["flag"] = 1
		else:
			already_search[sku + desc + quality][0] = -2
			log.log_error.insert(0, "用户：" + str(access.user[userIndex]['userName']) + "查出" + str(number) + "行的机型搜索不到")
			excel_fill(xlwt_worksheet, number - 1, 3, -1, show_default, 0)
	else:
		prices = already_search[sku + desc + quality]
		show_default = already_search[sku + desc + quality]["show_default"]
		for index in range(len(prices)):
			if index not in prices.keys():
				break
			price = prices[index]
			if price == -1:
				log.log_error.insert(0, "用户：" + str(access.user[userIndex]['userName']) + "没有查出" + str(number) + "行的价格")
				excel_fill(xlwt_worksheet, number - 1, 1, -1, show_default, index)
			elif price == -2:
				log.log_error.insert(0, "用户：" + str(access.user[userIndex]['userName']) + "查出" + str(number) + "行的机型搜索不到")
				excel_fill(xlwt_worksheet, number - 1, 3, -1, show_default, index)
			elif price == -4:
				log.log_error.insert(0, "用户：" + str(access.user[userIndex]['userName']) + "查出" + str(number) + "行中的属性存在多个选择")
				excel_fill(xlwt_worksheet, number - 1, -1, -1, show_default, index)
			else:
				log.log_success.insert(0, "用户：" + str(
					access.user[userIndex]['userName'] + "查出了" + str(number) + "行的价格为" + str(price) +
					"元"))
				excel_fill(xlwt_worksheet, number - 1, 2, price, show_default, index)
		deal_sell_price(already_search[sku + desc + quality]["skuId"], xlwt_worksheet, number - 1)


# 去掉字符串中的空格
def remove_space(s):
	return s.replace(' ', '').encode('utf-8').replace(b'\xc2\xa0', b'').decode('utf-8')


def init_first_row(xlwt_worksheet):
	"""
	初始化列名
	"""
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
	xlwt_worksheet.write(0, price_column_number + 10, label="单台出价1选法")
	xlwt_worksheet.write(0, price_column_number + 11, label="单台出价2选法")
	xlwt_worksheet.write(0, price_column_number + 12, label="单台出价3选法")
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
		log.log_error.insert(0, "没有可用用户了!!!")
		return -1
	init_first_row(xlwt_worksheet)
	for i in range(userNum):
		thread = userThread(i, threadLock, xlrd_worksheet, xlwt_worksheet)
		thread.start()
		threads.append(thread)
	for t in threads:
		t.join()
	return 1
