import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/machine/down-shelf'

export function getMachineDownShelf(currentPage, size, machine, bidDateScope) {
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

export function addMachineDownShelf(machines) {
  let data = []
  machines.forEach(machine => {
    data.push({
      'machineId': machine.id,
      'machineNumber': machine.number,
    })
  })

  return myAxios({
    url: BASE_URL + '/',
    method: 'post',
    data
  })
}
