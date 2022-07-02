import requests
import json
import random

from ..utils import getChromsome


class ProductApi:
	def __init__(self, access, log):
		self.common_header = {
			'Host': 'sjapi.aihuishou.com',
			'Connection': 'close'
		}

		self.product_log = []
		self.access = access
		self.log = log

	def simplify(self, keyword):
		return str(keyword).replace(' ', '').replace('(', '').replace(')', '').replace('（', '').replace('）', '').lower()

	# 通过key获取机器的Id
	def product_select(self, keyword, userIndex):
		userAgentIndex = random.randint(1, len(self.access.userAgents))
		url = "https://sjapi.aihuishou.com/sj-inspection-api/xcx-foundation/product"
		data = {"keyword": keyword}
		headers = {
			'Access-Token': self.access.user[userIndex]['token'],
			'Referer': 'https://servicewechat.com/wxfb796f037077edb6/6/page-frame.html',
			'Content-Type': 'application/json',
			'User-Agent': self.access.userAgents[userAgentIndex - 1]
		}
		retry_count = 5
		while retry_count > 0:
			resp = json.loads(requests.post(url, data=json.dumps(data), headers=headers).text)
			if 'data' in resp:
				if len(resp['data']) != 0:
					for item in resp['data']:
						if self.simplify(keyword) == self.simplify(item["productName"]):
							return item["productId"]
				return -1
			else:
				self.log.log_error.insert(0, "没有查询到机器的id, 尝试:" + str(5 - retry_count) + str(resp))
				retry_count -= 1
				if self.access.chromsome_is_invalid(resp['resultMessage']) == 1:
					headers['Chromosome'] = getChromsome.getChromsome()
					continue
				if self.access.authCode(resp['resultMessage']) == 1:
					self.log.log_error.insert(0, str(resp) + "当前用户为:" + str(self.access.user[userIndex]["userName"]) + ", 尝试次数：" + str(5 - retry_count))
					self.access.delay(10)
					continue
				if self.access.token_is_invalid(resp['resultMessage']) == 1:
					self.access.re_login(userIndex)
					continue
		return -2

	# 模拟正常流量
	def trace_log(self):
		url = "https://bi-log.aihuishou.com/trace/log/pjt?data=eyJkaXN0aW5jdF9pZCI6IjAxM1Z2WEdhMWQySG9EMDJ2cEhhMW5COXhrMFZ2WEdLIiwibGliIjp7IiRsaWIiOiJNaW5pUHJvZ3JhbSIsIiRsaWJfbWV0aG9kIjoiY29kZSIsIiRsaWJfdmVyc2lvbiI6IjEuMTMuMjAifSwicHJvcGVydGllcyI6eyIkbGliIjoiTWluaVByb2dyYW0iLCIkbGliX3ZlcnNpb24iOiIxLjEzLjIwIiwiJG5ldHdvcmtfdHlwZSI6IndpZmkiLCIkbWFudWZhY3R1cmVyIjoiaVBob25lIiwiJG1vZGVsIjoiaVBob25lIFhTPGlQaG9uZTExLDI" \
			  "%2BIiwiJHNjcmVlbl93aWR0aCI6Mzc1LCIkc2NyZWVuX2hlaWdodCI6ODEyLCIkb3MiOiJpT1MiLCIkb3NfdmVyc2lvbiI6IjE1LjUiLCIkbGF0ZXN0X3NjZW5lIjoi5b6u5L" \
			  "%2Bh6IGK5aSp5Li755WM6Z2i5LiL5ouJIiwiYXBwX25hbWUiOiJvcHRfbWluaSIsInBsYXRmb3JtIjoibWluaWFwcCIsInVzZXJfa2V5IjoyNzYxOTIsIiRyZWZlcnJlciI6InBhZ2VzL3Byb2R1Y3Qtc2VhcmNoL2luZGV4IiwiJHVybF9wYXRoIjoicGFnZXMvaW5kZXgvaW5kZXgiLCIkdXJsX3F1ZXJ5IjoiIiwiJGlzX2ZpcnN0X2RheSI6dHJ1ZX0sImFub255bW91c19pZCI6IjAxM1Z2WEdhMWQySG9EMDJ2cEhhMW5COXhrMFZ2WEdLIiwidHlwZSI6InRyYWNrIiwiZXZlbnQiOiIkTVBWaWV3U2NyZWVuIiwidGltZSI6MTY1NjE1NDUyMTY2NX0%3D"
		headers = {
			'Host': 'bi-log.aihuishou.com',
			'Connection': 'close',
			'content-type': 'application/json',
			'Accept-Encoding': 'gzip, deflate',
			'User-Agent': 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.23(0x1800172e) NetType/WIFI Language/zh_CN',
			'Referer': 'https://servicewechat.com/wxfb796f037077edb6/9/page-frame.html'
		}
		requests.get(url, headers=headers)

	# 拍机堂app
	def get_price_by_app(self, productId, pricePropertyValueIds, userIndex):
		url = "https://sjapi.aihuishou.com/opt-inquiry/quick-inquiry-price/inspection-info/get-by-ppv"
		data = {"productId": productId, "pricePropertyValueIds": pricePropertyValueIds}
		chromosome = getChromsome.getChromsome()
		headers = {
			'Host': 'sjapi.aihuishou.com',
			'Version': '2.44.0',
			'Chromosome': chromosome,
			'Version-Type': '1',
			'Platform': 'web',
			'Access-Token': self.access.user[userIndex]['token'],
			'Accept': 'application/json',
			'Content-Type': 'application/json',
			'Content-Length': str(len(json.dumps(data).replace(' ', ''))),
		}
		retry_count = 5
		while retry_count > 0:
			resp = json.loads(requests.post(url, data=json.dumps(data), headers=headers).text)
			if 'data' in resp and 'referencePrice' in resp['data']:
				price = resp['data']['referencePrice']

				self.access.user[userIndex]['cnt'] += 1
				if self.access.user[userIndex]['cnt'] == 60:
					temp_user = self.access.user[userIndex]
					self.log.log_error.insert(0, temp_user["userName"] + "用户已查60个，正进行重新登录")
					self.access.re_login(userIndex)

				return {"price": price, "skuId": resp['data']['skuId']}
			else:
				retry_count -= 1
				self.log.log_error.insert(0, '查价异常：' + str(5 - retry_count) + str(resp))
				if "SKU组合" in resp['resultMessage']:
					return {"price": -1, "skuId": -1}
				if self.access.chromsome_is_invalid(resp['resultMessage']) == 1:
					headers['Chromosome'] = getChromsome.getChromsome()
					continue
				if self.access.authCode(resp['resultMessage']) == 1:
					self.log.log_error.insert(0, str(resp) + "当前用户为:" + str(self.access.user[userIndex]["userName"]) + ", 尝试次数：" + str(5 - retry_count))
					self.access.delay(10)
					continue
				if self.access.token_is_invalid(resp['resultMessage']) == 1:
					self.access.re_login(userIndex)
					continue
		return -2

	# 获取手机类别中的所有品牌
	def get_brand(self, category):
		url = "https://sjapi.aihuishou.com/sj-inspection-api/xcx-foundation/brand"
		userIndex = random.randint(1, len(self.access.user))
		headers = {
			'Access-Token': self.access.user[userIndex - 1]["token"],
			'Content-Type': 'application/json',
			'Content-Length': '16',
		}
		data = {"categoryId": 1}
		retry_count = 5
		while retry_count > 0:
			resp = json.loads(
				requests.post(url, headers=headers, data=json.dumps(data)).text)
			if 'data' in resp:
				brands = resp['data']
				return brands
			else:
				return -1
		return -1

	# 获取某个品牌前num台的机器
	def get_all_machine(self, categoryId, brandId, num):
		url = "https://sjapi.aihuishou.com/sj-inspection-api/dp-foundation/product"
		userIndex = random.randint(1, len(self.access.user))
		data = {"categoryId": categoryId, "brandId": brandId}
		headers = {
			'Access-Token': self.access.user[userIndex - 1]["token"],
			'Content-Type': 'application/json',
			'Content-Length': str(len(json.dumps(data).replace(' ', '')))
		}
		retry_count = 5
		while retry_count > 0:
			resp = json.loads(
				requests.post(url, headers=headers, data=json.dumps(data)).text)
			if 'data' in resp:
				res = resp['data'][0:num]
				return res
			else:
				return -1
		return -1

	def get_desc(self, productId, userIndex):
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
		self.trace_log()
		url = "https://sjapi.aihuishou.com/supplier-api/frequently-inquiry-price/inspection-content/get?productId=" + str(productId)
		chromosome = getChromsome.getChromsome()
		headers = {
			'Host': 'sjapi.aihuishou.com',
			'Version': '2.44.0',
			'Chromosome': chromosome,
			'Access-Token': self.access.user[userIndex]['token'],
			'Connection': 'keep-alive',
			'Accept-Encoding': 'gzip, deflate, br',
			'Accept': '*/*',
		}
		retry_count = 5
		while retry_count > 0:
			resp = json.loads(requests.get(url, headers=headers).text)
			self.trace_log()
			if 'data' in resp and 'productInfos' in resp['data']:
				return resp['data']
			else:
				retry_count -= 1
				self.log.log_error.insert(0, '获取机器描述异常：' + str(5 - retry_count) + str(resp))
				if self.access.chromsome_is_invalid(resp['resultMessage']) == 1:
					headers['Chromosome'] = getChromsome.getChromsome()
					continue
				if self.access.authCode(resp['resultMessage']) == 1:
					self.log.log_error.insert(0, str(resp) + "当前用户为:" + str(self.access.user[userIndex]["userName"]) + ", 尝试次数：" + str(5 - retry_count))
					self.access.delay(10)
					continue
				if self.access.token_is_invalid(resp['resultMessage']) == 1:
					self.access.re_login(userIndex)
					continue
		return -2
