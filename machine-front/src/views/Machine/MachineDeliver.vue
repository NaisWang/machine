<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toEnterStorage">机器转交单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">机器转交单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">机器转交单详情</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 2">添加机器转交单</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <MachineDeliverShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></MachineDeliverShow>
    <MachineDeliverDetail v-else-if="componentFlag === 1"
                          :receiptDetailNumber="receiptDetailNumber" :isEdit="isEdit"></MachineDeliverDetail>
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
      isEdit:0
    }
  },
  methods: {
    ChangeComponentsFlag(componentFlag, receiptDetailNumber, isEdit) {
      this.componentFlag = componentFlag
      this.receiptDetailNumber = receiptDetailNumber
      this.isEdit = isEdit
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
