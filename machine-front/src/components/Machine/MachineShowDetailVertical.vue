<template>
  <div>
    <el-dialog
        :modal-append-to-body=false
        title="机器信息"
        :visible.sync="showDetail.value"
        :before-close="close"
        width="80%">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="机器属性信息" name="machineProperty">
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
                <span v-if="scope.row.key === 'categoryId'">{{
                    $store.state.machineCategoryCorr[scope.row.value]
                  }}</span>
                <span v-else-if="scope.row.key === 'brandId'">{{
                    $store.state.machineBrandCorr[scope.row.value]
                  }}</span>
                <!--   有关状态             -->
                <span v-else-if="scope.row.key === 'statusId'">{{
                    $store.state.machineStatusCorr[scope.row.value]
                  }}</span>
                <!--   有关库位             -->
                <span v-else-if="scope.row.key === 'storageLocationId'">{{
                    $store.state.subStorageLocationIdToNameCorr[scope.row.value]
                  }}</span>
                <!--   有关员工             -->
                <span v-else-if="['operateEmpId', 'purchaseEmpId', 'enterStorageEmpId'].indexOf(scope.row.key) !== -1">{{
                    $store.state.employeeNameCorr[scope.row.value]
                  }}</span>
                <!--   有关渠道             -->
                <span v-else-if="['purchaseChannelId', 'saleChannelId'].indexOf(scope.row.key) !== -1">{{
                    $store.state.machineChannelCorr[scope.row.value]
                  }}</span>
                <!--   有关单号             -->
                <span
                    v-else-if="['purchaseOrderId', 'enterStorageReceiptId', 'purchaseReturnReceiptId', 'marketOrderId', 'marketReturnReceiptId', 'marketReturnEnterStorageReceiptId', 'upShelfEnterStorageId', 'deliverReceiptId'].indexOf(scope.row.key) !== -1">{{
                    scope.row.value === 0 ? "" : scope.row.value
                  }}</span>
                <!--  有关机器描述              -->
                <span v-else-if="['qualityDesc', 'featureDesc'].indexOf(scope.row.key) !== -1">
                        <el-tag
                            v-for="item in (scope.row.value=== null ? '' : scope.row.value.split(','))"
                            :key="item" v-if="item !== ''">
                        {{ $store.state.machineIdToDesc[item] }}
                        </el-tag>
                </span>
                <span v-else-if="['needFix'].indexOf(scope.row.key) !== -1">
                        <el-tag type="warning"
                                v-for="item in (scope.row.value=== null ? '' : scope.row.value.split(','))"
                                :key="item" v-if="item !== ''">
                        {{ $store.state.machineIdToDesc[item] }}
                        </el-tag>
                </span>
                <span v-else-if="['fixed'].indexOf(scope.row.key) !== -1">
                        <el-tag type="success"
                                v-for="item in (scope.row.value=== null ? '' : scope.row.value.split(','))"
                                :key="item" v-if="item !== ''">
                        {{ $store.state.machineIdToDesc[item] }}
                        </el-tag>
                </span>
                <span v-else-if="['notFixed', 'fixToBad'].indexOf(scope.row.key) !== -1">
                        <el-tag type="danger"
                                v-for="item in (scope.row.value=== null ? '' : scope.row.value.split(','))"
                                :key="item" v-if="item !== ''">
                        {{ $store.state.machineIdToDesc[item] }}
                        </el-tag>
                </span>
                <span v-else>{{ scope.row.value }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="机器追踪" name="machineTrace">
          <el-table
              :data="machineTrace"
              style="width: 100%">

            <el-table-column
                prop="statusId"
                label="机器状态"
                width="180">
              <template #default="scope">
                <span>{{ $store.state.machineStatusCorr[scope.row.statusId] }}</span>
              </template>
            </el-table-column>

            <el-table-column
                prop="deliverStatusId"
                label="转交状态"
                width="180">
              <template #default="scope">
                <span v-if="scope.row.deliverStatusId=== 0">待转交</span>
                <span v-else-if="scope.row.deliverStatusId === 1">转交中</span>
                <span v-else-if="scope.row.deliverStatusId === 2">已接收</span>
              </template>
            </el-table-column>

            <el-table-column
                prop="deliverIntentionId"
                label="转交类型"
                width="180">
              <template #default="scope">
                {{ $store.state.deliverIntentionCorr[scope.row.deliverIntentionId] }}
              </template>
            </el-table-column>

            <el-table-column
                prop="receiptId"
                label="单据号"
                width="180">
            </el-table-column>

            <el-table-column
                prop="storageLocationId"
                label="库位"
                width="180">
              <template #default="scope">
                {{ $store.state.subStorageLocationIdToNameCorr[scope.row.storageLocationId] }}
              </template>
            </el-table-column>

            <el-table-column
                prop="isUpShelf"
                label="是否可以上架"
                width="180">
              <template #default="scope">
                <span v-if="scope.row.isUpShelf=== 0">可以</span>
                <span v-else-if="scope.row.isUpShelf === 1">不可用</span>
              </template>
            </el-table-column>

            <el-table-column
                prop="isRecall"
                label="召回"
                width="180">
              <template #default="scope">
                <span v-if="scope.row.isRecall === 1">召回</span>
              </template>
            </el-table-column>

            <el-table-column
                prop="time"
                label="时间"
                width="180">
            </el-table-column>

            <el-table-column
                prop="operateEmpId"
                label="操作人"
                width="180">
              <template #default="scope">
                <span>{{ $store.state.employeeNameCorr[scope.row.operateEmpId] }}</span>
              </template>
            </el-table-column>

            <el-table-column
                prop="comment"
                label="备注"
                width="180">
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
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
        "number": "物品编码",
        "imei": "imei号",
        "paijiBarcode": "拍机堂条码",
        "bagNumber": "袋子编码",
        "categoryId": "品类",
        "brandId": "品牌",
        "type": "型号",
        "sku": "sku",
        "rank": "等级",
        "describe": "机器描述",
        "qualityInspector": "质检方",
        "statusId": "状态",
        "purchaseChannelId": "采购渠道",
        "purchaseTime": "采购时间",
        "storageLocationId": "库位",
        "purchaseOrderId": "采购订单",
        "enterStorageReceiptId": "入库订单",
        "purchaseReturnReceiptId": "采购退货订单",
        "marketOrderId": "销售订单",
        "marketReturnReceiptId": "销售退货订单",
        "marketReturnEnterStorageReceiptId": "销退入库单号",
        "upShelfEnterStorageId": "上架入库单号",
        "deliverReceiptId": "转交单号",
        "onePrice": "一口价",
        "bidPrice": "最高价",
        "purchasePrice": "采购价",
        "goodPrice": "好的价格",
        "salePrice": "出库价格",
        "fixPrice": "维修价格",
        "saleChannelId": "销售渠道",
        "operateEmpId": "操作人",
        "operateDate": "操作日期",
        "purchaseEmpId": "采购人员",
        "enterStorageEmpId": "入库人员",
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
      },
      activeName: "machineProperty"
    }
  },
  props: ['tableName', 'machine', 'machineTrace', 'showDetail'],
  mounted() {
    this.initMachineTable()
  },
  methods: {
    //initTableField() {
    //  Object.values(this.$store.state.machineTableField[this.tableName]).forEach(item => {
    //    if (item['showWhenAdd'] === 0) {
    //      this.tableField.push(item)
    //    }
    //  })
    //},
    initMachineTable() {
      this.machineTable = []
      //Object.keys(this.machine).forEach(item => {
      //  console.log(this.machine)
      //  if (['previousStatusId'].indexOf(item) === -1) {
      //    this.machineTable.push({'key': item, 'value': this.machine[item]})
      //  }
      //})
      Object.keys(this.machineTableCorr).forEach(item => {
        this.machineTable.push({'key': item, 'value': this.machine[item]})
      })
    },
    close() {
      this.showDetail.value = false
      this.machine = {}
      this.machineTrace = {}
    }
  }
}
</script>

<style scoped>

</style>