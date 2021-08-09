<template>
  <div>
    <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <AddMachineByScan v-if="isRelease === 0 && $store.state.userId === operateEmpId" table-name="purchaseReturn"
                      operate-name="addMachineToPurchaseReturn"
                      :machines="machines"
                      :receiptId="receiptDetailNumber" @initShow="initMachines"></AddMachineByScan>

    <MachineShowDetail ref="child" :machines="machines" :paging="true"
                       :tableName="'purchaseReturn'" :is-release="isRelease" :extra-not-show="[]"
                       :receipt-id="receiptDetailNumber"></MachineShowDetail>

  </div>
</template>

<script>
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import initMachineCorr from "../../../utils/machineCorr";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import MachineSearch from "../../Machine/MachineSearch.vue";

export default {
  name: "PurchaseReturnDetail",
  data() {
    return {
      searchMachine: {},
      machines: [],
    }
  },
  props: ['receiptDetailNumber', 'isRelease', 'operateEmpId'],
  mounted() {
    this.initMachines()
  },
  methods: {
    initMachines() {
      this.searchMachine.purchaseReturnReceiptId = this.receiptDetailNumber;
      this.$refs.child.initMachinesByApi(this.searchMachine)
    },
    cancelAdvSearch() {
      this.searchMachine = {}
      this.initMachines()
    },
  },
  components: {
    MachineShowDetail,
    AddMachineByScan,
    MachineSearch
  }

}
</script>

<style scoped>

</style>