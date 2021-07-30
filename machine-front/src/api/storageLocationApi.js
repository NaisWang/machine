import myAxios from "../utils/myAxios";
import qs from "qs"

let BASE_URL = '/storage-location'

export function getStorageLocation() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}

export function addStorageLocation(storageLocation) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'post',
    data: storageLocation
  })
}

export function modifyStorageLocation(storageLocation) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: storageLocation
  })
}