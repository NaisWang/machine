import requests
import json
from app import app
from flask import jsonify

xd_contrast = {}
ahs_contrast = {}

xd_field = []
ahs_field = []

xd_combination_price_field = {
	"屏幕外观": [],
	"边框背板": [],
	"屏幕显示": []
}

paiji_combination_price_field = []

model_contrast = {}
mode_guarantee_battery = []

price_combination = []

guarantee_corr = {}
battery_corr = {}

excludeField = ["内存", "存储容量", "小型号", "机身颜色", "网络制式", "购买渠道"]
musthaveField = ["内存", "存储容量", "小型号", "表壳尺寸", "表壳材质", "连接", "网络模式", "版本", "屏幕尺寸", ""]

color = ["红", "橙", "黄", "绿", "青", "蓝", "紫", "金", "灰", "银", "白", "橙", "粉", "黑"]

default_choice = {
	"边框背板": [{'id': 7418, 'value': "外壳明显磕碰/掉漆（≥4毫米）"}, {'id': 2128, 'value': "壳明显磕碰/掉漆"},
					 {'id': 10024, 'value': "外壳无裂缝缺失"}],
	"屏幕外观": [{'id': 10025, 'value': "屏幕外观无碎裂"}, {'id': 2120, 'value': "屏有划痕"}],
	"是否可恢复出厂设置": [{'id': 9625, 'value': "已激活，可还原"}]
}


def init_contrast():
	global xd_combination_price_field
	global paiji_combination_price_field
	global xd_combination_price_field

	paiji_combination_price_field = []
	xd_combination_price_field = {
		"屏幕外观": [],
		"边框背板": [],
		"屏幕显示": []
	}

	url = "http://127.0.0.1:8081/machine/price/field"
	resp = json.loads(requests.get(url).text)
	for item in resp['obj']:
		if item["name"] not in excludeField:
			xd_contrast[item["value"]] = item["xdCheckout"]
			ahs_contrast[item['value']] = item["ahsCheckout"]
		if item["name"] in xd_combination_price_field.keys():
			paiji_combination_price_field.append(item["value"])
			if str(item["xdCheckout"]).replace(' ', '') != "" and item["xdCheckout"] != None:
				for subItem in str(item["xdCheckout"]).split('、'):
					if subItem not in xd_combination_price_field[item["name"]]:
						xd_combination_price_field[item["name"]].append(subItem)


def init_xd_field():
	for item in xd_contrast.values():
		for item1 in str(item).split("、"):
			if item1.replace(' ', '').lower() not in xd_field and item1.replace(' ', '').lower() != "" and item1.replace(' ',
																																																									 '').lower() != 'none':
				xd_field.append(item1.replace(' ', '').lower())


def init_ahs_field():
	for item in ahs_contrast.values():
		for item1 in str(item).split("、"):
			if item1.replace(' ', '').lower() not in ahs_field and item1.replace(' ', '').lower() != "" and item1.replace(
					' ', '').lower() != 'none':
				ahs_field.append(item1.replace(' ', '').lower())


def init_model_contrast():
	url = "http://127.0.0.1:8081/machine/price/model-contrast/"
	resp = json.loads(requests.get(url).text)
	for item in resp['obj']:
		model_contrast[str(item['excelModel']).replace(' ', '').replace('(', '').replace(')', '').replace('（', '').replace('）', '').lower()] = item['paijiModel']


def init_model_guarantee_battery():
	global mode_guarantee_battery
	url = "http://127.0.0.1:8081/machine/price/model-guarantee-battery/"
	resp = json.loads(requests.get(url).text)
	mode_guarantee_battery = resp['obj']


def init_guarantee_corr():
	global guarantee_corr
	url = "http://127.0.0.1:8081/machine/price/guarantee/"
	resp = json.loads(requests.get(url).text)
	guarantee_corr = resp['obj']


def init_battery_corr():
	global battery_corr
	url = "http://127.0.0.1:8081/machine/price/battery/"
	resp = json.loads(requests.get(url).text)
	battery_corr = resp['obj']


def init_price_combination():
	global price_combination
	url = "http://127.0.0.1:8081/machine/price/price-combination/"
	resp = json.loads(requests.get(url).text)
	price_combination = resp['obj']


@app.route("/xd-field", methods=['GET'])
def get_xd_field():
	return jsonify({"data": xd_field})


@app.route("/xd_combination_price_field", methods=['GET'])
def get_xd_combination_price_field():
	return jsonify(xd_combination_price_field)


@app.route("/paiji_combination_price_field", methods=['GET'])
def get_paiji_combination_price_field():
	return jsonify({"data": paiji_combination_price_field})


@app.route("/paiji-field", methods=['GET'])
def get_paiji_field():
	return jsonify({"data": list(xd_contrast.keys())})


def init():
	init_contrast()
	init_model_contrast()
	init_xd_field()
	init_ahs_field()
	init_model_guarantee_battery()
	init_guarantee_corr()
	init_battery_corr()
	init_price_combination()


init()
