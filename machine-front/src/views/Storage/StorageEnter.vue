<template>
  <div>
    <template style="margin-bottom: 15px">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item><a @click="toEnterStorage">入库单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">入库单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">入库单详情</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 2">添加入库单</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <StorageEnterShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></StorageEnterShow>
    <StorageEnterDetail v-else-if="componentFlag === 1"
                        :storageEnterOrderNumber="storageEnterOrderNumber" :is-release="isRelease"></StorageEnterDetail>
    <!--    <StorageEnterAdd v-else-if="componentFlag === 2" @func="ChangeComponentsFlag"-->
    <!--                     :purchaseOrderNumber="purchaseOrderNumber"></StorageEnterAdd>-->
  </div>
</template>

<script>
import StorageEnterShow from "../../components/Storage/storageEnter/StorageEnterShow.vue";
import StorageEnterDetail from "../../components/Storage/storageEnter/StorageEnterDetail.vue";
import StorageEnterAdd from "../../components/Storage/storageEnter/StorageEnterAdd.vue";

export default {
  name: "库存入库",
  data() {
    return {
      componentFlag: 0,
      storageEnterOrderNumber: null,
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
    ChangeComponentsFlag(componentFlag, storageEnterOrderNumber, isRelease) {
      this.componentFlag = componentFlag
      this.storageEnterOrderNumber = storageEnterOrderNumber
      //this.purchaseOrderNumber = purchaseOrderNumber
      this.isRelease = isRelease
    },
    toEnterStorage() {
      this.componentFlag = 0
    }
  },
  components: {
    StorageEnterShow,
    StorageEnterDetail,
    StorageEnterAdd
  }
}
</script>

<style scoped>

</style>