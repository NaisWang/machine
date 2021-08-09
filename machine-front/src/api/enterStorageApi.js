import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/machine/storage/enter-storage'

// 获取入库单据
export function getEnterStorageReceipt(currentPage, size, enterStorageReceipt) {
  let test = Object.assign(enterStorageReceipt, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 添加入库单据
//export function createEnterStorageReceipt(enterStorageReceipt, machine) {
//  return myAxios({
//    url: BASE_URL + '/',
//    method: 'put',
//    data: {
//      'enterStorageReceipt': JSON.stringify(enterStorageReceipt),
//      'machine': JSON.stringify(machine)
//    }
//  })
//}

// 添加入库单
export function createEnterStorageReceipt(enterStorage) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: enterStorage
  })
}


// 往入库单中添加机器
export function addMachineToEnterStorageReceipt(ids, receiptId) {
  return myAxios({
    url: BASE_URL + '/addMachine?receiptId=' + receiptId,
    method: 'put',
    data: ids
  })
}

// 删除入库单中的机器
export function deleteMachineForEnterStorageReceipt(id) {
  return myAxios({
    url: BASE_URL + '/deleteMachine/?id=' + id,
    method: 'delete',
  })
}

// 一键入库
export function createEnterStorageReceiptByOneKey(enterStorageReceipt, purchaseOrder) {
  return myAxios({
    url: BASE_URL + '/one-key-enter/',
    method: 'put',
    data: {
      'enterStorageReceipt': JSON.stringify(enterStorageReceipt),
      'purchaseOrder': JSON.stringify(purchaseOrder)
    }
  })
}

// 发布采购订单
export function releaseEnterStorageReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/release?receiptId=' + receiptId,
    method: 'get'
  })
}

// 修改入库单信息
export function modifyEnterStorageReceipt(receipt) {
  return myAxios({
    url: BASE_URL + '/modifyReceipt',
    method: 'put',
    data: receipt
  })
}

// 删除入库单
export function deleteEnterStorageReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'delete'
  })
}

// 创建、添加、发布入库单
export function createAddReleaseEnterStorageReceipt(numbers, receiptId, storageLocationId, deliverReceiptId) {
  let url = '/create-add-release?' + 'storageLocationId=' + storageLocationId + '&deliverReceiptId=' + deliverReceiptId
  if (receiptId !== undefined) {
    url += '&receiptId=' + receiptId
  }

  return myAxios({
    url: BASE_URL + url,
    method: 'put',
    data: numbers
  })
}
