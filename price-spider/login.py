import requests
import access
import json
import random
from app import app
from flask import request


@app.route("/testLogin")
def testLogin():
	print(request.args["username"])
	print(request.args["password"])
	resp = login(request.args["username"], request.args["password"])

	if resp == -1:
		return "-1"
	return "1"


def login(username, password):
	userAgentIndex = random.randint(1, len(access.userAgents))
	loginURL = "https://sjapi.aihuishou.com/sj-api/auth/login"
	data = {"userName": str(username), "passWord": str(password)}
	headers = {
		'Host': 'sjapi.aihuishou.com',
		'Content-Type': 'application/json',
		'Chromosome': '+VbmS6e5GRFurBOZUCeVTtGlME0vLzvirdEs3k2vwUkXlrfRJQtd'
									'/5MqAZf1mqmJbFJqnP5weVZCJ8wLpW8I9Cli27cYgbqHGOGY4klY7hJ9TPDAZX4q/Lzzq'
									'+qQsw9XtTGRQxDuBKQIAOoUEfGp0cXBoCOSmS9RAiMFWHlHTYpyPJEvvsvoSFfN3RrbfAaB|HwF6mQZkHckCzM61dTq4t'
									'/zopjkDojWE5H9y9ZPpLPA=|iOS|1.1$32$238F9E41CD06BA75$213',
		'App-Id': 'pjt432865',
		'Version': '2.17.0',
		'Accept': 'application/json',
		'App-Channel': '598302',
		'Device-Id': '1FA6E5EC-4EC7-456D-AFB0-0F09516B36B8',
		'Utm_ahs': '598302',
		'Compile-Num': '202106041809',
		'Accept-Language': 'zh-Hans-CN;q=1.0',
		'Version-Type': '1',
		'Platform': 'ios',
		'Accept-Encoding': 'gzip, deflate',
		'Distinct-Id': '1FA6E5EC-4EC7-456D-AFB0-0F09516B36B8',
		'User-Agent': access.userAgents[userAgentIndex - 1],
		'Connection': 'close'
	}
	proxy = access.get_proxy().get("proxy")
	retry_count = 5
	while retry_count > 0:
		try:
			resp = json.loads(
				requests.post(loginURL, headers=headers, data=json.dumps(data),
											proxies={"http": "http://{}".format(proxy)}).text)
			if 'data' in resp and "accessToken" in resp["data"]:
				return resp["data"]["accessToken"]
			else:
				return -1
		except Exception as e:
			print(e)
			retry_count -= 1
	access.delete_proxy(proxy)
	return -1
