import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    menuRoutes: [],
    machineCategoryCorr: {},
    machineStatusCorr: {},
    machineChannelCorr: {},
    machineBrandCorr: {},
    employeeNameCorr: {},
    imageUrlCorr: {},
  },
  mutations: {
    initMenuRoutes(state, data) {
      state.menuRoutes = data;
    },
    initMachineCategoryCorr(state, data) {
      state.machineCategoryCorr = data;
    },
    initMachineStatusCorr(state, data) {
      state.machineStatusCorr = data;
    },
    initMachineChannelCorr(state, data) {
      state.machineChannelCorr = data;
    },
    initMachineBrandCorr(state, data) {
      state.machineBrandCorr = data;
    },
    initEmployeeNameCorr(state, data) {
      state.employeeNameCorr = data;
    },
    initImageUrlCorr(state, data) {
      state.imageUrlCorr = data;
    },
  },
})
