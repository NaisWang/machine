<template>
  <div>

    <el-card class="box-card" v-for="(item, index) in Object.keys(finance)" :key="index">
      <div>{{ finance[item] + '元' }}</div>
      <div>{{ priceKeyToName[item] }}</div>
    </el-card>

  </div>
</template>

<script>

import {getFinance} from "../../api/financeApi";

export default {
  name: "财务账单",
  data() {
    return {
      finance: {},
      machines: [],
      dialogVisible: false,
      priceKeyToName: {
        'purchasePrice': "采购金额",
        'purchaseReturnPrice': '采购退货金额',
        'sellPrice': '销售金额',
        'refundPrice': '销售退款金额',
        'fixPrice': '维修金额'
      }
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initMyStatistics();
    },
    initMyStatistics() {
      getFinance().then(resp => {
        if (resp.data.code === 200) {
          this.finance = resp.data.obj
        }
      })
    },
    detail(search) {

    }
  }
}
</script>

<style scoped>

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 10px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.box-card {
  display: inline-block;
  width: 24%;
}
</style>
