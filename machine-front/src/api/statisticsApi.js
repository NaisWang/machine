import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/statistics'

// 获取所有数据
export function getAllStatistics(empIds, statusIds, dateScope) {
  return myAxios({
    url: BASE_URL + '/all',
    method: 'get',
    params: {
      empIds,
      statusIds,
      dateScope
    },
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 获取登录者的数据
export function getOneStatistics() {
  return myAxios({
    url: BASE_URL + '/one',
    method: 'get',
  })
}
