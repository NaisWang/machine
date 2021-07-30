import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/operate-trace'


/**
 *获取/搜索采购单中的所有机器信息, 分页
 */
export function getOperateTrace(currentPage, size, operateTrace, bidDateScope) {
  let test = Object.assign(operateTrace, {currentPage: currentPage, size: size, bidDateScope: bidDateScope})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}
