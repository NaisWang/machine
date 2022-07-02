import requests
import json
from ..utils import getChromsome


class LoginApi:
	def login(self, body, userName, userAgent, log):
		loginURL = "https://sjapi.aihuishou.com/sj-api/auth/login"
		chromosome = getChromsome.getChromsome()
		headers = {
			"Host": "sjapi.aihuishou.com",
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
			'User-Agent': userAgent
		}
		retry_count = 5
		while retry_count > 0:
			resp = json.loads(requests.post(loginURL, headers=headers, data=str(body)).text)
			print(resp)
			if 'data' in resp and "accessToken" in resp["data"]:
				log.log_error.insert(0, "登录成功, token为:" + str(resp["data"]["accessToken"]) + ", 用户名为:" + str(userName))
				return resp["data"]["accessToken"]
			else:
				return -1
		return -1

	def logout(self, token, userName, log):
		url = "https://sjapi.aihuishou.com/sj-api/account/logout"
		headers = {
			'Host': 'sjapi.aihuishou.com',
			'Access-Token': str(token),
			'Accept': 'application/json',
			'Content-Type': 'application/json'
		}
		retry_count = 2
		while retry_count > 0:
			resp = json.loads(requests.post(url, headers=headers).text)
			if resp['code'] == 200:
				log.log_error.insert(0, "退出登录成功, token:" + str(token) + ", 用户名为:" + str(userName))
				return True
			retry_count -= 1
		return False
