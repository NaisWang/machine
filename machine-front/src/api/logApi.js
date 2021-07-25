import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/system/log'

/**
 *获取/搜索操作日志, 分页
 */
export function getLog(currentPage, size, log, logTimeScope) {
  let test = Object.assign(log, {currentPage: currentPage, size: size, logTimeScope: logTimeScope})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}
