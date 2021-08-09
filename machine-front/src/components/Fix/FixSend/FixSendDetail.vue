<template>
  <div>
    <MachineSearch @searchMachines="initDeliverMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <AddMachineByScan v-if="isRelease === 0" :machines="machines" table-name="fixSend"
                      operate-name="addMachineToSendFix"
                      :receiptId="receiptDetailNumber" @initShow="initDeliverMachines"></AddMachineByScan>

    <div>
      <el-table
          :data="machines"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="sendFixDate"
            label="送修日期"
            width="170">
        </el-table-column>

        <el-table-column
            prop="receiveTime"
            label="取回日期"
            width="170">
        </el-table-column>

        <el-table-column
            prop="number"
            label="物品编码"
            width="170">
        </el-table-column>

        <el-table-column
            prop="fixItem"
            label="维修项"
            width="170">
        </el-table-column>

        <el-table-column
            prop="sendDestination"
            label="维修当口"
            width="170">
        </el-table-column>

        <el-table-column
            prop="isRepair"
            label="是否为返修"
            width="170">
          <template #default="scope">
            <span v-if="scope.row.isRepair === 0">否</span>
            <span v-else>是</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="fixStatus"
            label="接收状态"
            width="170">
          <template #default="scope">
            <span v-if="scope.row.fixStatus === 0">待送外修</span>
            <span v-else-if="scope.row.fixStatus === 1">送外修</span>
            <span v-else>已取回</span>
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
                  :disabled="!isRelease"
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
                v-if="isRelease === 0"
                size="mini"
                type="danger"
                @click="handleDelete(scope.row)">删除
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
                               :machine-trace="showMachineTrace" :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import initMachineCorr from "../../../utils/machineCorr";
import {getMachine} from "../../../api/machineApi";
import {deleteDeliverMachine} from "../../../api/deliverMachineApi";
import {deleteMachineForFixSendReceipt, getFixSendMachine} from "../../../api/fixSendMachineApi";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import MachineShowDetailVertical from "../../Machine/MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../../api/machineTraceApi";
import MachineSearch from "../../Machine/MachineSearch.vue";

export default {
  name: "FixSendDetail",
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
      addMachineInfo: {},
      dialogVisible: false,
      numberInput: "",
      showMachineTable: false,
      addDeliverMachinesInfo: [],
      addDeliverMachineInfo: {},
    }
  },
  props: ['receiptDetailNumber', 'isRelease'],
  mounted() {
    this.initDeliverMachines();
  },
  methods: {
    initDeliverMachines() {
      this.searchMachine.receiptId = this.receiptDetailNumber;
      getFixSendMachine(this.currentPage, this.size, this.searchMachine).then(resp => {
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
    addMachine() {
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('是否确定要删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMachineForFixSendReceipt(row.sendFixMachineId).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("删除成功");
            this.initDeliverMachines()
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    detail(row) {
      getMachine(1, 10, {"number": row.number}).then(resp => {
        console.log("fjdk")
        console.log(resp.data.obj.data);
        this.showDetailMachine = JSON.parse(JSON.stringify(resp.data.obj.data[0]));
        getMachineTrace({"number": row.number}).then(resp => {
          this.showMachineTrace = JSON.parse(JSON.stringify(resp.data.obj))
          this.showDetail.value = true
        })
      })
    }
  },
  components: {
    AddMachineByScan,
    MachineShowDetailVertical,
    MachineSearch
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>
