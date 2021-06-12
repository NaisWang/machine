import requests
import json
import access
import random
from app import app
from flask import request

common_header = {
	'Host': 'sjapi.aihuishou.com',
	'Connection': 'close'
}


def get_product_id(keyword):
	userIndex = random.randint(1, len(access.user))
	userAgentIndex = random.randint(1, len(access.userAgents))
	url = "https://sjapi.aihuishou.com/sj-inspection-api/xcx-foundation/product"
	data = {"keyword": keyword}
	headers = {
		'Access-Token': access.user[userIndex - 1]["token"],
		'User-Agent': access.userAgents[userAgentIndex - 1],
		'Referer': 'https://servicewechat.com/wxfb796f037077edb6/6/page-frame.html',
		'Content-Type': 'application/json',
	}
	headers.update(common_header)
	proxy = access.get_proxy().get("proxy")
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(
				requests.post(url, data=json.dumps(data), headers=headers, proxies={"http": "http://{}".format(proxy)}).text)
			if 'data' in resp and len(resp['data']) == 1:
				print(resp['data'])
				return resp['data']
			else:
				print("error")
				return -1
		except Exception:
			retry_count -= 1
	access.delete_proxy(proxy)
	return -1


def get_report_no(productId, pricePropertyValueIds):
	userIndex = random.randint(1, len(access.user))
	userAgentIndex = random.randint(1, len(access.userAgents))
	url = "https://sjapi.aihuishou.com/supplier-api/frequently-inquiry-price/inspection-report/create-by-ppv"
	data = {"productId": productId, "pricePropertyValueIds": pricePropertyValueIds}
	headers = {
		'Access-Token': access.user[userIndex - 1]["token"],
		'User-Agent': access.userAgents[userAgentIndex - 1],
		'Content-Type': 'application/json',
	}
	headers.update(common_header)
	proxy = access.get_proxy().get("proxy")
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(
				requests.post(url, data=json.dumps(data), headers=headers, proxies={"http": "http://{}".format(proxy)}).text)
			if 'data' in resp:
				print(resp['data']['reportNo'])
				return resp['data']['reportNo']
			else:
				print("error")
				print(-1)
		except Exception:
			retry_count -= 1
	access.delete_proxy(proxy)
	return -1


def get_price(reportNo):
	userIndex = random.randint(1, len(access.user))
	userAgentIndex = random.randint(1, len(access.userAgents))
	url = "https://sjapi.aihuishou.com/supplier-api/supply-product/v2/confirming-product-quality/" + reportNo
	headers = {
		'Access-Token': access.user[userIndex - 1]["token"],
		'User-Agent': access.userAgents[userAgentIndex - 1],
		'wToken': 'KIUR_BFQul0i40whfD/3YAUPk5pn3rtvEpuVgV6BNw9ZEbHALg+pFxd9ojeTvYO3zvMg8OUHaMzIANpS09'
							'+YMSuqEQnSDnxNqkSt4wHL14PDVcyw3aEJjhI/liDTVfoasF7d9'
							'+elgl2zb6uG67ySxfUpRPXibaziayY1htAogjAAZLjZ9XGdOkRfbJBkTQJ'
							'/yjfkzlJpk0pOSEZI8bkj6iSn1tJMr92ZYfkSuglMzoGrUuE+QX06S5oeqPKPd7feiv'
							'+smUHb9cZ0MLpi5JsmzNgP17T5QG6O1YwhMpK6vomD8TlvHHm/x85o2sAZwVNCUQ4WufzF77R+Iwln8KAIHot20pJZTE0'
							'+ypXw5UHsOW10g5hkc2H3c5Lro1R4Y0l2jSTEPeiri4edMmtvW3jEOE8isINdX9+SeWPsUxvAE5Flly+sbLJnkgaXeeRS7TRuuLJhh'
							'&IMHW_i0013dd481f50c8890660573867c246dd7daee549cb36',
		'App-ID': 'pjt432865',
		'Platform': 'ios',
		'Version': '2.16.11'
	}
	proxy = access.get_proxy().get("proxy")
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(requests.get(url, headers=headers, proxies={"http": "http://{}".format(proxy)}).text)
			if 'data' in resp:
				price = {'referencePrice': resp['data']['referencePrice'], 'p2Price': resp['data']['p2Price']}
				print(price)
				return price
			else:
				print("error")
				print(-1)
		except Exception:
			retry_count -= 1
	access.delete_proxy(proxy)
	return -1
