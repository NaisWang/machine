<template>
  <div>
    <MachineSearch @searchMachines="initDeliverMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>


    <div v-if="[2,7,8].indexOf(deliverIntentionId) !== -1">
      <span>入库单号: </span>
      <el-tag>{{ enterStorageReceiptId }}</el-tag>

      <span>库位: </span>
      <el-tag>{{ $store.state.subStorageLocationIdToNameCorr[storageLocationId] }}</el-tag>

      <el-button @click="dialogVisible = true">接收入库</el-button>
    </div>

    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>


    <div>

      <el-table
          :data="machines"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="machineNumber"
            label="物品编码"
            width="170">
        </el-table-column>

        <el-table-column
            prop="status"
            label="接收状态"
            width="170">
          <template #default="scope">
            <span v-if="scope.row.status === 0">待转交</span>
            <span v-else-if="scope.row.status === 1">转交中</span>
            <span v-else>已接收</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="isComplete"
            label="是否完成指标"
            width="170">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.isComplete === 0">没有</el-tag>
            <el-tag v-else-if="scope.row.isComplete === 1">完成</el-tag>
          </template>
        </el-table-column>

        <el-table-column
            prop="receiveEmpId"
            label="接收人"
            width="170">
          <template #default="scope">
            <span>{{ $store.state.employeeNameCorr[scope.row.receiveEmpId] }}</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="comment"
            label="备注"
            width="120">
          <template #default="scope">
            <el-popover
                placement="top-start"
                :width="200"
                trigger="hover"
                :content="scope.row.comment">
              <template #reference>
                <el-button style="width: 80px; text-overflow: ellipsis; overflow: hidden;" v-show="scope.row.comment">
                  {{
                    scope.row.comment
                  }}
                </el-button>
                <el-button style="width: 80px" v-show="!scope.row.comment">无</el-button>
              </template>
              <el-input
                  type="textarea"
                  autosize
                  @change="initUpdateMachine(scope.row, 'describe')"
                  :disabled="!isEdit"
                  :value="scope.row.comment"
                  v-model="scope.row.comment">
              </el-input>
            </el-popover>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button
                size="mini"
                type="success"
                @click="detail(scope.row)">详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div style="display: flex; justify-content: flex-end">
      <el-pagination
          background
          :current-page="currentPage"
          layout="sizes, prev, pager, next, ->, total"
          @current-change="currentChange"
          @size-change="sizeChange"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog
        title="提示"
        :visible.sync="dialogVisible"
        width="80%"
        :before-close="handleClose">
      <el-input style="width:  89%" clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
                v-model="numberInput"></el-input>
      <el-select style="width: 9%" v-model="searchMethod" placeholder="请选择">
        <el-option
            v-for="item in searchOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>

      <el-form>
        <el-form-item label="库位" v-if="storageLocationId === null">
          <el-select clearable v-model="editStorageLocationId"
                     size="mini" placeholder="库位">
            <el-option
                v-for="id in $store.state.empIdToStorageLocationIdsForGateCorr[$store.state.userId]"
                :label="$store.state.subStorageLocationIdToNameCorr[id]"
                :value="id + ''"
                :key="id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <MachineShowDetail :machines="scanMachine" :paging="false" table-name="machineDeliver"
                         table-operate="add" extra-not-show="[]"></MachineShowDetail>

      <span slot="footer" class="dialog-footer">
              <el-button @click="dialogVisible = false">取 消</el-button>
              <el-button type="primary" @click="submit">提 交</el-button>
            </span>
    </el-dialog>

    <MachineShowDetailVertical v-if="showDetail.value" :table-name="tableName" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :show-detail="showDetail"></MachineShowDetailVertical>
  </div>
</template>

<script>
import initMachineCorr from "../../../utils/machineCorr";
import {getDeliverMachine} from "../../../api/deliverMachineApi";
import {getMachine} from "../../../api/machineApi";
import {addDeliverMachine} from "../../../api/deliverMachineApi";
import {deleteDeliverMachine} from "../../../api/deliverMachineApi";
import {getMachineTrace} from "../../../api/machineTraceApi";
import MachineShowDetailVertical from "../MachineShowDetailVertical.vue";
import MachineSearch from "../MachineSearch.vue";
import MachineShowDetail from "../MachineShowDetail.vue";
import {createAddReleaseEnterStorageReceipt} from "../../../api/enterStorageApi";
import {createAddReleaseMarketReturnEnterStorageReceipt} from "../../../api/marketReturnEnterStorageApi";
import {createAddReleaseUpShelfEnterStorageReceipt} from "../../../api/upShelfEnterStorageApi";

export default {
  name: "MachineReceiveDetail",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      searchMachine: {},
      machines: [],
      currentPage: 1,
      size: 10,
      total: null,
      dialogVisible: false,
      numberInput: "",
      searchOptions: [{
        value: 'number',
        label: '物品编码'
      }, {
        value: 'imei',
        label: 'imei号'
      }, {
        value: 'paijiBarCode',
        label: '拍机堂条码'
      }],
      scanMachine: [],
      searchMethod: "number",
      editStorageLocationId: null
    }
  },
  props: ['receiptDetailNumber', 'deliverIntentionId', 'enterStorageReceiptId', 'storageLocationId'],
  components: {
    MachineShowDetailVertical,
    MachineSearch,
    MachineShowDetail
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initDeliverMachines();
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initDeliverMachines() {
      this.searchMachine.deliverReceiptId = this.receiptDetailNumber;
      getDeliverMachine(this.currentPage, this.size, this.searchMachine).then(resp => {
        if (resp.data.obj) {
          this.totol = resp.data.obj.total;
          this.machines = resp.data.obj.data;
        }
      })
    },
    cancelAdvSearch() {
      this.searchMachine = {}
      this.searchMachine.purchaseOrderId = this.receiptDetailNumber;
      this.initDeliverMachines();
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initDeliverMachines();
    },
    sizeChange(size) {
      this.size = size;
      this.initDeliverMachines();
    },
    detail(row) {
      getMachine(1, 10, {"number": row.machineNumber}).then(resp => {
        this.showDetailMachine = JSON.parse(JSON.stringify(resp.data.obj.data[0]));
        getMachineTrace({"number": row.machineNumber}).then(resp => {
          this.showMachineTrace = JSON.parse(JSON.stringify(resp.data.obj))
          this.showDetail.value = true
        })
      })
    },
    addMachineByScan() {
      if (this.numberInput !== "") {
        let search = {}
        if (this.searchMethod === 'number') {
          search['number'] = this.numberInput
        } else if (this.searchMethod === 'imei') {
          search['imei'] = this.numberInput
        } else if (this.searchMethod === 'paijiBarCode') {
          search['paijiBarcode'] = this.numberInput
        }
        getMachine(1, 10, search).then(resp => {
          if (resp.data.obj) {
            console.log(resp.data.obj)
            if (resp.data.obj.total === 0) {
              this.$message.error("没有该机器")
              return
            }
            if (resp.data.obj.total !== 1) {
              this.$message.error("该机器在库存中超过2个")
              return
            }
            if (this.judgeIsAdd(resp.data.obj.data[0].number) === -1) {
              this.$message.error("该机器已添加到该单据中了")
              return
            }

            let machine = resp.data.obj.data[0]

            if (machine.deliverReceiptId === 0 || machine.needCompleteDeliverReceiptId !== this.receiptDetailNumber) {
              this.$message.error("该机器不能添加")
              return
            }

            this.scanMachine.push(machine);
            this.numberInput = ""

          }
        })
      }
    },
    //判断编号为number的机器是否已经添加到单据中
    judgeIsAdd(number) {
      let flag = 1
      this.scanMachine.forEach(item => {
        console.log("itemNum" + item.number)
        console.log("num" + number)
        if (item.number === number) {
          flag = -1
        }
      })
      this.machines.forEach(item => {
        if (item.number === number) {
          flag = -1
        }
      })
      return flag
    },
    submit() {
      if (this.storageLocationId === null && this.editStorageLocationId === null) {
        this.$message.error("请选择一个库位！！！");
        return
      }
      let targetStorageLocationId = this.storageLocationId === null ? this.editStorageLocationId : this.storageLocationId;

      let numbers = []
      this.scanMachine.forEach(machine => {
        numbers.push(machine.number);
      })

      console.log(this.deliverIntentionId);

      if (this.deliverIntentionId === 2) {
        createAddReleaseEnterStorageReceipt(numbers, this.enterStorageReceiptId, targetStorageLocationId, this.receiptDetailNumber).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("操作成功");
            this.enterStorageReceiptId = resp.data.obj;
            this.storageLocationId = targetStorageLocationId
            this.scanMachine = []
            this.dialogVisible = false
            this.refresh()
          }
        })
      } else if (this.deliverIntentionId === 7) {
        createAddReleaseUpShelfEnterStorageReceipt(numbers, this.enterStorageReceiptId, targetStorageLocationId, this.receiptDetailNumber).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("操作成功");
            this.enterStorageReceiptId = resp.data.obj;
            this.storageLocationId = targetStorageLocationId
            this.scanMachine = []
            this.dialogVisible = false
            this.refresh()
          }
        })
      } else if (this.deliverIntentionId === 8) {
        createAddReleaseMarketReturnEnterStorageReceipt(numbers, this.enterStorageReceiptId, targetStorageLocationId, this.receiptDetailNumber).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("操作成功");
            this.enterStorageReceiptId = resp.data.obj;
            this.storageLocationId = targetStorageLocationId
            this.scanMachine = []
            this.dialogVisible = false
            this.refresh()
          }
        })
      }
    },
  },
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}

.el-table--small td, .el-table--small td {
  padding: 0 !important;
}
</style>
