import myAxios from "../utils/myAxios";
import qs from 'qs';

let BASE_URL = '/machine/shelf/up'

// 获取采购单信息
export function getUpShelfReceipt(currentPage, size, upShelfReceipt) {
  let test = Object.assign(upShelfReceipt, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}
