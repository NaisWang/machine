import {getDeliverReceiptById} from "../api/deliverReceiptApi";
import {getDeliverStatus, receiveDeliverMachine} from "../api/deliverMachineApi";

export function dealMachineJudge(machine, store, operateName) {

  if (['addMachineToEnterStorage', 'marketReturnEnterStorage'].indexOf(operateName) === -1 && machine.storageLocationId === null) {
    return Promise.resolve({
      "code": -1,
      message: "该机器还没有入库!!!"
    });
  }

  if ([20, 21, 23, 25, 27, 28, 30, 34].indexOf(machine.statusId) !== -1) {
    return Promise.resolve({
      "code": -1,
      message: "该机器状态是" + store.state.machineStatusCorr[machine.statusId] + ", 操作人是" + store.state.employeeNameCorr[machine.operateEmpId]
    });
  }

  if (machine.deliverReceiptId !== 0) {
    return getDeliverReceiptById(machine.deliverReceiptId).then(resp => {
      if (resp.data.obj) {
        let deliverReceipt = resp.data.obj;
        return getDeliverStatus(machine.id, deliverReceipt.deliverReceiptId).then(resp => {
          if (resp.data.obj === 0) {
            return Promise.resolve({
              "code": -1,
              "message": "该机器正处于待转交状态" + "操作人是：" + store.state.employeeNameCorr[deliverReceipt.operateEmpId]
            })
          } else if (resp.data.obj === 1) {
            if (deliverReceipt.receiveEmpIds.split(',').indexOf(JSON.parse(window.sessionStorage.getItem("user"))['id'] + '') === -1) {
              let receiveEmp = ""
              deliverReceipt['receiveEmpIds'].split(',').forEach(item => {
                receiveEmp += store.state.employeeNameCorr[item] + '、'
              })
              return Promise.resolve({"code": -1, "message": "该机器正处于转交中状态，你不是接收人, 请交给以下接收人: " + receiveEmp})
            } else if ([2, 7, 8].indexOf(deliverReceipt.deliverIntentionId) !== -1) {
              return Promise.resolve({"code": -1, "message": "该机器处于库存调拨中，请通过入库方式来接收"})
            } else {
              let receiveDialog = {}
              return receiveDeliverMachine(machine.id).then(resp => {
                if (resp.data.code === 200) {
                  receiveDialog = {
                    title: '接收成功',
                    message: '转交单类型：' + store.state.deliverIntentionCorr[deliverReceipt.deliverIntentionId],
                    type: 'success',
                    duration: 10000
                  }
                }
              }).then(() => {
                return machineStatusJudge(machine.statusId, operateName, store.state, receiveDialog);
              })
            }
          }
        })
      } else {
        return Promise.resolve({"code": -1, "message": "数据有错误"})
      }
    })
  } else {
    if (machine.operateEmpId !== store.state.userId) {
      return Promise.resolve({
        "code": -1,
        "message": "你不是该机器的现有处理人，你无权处理该机器！！！请给归还给" + store.state.employeeNameCorr[machine.operateEmpId]
      })
    }
    return Promise.resolve(machineStatusJudge(machine.statusId, operateName, store.state))
  }
}

function machineStatusJudge(statusId, operateName, state, receiveDialog) {
  if (operateName === 'addMachineToPurchaseReturn') {
    //往采购退货单中添加机器:   待入库 -> 退货中预定
    if (statusId !== 2) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是待入库, 不能进行采购退货操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'confirmPurchaseReturnSuccess') {
    //确定退货成功: 退货中 -> 退货成功
    if (statusId !== 3) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是退货中, 不能进行退货成功操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'confirmPurchaseReturnFail') {
    //确定退货失败: 退货中 -> 退货失败
    if (statusId !== 3) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是退货中, 不能进行退货失败操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'upShelf') {
    //机器上架:：待上架、已上架、提交并退回 -> 已上架
    if ([10, 11, 36].indexOf(statusId) === -1) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是待上架、已上架、提交并退回, 不能进行上架操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'addMachineToMarketOrder') {
    //往销售订单中添加机器: 已上架、待出库 -> 待销售
    if (statusId !== 26) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是已上架，待出库中, 不能进行销售操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'addMachineToMarketReturn') {
    //往销售退货订单中添加机器：已出库 -> 待销售退货
    if (statusId !== 13) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是已上架，待出库中, 不能进行销售操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'addMachineToEnterStorage') {
    // 往入库单中添加机器： 待入库、退货中(提示用户)、退货失败 -> 入库中
    //if ([1, 3, 4].indexOf(statusId) === -1) {
    //  return Promise.resolve({
    //    "code": -1,
    //    "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是待入库、退货中或退货失败中, 不能进行入库操作",
    //    "receiveDiagLog": receiveDialog
    //  })
    //}
    if (statusId === 3) {
      return Promise.resolve({"code": 0, "message": "该机器为退货中，是否要入库", "receiveDiagLog": receiveDialog})
    }
  } else if (operateName === 'addMachineToUpShelfEnterStorage') {
    //往上架入库单中添加机器：已上架  -> 已上架，待入库
    if (statusId !== 11) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是已上架中, 不能进行上架入库操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'addMachineToMarketReturnEnterStorage') {
    //往销退入库单中添加机器：销售退货 -> 待消退入库
    if (statusId !== 14) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是销售退货中, 不能进行销退入库操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'addMachineToSendFix') {
    //往送外修单中添加机器：已确维修项、送修取回 -> 待送外修
    if ([32, 31].indexOf(statusId) === -1) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是已确定维修项或送修取回中, 不能进行送外修操作",
        "receiveDiagLog": receiveDialog
      })
    }
  } else if (operateName === 'receiveSendFixMachine') {
    // 送修取回：送外修 -> 送修取回
    if (statusId === 17) {
      return Promise.resolve({
        "code": -1,
        "message": "该机器状态是" + state.machineStatusCorr[statusId] + ", 而不是送外修, 不能进行送外修操作",
        "receiveDiagLog": receiveDialog
      })
    }
  }
  return Promise.resolve({"code": 1, "receiveDiagLog": receiveDialog})

}