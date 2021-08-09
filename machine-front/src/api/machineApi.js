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
    'comment': machine.comment.toString() + (machine.editComment1 == null || machine.editComment1.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment1.toString(),
    'storageLocationId': machine.storageLocationId,
    "isUpShelf": machine.isUpShelf
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
    'comment': machine.comment.toString() + (machine.editComment2 == null || machine.editComment2.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment2.toString(),
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
    "statusId": machine.statusId,
    "sku": machine.sku,
    "isUpShelf": machine.isUpShelf
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
 * 机器维修完成或维修中，如过type为0，表示维修完成，type为1，表示维修中
 */
export function modifyFixComplete(machine, type) {
  let data = {
    'id': machine.id,
    'number': machine.number,
    'fixed': machine.fixed.toString(),
    'notFixed': machine.notFixed.toString(),
    'fixToBad': machine.fixToBad.toString(),
    'comment': machine.comment.toString() + (machine.editComment == null || machine.editComment.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment.toString(),
    'storageLocationId': machine.storageLocationId,
    "statusId": machine.statusId,
    "sku": machine.sku,
    "isUpShelf": machine.isUpShelf
  }

  return myAxios({
    url: BASE_URL + '/modify/fixComplete?type=' + type,
    method: 'put',
    data
  })
}

/**
 * 机器上架
 */
export function modifyMachineUpShelf(machine, type) {
  let data = []

  data = {
    'id': machine.id,
    'number': machine.number,
    'goodPrice': machine.goodPrice,
    'onePrice': machine.onePrice,
    'bidPrice': machine.bidPrice,
    'rankDesc': machine.rankDesc,
    'bagNumber': machine.bagNumber,
    'comment': machine.comment.toString() + (machine.editComment3 == null || machine.editComment3.replaceAll(' ', '') === '') ? "" : '、' + machine.editComment3.toString(),
    "statusId": machine.statusId,
    'storageLocationId': machine.storageLocationId,
  }

  return myAxios({
    url: BASE_URL + '/modify/upShelf?type=' + type,
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
