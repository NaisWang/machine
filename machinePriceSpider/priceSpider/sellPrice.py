import json
import datetime
import requests
import paijiContrast
import time

sell_product = {}
sold_product = {}


def get_all_sell_products():
	global sell_product

	url = "https://sjapi.aihuishou.com/supplier-service/app/items?isBatch=0&page=0&size=20&status=1"
	headers = {
		"Access-Token": "297d742391544159823a877f89acacb2",
		"Host": "sjapi.aihuishou.com",
		# "chromosome": '3X0KnRKBewlixdInrXQJS9Tiug5fvJe5LDJ5LgzqDAl6eXzZlFa0JzVIpOvGp4m3YV65ce2j3ZixpN6LK+p32sYThbA5s'
		#							'/npg9IRYnqXqfrVaTTSNzptMRuFpzWr9DokmNSed224xQ8iARdIeKuk65whJZlciXcNI4pIFjJYcx6F98neaw4yjWCA3UD1v0kWPMMUHVoiZbizyQ3zivz1+8EEoqbFf4SMKviqottoSh2CHXi53ozn6aPwJPn279iNnqcum4KAfDbpCHwdPU+W2fEflt9LPl/4PEopDXdCw24=|t/FktEwXftdUmJLsMRl/gIUsNCGlCpNQdM1vGlSyheM=|iOS|1.1$32$03AC4125DB67EF89$321',

		# "App-ID": "pjt432865",
		# "Accept": "application/json",
		# "Version": "2.18.1",
		# "utm_ahs": "598302",
		# "app-channel": "598302",
		# "Device-ID": "4069E213-D417-48CC-8E10-1F5AFFA10676",
		# "Compile-Num": "202106281951",
		# "Accept-Encoding": "gzip, deflate",
		# "Accept-Language": "zh-Hans-SR;q=1.0",
		# "Platform": "ios",
		# "Distinct-Id": "4069E213-D417-48CC-8E10-1F5AFFA10676",
		# "Version-Type": "1",
		# "Access-Token": "4733f9caf155b4ac082f641a6b9bd6e4",
		# "User-Agent": "OPTapp/2.18.1 (com.aihuishou.OPTapp.P; build:202106281951; iOS 14.7.0) Alamofire/4.9.1",
		# "Connection": "close",
		# "Content-Type": "application/json"
	}
	resp = json.loads(requests.get(url, headers=headers).text)
	if "totalCount" in resp.keys():
		totalCount = resp["totalCount"]
		pages = int(totalCount / 20)
		print("pages" + str(pages))
		if totalCount % 20 != 0:
			pages += 1
		for i in range(pages):
			time.sleep(0.5)
			url = "https://sjapi.aihuishou.com/supplier-service/app/items?isBatch=0&page=" + str(i) + "&size=20&status=1"
			resp = json.loads(requests.get(url, headers=headers).text)
			if 'data' in resp.keys():
				for item in resp["data"]:
					if item["skuId"] not in sell_product.keys():
						sell_product[item["skuId"]] = {}
						sell_product[item["skuId"]]['day'] = [
							cal_days(time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()), item['createDt'])]
						sell_product[item["skuId"]]['early_create_date'] = item['createDt']
					else:
						sell_product[item["skuId"]]['day'].append(
							cal_days(time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()), item['createDt']))
						if cal_days(sell_product[item["skuId"]]["early_create_date"], item['createDt']) > 0:
							sell_product[item["skuId"]]['early_create_date'] = item['createDt']
		print(sell_product)
		return sell_product


def get_all_sold_product():
	global sold_product
	for index in range(2, 6):
		url = "https://sjapi.aihuishou.com/supplier-service/app/items?isBatch=0&page=0&size=20&status=" + str(index)
		headers = {
			"Access-Token": "297d742391544159823a877f89acacb2",
			"Host": "sjapi.aihuishou.com"
		}
		resp = json.loads(requests.get(url, headers=headers).text)
		if "totalCount" in resp.keys():
			totalCount = resp["totalCount"]
			pages = int(totalCount / 20)
			if totalCount % 20 != 0:
				pages += 1
			flag = 1
			for i in range(pages):
				time.sleep(0.5)
				if flag == 0:
					break
				url = "https://sjapi.aihuishou.com/supplier-service/app/items?isBatch=0&page=" + str(
					i) + "&size=20&status=" + str(index)
				resp = json.loads(requests.get(url, headers=headers).text)
				if 'data' in resp.keys():
					for item in resp["data"]:
						if cal_days(time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()), item['createDt']) > 15:
							flag = 0
							break
						if item["skuId"] not in sold_product.keys():
							sold_product[item['skuId']] = {}
							sold_product[item['skuId']]['day'] = [cal_days(item['soldDt'], item['createDt'])]
							sold_product[item['skuId']]['lately_sold_date'] = item['soldDt']
							sold_product[item['skuId']]['lately_sold_price'] = item['fixedPrice']
						else:
							sold_product[item['skuId']]['day'].append(cal_days(item['soldDt'], item['createDt']))
							if cal_days(item['soldDt'], sold_product[item['skuId']]['lately_sold_date']) > 0:
								sold_product[item['skuId']]['lately_sold_date'] = item['soldDt']
								sold_product[item['skuId']]['lately_sold_price'] = item['fixedPrice']
		print(sold_product)


def cal_days(day1, day2):
	date1 = datetime.datetime.strptime(day1[0:10], "%Y-%m-%d")
	date2 = datetime.datetime.strptime(day2[0:10], "%Y-%m-%d")
	num = (date1 - date2).days
	return num


#def init():
#	get_all_sell_products()
#	get_all_sold_product()
#
#
#init()
