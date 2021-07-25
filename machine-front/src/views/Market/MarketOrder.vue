<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toEnterStorage">销售单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">销售单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">销售单详情</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 2">添加销售单</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <MarketOrderShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></MarketOrderShow>
    <MarketOrderDetail v-else-if="componentFlag === 1"
                       :receiptDetailNumber="receiptDetailNumber" :isRelease="isRelease"></MarketOrderDetail>
    <MarketOrderAdd v-else-if="componentFlag === 2"></MarketOrderAdd>
  </div>
</template>

<script>
import MarketOrderShow from "../../components/Market/marketOrder/MarketOrderShow.vue";
import MarketOrderDetail from "../../components/Market/marketOrder/MarketOrderDetail.vue";
import MarketOrderAdd from "../../components/Market/marketOrder/MarketOrderAdd.vue";

export default {
  name: "销售订单",
  data() {
    return {
      componentFlag: 0,
      receiptDetailNumber: null,
      isRelease: 0,
    }
  },
  methods: {
    ChangeComponentsFlag(componentFlag, receiptDetailNumber, isRelease) {
      this.componentFlag = componentFlag
      this.receiptDetailNumber = receiptDetailNumber
      this.isRelease = isRelease
    },
    toEnterStorage() {
      this.componentFlag = 0
    }
  },
  components: {
    MarketOrderAdd,
    MarketOrderDetail,
    MarketOrderShow
  }
}
</script>

<style scoped>

</style>