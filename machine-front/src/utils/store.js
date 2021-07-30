import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    userId: null,
    menuRoutes: [],
    machineCategoryCorr: {},
    machineStatusCorr: {},
    machineChannelCorr: {},
    machineBrandCorr: {},
    employeeNameCorr: {},
    imageUrlCorr: {},

    // 标签栏设置
    editableTabs: [],
    includeList: [],
    editableTabsValue: null,

    // 机器以及扫码添加时详细页面所要展示的数据
    machineTableField: {},

    // 获取拍机堂字段（包含分类）
    machineDesc: {},

    //机器描述id-value对照
    machineIdToDesc: {},

    //机器转交类别对应表
    deliverIntentionCorr: {},

    //子库位id - 库位名对应关系
    subStorageLocationIdToNameCorr: {},

    subStorageLocationIdToGateEmpIdCorr: {},

    // 员工id - 身为库管门卫的的库位ids
    empIdToStorageLocationIdsForGateCorr: {},

    //storageLocationCorr: {},

    //库位id - 员工ids的对应关系
    storageLocationIdToEmpIdsCorr: {},

    //员工id - 库位id对应关系
    empIdToStorageLocationIdCorr: {},

    // 角色id-角色名对应关系
    rolesCorr: {},

    // 角色id - 员工ids对应关系
    roleIdToEmpIdsCorr: {},
    empIdToRoleIdsCorr: {},

  },
  mutations: {
    initUserId(state, data) {
      state.userId = data;
    },
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
    initOneKeyEnterStorageMachine(state, data) {
      state.oneKeyEnterStorageMachine = data;
    },

    initEditableTabs(state, data) {
      state.editableTabs = data
    },
    editableTabsPush(state, item) {
      state.editableTabs.push(item);
    },

    includeListPush(state, item) {
      state.includeList.push(item)
    },
    initIncludeList(state, data) {
      state.includeList = data
    },

    initEditableTabsValue(state, data) {
      state.editableTabsValue = data
    },

    initMachineTableField(state, data) {
      state.machineTableField = data;
    },

    initMachineDesc(state, data) {
      state.machineDesc = data
    },

    initMachineIdToDesc(state, data) {
      state.machineIdToDesc = data
    },

    initDeliverIntentionCorr(state, data) {
      state.deliverIntentionCorr = data;
    },

    //initStorageLocationCorr(state, data) {
    //  state.storageLocationCorr = data
    //},

    initRolesCorr(state, data) {
      state.rolesCorr = data
    },

    initRoleIdToEmpIdsCorr(state, data) {
      state.roleIdToEmpIdsCorr = data;
    },
    initEmpIdToRoleIdsCorr(state, data) {
      state.empIdToRoleIdsCorr = data
    },

    initEmpIdToStorageLocationIdCorr(state, data) {
      state.empIdToStorageLocationIdCorr = data
    },

    initStorageLocationIdToEmpIdsCorr(state, data) {
      state.storageLocationIdToEmpIdsCorr = data
    },

    initSubStorageLocationIdToNameCorr(state, data) {
      state.subStorageLocationIdToNameCorr = data
    },
    initSubStorageLocationIdToGateEmpIdCorr(state, data) {
      state.subStorageLocationIdToGateEmpIdCorr = data
    },
    initEmpIdToStorageLocationIdsForGateCorr(state, data) {
      state.empIdToStorageLocationIdsForGateCorr = data
    },

  },
})
