from login import login
import requests
import log
import json

userAgents = [
	'OPTapp/2.16.11 (com.aihuishou.OPTapp.P; build:202106092123; iOS 14.6.0) Alamofire/4.9.1',
	'Mozilla/5.0 (iPhone; CPU iPhone OS 14_4_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, '
	'like Gecko) '
	'Mobile/15E148 MicroMessenger/8.0.6(0x18000632) NetType/WIFI Language/zh_CN',
]

user = []


def get_user():
	url = "http://127.0.0.1:8085/machine/price/paiji-user/"
	resp = json.loads(requests.get(url).text)
	for item in resp['obj']:
		user.append({"userName": item['username'], "passWord": item['password'], "token": "", "login_times": 0})


def token_is_invalid(resp, index):
	if len(user) <= 0:
		log.log_error.append("没有可用用户了!!!")
		return -2
	if "失效" in resp:
		if user[index]["login_times"] == 3:
			log.log_error.append(user[index]["userName"] + "用户已失效")
			del user[index]
			if len(user) <= 0:
				log.log_error.append("没有可用用户了!!!")
				print("fjdkfjdkfjdfkjdfkdj")
				return -2
			return user[0]["token"]
		update_token(index)
		user[index]["login_times"] += 1
		return user[index]["token"]
	return 1


def get_proxy():
	return requests.get("http://127.0.0.1:5010/get/").json()


def delete_proxy(proxy):
	requests.get("http://127.0.0.1:5010/delete/?proxy={}".format(proxy))


def init_user():
	get_user()
	for item in user:
		resp = login(item["userName"], item["passWord"])
		if resp == -1:
			log.log_error.append(item["userName"] + "用户信息有错误")
		else:
			item["token"] = resp


def update_token(index):
	userInfo = user[index]
	resp = login(userInfo["userName"], userInfo["passWord"])
	if resp == -1:
		log.log_error.append(userInfo["userName"] + "用户信息有错误")
	else:
		userInfo["token"] = resp
