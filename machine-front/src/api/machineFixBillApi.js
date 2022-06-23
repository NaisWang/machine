import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/machine/fix-bill'

/**
 * 获取维修账单
 */
export function getFixBill() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}

/**
 * 修改维修账单
 */
export function ModifyFixBill(fixBill) {
  let data = {
    'id': fixBill.id,
    'reparation': fixBill.reparation,
    'comment': fixBill.fixBillComment,
  }
  if (fixBill.confirm === 1) {
    data['confirm'] = 1
  }
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    params: data
  })
}
