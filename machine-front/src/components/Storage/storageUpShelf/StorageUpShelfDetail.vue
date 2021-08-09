<template>
  <div>
    <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <AddMachineByScan v-if="isRelease === 0 && $store.state.userId === operateEmpId" table-name="upShelfEnterStorage"
                      operate-name="addMachineToUpShelfEnterStorage" :machines="machines"
                      :receiptId="receiptId" @initShow="initMachines"></AddMachineByScan>

    <MachineShowDetail ref="child" :machines="machines" :paging="true" :tableName="'upShelfEnterStorage'"
                       :is-release="isRelease" :extra-not-show="[]" :receipt-id="receiptId"></MachineShowDetail>
  </div>
</template>

<script>
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import initMachineCorr from "../../../utils/machineCorr";
import {addMachineToEnterStorageReceipt} from "../../../api/enterStorageApi";
import MachineShowDetailVertical from "../../Machine/MachineShowDetailVertical.vue";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import MachineSearch from "../../Machine/MachineSearch.vue";

export default {
  name: "StorageUpShelfDetail",
  data() {
    return {
      searchMachine: {},
      machines: [],
      dialogVisible: false,
      showMachineTable: false,
      numberInput: "",
      addMachineInfo: {}
    }
  },
  props: ['receiptId', 'isRelease', 'operateEmpId'],
  mounted() {
    this.initMachines()
  },
  methods: {
    initMachines() {
      this.searchMachine.upShelfEnterStorageId = this.receiptId;
      this.$refs.child.initMachinesByApi(this.searchMachine)
    },
    cancelAdvSearch() {
      this.searchMachine = {}
      this.initMachines()
    },
  },
  components: {
    AddMachineByScan,
    MachineShowDetail,
    MachineShowDetailVertical,
    MachineSearch
  }
}
</script>

<style scoped>

</style>
