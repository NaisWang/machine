<template>
  <div>
    <MachineSearch @searchMachines="initDeliverMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <AddMachineByScan v-if="isEdit === 0 && $store.state.userId === operateEmpId" :machines="machines"
                      table-name="machineDeliver"
                      :operate-name="'machineDeliver-' + deliverIntentionId"
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

            <el-button
                v-if="isEdit === 0 || (scope.row.status === 1 && scope.row.nowReceiptId === receiptDetailNumber)"
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
                               :machine-trace="showMachineTrace" :machine-detection="showMachineDetection"
                               :show-detail="showDetail"></MachineShowDetailVertical>


    <!--    <el-dialog-->
    <!--        title="机器信息"-->
    <!--        :visible.sync="dialogVisible"-->
    <!--        width="80%">-->

    <!--      <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"-->
    <!--                v-model="numberInput"></el-input>-->

    <!--      <MachineShowDetailVertical v-if="showMachineTable" ref="child" :machine="addMachineInfo"-->
    <!--                                 table-name="storageSearch"></MachineShowDetailVertical>-->

    <!--      <div v-if="showMachineTable">-->
    <!--        <hr>-->
    <!--        <span>备注：</span>-->
    <!--        <el-input type="text" style="width: 70%" v-model="addDeliverMachineInfo.comment"-->
    <!--                  placeholder="请填写添加这台机器到转交单的备注信息"></el-input>-->
    <!--      </div>-->

    <!--      <span slot="footer" class="dialog-footer">-->
    <!--                              <el-button @click="dialogVisible = false">取 消</el-button>-->
    <!--                              <el-button type="primary" @click="add">添 加</el-button>-->
    <!--                            </span>-->
    <!--    </el-dialog>-->
  </div>
</template>

<script>
import initMachineCorr from "../../../utils/machineCorr";
import {getDeliverMachine} from "../../../api/deliverMachineApi";
import {getMachine} from "../../../api/machineApi";
import {addDeliverMachine} from "../../../api/deliverMachineApi";
import {deleteDeliverMachine} from "../../../api/deliverMachineApi";
import AddMachineByScan from "../AddMachineByScan.vue";
import MachineShowDetailVertical from "../MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../../api/machineTraceApi";
import MachineSearch from "../MachineSearch.vue";
import {getMachineDetection} from "../../../api/machineDetection";
import {dealMachineJudge} from "../../../utils/dealMachineJudge";

export default {
  name: "MachineDeliverDetail",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      searchMachine: {},
      showMachineDetection: {},
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
      deliverIntentionIdToName: {
        //退回返修
        14: "deliverMachineToReturnFix",
        // 返修
        15: "deliverMachineToFix"
      }
    }
  },
  props: ['receiptDetailNumber', 'isEdit', 'operateEmpId', 'deliverIntentionId'],
  mounted() {
    this.initDeliverMachines();
  },
  methods: {
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
      this.initDeliverMachines()
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
        console.log(row)
        deleteDeliverMachine(row.machineNumber, this.receiptDetailNumber).then(resp => {
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
