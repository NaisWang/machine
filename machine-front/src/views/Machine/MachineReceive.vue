<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toEnterStorage">机器待接收单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">机器待接收单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">机器待接收单详情</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <MachineReceiveShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></MachineReceiveShow>
    <machine-receive-detail v-else-if="componentFlag === 1"
                            :receiptDetailNumber="receiptDetailNumber"
                            :deliver-intention-id="deliverIntentionId"
                            :storage-location-id="storageLocationId"
                            :enter-storage-receipt-id="enterStorageReceiptId"></machine-receive-detail>
  </div>
</template>

<script>
import MachineReceiveShow from "../../components/Machine/MachineReceive/MachineReceiveShow.vue";
import MachineReceiveDetail from "../../components/Machine/MachineReceive/MachineReceiveDetail.vue";

export default {
  name: "机器接收",
  data() {
    return {
      componentFlag: 0,
      receiptDetailNumber: null,
      deliverIntentionId: null,
      enterStorageReceiptId: null,
      storageLocationId: null,
      isEdit: 0
    }
  },
  methods: {
    ChangeComponentsFlag(componentFlag, receiptDetailNumber, deliverIntentionId, enterStorageReceiptId, storageLocationId) {
      this.componentFlag = componentFlag
      this.receiptDetailNumber = receiptDetailNumber
      this.deliverIntentionId = deliverIntentionId
      this.enterStorageReceiptId = enterStorageReceiptId
      this.storageLocationId = storageLocationId;
    },
    toEnterStorage() {
      this.componentFlag = 0
    }
  },
  components: {
    MachineReceiveShow,
    MachineReceiveDetail
  }
}
</script>

<style scoped>

</style>
