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
	url = "http://120.79.195.87:8081/machine/price/paiji-user/"
	resp = json.loads(requests.get(url).text)
	for item in resp['obj']:
		user.append({"userName": item['username'], "passWord": item['password'], "token": "", "login_times": 0, "chromosome": item['chromosome'], "body" : item['body']})


def token_is_invalid(resp, index):
	global user

	#if len(user) <= 0:
	#	log.log_error.append("没有可用用户了!!!")
	#	return -2
	if "失效" in resp:
		# TODO
		return -2
		if user[index]["login_times"] == 6:
			log.log_error.append(user[index]["userName"] + "用户已失效")
			del user[index]
			if len(user) <= 0:
				log.log_error.append("没有可用用户了!!!")
				return -2
			return user[0]["token"]
		update_token(index)
		user[index]["login_times"] += 1
		return user[index]["token"]
	return 1


def chromsome_is_invalid(resp, index):
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
		#item["token"] = "49e951cff912c2ab75a1d5a89492af1c"	
		time.sleep(5)
		print(time.strftime('%H:%M:%S'),'hahaha')
		resp = login(item["chromosome"], item["body"])
		if resp == -1:
			log.log_error.append(item["userName"] + "用户信息有错误")
		else:
			item["token"] = resp

def update_token():
	get_user()	
	for item in user:
		logout(item['token'])
		time.sleep(7)
		print(time.strftime('%H:%M:%S'),item['userName'],'重新登录')
		resp = login(userInfo["chromosome"], item["body"])
		if resp == -1:
			log.log_error.append(item["userName"] + "用户信息有错误")
		else:
			item['token'] = resp
	return True