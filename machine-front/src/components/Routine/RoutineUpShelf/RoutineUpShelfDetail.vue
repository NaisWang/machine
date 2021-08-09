<template>
  <div>
    <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <MachineShowDetail ref="child" :machines="machines" :paging="true" :tableName="'machineShelf'" :receipt-id="storageEnterOrderNumber"></MachineShowDetail>

  </div>
</template>

<script>
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import initMachineCorr from "../../../utils/machineCorr";
import MachineSearch from "../../Machine/MachineSearch.vue";

export default {
  name: "RoutineShelfDetail",
  data() {
    return {
      searchMachine: {},
      machines: [],
    }
  },
  props: ['storageEnterOrderNumber'],
  mounted() {
    this.initMachines();
  },
  methods: {
    initMachines() {
      this.searchMachine.enterStorageReceiptId = this.storageEnterOrderNumber;
      this.$refs.child.initMachinesByApi(this.searchMachine)
    },
    cancelAdvSearch() {
      this.searchMachine = {}
      this.initMachines()
    },
  },
  components: {
    MachineShowDetail,
    MachineSearch
  }
}
</script>

<style scoped>

</style>
