import {getMachineTableField} from "../api/machineTableFieldApi";
import initMachineCorr from "../utils/machineCorr";
import {getMachineDesc} from "../api/machineDesc";
import {getMachineDescTable} from "../api/machineDesc";
import {getDeliverIntention} from "../api/deliverIntentionApi";
import {getStorageLocation} from "../api/storageLocationApi";
import {getRole} from "../api/roleApi";
import {getEmpRole} from "../api/empRoleApi";
import {getEmpStorageLocationCorr, getStorageLocationIdToEmpIdsCorr} from "../api/employeeApi";
import {getSubStorageLocation} from "../api/subStorageLocationApi";
import ro from "element-ui/src/locale/lang/ro";

export function initialStoreInformation(store) {
  store.commit('initUserId', JSON.parse(window.sessionStorage.getItem('user'))['id'])

  initMachineCorr(store)

  getMachineDesc().then(resp => {
    if (resp.data.obj) {
      resp.data.obj.phone.all = Object.assign({}, resp.data.obj['phone']['functionInfos'], resp.data.obj['phone']['qualityInfos'])
      resp.data.obj.tablet.all = Object.assign({}, resp.data.obj['tablet']['functionInfos'], resp.data.obj['tablet']['qualityInfos'])
      resp.data.obj.watch.all = Object.assign({}, resp.data.obj['watch']['functionInfos'], resp.data.obj['watch']['qualityInfos'])
      store.commit('initMachineDesc', resp.data.obj)
    }
  })

  getMachineDescTable().then(resp => {
    let ans = {}
    if (resp.data.obj) {
      Object.keys(resp.data.obj).forEach(key => {
        resp.data.obj[key].forEach(item => {
          ans[item['id']] = item['value']
        })
      })
    }
    store.commit('initMachineIdToDesc', ans);
  })

  getMachineTableField().then(resp => {
    if (resp.data.obj) {
      store.commit('initMachineTableField', resp.data.obj)
      console.log("aaa")
    }
  })

  getDeliverIntention().then(resp => {
    let deliverIntentionCorr = {}
    let data = resp.data.obj
    data.forEach(item => {
      deliverIntentionCorr[item['id']] = item['name'];
    })
    store.commit('initDeliverIntentionCorr', deliverIntentionCorr);
  })


  initRolesCorr(store).then(() => {
    initRoleIdToEmpIds(store).then(() => {
      initSubStorageLocationToNameAndToEmpIdsAndToGateEmpId(store).then()
    })
  })
}


export function judgeIsUpdate(store) {
  if (Object.keys(store.state.machineCategoryCorr).length === 0 || Object.keys(store.state.machineTableField).length === 0) {
    return true
  }
  return false
}

//export function initStorageLocation(store) {
//  getStorageLocation().then(resp => {
//    let storageLocationCorr = {}
//    let data = resp.data.obj
//    data.forEach(item => {
//      storageLocationCorr[item['id']] = item['name']
//    })
//    store.commit('initStorageLocationCorr', storageLocationCorr);
//  })
//}

export function initSubStorageLocationToNameAndToEmpIdsAndToGateEmpId(store) {
  return getSubStorageLocation().then(resp => {
    let subStorageLocationToNameCorr = {}

    let subStorageLocationIdToGateEmpIdCorr = {}
    // 员工id - 身为库管门卫的的库位ids
    let empIdToStorageLocationIdsForGateCorr = {}

    let data = resp.data.obj
    data.forEach(item => {
      subStorageLocationToNameCorr[item['id']] = item['name']
      subStorageLocationIdToGateEmpIdCorr[item['id']] = item['gateEmpId']

      if (item['gateEmpId'] === '' || item['gateEmpId'] === null) {
        store.state.roleIdToEmpIdsCorr[10].forEach(empId => {
          if (Object.keys(empIdToStorageLocationIdsForGateCorr).indexOf(empId + '') === -1) {
            empIdToStorageLocationIdsForGateCorr[empId + ''] = []
          }
          empIdToStorageLocationIdsForGateCorr[empId + ''].push(item['id'])
        })
      } else {
        item['gateEmpId'].split(',').forEach(gateEmpId => {
          if (Object.keys(empIdToStorageLocationIdsForGateCorr).indexOf(gateEmpId + '') === -1) {
            empIdToStorageLocationIdsForGateCorr[gateEmpId + ''] = []
            empIdToStorageLocationIdsForGateCorr[gateEmpId + ''].push(item['id'])
          } else {
            empIdToStorageLocationIdsForGateCorr[gateEmpId + ''].push(item['id'])
          }
        })
      }


    })
    store.commit('initSubStorageLocationIdToNameCorr', subStorageLocationToNameCorr);
    store.commit('initSubStorageLocationIdToGateEmpIdCorr', subStorageLocationIdToGateEmpIdCorr);
    store.commit('initEmpIdToStorageLocationIdsForGateCorr', empIdToStorageLocationIdsForGateCorr);
  })
}

export function initRolesCorr(store) {
  return getRole().then(resp => {
    let rolesCorr = {}
    let data = resp.data.obj
    data.forEach(item => {
      rolesCorr[item['id']] = item['zhName']
    })
    store.commit('initRolesCorr', rolesCorr);
  })
}

export function initRoleIdToEmpIds(store) {


  let roleIdToEmpIdsCorr = {}
  let empIdToRoleIdsCorr = {}

  return getEmpRole().then(resp => {
    let data = resp.data.obj
    data.forEach(item => {
      if (Object.keys(roleIdToEmpIdsCorr).indexOf(item['roleId'] + '') === -1) {
        roleIdToEmpIdsCorr[item['roleId']] = []
      }
      roleIdToEmpIdsCorr[item['roleId']].push(item['employeeId'])

      if (Object.keys(empIdToRoleIdsCorr).indexOf(item['employeeId'] + '') === -1) {
        empIdToRoleIdsCorr[item['employeeId']] = []
      }
      empIdToRoleIdsCorr[item['employeeId']].push(item['roleId'])
    })
    store.commit('initRoleIdToEmpIdsCorr', roleIdToEmpIdsCorr);
    store.commit('initEmpIdToRoleIdsCorr', empIdToRoleIdsCorr);
  })

}

//export function initEmpIdToStorageLocationIdCorr(store) {
//  getEmpStorageLocationCorr().then(resp => {
//    store.commit("initEmpIdToStorageLocationIdCorr", resp.data.obj)
//  })
//}
//
//export function initStorageLocationIdToEmpIdsCorr(store) {
//  getStorageLocationIdToEmpIdsCorr().then(resp => {
//    store.commit("initStorageLocationIdToEmpIdsCorr", resp.data.obj)
//  })
//}

