from login import login, logout
import requests
import log
import json
import time

user = []

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
	global user

	if "访问行为异常" in resp:
		return 1
	return 0

def get_proxy():
	return requests.get("http://127.0.0.1:5010/get/").json()


def delete_proxy(proxy):
	requests.get("http://127.0.0.1:5010/delete/?proxy={}".format(proxy))


def init_user():
	get_user()
	for item in user:
		#item["token"] = "2c19000d17a79ddfe8da73b8ade15ef9"	
		resp = login(item["chromosome"], item["body"])
		if resp == -1:
			log.log_error.append(item["userName"] + "用户信息有错误")
		else:
			item["token"] = resp


def update_token(index):
	userInfo = user[index]
	logout(userInfo['token'])
	time.sleep(10)
	print(time.strftime('%H:%M:%S'),'重新登录')
	resp = login(userInfo["chromosome"], userInfo["body"])
	if resp == -1:
		log.log_error.append(userInfo["userName"] + "用户信息有错误")
	else:
		userInfo["token"] = resp
		return True
	return False
