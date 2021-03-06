import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/up-shelf/enter-storage'


// 获取上架入库单据
export function getUpShelfEnterStorage(currentPage, size, enterStorage) {
  let test = Object.assign(enterStorage, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 获取上架入库单据中的机器
export function getUpShelfEnterStorageToMachine(currentPage, size, receiptId) {
  let test = Object.assign({receiptId: receiptId}, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/machines',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 添加销退入库单
export function createUpShelfEnterStorage(enterStorage) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: enterStorage
  })
}

// 修改上架入库单信息
export function modifyUpShelfEnterStorage(receipt) {
  return myAxios({
    url: BASE_URL + '/modifyReceipt',
    method: 'put',
    data: receipt
  })
}

// 往上架入库单中添加机器
export function addMachineToUpShelfEnterStorage(ids, receiptId) {
  return myAxios({
    url: BASE_URL + '/addMachine?receiptId=' + receiptId,
    method: 'put',
    data: ids
  })
}

// 删除上架入库单中的机器
export function deleteMachineForUpShelfEnterStorage(id) {
  return myAxios({
    url: BASE_URL + '/deleteMachine/?id=' + id,
    method: 'delete',
  })
}

// 发布上架入库订单
export function releaseUpShelfEnterStorage(receiptId) {
  return myAxios({
    url: BASE_URL + '/release?receiptId=' + receiptId,
    method: 'get'
  })
}

// 删除上架入库单
export function deleteUpShelfEnterStorage(receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'delete'
  })
}

// 创建、添加、发布入库单
export function createAddReleaseUpShelfEnterStorageReceipt(numbers, receiptId, storageLocationId, deliverReceiptId) {
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


