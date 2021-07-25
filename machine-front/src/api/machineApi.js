import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/machine'

/**
 *获取/搜索采购单中的所有机器信息, 分页
 */
export function getMachine(currentPage, size, machine, bidDateScope) {
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

/**
 * 修改机器数据
 */
export function modifyMachine(machine) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: machine
  })
}

/**
 * 修改机器成色检测信息
 */
export function modifyMachineQuality(machines) {
  let data = []

  machines.forEach(machine => {
    data.push({'id': machine.id, 'qualityDesc': machine.qualityDesc.toString(), 'comment': machine.comment.toString()})
  })

  return myAxios({
    url: BASE_URL + '/modify/quality',
    method: 'put',
    data
  })
}

/**
 * 修改机器功能检测信息
 */
export function modifyMachineFeature(machines) {
  let data = []

  machines.forEach(machine => {
    data.push({
      'id': machine.id,
      'featureDesc': machine.featureDesc.toString(),
      'comment': machine.comment.toString(),
      'paijiBarcode': machine.paijiBarcode
    })
  })

  return myAxios({
    url: BASE_URL + '/modify/feature',
    method: 'put',
    data
  })
}


/**
 * 修改机器状态为处理中
 */
export function modifyMachineStatusTo5(machines) {
  let data = []

  machines.forEach(machine => {
    data.push({
      'id': machine.id,
    })
  })

  return myAxios({
    url: BASE_URL + '/modify/status-to5',
    method: 'put',
    data
  })
}
