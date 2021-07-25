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
