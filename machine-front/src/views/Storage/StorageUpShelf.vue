<template>
  <div>
    <template style="margin-bottom: 15px">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><a @click="toEnterStorage">上架入库单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">上架入库单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">上架入库单详情</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <StorageUpShelfShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></StorageUpShelfShow>
    <StorageUpShelfDetail v-else-if="componentFlag === 1"
                          :receiptId="receiptId" :is-release="isRelease"></StorageUpShelfDetail>
  </div>
</template>

<script>
import StorageUpShelfShow from "../../components/Storage/storageUpShelf/StorageUpShelfShow.vue";
import StorageUpShelfDetail from "../../components/Storage/storageUpShelf/StorageUpShelfDetail.vue";

export default {
  name: "上架入库",
  data() {
    return {
      componentFlag: 0,
      receiptId: null,
      purchaseOrderNumber: null,
      isRelease: 0,
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
    ChangeComponentsFlag(componentFlag, receiptId, isRelease) {
      this.componentFlag = componentFlag
      this.receiptId = receiptId
      //this.purchaseOrderNumber = purchaseOrderNumber
      this.isRelease = isRelease
    },
    toEnterStorage() {
      this.componentFlag = 0
    }
  },
  components: {
    StorageUpShelfDetail,
    StorageUpShelfShow
  }
}
</script>

<style scoped>

</style>
