import execjs

with open('getChromsome.js', 'r', encoding='UTF-8') as f:
    js_code = f.read()

context = execjs.compile(js_code)

def getChromsome():
	# 参数一为函数名，参数二和三为函数的参数
	return context.call("getChromsome") 