<template>
  <div>
    <template style="margin-bottom: 15px">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><a @click="toEnterStorage">销退入库单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">销退入库单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">销退入库单详情</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 2">添加销退入库单</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <StorageMarketReturnShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></StorageMarketReturnShow>
    <StorageMarketReturnDetail v-else-if="componentFlag === 1"
                               :receiptId="receiptId" :is-release="isRelease"
                               :operate-emp-id="operateEmpId"></StorageMarketReturnDetail>
    <!--    <StorageEnterAdd v-else-if="componentFlag === 2" @func="ChangeComponentsFlag"-->
    <!--                     :purchaseOrderNumber="purchaseOrderNumber"></StorageEnterAdd>-->
  </div>
</template>

<script>
import StorageMarketReturnDetail from "../../components/Storage/StorageMarketReturn/StorageMarketReturnDetail.vue";
import StorageMarketReturnShow from "../../components/Storage/StorageMarketReturn/StorageMarketReturnShow.vue";

export default {
  name: "销退入库",
  data() {
    return {
      componentFlag: 0,
      receiptId: null,
      purchaseOrderNumber: null,
      isRelease: 0,
      operateEmpId: null
    }
  },
  activated() {
    if (Object.keys(this.$route.query).indexOf("oneKeyEnterStoragePurchaseOrder") !== -1) {
      console.log("aaa" + this.$route.query.oneKeyEnterStoragePurchaseOrder)
      this.ChangeComponentsFlag(2, null, this.$route.query.oneKeyEnterStoragePurchaseOrder)
    }
  },
  //mounted() {
  //  if (Object.keys(this.$route.query).indexOf("oneKeyEnterStoragePurchaseOrder")) {
  //    console.log("aaa" + this.$route.query.oneKeyEnterStoragePurchaseOrder)
  //    this.changeComponentFlag(2, null, this.$route.query.oneKeyEnterStoragePurchaseOrder)
  //  }
  //},
  methods: {
    ChangeComponentsFlag(componentFlag, receiptId, isRelease, operateEmpId) {
      this.componentFlag = componentFlag
      this.receiptId = receiptId
      //this.purchaseOrderNumber = purchaseOrderNumber
      this.isRelease = isRelease
      this.operateEmpId = operateEmpId
    },
    toEnterStorage() {
      this.componentFlag = 0
    }
  },
  components: {
    StorageMarketReturnDetail,
    StorageMarketReturnShow
  }
}
</script>

<style scoped>

</style>
