import myAxios from "../utils/myAxios"
import qs from 'qs'

const BASE_URL = '/machine/market/return'

/**
 *获取/搜索采购单中的所有机器信息, 分页
 */
export function getMarketReturnReceipt(currentPage, size, marketReturnReceipt, bidDateScope) {
  let test = Object.assign(marketReturnReceipt, {currentPage: currentPage, size: size, bidDateScope: bidDateScope})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 添加销售退货
export function createMarketReturnReceipt(receipt) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: receipt
  })
}

// 修改销售退货单信息
export function modifyMarketReturnReceipt(receipt) {
  return myAxios({
    url: BASE_URL + '/modifyReceipt',
    method: 'put',
    data: receipt
  })
}

// 往销售退货单中添加机器
export function addMachineToMarketReturnReceipt(ids, receiptId) {
  return myAxios({
    url: BASE_URL + '/addMachine?receiptId=' + receiptId,
    method: 'put',
    data: ids
  })
}
// 删除销售退货订单订单中的机器
export function deleteMachineForMarketReturnReceipt(id) {
  return myAxios({
    url: BASE_URL + '/deleteMachine?id=' + id,
    method: 'delete'
  })
}

// 发布销售退货订单
export function releaseMarketReturnReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/release?receiptId=' + receiptId,
    method: 'get'
  })
}

// 删除销售退货单
export function deleteMarketReturnReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'delete'
  })
}
