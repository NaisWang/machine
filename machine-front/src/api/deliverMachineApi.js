import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/deliver/machine'

// 获取转交单中机器
export function getDeliverMachine(currentPage, size, deliverMachine) {
  let test = Object.assign(deliverMachine, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

//往转交单中添加机器
export function addDeliverMachine(deliverMachine) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: deliverMachine
  })
}


// 往转交单中添加机器
export function addMachineToDeliverReceipt(ids, receiptId) {
  return myAxios({
    url: BASE_URL + '/addMachine?receiptId=' + receiptId,
    method: 'put',
    data: ids
  })
}

//删除转交单中机器
export function deleteDeliverMachine(machineNumber, receiptId) {
  return myAxios({
    url: BASE_URL + '/?machineNumber=' + machineNumber + "&receiptId=" + receiptId,
    method: 'delete'
  })
}

// 获取机器的转交状态
export function getDeliverStatus(machineId, deliverReceiptId) {
  return myAxios({
    url: BASE_URL + "/status?machineId=" + machineId + "&deliverReceiptId=" + deliverReceiptId,
    method: 'get'
  })
}

//接收机器
export function receiveDeliverMachine(machineId) {
  return myAxios({
    url: BASE_URL + "/receive?machineId=" + machineId,
    method: 'get'
  })
}