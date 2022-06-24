import requests
import access
import json
import log
import random
from app import app
from flask import request


@app.route("/testLogin")
def testLogin():
	
	resp = login(request.args["username"], request.args["password"])

	if resp == -1:
		return "-1"
	return "1"



def login(chromosome, body):
	loginURL = "https://sjapi.aihuishou.com/sj-api/auth/login"
	headers = {
		"Host": "sjapi.aihuishou.com",
		"User-Agent": "OPTapp/2.43.1 (com.aihuishou.OPTapp.P; build:202206171208; iOS 15.5.0) Alamofire/2.43.1",
		"App-ID": "pjt432865",
		"app-channel": "598302",
		"Version": "2.43.1",
		"chromosome": str(chromosome),
		"Version-Type": "1",
		"Platform": "ios",
		"Content-Length": "172",
		"Connection": "close",
		"Accept-Language": "zh-Hans-CN;q=1.0, en-CN;q=0.9",
		"Accept": "application/json",
		"Content-Type": "text/plain",
		"Accept-Encoding": "gzip, deflate",
		"Key-Version": "1000"
	}
	retry_count = 1
	while retry_count > 0:
		try:
			#return "f151d2509a7fbf7d23121688291d5d44"
			resp = json.loads(requests.post(loginURL, headers=headers, data=str(body)).text)
			if 'data' in resp and "accessToken" in resp["data"]:
				log.log_error.append("登录成功, token为:" + str(resp["data"]["accessToken"]))
				return resp["data"]["accessToken"]
			else:
				return -1
		except Exception as e:
			print(e)
			retry_count -= 1
	return -1

def logout(token):
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
				log.log_error.append("退出登录成功, token为:" + str(token))
				return True
			retry_count -= 1
		except Exception as e:
			retry_count -= 1
	return False
	
	