import requests
from ..api import LoginApi, PaijiContrastApi
import random


class Access:

	def __init__(self, log):
		self.log = log
		self.paijiContrastApi = PaijiContrastApi.PaijiContrastApi()
		self.loginApi = LoginApi.LoginApi()
		self.user = []

		self.userAgents = [
			'OPTapp/2.16.11 (com.aihuishou.OPTapp.P; build:202106092123; iOS 14.6.0) Alamofire/4.9.1',
			'Mozilla/5.0 (iPhone; CPU iPhone OS 14_4_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, '
			'like Gecko) '
			'Mobile/15E148 MicroMessenger/8.0.6(0x18000632) NetType/WIFI Language/zh_CN',
		]
		self.init_user()

	def get_user(self):
		self.user = []

		paiji_user = self.paijiContrastApi.get_paiji_user()

		for item in paiji_user:
			self.user.append({"userName": item['username'], "passWord": item['password'], "token": "", "login_times": 0, "chromosome": item['chromosome'], "body": item['body'], "cnt": 0})

	def token_is_invalid(self, resp):
		if "失效" in resp:
			return 1
		return 0

	def chromsome_is_invalid(self, resp):
		if "访问行为异常" in resp:
			return 1
		return 0

	def authCode(self, resp):
		if '验证码' in resp:
			self.log.authCode = 1
			return 1
		return 0

	def init_user(self):
		self.get_user()
		for item in self.user:
			for i in range(3):
				self.delay(1)
				userAgentIndex = random.randint(1, len(self.userAgents))
				resp = self.loginApi.login(item["body"], item["userName"], self.userAgents[userAgentIndex - 1], self.log)
				if resp == -1:
					self.log.log_error.insert(0, item["userName"] + "用户信息有错误, 尝试次数:" + str(i + 1))
				else:
					item["token"] = resp
					break

	def logout_all(self):
		for item in self.user:
			self.loginApi.logout(item['token'], item["userName"], self.log)

	def re_login(self, userIndex):
		item = self.user[userIndex]
		self.loginApi.logout(item['token'], item['userName'], self.log)
		for i in range(3):
			self.delay(1)
			userAgentIndex = random.randint(1, len(self.userAgents))
			resp = self.loginApi.login(item["body"], item['userName'], self.userAgents[userAgentIndex - 1], self.log)
			if resp == -1:
				self.log.log_error.insert(0, item["userName"] + "用户信息有错误: " + str(i + 1))
			else:
				item['token'] = resp
				break

	def delay(self, second):
		URL = 'https://httpbin.org/delay/' + str(second)
		requests.get(URL)
