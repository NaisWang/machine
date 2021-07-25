import {getDeliverStatus} from "../api/deliverMachineApi";
import {getDeliverReceiptById} from "../api/deliverReceiptApi";

export function deliverJudge(machine, store, deliverJudgeResult) {
  return getDeliverReceiptById(machine.deliverReceiptId).then(resp => {
    if (resp.data.obj) {
      let deliverReceipt = resp.data.obj;
      deliverJudgeResult.deliverReceipt = deliverReceipt
      return getDeliverStatus(machine.id, deliverReceipt.deliverReceiptId).then(resp => {
        if (resp.data.obj === 0) {
          deliverJudgeResult.code = -1
          deliverJudgeResult.message = "该机器正处于待转交状态" + "操作人是：" + store.state.employeeNameCorr[deliverReceipt.operateEmpId]
        } else if (resp.data.obj === 1) {
          if (deliverReceipt.receiveEmpIds.split(',').indexOf(JSON.parse(window.sessionStorage.getItem("user"))['id'] + '') === -1) {
            let receiveEmp = ""
            deliverJudgeResult.deliverReceipt['receiveEmpIds'].split(',').forEach(item => {
              receiveEmp += store.state.employeeNameCorr[item] + '、'
            })
            deliverJudgeResult.code = -1
            deliverJudgeResult.message = "该机器正处于转交中状态，你不是接收人, 请交给以下接收人: " + receiveEmp
          } else {
            deliverJudgeResult.code = 1
          }
        }
      })
    } else {
      deliverJudgeResult.code = -1;
      deliverJudgeResult.message = "数据有错误"
    }
  })
}
