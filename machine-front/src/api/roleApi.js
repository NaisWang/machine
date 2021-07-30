import myAxios from "../utils/myAxios";

import qs from "qs"

let BASE_URL = '/role'

export function getRole() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}