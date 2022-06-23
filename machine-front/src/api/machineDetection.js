import myAxios from "../utils/myAxios";
import qs from "qs"

let BASE_URL = '/machine/detection'

//获取机器追踪信息
export function getMachineDetection(machineDetection) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: machineDetection,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}
