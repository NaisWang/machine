<template>
  <div>
    <div>
      <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>
    </div>
    <el-input clearable style="width: 89%" placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
              v-model="numberInput"></el-input>

    <el-select style="width: 9%" v-model="searchMethod" placeholder="请选择">
      <el-option
          v-for="item in searchOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value">
      </el-option>
    </el-select>

    <el-table
        :data="allOperateMachines"
        style="width: 100%">
      <el-table-column
          prop="machineNumber"
          label="物品编码"
          width="170">
      </el-table-column>

      <el-table-column
          prop="machineSku"
          label="sku"
          width="170">
      </el-table-column>

      <el-table-column
          prop="fixPrice"
          label="维修价格"
          width="170">
      </el-table-column>

      <el-table-column
          prop="reparation"
          label="维修赔款"
          width="170">
      </el-table-column>

      <el-table-column
          prop="time"
          label="时间"
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
              @click="nowEditMachine = scope.row; dialogVisible=true">编辑
          </el-button>

          <el-button
              v-if="scope.row.confirm === 0"
              size="mini"
              type="success"
              @click="confirm(scope.row.id)">确认
          </el-button>

          <el-button
              v-else
              size="mini"
              disabled>已确认
          </el-button>

        </template>
      </el-table-column>

    </el-table>

    <div style="display: flex; justify-content: flex-end">
      <el-pagination
          background
          layout="sizes, prev, pager, next, ->, total"
          @current-change="currentChange"
          @size-change="sizeChange"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog
        title="维修账单"
        :visible.sync="dialogVisible"
        width="90%">
      <h2 style="color: #409EFF">维修赔款：</h2>
      <el-form label-width="80px">
        <el-form-item label="维修赔款">
          <el-input v-model="nowEditMachine.reparation"></el-input>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="nowEditMachine.fixBillComment"></el-input>
        </el-form-item>
      </el-form>

      <span class="dialog-footer" slot="footer">
                  <el-button @click="dialogVisible = false">取 消</el-button>
                  <el-button type="primary" @click="submit">提 交</el-button>
                </span>
    </el-dialog>


    <MachineShowDetailVertical v-if="showDetail.value" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :machine-detection="showMachineDetection"
                               :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine} from "../../api/machineApi";
import {modifyMachineQuality} from "../../api/machineApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";
import {getOperateTrace} from "../../api/operateTraceApi";
import MachineShowDetailVertical from "../../components/Machine/MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../api/machineTraceApi";
import {getMachineDetection} from "../../api/machineDetection";
import {getFixBill, ModifyFixBill} from "../../api/machineFixBillApi";

export default {
  name: "维修账单",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      showMachineDetection: {},
      allOperateMachines: [],
      test: null,
      numberInput: "",
      dialogVisible: false,
      nowEditMachine: {},
      category: {1: 'phone', 2: 'tablet', 3: '手表'},
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
      searchMethod: "number",
      currentPage: 1,
      size: 10,
      total: null
    }
  },
  mounted() {
    this.refresh();
  },
  methods: {
    refresh(type) {
      this.initOperateTrace();
      if (type === 1) {
        this.$message.success("更新成功");
      }
    },
    initOperateTrace() {
      getFixBill().then(resp => {
        if (resp.data.code === 200) {
          this.allOperateMachines = resp.data.obj.data
          this.total = resp.data.obj.total
        }
      })
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initOperateTrace()
    },
    sizeChange(size) {
      this.size = size;
      this.initOperateTrace()
    },
    confirm(id) {
      this.$confirm('是否确定要确定该维修账单', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ModifyFixBill({'id': id, 'confirm': 1}).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功");
            this.numberInput = ""
            this.nowEditMachine = {}
            this.dialogVisible = false
            this.refresh()
          } else {
            this.$message.error("提交失败");
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消提交'
        });
      });
    },
    submit() {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
        ModifyFixBill(temp).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功");
            this.numberInput = ""
            this.nowEditMachine = {}
            this.dialogVisible = false
            this.refresh()
          } else {
            this.$message.error("提交失败");
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消提交'
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
    MachineShowDetail,
    MachineShowDetailVertical
  }

}
</script>

<style>
.el-select-dropdown__wrap {
  max-height: none;
}

.el-checkbox-group {
  font-size: 1px;
}

.el-dialog {
  margin-top: 0 !important;
}
</style>
