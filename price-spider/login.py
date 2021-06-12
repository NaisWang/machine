import requests
import json


def login(username, password):
	loginURL = "https://sjapi.aihuishou.com/sj-api/auth/login"
	data = {"userName": username, "passWord": password}
	headers = {
		'Host': 'sjapi.aihuishou.com',
		'Content-Type': 'application/json',
		'Chromosome': '+VbmS6e5GRFurBOZUCeVTtGlME0vLzvirdEs3k2vwUkXlrfRJQtd'
									'/5MqAZf1mqmJbFJqnP5weVZCJ8wLpW8I9Cli27cYgbqHGOGY4klY7hJ9TPDAZX4q/Lzzq'
									'+qQsw9XtTGRQxDuBKQIAOoUEfGp0cXBoCOSmS9RAiMFWHlHTYpyPJEvvsvoSFfN3RrbfAaB|HwF6mQZkHckCzM61dTq4t'
									'/zopjkDojWE5H9y9ZPpLPA=|iOS|1.1$32$238F9E41CD06BA75$213',
		'App-Id': 'pjt432865',
		'Version': '2.16.11',
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
		'User-Agent': 'OPTapp/2.16.10 (com.aihuishou.OPTapp.P; build:202106041809; iOS 14.6.0) Alamofire/4.9.1',
		'Content-Length': '53',
		'Connection': 'close'
	}
	resp = requests.post(loginURL, data=json.dumps(data), headers=headers)
	respData = json.loads(resp.text)
	if 'data' in respData and "accessToken" in respData["data"]:
		return respData["data"]["accessToken"]
	else:
		return -1
