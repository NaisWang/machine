import myAxios from "../utils/myAxios";
import qs from 'qs'

const BASE_URL = '/market-return-receipt-to-machine'

//获取销售单中的机器
export function getMarketReturnReceiptToMachine(currentPage, size, receiptId) {
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
export function modifyMarketReturnReceiptToMachine(marketReturnReceiptToMachine) {
  let data = {
    'receiptId': marketReturnReceiptToMachine.receiptId,
    'machineId': marketReturnReceiptToMachine.machineId,
    "refundPrice": marketReturnReceiptToMachine.editRefundPrice
  }

  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    params: data
  })

}
