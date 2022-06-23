import myAxios from "../utils/myAxios";
import qs from 'qs'
import {receiveDeliverMachine} from "./deliverMachineApi";

let BASE_URL = '/machine/purchase/order'

// 获取采购单信息
export function getPurchaseOrderInfo(currentPage, size, purchaseOrder) {
  let test = Object.assign(purchaseOrder, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

//获取采购单中的机器
export function getPurchaseOrderReceiptToMachine(currentPage, size, receiptId) {
  let test = Object.assign({receiptId: receiptId}, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/machines',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 获取采购单最大id
export function getPurchaseOrderReceiptMaxId() {
  return myAxios({
    url: BASE_URL + '/getMaxId',
    method: 'get',
  })
}

// 添加采购信息
//export function createPurchaseReceipt(purchaseOrderReceipt, machine) {
//  return myAxios({
//    url: BASE_URL + '/',
//    method: 'put',
//    data: {
//      'purchaseOrderReceipt': JSON.stringify(purchaseOrderReceipt),
//      'machine': JSON.stringify(machine)
//    }
//  })
//}

// 添加采购信息
export function createPurchaseReceipt(purchaseOrderReceipt) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: purchaseOrderReceipt
  })
}

// 往采购单中添加信息
export function addMachineToPurchaseReceipt(machines, receiptId) {
  return myAxios({
    url: BASE_URL + '/addMachine?receiptId=' + receiptId,
    method: 'put',
    data: machines
  })
}

// 发布采购订单
export function releasePurchaseReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/release?receiptId=' + receiptId,
    method: 'get'
  })
}

// 删除采购订单中的机器
export function deleteMachineForPurchaseOrder(id) {
  return myAxios({
    url: BASE_URL + '/deleteMachine?id=' + id,
    method: 'delete'
  })
}

// 修改采购单中机器的信息
export function modifyMachineForPurchaseOrder(machine) {
  return myAxios({
    url: BASE_URL + '/modifyMachine',
    method: 'put',
    data: machine
  })
}

// 修改采购单信息
export function modifyPurchaseOrderReceipt(purchaseReceipt) {
  return myAxios({
    url: BASE_URL + '/modifyReceipt',
    method: 'put',
    data: purchaseReceipt
  })
}

// 删除采购单
export function deletePurchaseOrderReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'delete'
  })
}