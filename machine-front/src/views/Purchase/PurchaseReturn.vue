<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toPurchaseOrder">退货单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">退货单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">退货单详情</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 2">添加退货单</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <PurchaseReturnShow @func="changeComponentFlag" v-if="componentFlag === 0"></PurchaseReturnShow>
    <PurchaseReturnDetail v-else-if="componentFlag === 1"
                          :receiptDetailNumber="receiptDetailNumber" :isRelease="isRelease"></PurchaseReturnDetail>
    <PurchaseReturnAdd v-else-if="componentFlag === 2"></PurchaseReturnAdd>
  </div>
</template>

<script>
import PurchaseReturnShow from "../../components/Purchase/purchaseReturn/PurchaseReturnShow.vue";
import PurchaseReturnAdd from "../../components/Purchase/purchaseReturn/PurchaseReturnAdd.vue";
import PurchaseReturnDetail from "../../components/Purchase/purchaseReturn/PurchaseReturnDetail.vue";


export default {
  name: "采购退货",
  data() {
    return {
      componentFlag: 0,
      receiptDetailNumber: undefined,
      isRelease: 0,
    }
  },
  methods: {
    changeComponentFlag(flag, receiptDetailNumber, isRelease) {
      this.componentFlag = flag
      this.receiptDetailNumber = receiptDetailNumber
      this.isRelease = isRelease
    },
    toPurchaseOrder() {
      this.componentFlag = 0
    }
  },
  components: {
    PurchaseReturnShow,
    PurchaseReturnAdd,
    PurchaseReturnDetail
  }
}
</script>

<style scoped>

</style>