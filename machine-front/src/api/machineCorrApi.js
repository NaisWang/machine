import myAxios from "../utils/myAxios";

let BASE_URL = "/machine/corr"

export function getCategoryCorr() {
  return myAxios({
    url: BASE_URL + '/category',
    method: 'get'
  })
}

export function getBrandCorr() {
  return myAxios({
    url: BASE_URL + '/brand',
    method: 'get'
  })
}

export function getChannelCorr() {
  return myAxios({
    url: BASE_URL + '/channel',
    method: 'get'
  })
}

export function getStatusCorr() {
  return myAxios({
    url: BASE_URL + '/status',
    method: 'get'
  })
}
