import myAxios from "../utils/myAxios";
import qs from "qs"

let BASE_URL = '/machine-trace'

//获取机器追踪信息
export function getMachineTrace(machineTrace) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: machineTrace,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}
