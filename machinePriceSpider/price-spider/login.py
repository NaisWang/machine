import requests
import access
import json
import log
import random
from app import app
from flask import request
from getChromsome import getChromsome

@app.route("/testLogin")
def testLogin():
	
	resp = login(request.args["username"], request.args["password"])

	if resp == -1:
		return "-1"
	return "1"



def login(chromosome, body, userName):
	userAgentIndex = random.randint(1, len(access.userAgents))
	loginURL = "https://sjapi.aihuishou.com/sj-api/auth/login"

	#payload = "cwmMnp1HwwmamSqys3N0PGhSKvoXdYpWM7/KLZaUMKpmvpFvfdOWT7cSaajurZ9cOvqk+xcpd1k+p1tkDt4xbys9XSHKWAQdqhMxNzSIp8BiUz1TBKrq1y11DofTPGaw5Rg7zECMBqc8GAYv0ZLV98KAm+5WEHIySADGVGuOGIw="

	#headers = {
	#	'Host': 'sjapi.aihuishou.com',
	#	'User-Agent': 'OPTapp/2.43.1 (com.aihuishou.OPTapp.P; build:202206171208; iOS 15.5.0) Alamofire/2.43.1',
	#	'App-ID': 'pjt432865',
	#	'app-channel': '598302',
	#	'Version': '2.43.1',
	#	'chromosome': str(chromosome),
	#	'Version-Type': '1',
	#	'Platform': 'ios',
	#	'Content-Length': '172',
	#	'Connection': 'close',
	#	'Accept-Language': 'zh-Hans-CN;q=1.0, en-CN;q=0.9',
	#	'Accept': 'application/json',
	#	'Content-Type': 'text/plain',
	#	'Accept-Encoding': 'gzip, deflate',
	#	'Key-Version': '1000',
	#}
	chromosome = getChromsome()
	headers = {
		"Host": "sjapi.aihuishou.com",
		"User-Agent": "OPTapp/2.43.1 (com.aihuishou.OPTapp.P; build:202206171208; iOS 15.5.0) Alamofire/2.43.1",
		"App-ID": "pjt432865",
		"app-channel": "598302",
		"Version": "2.44.0",
		"chromosome": str(chromosome),
		"Version-Type": "1",
		"Platform": "ios",
		'Content-Length': str(len(body)),
		"Connection": "close",
		"Accept-Language": "zh-Hans-CN;q=1.0, en-CN;q=0.9",
		"Accept": "application/json",
		"Content-Type": "text/plain",
		"Accept-Encoding": "gzip, deflate",
		"Key-Version": "1000",
		'User-Agent': access.userAgents[userAgentIndex - 1]
	}
	#proxy = access.get_proxy().get("proxy")
	retry_count = 1
	while retry_count > 0:
		try:
			#return "08dcef810468f5cf0d695c591a42dcf5"
			#response = requests.request("POST", url, headers=headers, data=payload)
			resp = json.loads(requests.post(loginURL, headers=headers, data=str(body)).text)
			print(resp)
			if 'data' in resp and "accessToken" in resp["data"]:
				log.log_error.append("登录成功, token为:" + str(resp["data"]["accessToken"]) + ", 用户名为:" + str(userName))
				return resp["data"]["accessToken"]
			else:
				return -1
		except Exception as e:
			print(e)
			retry_count -= 1
	#access.delete_proxy(proxy)
	return -1

def logout(token, userName):
	url = "https://sjapi.aihuishou.com/sj-api/account/logout"
	headers = {
		'Host': 'sjapi.aihuishou.com',
		'Access-Token': str(token),
		'Accept': 'application/json',
		'Content-Type': 'application/json'
	}
	retry_count = 2
	while retry_count > 0:
		try:
			resp = json.loads(requests.post(url, headers=headers).text)
			if resp['code'] == 200:
				log.log_error.append("退出登录成功, token:" + str(token) + ", 用户名为:" + str(userName))
				return True
			retry_count -= 1
		except Exception as e:
			retry_count -= 1
	return False
	
	