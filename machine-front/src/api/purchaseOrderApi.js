import myAxios from "../utils/myAxios";
import qs from 'qs'

const BASE_URL = '/machine/purchase'

/**
 *获取/搜索采购单中的所有机器信息, 分页
 */
export function getAllPurchaseOrder(currentPage, size, purchaseOrder, bidDateScope) {
  let test = Object.assign(purchaseOrder, {currentPage: currentPage, size: size, bidDateScope: bidDateScope})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

/**
 * 修改采购单中的数据
 */
export function modifyMachine(purchaseOrder) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: purchaseOrder
  })
}