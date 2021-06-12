import myAxios from "../utils/myAxios";

let BASE_URL = '/employee/corr'

export function getEmpImageCorr() {
  return myAxios({
    url: BASE_URL + '/image',
    method: 'get'
  })
}

export function getEmpNameCorr() {
  return myAxios({
    url: BASE_URL + '/name',
    method: 'get'
  })
}

