import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/system/finance'

/**
 * 获取财务账单
 */
export function getFinance() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}
