from login import login
import requests

userAgents = [
	'OPTapp/2.16.11 (com.aihuishou.OPTapp.P; build:202106092123; iOS 14.6.0) Alamofire/4.9.1',
	'Mozilla/5.0 (iPhone; CPU iPhone OS 14_4_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, '
	'like Gecko) '
	'Mobile/15E148 MicroMessenger/8.0.6(0x18000632) NetType/WIFI Language/zh_CN',
]

user = [
	{"index": 1, "userName": "15364117100", "passWord": "whz1152957995", "token": "bc39e88c64cfa6d3e66f2cfa495382ec"}
]


def get_proxy():
	return requests.get("http://127.0.0.1:5010/get/").json()


def delete_proxy(proxy):
	requests.get("http://127.0.0.1:5010/delete/?proxy={}".format(proxy))


def init_user():
	for item in user:
		resp = login(item["userName"], item["passWord"])
		if resp == -1:
			print(item["userName"] + "信息有错误")
		else:
			item["token"] = resp


def update_token(index):
	userInfo = user[index]
	resp = login(userInfo["userName"], userInfo["passWord"])
	if resp == -1:
		print(userInfo["userName"] + "信息有错误")
	else:
		userInfo["token"] = resp
