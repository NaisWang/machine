<template>
  <div>
    <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <AddMachineByScan v-if="isRelease === 0 && $store.state.userId === operateEmpId" table-name="marketReturn"
                      operate-name="addMachineToMarketReturn"
                      :machines="machines"
                      :receiptId="receiptDetailNumber" @initShow="initMachines"></AddMachineByScan>

    <!--    <MachineShowDetail ref="child" :machines="machines" :paging="true"
                           :tableName="'marketReturn'" :is-release="isRelease" :extra-not-show="[]"
                           :receipt-id="receiptDetailNumber"></MachineShowDetail>
        -->

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
            prop="machineSku"
            label="SKU"
            width="170">
        </el-table-column>

        <el-table-column
            prop="refundPrice"
            label="退款金额"
            width="170">
        </el-table-column>

        <el-table-column
            prop="operateTime"
            label="操作时间"
            width="170">
        </el-table-column>

        <el-table-column
            prop="operateEmpId"
            label="操作人"
            width="170">
          <template #default="scope">
            {{ $store.state.employeeNameCorr[scope.row.operateEmpId] }}
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

            <el-button
                size="mini"
                type="success"
                @click="editDialogVisible = true; nowEditMachine = scope.row">修改
            </el-button>

            <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.row.machineId)">删除
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

    <MachineShowDetailVertical v-if="showDetail.value" :table-name="tableName" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :machine-detection="showMachineDetection"
                               :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import initMachineCorr from "../../../utils/machineCorr";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import MachineSearch from "../../Machine/MachineSearch.vue";
import {getMarketReturnReceiptToMachine} from "../../../api/marketReturnToMachineApi";
import {getMachine} from "../../../api/machineApi";
import {getMachineTrace} from "../../../api/machineTraceApi";
import {getMachineDetection} from "../../../api/machineDetection";
import MachineShowDetailVertical from "../../Machine/MachineShowDetailVertical.vue";
import {deleteMachineForMarketReturnReceipt} from "../../../api/marketReturnReceiptApi";

export default {
  name: "MarketReturnDetail",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      showMachineDetection: {},
      searchMachine: {},
      machines: [],
      currentPage: 1,
      sizes: 10,
      total: null,
    }
  },
  props: ['receiptDetailNumber', 'isRelease', 'operateEmpId'],
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initMachines()
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initMachines() {
      this.searchMachine.marketReturnReceiptId = this.receiptDetailNumber;
      //  this.$refs.child.initMachinesByApi(this.searchMachine)
      getMarketReturnReceiptToMachine(this.currentPage, this.size, this.receiptDetailNumber).then(resp => {
        this.total = resp.data.obj.total
        this.machines = resp.data.obj.data
        if (this.machines === undefined) {
          this.machines = []
        }
      })
    },
    cancelAdvSearch() {
      this.searchMachine = {}
      this.searchMachine.purchaseOrderId = this.receiptDetailNumber;
      this.$refs.child.initMachinesByApi(this.searchMachine)
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
      getMachine(1, 10, {"id": row.machineId}).then(resp => {
        this.showDetailMachine = JSON.parse(JSON.stringify(resp.data.obj.data[0]));
        getMachineTrace({"machineId": row.machineId}).then(resp => {
          this.showMachineTrace = JSON.parse(JSON.stringify(resp.data.obj))
          getMachineDetection({"machineId": row.machineId}).then(resp => {
            this.showMachineDetection = JSON.parse(JSON.stringify(resp.data.obj))
            this.showDetail.value = true
          })
        })
      })
    },
    handleDelete(machineId) {
      this.$confirm('是否确定要删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMachineForMarketReturnReceipt(machineId, this.receiptDetailNumber).then(resp => {
          if (resp.data.code === 200) {
            this.refresh();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    }
  },
  components: {
    MachineShowDetail,
    AddMachineByScan,
    MachineSearch,
    MachineShowDetailVertical
  }
}
</script>

<style scoped>

</style>