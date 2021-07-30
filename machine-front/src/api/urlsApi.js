import myAxios from "../utils/myAxios";
import qs from "qs"

let BASE_URL = '/url'

export function getUrls() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}

export function updateUrl(urls) {
  let addUrls = JSON.parse(JSON.stringify(urls));
  addUrls.forEach(item => {
    item.roleIds = item.roleIds.toString();
  })
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: addUrls
  })
}
