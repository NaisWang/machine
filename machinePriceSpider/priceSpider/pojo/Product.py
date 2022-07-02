from ..api import ProductApi


class Product:
	def __init__(self, access, log):
		self.productApi = ProductApi.ProductApi(access, log)

	def get_price_new(self, productId, pricePropertyValueIds, userIndex):
		"""
		通过productId， pricePropertyValueIds获取价格
		"""
		return self.productApi.get_price_by_app(productId, pricePropertyValueIds, userIndex)

	def keyword_5g(self, keyword, userIndex):
		if "(5g版)" in keyword or "（5g版）" in keyword or "(5g)" in keyword or "（5g）" in keyword:
			tempKeyWord = keyword
			keyword = keyword.replace('(5g版)', '(5g)').replace('（5g版）', '（5g）')
			res = self.productApi.product_select(keyword, userIndex)
			if res == -2:
				return -2
			if res != -1:
				return res

			keyword = tempKeyWord
			keyword = keyword.replace('(5g)', '(5g版)').replace('（5g）', '（5g版）')
			res = self.productApi.product_select(keyword, userIndex)
			if res == -2:
				return -2
			if res != -1:
				return res

			keyword = tempKeyWord
			keyword = keyword.replace('(5g版)', '').replace('（5g版）', '').replace('(5g)', '').replace('（5g）', '')
			res = self.productApi.product_select(keyword, userIndex)
			if res == -2:
				return -2
			if res != -1:
				return res

		return -1

	def keyword_repalce(self, keyword, origin, target, userIndex):
		keyword = keyword.replace(origin, target)
		res = self.productApi.product_select(keyword, userIndex)
		if res == -2:
			return -2
		if res != -1:
			return res
		resp = self.keyword_5g(keyword, userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp
		return -1

	def get_product_id(self, keyword, userIndex, paijiContrast):
		keyword = str(keyword).lower().replace(' ', '')
		res = self.productApi.product_select(keyword, userIndex)
		if res == -2:
			return -2
		if res != -1:
			return res

		resp = self.keyword_5g(keyword, userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

		resp = self.keyword_repalce(keyword, "iphone", "苹果 iphone", userIndex)
		if resp == -2:
			return -2
		if resp != -1:
			return resp

		if "iphone" in keyword and "苹果" not in keyword:
			resp = self.keyword_repalce(keyword, "iphone", "苹果 iphone", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if 'realme' in keyword and '真我' in keyword:
			resp = self.keyword_repalce(keyword, "真我", "", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if 'vivo' in keyword and 'iqoo' in keyword:
			resp = self.keyword_repalce(keyword, "vivo", "", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if '红米' in keyword:
			resp = self.keyword_repalce(keyword, "红米", "redmi", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if "小米手机" in keyword:
			resp = self.keyword_repalce(keyword, "小米手机", "小米", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if "小米" in keyword and "手机" not in keyword:
			resp = self.keyword_repalce(keyword, "小米", "小米手机", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if "坚果" in keyword and "锤子" not in keyword:
			resp = self.keyword_repalce(keyword, "坚果", "锤子 坚果", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if "魅蓝" in keyword and "魅族" not in keyword:
			resp = self.keyword_repalce(keyword, "魅蓝", "魅族 魅蓝", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if "galaxy" in keyword and "三星" not in keyword:
			resp = self.keyword_repalce(keyword, "galaxy", "三星 galaxy", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		if "三星" in keyword and "galaxy" not in keyword:
			resp = self.keyword_repalce(keyword, "三星", "三星 galaxy", userIndex)
			if resp == -2:
				return -2
			if resp != -1:
				return resp

		keyword = keyword.replace(' ', '').replace('(', '').replace(')', '').replace('（', '').replace('）', '').lower()
		if keyword in paijiContrast.model_contrast.keys():
			res = self.productApi.product_select(paijiContrast.model_contrast[keyword], userIndex)
			if res == -2:
				return -2
			if res != -1:
				return res
		return -1
