import * as machineCorrApi from "../api/machineCorrApi"
import * as empCorrApi from "../api/EmpCorrApi"

function ArrayToObject(data, valueName) {
  let ans = {}
  data.forEach(item => {
    ans[parseInt(item.id)] = item[valueName];
  })
  return ans;
}

export default function (store) {
  machineCorrApi.getCategoryCorr().then(resp => {
    store.commit("initMachineCategoryCorr", ArrayToObject(resp.data.obj, "name"));
  })
  machineCorrApi.getBrandCorr().then(resp => {
    store.commit("initMachineBrandCorr", ArrayToObject(resp.data.obj, "name"));
  })
  machineCorrApi.getStatusCorr().then(resp => {
    store.commit("initMachineStatusCorr", ArrayToObject(resp.data.obj, "name"))
  })
  machineCorrApi.getChannelCorr().then(resp => {
    store.commit("initMachineChannelCorr", ArrayToObject(resp.data.obj, "name"))
  })
  empCorrApi.getEmpImageCorr().then(resp => {
    store.commit("initImageUrlCorr", ArrayToObject(resp.data.obj, "url"))
  })
  empCorrApi.getEmpNameCorr().then(resp => {
    store.commit("initEmployeeNameCorr", ArrayToObject(resp.data.obj, "name"))
  })
}


//根据名字获取Corr下标
export function getCorrIndexByName(corr, name) {
  let ans = -1;
  Object.keys(corr).forEach(item => {
    if (name === corr[item]) {
      ans = item
    }
  })
  return ans
}