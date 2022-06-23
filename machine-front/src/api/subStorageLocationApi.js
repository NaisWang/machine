import myAxios from "../utils/myAxios";
import qs from "qs"

let BASE_URL = '/sub-storage-location'

// 获取子库位
export function getSubStorageLocation(parentStorageLocationId) {
  let url = '/?'
  if (parentStorageLocationId !== undefined)
    url += 'parentStorageLocationId=' + parentStorageLocationId
  return myAxios({
    url: BASE_URL + url,
    method: 'get'
  })
}

export function addSubStorageLocation(data) {
  let subStorageLocation = JSON.parse(JSON.stringify(data));
  subStorageLocation.gateEmpId = subStorageLocation.gateEmpId.toString();
  return myAxios({
    url: BASE_URL + '/',
    method: 'post',
    data: subStorageLocation
  })
}

export function modifySubStorageLocation(data) {
  let subStorageLocation = JSON.parse(JSON.stringify(data));
  subStorageLocation.gateEmpId = subStorageLocation.editGateEmpId.length === 0 ? null : subStorageLocation.editGateEmpId.toString();
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: subStorageLocation
  })
}
