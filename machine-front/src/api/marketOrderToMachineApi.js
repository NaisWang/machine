import myAxios from "../utils/myAxios";
import qs from 'qs'

const BASE_URL = '/market-order-receipt-to-machine'

//获取销售单中的机器
export function getMarketOrderReceiptToMachine(currentPage, size, receiptId) {
  let test = Object.assign({receiptId: receiptId}, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

//修改销售单中的机器的信息
export function modifyMarketOrderReceiptToMachine(marketOrderReceiptToMachine) {
  let data = {
    'receiptId': marketOrderReceiptToMachine.receiptId,
    'machineId': marketOrderReceiptToMachine.machineId,
    "sellPrice": marketOrderReceiptToMachine.editSellPrice
  }

  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    params: data
  })

}
