import myAxios from "../utils/myAxios";

import qs from "qs"


let BASE_URL = '/personnel/emp'

/**
 * 获取所有员工
 */
export function getAllEmp(employee) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: employee,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

/**
 * 添加操作员
 */
export function addEmp(data) {
  let employee = JSON.parse(JSON.stringify(data))
  employee.allRoleIds = employee.allRoleIds.toString();
  return myAxios({
    url: BASE_URL + '/',
    method: 'post',
    data: employee
  })
}


/**
 * 添加操作员
 */
export function updateEmp(data) {
  let employee = JSON.parse(JSON.stringify(data))
  employee.allRoleIds = employee.allRoleIds.toString();
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: employee
  })
}


/**
 * 更新操作员角色
 */
export function updateAdminRole(adminId, rids) {
  return myAxios({
    url: BASE_URL + '/role',
    method: 'put',
    params: {
      adminId,
      rids
    },
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}


/**
 * 获取员工-库位对应关系
 */
//export function getEmpStorageLocationCorr() {
//  return myAxios({
//    url: BASE_URL + '/empToStorageLocationCorr',
//    method: 'get',
//  })
//}
//
///**
// * 获取库位id-员工ids的对应关系
// */
//export function getStorageLocationIdToEmpIdsCorr() {
//  return myAxios({
//    url: BASE_URL + '/storageLocationToEmpCorr',
//    method: 'get'
//  })
//}