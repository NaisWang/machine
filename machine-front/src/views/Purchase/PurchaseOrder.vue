<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toPurchaseOrder">采购单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">采购单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">采购单详情</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 2">添加采购单</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <PurchaseOrderReceiptShow v-if="componentFlag === 0"
                              @changeComponentFlag="changeComponentFlag"></PurchaseOrderReceiptShow>
    <PurchaseOrderReceiptDetail v-else-if="componentFlag === 1"
                                @changeComponentFlag="changeComponentFlag"
                                :receiptDetailNumber="receiptDetailNumber"
                                :isRelease="isRelease"></PurchaseOrderReceiptDetail>
    <PurchaseOrderReceiptAdd v-else-if="componentFlag === 2"
                             @changeComponentFlag="changeComponentFlag"></PurchaseOrderReceiptAdd>
  </div>
</template>

<script>
import PurchaseOrderReceiptShow from "../../components/Purchase/purchaseOrder/PurchaseOrderReceiptShow.vue";
import PurchaseOrderReceiptAdd from "../../components/Purchase/purchaseOrder/PurchaseOrderReceiptAdd.vue";
import PurchaseOrderReceiptDetail from "../../components/Purchase/purchaseOrder/PurchaseOrderReceiptDetail.vue";

export default {
  name: "采购订单",
  data() {
    return {
      componentFlag: 0,
      receiptDetailNumber: undefined,
      isRelease: 0
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
    PurchaseOrderReceiptShow,
    PurchaseOrderReceiptAdd,
    PurchaseOrderReceiptDetail
  }
}
</script>

<style scoped>

</style>
