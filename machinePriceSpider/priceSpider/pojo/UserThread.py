import threading


class UserThread(threading.Thread):
	def __init__(self, userIndex, threadLock, xlrd_worksheet, xlwt_worksheet, access, excel):
		threading.Thread.__init__(self)
		self.username = access.user[userIndex]['userName']
		self.userIndex = userIndex
		self.threadLock = threadLock
		self.token = access.user[userIndex]['token']
		self.xlrd_worksheet = xlrd_worksheet
		self.xlwt_worksheet = xlwt_worksheet
		self.excel = excel

	def run(self):
		while self.excel.count < self.xlrd_worksheet.nrows and self.excel.search_price_flag == 1:
			self.threadLock.acquire()
			temp = 0
			if self.excel.count < self.xlrd_worksheet.nrows and self.excel.search_price_flag == 1:
				temp = self.excel.count
				self.excel.completeRows += 1
				self.excel.count += 1
			self.threadLock.release()
			self.excel.get_price(temp, self.xlrd_worksheet, self.xlwt_worksheet, self.userIndex)
