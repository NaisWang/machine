import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/statistics'

export function getStatistics(id) {
  return myAxios({
    url: BASE_URL + '/?id=' + id,
    method: 'get'
  })
}

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

