import myAxios from "../utils/myAxios";

let BASE_URL = '/machine/desc'

export function getMachineDesc() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}

export function getMachineDescTable() {
  return myAxios({
    url: BASE_URL + '/getTable/',
    method: 'get'
  })
}

export function updateMachineDesc(machineDesc) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: machineDesc['functionInfos'].concat(machineDesc['qualityInfos'])
  })
}
