import myAxios from "../utils/myAxios";

const BASE_URL = '/machine/general/purchase-order-modify'

/**
 * 判断当前用户能修改采购单中哪些字段
 */
export function isModifyFiled() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}