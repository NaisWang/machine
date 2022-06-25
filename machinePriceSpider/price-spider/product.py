# encoding: utf-8
import sys

import requests
import json
import access
import random

import log

import paijiContrast
from app import app
from flask import request, jsonify
import time

from getChromsome import getChromsome

common_header = {
	'Host': 'sjapi.aihuishou.com',
	'Connection': 'close'
}

product_log = []


def generate_product_log(userName, methodName, arguments, errorInfo):
	global product_log
	try:
		product_log.append(userName + "用户, 使用" + str(methodName) + "方法,参数为" + str(arguments) + "时，出现该信息：" + str(errorInfo))
	except Exception as e:
		print(e)


def keyword_5g(keyword, userIndex):
	if "(5g版)" in keyword or "（5g版）" in keyword or "(5g)" in keyword or "（5g）" in keyword:
		tempKeyWord = keyword
		keyword = keyword.replace('(5g版)', '(5g)').replace('（5g版）', '（5g）')
		res = product_select(keyword, userIndex)
		if res == -2:
			return -2
		if res != -1:
			return res

		keyword = tempKeyWord
		keyword = keyword.replace('(5g)', '(5g版)').replace('（5g）', '（5g版）')
		res = product_select(keyword, userIndex)
		if res == -2:
			return -2
		if res != -1:
			return res

		keyword = tempKeyWord
		keyword = keyword.replace('(5g版)', '').replace('（5g版）', '').replace('(5g)', '').replace('（5g）', '')
		res = product_select(keyword, userIndex)
		if res == -2:
			return -2
		if res != -1:
			return res

	return -1


def keyword_repalce(keyword, origin, target, userIndex):
	keyword = keyword.replace(origin, target)
	res = product_select(keyword, userIndex)
	if res == -2:
		return -2
	if res != -1:
		return res
	resp = keyword_5g(keyword, userIndex)
	if resp == -2:
		return -2
	if resp != -1:
		return resp
	return -1


def get_product_id(keyword, userIndex):
	keyword = str(keyword).lower().replace(' ', '')
	res = product_select(keyword, userIndex)
	if res == -2:
		return -2
	if res != -1:
		return res

	resp = keyword_5g(keyword, userIndex)
	if resp == -2:
		return -2
	if resp != -1:
		return resp

	resp = keyword_repalce(keyword, "iphone", "苹果 iphone", userIndex)
	if resp == -2:
		return -2
	if resp != -1:
		return resp

	if "iphone" in keyword and "苹果" not in keyword:
		resp = keyword_repalce(keyword, "iphone", "苹果 iphone", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if 'realme' in keyword and '真我' in keyword:
		resp = keyword_repalce(keyword, "真我", "", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if 'vivo' in keyword and 'iqoo' in keyword:
		resp = keyword_repalce(keyword, "vivo", "", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if '红米' in keyword:
		resp = keyword_repalce(keyword, "红米", "redmi", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if "小米手机" in keyword:
		resp = keyword_repalce(keyword, "小米手机", "小米", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if "小米" in keyword and "手机" not in keyword:
		resp = keyword_repalce(keyword, "小米", "小米手机", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if "坚果" in keyword and "锤子" not in keyword:
		resp = keyword_repalce(keyword, "坚果", "锤子 坚果", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if "魅蓝" in keyword and "魅族" not in keyword:
		resp = keyword_repalce(keyword, "魅蓝", "魅族 魅蓝", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if "galaxy" in keyword and "三星" not in keyword:
		resp = keyword_repalce(keyword, "galaxy", "三星 galaxy", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	if "三星" in keyword and "galaxy" not in keyword:
		resp = keyword_repalce(keyword, "三星", "三星 galaxy", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

	keyword = keyword.replace(' ', '').replace('(', '').replace(')', '').replace('（', '').replace('）', '').lower()
	if keyword in paijiContrast.model_contrast.keys():
		res = product_select(paijiContrast.model_contrast[keyword], userIndex)
		if res == -2:
			return -2
		if res != -1:
			return res
	return -1


# 通过key获取机器的Id
def product_select(keyword, userIndex):
	userAgentIndex = random.randint(1, len(access.userAgents))
	url = "https://sjapi.aihuishou.com/sj-inspection-api/xcx-foundation/product"
	data = {"keyword": keyword}
	headers = {
		'Access-Token': access.user[userIndex]['token'],
		'Referer': 'https://servicewechat.com/wxfb796f037077edb6/6/page-frame.html',
		'Content-Type': 'application/json',
		'User-Agent': access.userAgents[userAgentIndex - 1]
	}
	headers.update(common_header)
	#proxy = access.get_proxy().get("proxy")
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(
				requests.post(url, data=json.dumps(data), headers=headers).text)
			res = access.token_is_invalid(resp['resultMessage'], userIndex)
			if res == -2:
				return -2
			if res != 1:
				return product_select(keyword, 0)
			if 'data' in resp and len(resp['data']) != 0:
				for item in resp['data']:
					if simplify(keyword) == simplify(item["productName"]):
						generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
																 str(item["productId"]))
						return item["productId"]
				return -1
			else:
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return -1
		except Exception as e:
			print(e)
			retry_count -= 1
	#access.delete_proxy(proxy)
	return -1


def simplify(keyword):
	return str(keyword).replace(' ', '').replace('(', '').replace(')', '').replace('（', '').replace('）', '').lower()

# 模拟正常流量
def trace_log():
	url = "https://bi-log.aihuishou.com/trace/log/pjt?data=eyJkaXN0aW5jdF9pZCI6IjAxM1Z2WEdhMWQySG9EMDJ2cEhhMW5COXhrMFZ2WEdLIiwibGliIjp7IiRsaWIiOiJNaW5pUHJvZ3JhbSIsIiRsaWJfbWV0aG9kIjoiY29kZSIsIiRsaWJfdmVyc2lvbiI6IjEuMTMuMjAifSwicHJvcGVydGllcyI6eyIkbGliIjoiTWluaVByb2dyYW0iLCIkbGliX3ZlcnNpb24iOiIxLjEzLjIwIiwiJG5ldHdvcmtfdHlwZSI6IndpZmkiLCIkbWFudWZhY3R1cmVyIjoiaVBob25lIiwiJG1vZGVsIjoiaVBob25lIFhTPGlQaG9uZTExLDI%2BIiwiJHNjcmVlbl93aWR0aCI6Mzc1LCIkc2NyZWVuX2hlaWdodCI6ODEyLCIkb3MiOiJpT1MiLCIkb3NfdmVyc2lvbiI6IjE1LjUiLCIkbGF0ZXN0X3NjZW5lIjoi5b6u5L%2Bh6IGK5aSp5Li755WM6Z2i5LiL5ouJIiwiYXBwX25hbWUiOiJvcHRfbWluaSIsInBsYXRmb3JtIjoibWluaWFwcCIsInVzZXJfa2V5IjoyNzYxOTIsIiRyZWZlcnJlciI6InBhZ2VzL3Byb2R1Y3Qtc2VhcmNoL2luZGV4IiwiJHVybF9wYXRoIjoicGFnZXMvaW5kZXgvaW5kZXgiLCIkdXJsX3F1ZXJ5IjoiIiwiJGlzX2ZpcnN0X2RheSI6dHJ1ZX0sImFub255bW91c19pZCI6IjAxM1Z2WEdhMWQySG9EMDJ2cEhhMW5COXhrMFZ2WEdLIiwidHlwZSI6InRyYWNrIiwiZXZlbnQiOiIkTVBWaWV3U2NyZWVuIiwidGltZSI6MTY1NjE1NDUyMTY2NX0%3D"
	headers = {
		'Host': 'bi-log.aihuishou.com',
		'Connection': 'close',
		'content-type': 'application/json',
		'Accept-Encoding': 'gzip, deflate',
		'User-Agent': 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.23(0x1800172e) NetType/WIFI Language/zh_CN',
		'Referer': 'https://servicewechat.com/wxfb796f037077edb6/9/page-frame.html'
	}
	requests.get(url, headers=headers)
	

# 通过productId， pricePropertyValueIds获取RrepostNo
def get_report_no(productId, pricePropertyValueIds, userIndex):
	url = "https://sjapi.aihuishou.com/supplier-api/frequently-inquiry-price/inspection-report/create-by-ppv"
	data = {"productId": productId, "pricePropertyValueIds": pricePropertyValueIds}
	headers = {
		'Access-Token': access.user[userIndex]['token'],
		'Content-Type': 'application/json',
	}
	headers.update(common_header)
	#proxy = access.get_proxy().get("proxy")
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(
				requests.post(url, data=json.dumps(data), headers=headers).text)
			res = access.token_is_invalid(resp['resultMessage'], userIndex)
			if res == -2:
				return -2
			if res != 1:
				return get_report_no(productId, pricePropertyValueIds, 0)
			if 'data' in resp:
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp['data']['reportNo']))
				return resp['data']['reportNo']
			else:
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return -1
		except Exception as e:
			retry_count -= 1
	#access.delete_proxy(proxy)
	return -1

# 拍机堂小程序
def get_price_by_mini(productId, pricePropertyValueIds, userIndex):
	#userAgentIndex = random.randint(1, len(access.userAgents))
	print("productId:" + str(productId))
	print("priceProperty:" + str(pricePropertyValueIds))
	url = "https://sjapi.aihuishou.com/opt-inquiry/mini-inquiry-price/price-info/by-ppv"
	data = {"productId": productId, "pricePropertyValueIds": pricePropertyValueIds}
	chromosome = getChromsome()
	headers = {
		'Access-Token': '49e951cff912c2ab75a1d5a89492af1c',
		'Host': 'sjapi.aihuishou.com',
		'Content-Type': 'application/json',
		'Content-Length': str(len(json.dumps(data).replace(' ','')))
	}
	retry_count = 5
	#proxy = access.get_proxy().get("proxy")
	#print(proxy)
	resp = ""
	while retry_count > 0:
		try:
			resp = json.loads(requests.post(url, data=json.dumps(data), headers=headers).text)
			trace_log()
			if 'data' in resp and 'guidePrice' in resp['data']:
				price = resp['data']['guidePrice']
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(price))
				return {"price": price, "skuId": resp['data']['skuId']}
			else:
				if access.authCode(resp['resultMessage']) == 1:
					print(resp)
					log.log_error.append(resp)
					return -2;
					#if access.update_token(0) == False:
					#	log.log_error.append("更新失败")
					#	return -2
					#continue
				log.log_error.append(resp)
				res = access.token_is_invalid(resp['resultMessage'], userIndex)
				if res == -2:
					generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
															str("token失效"))
					return -2
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return {"price": -1, "skuId": -1}
		except Exception as e:
			retry_count -= 1
	#access.delete_proxy(proxy)
	return {"price": -1, "skuId": -1}

# 拍机堂app
def get_price_by_app(productId, pricePropertyValueIds, userIndex):
	#userAgentIndex = random.randint(1, len(access.userAgents))
	print("productId:" + str(productId))
	print("priceProperty:" + str(pricePropertyValueIds))
	url = "https://sjapi.aihuishou.com/opt-inquiry/quick-inquiry-price/inspection-info/get-by-ppv"
	data = {"productId": productId, "pricePropertyValueIds": pricePropertyValueIds}
	chromosome = getChromsome()
	headers = {
		'Host': 'sjapi.aihuishou.com',
		'Version': '2.43.1',
		'Chromosome': chromosome,
		'Version-Type': '1',
		'Platform': 'web',
		'Access-Token': access.user[userIndex]['token'] ,
		'Accept': 'application/json',
		'Content-Type': 'application/json',
		'Content-Length':str(len(json.dumps(data).replace(' ',''))),
		#'User-Agent': access.userAgents[userAgentIndex - 1]
	}
	retry_count = 5
	#proxy = access.get_proxy().get("proxy")
	#print(proxy)
	resp = ""
	while retry_count > 0:
		try:
			resp = json.loads(requests.post(url, data=json.dumps(data), headers=headers).text)
			if 'data' in resp and 'referencePrice' in resp['data']:
				price = resp['data']['referencePrice']
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(price))
				return {"price": price, "skuId": resp['data']['skuId']}
			else:
				if access.chromsome_is_invalid(resp['resultMessage'], userIndex) == 1:
					headers['Chromosome'] = getChromsome()
					retry_count -= 1
					continue
				if access.authCode(resp['resultMessage']) == 1:
					print(resp)
					log.log_error.append(resp)
					return -2;
					#if access.update_token(0) == False:
					#	log.log_error.append("更新失败")
					#	return -2
					#continue
				log.log_error.append(resp)
				res = access.token_is_invalid(resp['resultMessage'], userIndex)
				if res == -2:
					generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
															str("token失效"))
					return -2
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return {"price": -1, "skuId": -1}
		except Exception as e:
			retry_count -= 1
	#access.delete_proxy(proxy)
	return {"price": -1, "skuId": -1}


# 通过productId， pricePropertyValueIds获取价格
def get_price_new(productId, pricePropertyValueIds, userIndex):
	#trace_log()
	return get_price_by_app(productId, pricePropertyValueIds, userIndex)

# 通过reptortNo获取价格
def get_price(reportNo, userIndex):
	url = "https://sjapi.aihuishou.com/supplier-api/supply-product/v2/confirming-product-quality/" + reportNo
	headers = {
		# 'Access-Token': access.user[userIndex - 1]["token"],
		'Access-Token': access.user[userIndex]['token'],
		'wToken': 'KIUR_BFQul0i40whfD/3YAUPk5pn3rtvEpuVgV6BNw9ZEbHALg+pFxd9ojeTvYO3zvMg8OUHaMzIANpS09'
							'+YMSuqEQnSDnxNqkSt4wHL14PDVcyw3aEJjhI/liDTVfoasF7d9'
							'+elgl2zb6uG67ySxfUpRPXibaziayY1htAogjAAZLjZ9XGdOkRfbJBkTQJ'
							'/yjfkzlJpk0pOSEZI8bkj6iSn1tJMr92ZYfkSuglMzoGrUuE+QX06S5oeqPKPd7feiv'
							'+smUHb9cZ0MLpi5JsmzNgP17T5QG6O1YwhMpK6vomD8TlvHHm/x85o2sAZwVNCUQ4WufzF77R+Iwln8KAIHot20pJZTE0'
							'+ypXw5UHsOW10g5hkc2H3c5Lro1R4Y0l2jSTEPeiri4edMmtvW3jEOE8isINdX9+SeWPsUxvAE5Flly'
							'+sbLJnkgaXeeRS7TRuuLJhh'
							'&IMHW_i0013dd481f50c8890660573867c246dd7daee549cb36',
		'App-ID': 'pjt432865',
		'Platform': 'ios',
		'Version': '2.19.1'
	}
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(requests.get(url, headers=headers).text)
			res = access.token_is_invalid(resp['resultMessage'], userIndex)
			if res == -2:
				return -2
			if res != 1:
				return get_price(reportNo, 0)
			if 'data' in resp:
				price = resp['data']['p2Price']
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(price))
				return {"price": price, "skuId": resp['data']['skuId']}
			else:
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return {"price": -1, "skuId": -1}
		except Exception as e:
			retry_count -= 1
	return {"price": -1, "skuId": -1}


# 获取手机对应的类别编号
def get_category_id():
	url = "https://sjapi.aihuishou.com/sj-inspection-api/xcx-foundation/category"
	userIndex = random.randint(1, len(access.user))
	headers = {
		'Access-Token': access.user[userIndex - 1]["token"],
	}
	headers.update(common_header)
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(requests.post(url, headers=headers).text)
			if 'data' in resp:
				categories = resp['data']
				for item in categories:
					if item["categoryName"] == "手机":
						generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
																 str(item["categoryId"]))
						return item["categoryId"]
					return -1
				return -1
			else:
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return -1
		except Exception as e:
			retry_count -= 1
	return -1


# 获取手机类别中的所有品牌
def get_brand(category):
	url = "https://sjapi.aihuishou.com/sj-inspection-api/xcx-foundation/brand"
	userIndex = random.randint(1, len(access.user))
	headers = {
		'Access-Token': access.user[userIndex - 1]["token"],
		'Content-Type': 'application/json',
		'Content-Length': '16',
	}
	data = {"categoryId": 1}
	retry_count = 5
	#proxy = access.get_proxy().get("proxy")
	while retry_count > 0:
		try:
			resp = json.loads(
				requests.post(url, headers=headers, data=json.dumps(data)).text)
			if 'data' in resp:
				brands = resp['data']
				#generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
				#										 str(brands))
				return brands
			else:
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return -1
		except Exception as e:
			retry_count -= 1
	#access.delete_proxy(proxy)
	return -1


# 获取某个品牌前num台的机器
def get_all_machine(categoryId, brandId, num):
	url = "https://sjapi.aihuishou.com/sj-inspection-api/dp-foundation/product"
	userIndex = random.randint(1, len(access.user))
	data = {"categoryId": categoryId, "brandId": brandId}
	headers = {
		'Access-Token': access.user[userIndex - 1]["token"],
		'Content-Type': 'application/json',
		'Content-Length':str(len(json.dumps(data).replace(' ','')))
	}
	retry_count = 5
	#proxy = access.get_proxy().get("proxy")
	while retry_count > 0:
		try:
			resp = json.loads(
				requests.post(url, headers=headers, data=json.dumps(data)).text)
			if 'data' in resp:
				res = resp['data'][0:num]
				#generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
				#										 str(res))
				return res
			else:
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return -1
		except Exception as e:
			retry_count -= 1
	#access.delete_proxy(proxy)
	return -1


# @app.route('/desc')
def get_desc(productId, userIndex):
	"""
	返回的数据格式
		"productInfo":[
		{
			"id": 1120,
			"name":  "灏忓瀷鍙?,
			"typeName": "鍩烘湰闂",
			"pricePropertyValueVos":[
				{
					"id": 5226,
					"propertyName": 1120,
					"value": "A1864",
				}
			]
		}
	],
	"qualityInfos":[
		{
			"id": 1120,
			"name":  "灏忓瀷鍙?,
			"typeName": "鍩烘湰闂",
			"pricePropertyValueVos":[
				{
					"id": 5226,
					"propertyName": 1120,
					"value": "A1864",
				}
			]
		}
	],
	"functionInfos":[
		{
			"id": 1120,
			"name":  "灏忓瀷鍙?,
			"typeName": "鍩烘湰闂",
			"pricePropertyValueVos":[
				{
					"id": 5226,
					"propertyName": 1120,
					"value": "A1864",
				}
			]
		}
	],
	"""
	trace_log()
	userAgentIndex = random.randint(1, len(access.userAgents))
	url = "https://sjapi.aihuishou.com/supplier-api/frequently-inquiry-price/inspection-content/get?productId=" + \
				str(productId)
	chromosome = getChromsome()
	headers = {
		'Host': 'sjapi.aihuishou.com',
		'Version': '2.43.1',
		'Chromosome': chromosome,
		'Access-Token': access.user[userIndex]['token'] ,
		'Connection':'keep-alive',
		'Accept-Encoding':'gzip, deflate, br',
		'Accept':'*/*',
		#'User-Agent': access.userAgents[userAgentIndex - 1]
	}
	#proxy = access.get_proxy().get("proxy")
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(
				requests.get(url, headers=headers).text)
			trace_log()
			if 'data' in resp and 'productInfos' in resp['data']:
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp['data']))
				return resp['data']
			else:
				if access.chromsome_is_invalid(resp['resultMessage'], userIndex) == 1:
					headers['Chromosome'] = getChromsome()
					retry_count -= 1
					continue
				log.log_error.append(resp)
				res = access.token_is_invalid(resp['resultMessage'], userIndex)
				if res == -2:
					generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
															str("token失效"))
					return -2
				generate_product_log(access.user[userIndex]['userName'], sys._getframe().f_code.co_name, str(locals()),
														 str(resp))
				return -1
		except Exception as e:
			retry_count -= 1
	#access.delete_proxy(proxy)
	return -1


@app.route('/update')
def update_desc():
	access.init_user()
	times = 1

	categoryId = 1
	#while categoryId == -1 and times < 5:
	#	categoryId = get_category_id()
	#	times += 1

	brands = get_brand(categoryId)
	print("brands")
	print(brands)

	desc = {}
	count = 0
	for brand in brands:
		if count == 3:
			break
		products = []
		if str(brand['brandName']).strip() == '苹果':
			count += 1
			products = get_all_machine(categoryId, brand["brandId"], -1)
			print("a")
			print(products)
		if str(brand['brandName']).strip() == '华为':
			count += 1
			products = get_all_machine(categoryId, brand["brandId"], 50)
			print("b")
			print(products)
		if str(brand['brandName']).strip() == '三星':
			count += 1
			#products = get_all_machine(categoryId, brand["brandId"], 50)
			#print("c")
			#print(products)
		if count != 0:
			if products != -1:
				for product in products:
					productDesc = get_desc(str(product['productId']), 0)
					print("product")
					print(productDesc)
					for key in productDesc.keys():
						for item in productDesc[key]:
							if 'pricePropertyValueVos' in item:
								print("--------------------------")
								for pricePropertyValue in item['pricePropertyValueVos']:
									print("--------------------------")
									descKey = str(item['name']).strip()
									if descKey not in desc:
										desc[descKey] = []
									flag = 0
									for subItem in desc[descKey]:
										if pricePropertyValue['id'] == subItem['id']:
											flag = 1
											break
									if flag == 0:
										if descKey != "小型号" or (descKey == "小型号" and '其他' not in pricePropertyValue['value']):
											desc[descKey].append({
												"id": pricePropertyValue['id'],
												"parentValue": key,
												"value": str(pricePropertyValue['value']).strip() if isinstance(pricePropertyValue['value'],
																																												str) else pricePropertyValue[
													'value'],
												"name": descKey,
											})
	res = []
	for key in desc.keys():
		res.extend(desc[key])

	print("d")
	print(res)
	return jsonify(res)