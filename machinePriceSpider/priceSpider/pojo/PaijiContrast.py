import requests
import json
from flask import jsonify
from ..api import PaijiContrastApi


class PaijiContrast:

	def __init__(self):
		self.paijiContrastApi = PaijiContrastApi.PaijiContrastApi()

		# key: 拍机堂属性id。value: 对照表中改属性的所有机况描述
		self.xd_contrast = {}
		self.ahs_contrast = {}

		self.xd_field = []
		self.ahs_field = []

		self.xd_combination_price_field = {
			"屏幕外观": [],
			"边框背板": [],
			"屏幕显示": []
		}

		self.paiji_combination_price_field = []

		self.model_contrast = {}
		self.mode_guarantee_battery = []

		self.price_combination = []

		self.guarantee_corr = {}
		self.battery_corr = {}

		self.excludeField = ["内存", "存储容量", "小型号", "机身颜色", "网络制式", "购买渠道"]
		self.musthaveField = ["内存", "存储容量", "小型号", "表壳尺寸", "表壳材质", "连接", "网络模式", "版本", "屏幕尺寸", ""]

		self.color = ["红", "橙", "黄", "绿", "青", "蓝", "紫", "金", "灰", "银", "白", "橙", "粉", "黑"]

		self.default_choice = {
			"边框背板": [{'id': 7418, 'value': "外壳明显磕碰/掉漆（≥4毫米）"}, {'id': 2128, 'value': "壳明显磕碰/掉漆"},
					 {'id': 10024, 'value': "外壳无裂缝缺失"}],
			"屏幕外观": [{'id': 10025, 'value': "屏幕外观无碎裂"}, {'id': 2120, 'value': "屏有划痕"}],
			"是否可恢复出厂设置": [{'id': 9625, 'value': "已激活，可还原"}]
		}
		self.init()

	def init_contrast(self):
		self.paiji_combination_price_field = []
		self.xd_combination_price_field = {
			"屏幕外观": [],
			"边框背板": [],
			"屏幕显示": []
		}

		price_field = self.paijiContrastApi.get_price_filed()

		for item in price_field:
			if item["name"] not in self.excludeField:
				self.xd_contrast[item["id"]] = item["xdCheckout"]
				self.ahs_contrast[item['id']] = item["ahsCheckout"]
			if item["name"] in self.xd_combination_price_field.keys():
				self.paiji_combination_price_field.append(item["value"])
				if str(item["xdCheckout"]).replace(' ', '') != "" and item["xdCheckout"] != None:
					for subItem in str(item["xdCheckout"]).split('、'):
						if subItem not in self.xd_combination_price_field[item["name"]]:
							self.xd_combination_price_field[item["name"]].append(subItem)

	def init_xd_field(self):
		for item in self.xd_contrast.values():
			for item1 in str(item).split("、"):
				if item1.replace(' ', '').lower() not in self.xd_field and item1.replace(' ', '').lower() != "" and item1.replace(' ', '').lower() != 'none':
					self.xd_field.append(item1.replace(' ', '').lower())

	def init_ahs_field(self):
		for item in self.ahs_contrast.values():
			for item1 in str(item).split("、"):
				if item1.replace(' ', '').lower() not in self.ahs_field and item1.replace(' ', '').lower() != "" and item1.replace(' ', '').lower() != 'none':
					self.ahs_field.append(item1.replace(' ', '').lower())

	def init_model_contrast(self):
		model_contrast = self.paijiContrastApi.get_model_contrast()
		for item in model_contrast:
			self.model_contrast[str(item['excelModel']).replace(' ', '').replace('(', '').replace(')', '').replace('（', '').replace('）', '').lower()] = item['paijiModel']

	def init_model_guarantee_battery(self):
		model_guarantee_battery = self.paijiContrastApi.get_model_guarantee_battery()
		self.mode_guarantee_battery = model_guarantee_battery

	def init_guarantee_corr(self):
		guarantee_corr = self.paijiContrastApi.get_guarantee_corr()
		self.guarantee_corr = guarantee_corr

	def init_battery_corr(self):
		battery_corr = self.paijiContrastApi.get_battery_corr()
		self.battery_corr = battery_corr

	def init_price_combination(self):
		price_combination = self.paijiContrastApi.get_price_combination()
		self.price_combination = price_combination

	def init(self):
		self.init_contrast()
		self.init_model_contrast()
		self.init_xd_field()
		self.init_ahs_field()
		self.init_model_guarantee_battery()
		self.init_guarantee_corr()
		self.init_battery_corr()
		self.init_price_combination()
