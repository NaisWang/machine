from flask import Flask, request
from flask_cors import *
# import gevent
# from gevent.pywsgi import WSGIServer
# from gevent import monkey
# monkey.patch_all()
from priceSpider.pojo import Excel, Log, UserThread, PaijiContrast, Access
from flask import request, Response, make_response, jsonify
import threading
from priceSpider.api import ProductApi
import xlrd
from xlwt import *
from xlutils.copy import copy
import io

excel = {"allRows": 0, "completeRows": 0}
access = {}
log = {}

app = Flask(__name__)


@app.route("/price_excel/import", methods=['POST', 'GET'])
def import_excel():
	global excel
	global access
	global log

	log = Log.Log()
	access = Access.Access(log)
	paijiContrast = PaijiContrast.PaijiContrast()
	excel = Excel.Excel(log, access, paijiContrast)

	# 获取对照表
	files = request.files.getlist("files")
	# 处理对照表，获取comparison_price对象
	excel.dealComparisonExcel(files)

	# 获取询价表
	file = request.files.get("file")

	# 获取询价方式
	searchPriceMethod = request.form["searchPriceMethod"]
	if searchPriceMethod == "1":
		excel.use_contrast = paijiContrast.xd_contrast
		excel.search_price_method = 1
	else:
		excel.use_contrast = paijiContrast.ahs_contrast
		excel.search_price_method = 0

	f = file.read()
	oldWb = xlrd.open_workbook(file_contents=f, formatting_info=True)
	oldws = oldWb.sheets()[0]

	newWb = copy(oldWb)
	newWs = newWb.get_sheet(0)

	excel.allRows = oldws.nrows

	# 询价
	ans = traverse_excel(oldws, newWs)
	if ans != 1:
		return "出现错误!!!"

	output = io.BytesIO()
	newWb.save(output)
	response = make_response(output.getvalue())

	filename = "export.xls"

	response.headers["Content-Type"] = "application/octet-stream; charset=UTF-8"
	response.headers["Content-Disposition"] = "attachment; filename={}".format(filename)
	response.headers["Access-Control-Allow-Origin"] = '*'  # 允许使用响应数据的域。也可以利用请求header中的host字段做一个过滤器。
	response.headers["Access-Control-Allow-Methods"] = 'POST, GET, OPTIONS'  # 允许的请求方法
	response.headers["Access-Control-Allow-Headers"] = "x-requested-with,content-type"  # 允许的请求header

	access.delay(1)
	access.logout_all()

	return response


@app.route("/stop-search-price", methods=['POST', 'GET'])
def stop_search_price():
	access.logout_all()
	excel.search_price_flag = 0


def traverse_excel(xlrd_worksheet, xlwt_worksheet):
	userNum = len(access.user)
	threads = []
	threadLock = threading.Lock()
	excel.column_name_number = excel.get_column_name_number(xlrd_worksheet)
	if excel.judge_excel_legal(excel.column_name_number, "询价表") == -1:
		return -1
	if userNum == 0:
		log.log_error.insert(0, "没有可用用户了!!!")
		return -1
	excel.init_first_row(xlwt_worksheet)
	for i in range(userNum):
		thread = UserThread.UserThread(i, threadLock, xlrd_worksheet, xlwt_worksheet, access, excel)
		thread.start()
		threads.append(thread)
	for t in threads:
		t.join()
	return 1


@app.route('/update')
def update_desc():
	productApi = ProductApi()

	access = Access()

	times = 1

	categoryId = 1
	# while categoryId == -1 and times < 5:
	#	categoryId = get_category_id()
	#	times += 1

	brands = productApi.get_brand(categoryId)
	print("brands")
	print(brands)

	desc = {}
	count = 0
	for brand in brands:
		if count == 3:
			break
		products = []
		if str(brand['brandName']).strip() == '苹果':
			count += 1
			products = productApi.get_all_machine(categoryId, brand["brandId"], -1)
			print("a")
			print(products)
		if str(brand['brandName']).strip() == '华为':
			count += 1
			products = productApi.get_all_machine(categoryId, brand["brandId"], 50)
			print("b")
			print(products)
		if str(brand['brandName']).strip() == '三星':
			count += 1
		# products = get_all_machine(categoryId, brand["brandId"], 50)
		# print("c")
		# print(products)
		if count != 0:
			if products != -1:
				for product in products:
					productDesc = productApi.get_desc(str(product['productId']), 0)
					print("product")
					print(productDesc)
					for key in productDesc.keys():
						for item in productDesc[key]:
							if 'pricePropertyValueVos' in item:
								print("--------------------------")
								for pricePropertyValue in item['pricePropertyValueVos']:
									print("--------------------------")
									descKey = str(item['name']).strip()
									if descKey not in desc:
										desc[descKey] = []
									flag = 0
									for subItem in desc[descKey]:
										if pricePropertyValue['id'] == subItem['id']:
											flag = 1
											break
									if flag == 0:
										if descKey != "小型号" or (descKey == "小型号" and '其他' not in pricePropertyValue['value']):
											desc[descKey].append({
												"id": pricePropertyValue['id'],
												"parentValue": key,
												"value": str(pricePropertyValue['value']).strip() if isinstance(pricePropertyValue['value'], str) else pricePropertyValue['value'],
												"name": descKey,
											})
	res = []
	for key in desc.keys():
		res.extend(desc[key])

	print("d")
	print(res)
	return jsonify(res)


@app.route("/log", methods=['POST', 'GET'])
def get_log():
	if "allRows" in excel:
		response = make_response(jsonify({"log_success": log.log_success, "log_error": log.log_error, "authCode": log.authCode, "allRows": excel.allRows, "completeRows": excel.completeRows}))
	else:
		response = make_response(jsonify({"log_success": log.log_success, "log_error": log.log_error, "authCode": log.authCode}))
	log.authCode = 0

	response.headers["Access-Control-Allow-Origin"] = '*'  # 允许使用响应数据的域。也可以利用请求header中的host字段做一个过滤器。
	response.headers["Access-Control-Allow-Methods"] = 'POST, GET, OPTIONS'  # 允许的请求方法
	response.headers["Access-Control-Allow-Headers"] = "x-requested-with,content-type"  # 允许的请求header

	return response


if __name__ == '__main__':
	app.config['JSON_AS_ASCII'] = False
	CORS(app, resources=r'/*')
	# server = WSGIServer(('0.0.0.0', 5000), app)
	# server.serve_forever()
	# app.run()
	# app.run(host="127.0.0.1", port=5002)
	app.run(host="0.0.0.0", port=5000)
