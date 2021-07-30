import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/machine/purchase/return'

// 获取采购退货单信息
export function getPurchaseReturnReceipt(currentPage, size, purchaseReturnReceipt) {
  let test = Object.assign(purchaseReturnReceipt, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 添加采购退货信息
export function createPurchaseReturnReceipt(purchaseReturnReceipt) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: purchaseReturnReceipt
  })
}

// 往入库单中添加机器
export function addMachineToPurchaseReturnReceipt(ids, receiptId) {
  return myAxios({
    url: BASE_URL + '/addMachine?receiptId=' + receiptId,
    method: 'put',
    data: ids
  })
}

// 修改采购退货单信息
export function modifyPurchaseReturnReceipt(receipt) {
  return myAxios({
    url: BASE_URL + '/modifyReceipt',
    method: 'put',
    data: receipt
  })
}

// 删除采购退货单
export function deletePurchaseReturnReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'delete'
  })
}

// 删除采购退货订单中的机器
export function deleteMachineForPurchaseReturnReceipt(id) {
  return myAxios({
    url: BASE_URL + '/deleteMachine?id=' + id,
    method: 'delete'
  })
}

// 发布采购退货订单
export function releasePurchaseReturnReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/release?receiptId=' + receiptId,
    method: 'get'
  })
}

// 设置退货成功
export function purchaseReturnSuccess(number) {
  return myAxios({
    url: BASE_URL + '/returnSuccess?number=' + number,
    method: 'get'
  })
}

// 设置退货失败
export function purchaseReturnError(number) {
  return myAxios({
    url: BASE_URL + '/returnError?number=' + number,
    method: 'get'
  })
}
