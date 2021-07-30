import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = "/deliver/receipt"


// 获取转交单信息
export function getDeliverReceipt(currentPage, size, receipt) {
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

// 获取需要接收的转交单
export function getReceiveReceipt(currentPage, size, receipt) {
  let test = Object.assign(receipt, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/receive',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

//通过receiptId获取转交单
export function getDeliverReceiptById(receiptId) {
  return myAxios({
    url: BASE_URL + '/getByMachineId/?receiptId=' + receiptId,
    method: 'get'
  })
}

//通过deliverMachineId获取转交单
export function getDeliverReceiptByDeliverMachineId(deliverMachineId) {
  return myAxios({
    url: BASE_URL + '/getByDeliverMachineId/?deliverMachineId' + deliverMachineId,
    method: 'get'
  })
}


// 添加转交单信息
export function createDeliverReceipt(receipt) {
  receipt.receiveEmpIds = receipt.receiveEmpIds.toString();
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: receipt
  })
}

//删除转交单
export function deleteDeliverReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'delete'
  })
}

//发布转交单
export function releaseDeliverReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/release?receiptId=' + receiptId,
    method: 'get'
  })
}

// 获取用户id需要接收的转交单
export function getReceiveDeliverReceipt() {
  return myAxios({
    url: BASE_URL + '/receive',
    method: 'get'
  })
}

