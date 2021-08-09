import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/market/return/enter-storage'

// 获取入库单据
export function getMarketReturnEnterStorageReceipt(currentPage, size, marketReturnEnterStorageReceipt) {
  let test = Object.assign(marketReturnEnterStorageReceipt, {currentPage: currentPage, size: size})
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

// 添加销退入库单
export function createMarketReturnEnterStorageReceipt(enterStorage) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: enterStorage
  })
}


// 往销退入库单中添加机器
export function addMachineToMarketReturnEnterStorageReceipt(ids, receiptId) {
  return myAxios({
    url: BASE_URL + '/addMachine?receiptId=' + receiptId,
    method: 'put',
    data: ids
  })
}

// 删除销退入库单中的机器
export function deleteMachineForMarketReturnEnterStorageReceipt(id) {
  return myAxios({
    url: BASE_URL + '/deleteMachine/?id=' + id,
    method: 'delete',
  })
}

// 一键入库
// export function createEnterStorageReceiptByOneKey(enterStorageReceipt, purchaseOrder) {
//   return myAxios({
//     url: BASE_URL + '/one-key-enter/',
//     method: 'put',
//     data: {
//       'enterStorageReceipt': JSON.stringify(enterStorageReceipt),
//       'purchaseOrder': JSON.stringify(purchaseOrder)
//     }
//   })
// }

// 发布销退入库订单
export function releaseMarketReturnEnterStorageReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/release?receiptId=' + receiptId,
    method: 'get'
  })
}

// 修改销退入库单信息
export function modifyMarketReturnEnterStorageReceipt(receipt) {
  return myAxios({
    url: BASE_URL + '/modifyReceipt',
    method: 'put',
    data: receipt
  })
}

// 删除销退入库单
export function deleteMarketReturnEnterStorageReceipt(receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'delete'
  })
}

// 创建、添加、发布入库单
export function createAddReleaseMarketReturnEnterStorageReceipt(numbers, receiptId, storageLocationId, deliverReceiptId) {
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

