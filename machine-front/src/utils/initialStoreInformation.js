import {getMachineTableField} from "../api/machineTableFieldApi";
import initMachineCorr from "../utils/machineCorr";
import {getMachineDesc} from "../api/machineDesc";
import {getMachineDescTable} from "../api/machineDesc";
import {getDeliverIntention} from "../api/deliverIntentionApi";

export function initialStoreInformation(store) {
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

}

export function judgeIsUpdate(store) {
  if (Object.keys(store.state.machineCategoryCorr).length === 0 || Object.keys(store.state.machineTableField).length === 0) {
    return true
  }
  return false
}
