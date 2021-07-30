import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/machine/recall'

export function getMachineRecall(currentPage, size, machine, bidDateScope) {
  let test = Object.assign(machine, {currentPage: currentPage, size: size, bidDateScope: bidDateScope})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

export function addMachineRecall(machines) {
  let data = []
  machines.forEach(machine => {
    data.push({
      'number': machine.number,
      'preOperateEmpId': machine.operateEmpId,
      'statusId': machine.statusId,
      'storageLocationId': machine.storageLocationId
    })
  })

  return myAxios({
    url: BASE_URL + '/',
    method: 'post',
    data
  })
}
