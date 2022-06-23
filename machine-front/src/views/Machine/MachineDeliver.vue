<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toEnterStorage">机器转交单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">机器转交单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">机器转交单(
          {{ $store.state.deliverIntentionCorr[deliverIntentionId] }} )详情
        </el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 2">添加机器转交单</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <MachineDeliverShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></MachineDeliverShow>
    <MachineDeliverDetail v-else-if="componentFlag === 1"
                          :receiptDetailNumber="receiptDetailNumber" :isEdit="isEdit"
                          :operate-emp-id="operateEmpId"
                          :deliver-intention-id="deliverIntentionId"></MachineDeliverDetail>
    <MachineDeliverAdd v-else-if="componentFlag === 2"></MachineDeliverAdd>
  </div>
</template>

<script>
import MachineDeliverAdd from "../../components/Machine/MachineDeliver/MachineDeliverAdd.vue";
import MachineDeliverDetail from "../../components/Machine/MachineDeliver/MachineDeliverDetail.vue";
import MachineDeliverShow from "../../components/Machine/MachineDeliver/MachineDeliverShow.vue";

export default {
  name: "机器转交",
  data() {
    return {
      componentFlag: 0,
      receiptDetailNumber: null,
      isEdit: 0,
      operateEmpId: null,
      deliverIntentionId: null,
    }
  },
  methods: {
    ChangeComponentsFlag(componentFlag, receiptDetailNumber, isEdit, operateEmpId, deliverIntentionId) {
      this.componentFlag = componentFlag
      this.receiptDetailNumber = receiptDetailNumber
      this.isEdit = isEdit
      this.operateEmpId = operateEmpId;
      this.deliverIntentionId = deliverIntentionId
    },
    toEnterStorage() {
      this.componentFlag = 0
    }
  },
  components: {
    MachineDeliverDetail,
    MachineDeliverAdd,
    MachineDeliverShow
  }
}
</script>

<style scoped>

</style>
