from login import login, logout
import requests
import log
import json
import time

user = []

userAgents = [
	'OPTapp/2.16.11 (com.aihuishou.OPTapp.P; build:202106092123; iOS 14.6.0) Alamofire/4.9.1',
	'Mozilla/5.0 (iPhone; CPU iPhone OS 14_4_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, '
	'like Gecko) '
	'Mobile/15E148 MicroMessenger/8.0.6(0x18000632) NetType/WIFI Language/zh_CN',
]


def get_user():
	global user
	user = []

	url = "http://120.79.195.87:8081/machine/price/paiji-user/"
	resp = json.loads(requests.get(url).text)
	for item in resp['obj']:
		user.append({"userName": item['username'], "passWord": item['password'], "token": "", "login_times": 0, "chromosome": item['chromosome'], "body": item['body']})


def token_is_invalid(resp):
	if "失效" in resp:
		return 1
	return 0


def chromsome_is_invalid(resp):
	if "访问行为异常" in resp:
		return 1
	return 0


def authCode(resp):
	if '验证码' in resp:
		return 1
	return 0


def get_proxy():
	return requests.get("http://120.79.195.87:5010/get/").json()


def delete_proxy(proxy):
	requests.get("http://120.79.195.87:5010/delete/?proxy={}".format(proxy))


def init_user():
	get_user()
	for item in user:
		for i in range(3):
			delay(1)
			resp = login(item["chromosome"], item["body"], item["userName"])
			if resp == -1:
				log.log_error.insert(0, item["userName"] + "用户信息有错误, 尝试次数:" + str(i + 1))
			else:
				item["token"] = resp
				break


def logout_all():
	for item in user:
		logout(item['token'], item["userName"])
		delay(1)


def update_token():
	# get_user()
	for item in user:
		logout(item['token'], item['userName'])
		for i in range(3):
			delay(1)
			resp = login(item["chromosome"], item["body"], item['userName'])
			if resp == -1:
				log.log_error.insert(0, item["userName"] + "用户信息有错误: " + str(i + 1))
			else:
				item['token'] = resp
				break
	return True


def delay(second):
	URL = 'https://httpbin.org/delay/' + str(second)
	requests.get(URL)
