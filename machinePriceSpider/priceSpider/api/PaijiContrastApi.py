import json
import requests
from ..utils import Global


class PaijiContrastApi:
	def __init__(self):
		print("")

	def get_model_contrast(self):
		url = Global.Global.BASEURL + "/machine/price/model-contrast/"
		resp = json.loads(requests.get(url).text)
		return resp['obj']

	def get_model_guarantee_battery(self):
		url = Global.Global.BASEURL + "/machine/price/model-guarantee-battery/"
		resp = json.loads(requests.get(url).text)
		return resp['obj']

	def get_guarantee_corr(self):
		url = Global.Global.BASEURL + "/machine/price/guarantee/"
		resp = json.loads(requests.get(url).text)
		return resp['obj']

	def get_battery_corr(self):
		url = Global.Global.BASEURL + "/machine/price/battery/"
		resp = json.loads(requests.get(url).text)
		return resp['obj']

	def get_price_combination(self):
		url = Global.Global.BASEURL + "/machine/price/price-combination/"
		resp = json.loads(requests.get(url).text)
		return resp['obj']

	def get_price_filed(self):
		url = Global.Global.BASEURL + "/machine/price/field"
		resp = json.loads(requests.get(url).text)
		return resp['obj']

	def get_paiji_user(self):
		url = Global.Global.BASEURL + "/machine/price/paiji-user/"
		resp = json.loads(requests.get(url).text)
		return resp['obj']
