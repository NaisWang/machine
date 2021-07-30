import myAxios from "../utils/myAxios";
import qs from 'qs'
import {compile} from "vue-template-compiler";

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
export function modifyMachineQuality(machine) {
  let data = {}

  data = {
    'id': machine.id,
    'number': machine.number,
    'qualityDesc': machine.qualityDesc.toString(),
    'comment': machine.comment.toString() + (machine.editComment == null || machine.editComment.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment.toString(),
    'storageLocationId': machine.storageLocationId
  }

  return myAxios({
    url: BASE_URL + '/modify/quality',
    method: 'put',
    data
  })
}

/**
 * 修改机器功能检测信息
 */
export function modifyMachineFeature(machine) {
  let data = []

  data = {
    'id': machine.id,
    'number': machine.number,
    'featureDesc': machine.featureDesc.toString(),
    'comment': machine.comment.toString() + (machine.editComment == null || machine.editComment.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment.toString(),
    'paijiBarcode': machine.editPaijiBarcode,
    'storageLocationId': machine.storageLocationId
  }

  return myAxios({
    url: BASE_URL + '/modify/feature',
    method: 'put',
    data
  })
}

/**
 * 确定机器维修项
 */
export function modifyFixItem(machine) {
  let data = {
    'id': machine.id,
    'number': machine.number,
    'needFix': machine.needFix.toString(),
    'comment': machine.comment.toString() + (machine.editComment == null || machine.editComment.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment.toString(),
    'storageLocationId': machine.storageLocationId,
    "statusId": machine.statusId
  }

  return myAxios({
    url: BASE_URL + '/modify/needFix',
    method: 'put',
    data
  })
}

/**
 * 确定机器是否可以上架
 */
export function modifyCanUpShelf(machines, type) {
  let data = []
  machines.forEach(machine => {
    data.push({
      "id": machine.id,
      "number": machine.number,
      "statusId": machine.statusId,
      "comment": machine.comment,
      'storageLocationId': machine.storageLocationId,
    })
  })

  return myAxios({
    url: BASE_URL + '/modify/canUpShelf?type=' + type,
    method: 'put',
    data
  })
}

/**
 * 机器维修完成
 */
export function modifyFixComplete(machine) {
  let data = {
    'number': machine.number,
    'fixed': machine.fixed.toString(),
    'notFixed': machine.notFixed.toString(),
    'fixToBad': machine.fixToBad.toString(),
    'comment': machine.comment.toString() + (machine.editComment == null || machine.editComment.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment.toString(),
    'storageLocationId': machine.storageLocationId,
    "statusId": machine.statusId
  }

  return myAxios({
    url: BASE_URL + '/modify/fixComplete',
    method: 'put',
    data
  })
}

/**
 * 机器上架
 */
export function modifyMachineUpShelf(machine) {
  let data = []

  data = {
    'number': machine.number,
    'goodPrice': machine.editGoodPrice,
    'onePrice': machine.editOnePrice,
    'bidPrice': machine.editBidPrice,
    'rankDesc': machine.editRankDesc == null ? "" : machine.editRankDesc.toString(),
    'bagNumber': machine.editBagNumber == null ? "" : machine.editBagNumber.toString(),
    'comment': machine.comment.toString() + (machine.editComment == null || machine.editComment.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment.toString(),
    "statusId": machine.statusId,
    'storageLocationId': machine.storageLocationId,
  }

  return myAxios({
    url: BASE_URL + '/modify/upShelf',
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
      'number': machine.number,
    })
  })

  return myAxios({
    url: BASE_URL + '/modify/status-to5',
    method: 'put',
    data
  })
}
