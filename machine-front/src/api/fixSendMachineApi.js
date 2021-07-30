import myAxios from "../utils/myAxios";
import qs from 'qs'

let BASE_URL = '/machine/fix/send/machine'


// 获取外修单据中的机器
export function getFixSendMachine(currentPage, size, fixSendMachine) {
  let test = Object.assign(fixSendMachine, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

// 往外修单据中添加机器
export function addMachineToFixSendReceipt(machine, receiptId) {
  return myAxios({
    url: BASE_URL + '/?receiptId=' + receiptId,
    method: 'put',
    data: machine
  })
}

//删除送外修单中的机器
export function deleteMachineForFixSendReceipt(id) {
  return myAxios({
    url: BASE_URL + '/?id=' + id,
    method: 'delete'
  })
}

// 取回送修机器
export function receiveMachine(number) {
  return myAxios({
    url: BASE_URL + '/receiveMachine?number=' + number,
    method: 'get'
  })
}