<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toEnterStorage">送外修单</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">送外修单</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">送外修单详情</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <FixSendShow @func="ChangeComponentsFlag" v-if="componentFlag === 0"></FixSendShow>
    <FixSendDetail v-else-if="componentFlag === 1"
                   :receiptDetailNumber="receiptDetailNumber" :isRelease="isRelease"></FixSendDetail>
  </div>
</template>

<script>
import FixSendShow from "../../components/Fix/FixSend/FixSendShow.vue";
import FixSendDetail from "../../components/Fix/FixSend/FixSendDetail.vue";

export default {
  name: "销售退货",
  data() {
    return {
      componentFlag: 0,
      receiptDetailNumber: null,
      isRelease: 0
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
    FixSendShow,
    FixSendDetail
  }
}
</script>

<style scoped>

</style>
