import requests
import json
from app import app
from flask import jsonify

xd_contrast = {}
ahs_contrast = {}

xd_field = []
ahs_field = []

model_contrast = {}
mode_guarantee_battery = []

price_combination = []

guarantee_corr = {}
battery_corr = {}

excludeField = ["内存", "存储容量", "小型号", "机身颜色", "网络制式", "购买渠道"]
musthaveField = ["内存", "存储容量", "小型号", "表壳尺寸", "表壳材质", "连接", "网络模式", "版本", "屏幕尺寸"]

color = ["红", "橙", "黄", "绿", "青", "蓝", "紫", "金", "灰", "银", "白", "橙", "粉", "青"]


def init_contrast():
	url = "http://127.0.0.1:8085/machine/price/field"
	resp = json.loads(requests.get(url).text)
	for item in resp['obj']:
		if item["name"] not in excludeField:
			xd_contrast[item["value"]] = item["xdCheckout"]
			ahs_contrast[item['value']] = item["ahsCheckout"]


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
	url = "http://127.0.0.1:8085/machine/price/model-contrast/"
	resp = json.loads(requests.get(url).text)
	for item in resp['obj']:
		model_contrast[str(item['excelModel']).replace(' ', '').lower()] = item['paijiModel']


def init_model_guarantee_battery():
	global mode_guarantee_battery
	url = "http://127.0.0.1:8085/machine/price/model-guarantee-battery/"
	resp = json.loads(requests.get(url).text)
	mode_guarantee_battery = resp['obj']


def init_guarantee_corr():
	global guarantee_corr
	url = "http://127.0.0.1:8085/machine/price/guarantee/"
	resp = json.loads(requests.get(url).text)
	guarantee_corr = resp['obj']


def init_battery_corr():
	global battery_corr
	url = "http://127.0.0.1:8085/machine/price/battery/"
	resp = json.loads(requests.get(url).text)
	battery_corr = resp['obj']


def init_price_combination():
	global price_combination
	url = "http://127.0.0.1:8085/machine/price/price-combination/"
	resp = json.loads(requests.get(url).text)
	price_combination = resp['obj']


@app.route("/xd-field", methods=['GET'])
def get_xd_field():
	return jsonify({"data": xd_field})


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
