import myAxios from "../utils/myAxios";
import qs from 'qs'

const BASE_URL = '/machine/market/order'

/**
 *获取/搜索采购单中的所有机器信息, 分页
 */
export function getMarketOrderReceipt(currentPage, size, marketOrder, bidDateScope) {
  let test = Object.assign(marketOrder, {currentPage: currentPage, size: size, bidDateScope: bidDateScope})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

//// 添加采购退货信息
//export function createMarketOrderReceipt(marketOrderReceipt, machine) {
//  return myAxios({
//    url: BASE_URL + '/',
//    method: 'put',
//    data: {
//      'marketOrderReceipt': JSON.stringify(marketOrderReceipt),
//      'machine': JSON.stringify(machine)
//    }
//  })
//}


// 添加销售订单信息
export function createMarketReturnReceipt(marketReturnReceipt) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: marketReturnReceipt
  })
}

// 修改采购退货单信息
export function modifyMarketReturnReceipt(receipt) {
  return myAxios({
    url: BASE_URL + '/modifyReceipt',
    method: 'put',
    data: receipt
  })
}

// 往销售单中添加机器
export function addMachineToMarketOrderReceipt(ids, receiptId) {
  return myAxios({
    url: BASE_URL + '/addMachine?receiptId=' + receiptId,
    method: 'put',
    data: ids
  })
}
