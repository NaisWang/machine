<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toEnterStorage">销售退货单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">销售退货单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">销售退货单详情</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 2">添加销售退货单</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <MarketReturnShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></MarketReturnShow>
    <MarketReturnDetail v-else-if="componentFlag === 1"
                        :receiptDetailNumber="receiptDetailNumber"></MarketReturnDetail>
    <MarketReturnAdd v-else-if="componentFlag === 2"></MarketReturnAdd>
  </div>
</template>

<script>
import MarketReturnDetail from "../../components/Market/marketReturn/MarketReturnDetail.vue";
import MarketReturnAdd from "../../components/Market/marketReturn/MarketReturnAdd.vue";
import MarketReturnShow from "../../components/Market/marketReturn/MarketReturnShow.vue";

export default {
  name: "销售退货",
  data() {
    return {
      componentFlag: 0,
      receiptDetailNumber: null
    }
  },
  methods: {
    ChangeComponentsFlag(componentFlag, receiptDetailNumber) {
      this.componentFlag = componentFlag
      this.receiptDetailNumber = receiptDetailNumber
    },
    toEnterStorage() {
      this.componentFlag = 0
    }
  },
  components: {
    MarketReturnAdd,
    MarketReturnDetail,
    MarketReturnShow
  }
}
</script>

<style scoped>

</style>