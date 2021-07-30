import myAxios from "../utils/myAxios";
import qs from 'qs'


let BASE_URL = '/machine/fix/send/receipt'

// 获取送修单据
export function getFixSendReceipt(currentPage, size, receipt) {
  let test = Object.assign(receipt, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 添加送修单
export function createFixSendReceipt(receipt) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: receipt
  })
}

// 修改送外修单信息
export function modifyFixSendReceipt(receipt) {
  return myAxios({
    url: BASE_URL + '/modifyReceipt',
    method: 'put',
    data: receipt
  })
}

// 删除送修单
export function deleteFixSendReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'delete'
  })
}

// 发布送修单
export function releaseFixSendReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/release?receiptId=' + receiptId,
    method: 'get'
  })
}
