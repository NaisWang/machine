import myAxios from "../utils/myAxios";

import qs from "qs"

let BASE_URL = '/employee/role'

export function getEmpRole() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}
