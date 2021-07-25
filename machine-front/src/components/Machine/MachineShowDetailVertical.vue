<template>
  <div>
    <el-table
        :data="machineTable"
        style="width: 100%">

      <el-table-column
          prop="key"
          label="属性"
          width="180">
        <template #default="scope">
          <span>{{ machineTableCorr[scope.row.key] }}</span>
        </template>
      </el-table-column>

      <el-table-column
          prop="value"
          label="描述">
        <template #default="scope">
          <span v-if="scope.row.key === 'categoryId'">{{ $store.state.machineCategoryCorr[scope.row.value] }}</span>
          <span v-else-if="scope.row.key === 'brandId'">{{ $store.state.machineBrandCorr[scope.row.value] }}</span>
          <span v-else-if="scope.row.key === 'statusId'">{{ $store.state.machineStatusCorr[scope.row.value] }}</span>
          <span v-else-if="scope.row.key === 'saleChannelId'">{{
              $store.state.machineChannelCorr[scope.row.value]
            }}</span>
          <span v-else-if="['operateEmpId', 'purchaseEmpId', 'enterStorageEmpId'].indexOf(scope.row.key) !== -1">{{
              $store.state.employeeNameCorr[scope.row.value]
            }}</span>
          <span v-else-if="['qualityDesc', 'featureDesc'].indexOf(scope.row.key) !== -1">
                        <span
                            v-for="item in (scope.row.value=== null ? '' : scope.row.value.split(','))"
                            :key="item" v-if="item !== ''"
                            style="border: 1px solid black; margin: 1px">
                        {{ $store.state.machineIdToDesc[item] }}
                        </span>
                    </span>
          <span v-else>{{ scope.row.value }}</span>
        </template>
      </el-table-column>

    </el-table>
  </div>
</template>

<script>
export default {
  name: "MachineShowDetailVertical",
  data() {
    return {
      tableField: [],
      machineTable: [],
      machineTableCorr: {
        "id": "id",
        "purchaseOrderId": "采购订单",
        "enterStorageReceiptId": "入库订单",
        "purchaseReturnReceiptId": "采购退货订单",
        "marketOrderId": "销售订单",
        "marketReturnReceiptId": "销售退货订单",
        "number": "物品编码",
        "imei": "imei号",
        "paijiBarcode": "拍机堂条码",
        "categoryId": "品类",
        "brandId": "品牌",
        "type": "型号",
        "sku": "sku",
        "rank": "等级",
        "onePrice": "一口价",
        "bidPrice": "最高价",
        "purchasePrice": "采购价",
        "goodPrice": "好的价格",
        "salePrice": "出库价格",
        "fixPrice": "维修价格",
        "describe": "机器描述",
        "stockLocation": "库位",
        "statusId": "状态",
        "saleChannelId": "销售渠道",
        "operateEmpId": "操作人",
        "operateDate": "操作日期",
        "purchaseEmpId": "采购人员",
        "qualityInspector": "质检方",
        "enterStorageEmpId": "入库人员",
        "biddingDate": "采购日期",
        "enterStorageDate": "入库日期",
        "outStorageDate": "出库日期",
        "outStorageBatch": "出库批次",
        "qualityDesc": "成色检测描述",
        "featureDesc": "功能检测描述",
        "rankDesc": "等级检测描述",
        "needFix": "需要维修项",
        "fixed": "已维修好的项",
        "notFixed": "没有维修好的项",
        "fixToBad": "修坏的项",
        "comment": "备注",
        "deliver_machine_id": "转交单号",
        "bagNumber": "袋子编码"
      }
    }
  },
  props: ['tableName', 'machine'],
  mounted() {
    this.initMachineTable()
  },
  methods: {
    initTableField() {
      Object.values(this.$store.state.machineTableField[this.tableName]).forEach(item => {
        if (item['showWhenAdd'] === 0) {
          this.tableField.push(item)
        }
      })
    },
    initMachineTable() {
      this.machineTable = []
      Object.keys(this.machine).forEach(item => {
        console.log(this.machine)
        this.machineTable.push({'key': item, 'value': this.machine[item]})
      })
    }
  }
}
</script>

<style scoped>

</style>