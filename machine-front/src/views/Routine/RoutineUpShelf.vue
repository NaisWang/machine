<template>
  <div class="routineQuality">
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
          prop="number"
          label="物品编码"
          width="170">
      </el-table-column>

      <el-table-column
          prop="sku"
          label="sku"
          width="170">
      </el-table-column>

      <el-table-column
          prop="beforeStatusId"
          label="操作前的状态"
          width="170">
        <template #default="scope">
          {{ $store.state.machineStatusCorr[scope.row.beforeStatusId] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="afterStatusId"
          label="操作后的状态"
          width="170">
        <template #default="scope">
          {{ $store.state.machineStatusCorr[scope.row.afterStatusId] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="storageLocationId"
          label="库位"
          width="170">
        <template #default="scope">
          {{ $store.state.subStorageLocationIdToNameCorr[scope.row.storageLocationId] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="operateTime"
          label="时间"
          width="170">
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
        :modal-append-to-body='false'
        v-if="Object.keys(nowEditMachine).length !== 0"
        title="确定上架信息"
        :before-close="handleClose"
        :visible.sync="dialogVisible"
        width="90%">
      <MachineShowDetail :machines="[nowEditMachine]" :paging="false"
                         :tableName="'machineShelf'" table-operate="add"
                         :extra-not-show="['qualityDesc', 'featureDesc', 'onePrice', 'bidPrice','goodPrice','bagNumber','rankDesc','footer', 'operate']"></MachineShowDetail>

      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">检测情况项：</h2>

      <el-form status-icon label-width="100px" class="demo-ruleForm">
        <el-form-item label="成色检测情况"
                      style="margin: 0;">
          <el-tag
              v-for="item in (nowEditMachine.qualityDesc=== null ? '' : nowEditMachine.qualityDesc.split(','))"
              :key="item" v-if="item !== ''">
            {{ $store.state.machineIdToDesc[item] }}
          </el-tag>
        </el-form-item>

        <el-form-item label="功能检测情况"
                      style="margin: 0;">
          <el-tag
              v-for="item in (nowEditMachine.featureDesc === null ? '' : nowEditMachine.featureDesc.split(','))"
              :key="item" v-if="item !== ''">
            {{ $store.state.machineIdToDesc[item] }}
          </el-tag>
        </el-form-item>

        <el-form-item label="机器状态"
                      style="margin: 0;">
          <el-tag>{{ $store.state.machineStatusCorr[nowEditMachine.statusId] }}</el-tag>
        </el-form-item>

        <el-form-item label="机器备注"
                      style="margin: 0;">
          <el-input disabled :value="nowEditMachine.comment"></el-input>
        </el-form-item>
      </el-form>


      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">上架项：</h2>

      <el-form :model="nowEditMachine" status-icon ref="form" label-width="100px" class="demo-ruleForm">

        <el-form-item :prop="'goodPrice'"
                      label="好的价格"
                      style="margin: 0;"
                      :show-message="false">

          <el-input type="text" v-model="nowEditMachine.goodPrice" placeholder="请输入价格"></el-input>
        </el-form-item>

        <el-form-item :prop="'onePrice'"
                      label="一口价"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">
          <el-input type="text" v-model="nowEditMachine.onePrice" placeholder="请输入价格"></el-input>
        </el-form-item>

        <el-form-item :prop="'bidPrice'"
                      label="最高价"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">
          <el-input type="text" v-model="nowEditMachine.bidPrice" placeholder="请输入价格"></el-input>
        </el-form-item>

        <el-form-item :prop="'rankDesc'"
                      label="机器等级"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">

          <el-input type="text" v-model="nowEditMachine.rankDesc" placeholder="请输入机器等级检测"></el-input>
        </el-form-item>

        <el-form-item :prop="'bagNumber'"
                      label="袋子编码"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">

          <el-input type="text" v-model="nowEditMachine.bagNumber" placeholder="请输入袋子编码"></el-input>
        </el-form-item>

        <el-form-item :prop="'editComment3'"
                      label="备注"
                      style="margin: 0;"
                      :show-message="false">
          <el-input type="text" v-model="nowEditMachine.editComment3" placeholder="请输入备注"></el-input>
        </el-form-item>

      </el-form>


      <span class="dialog-footer" slot="footer">
                  <el-button @click="cancel">取 消</el-button>
                  <el-button type="primary" @click="submit(1)">提交并退回</el-button>
                  <el-button v-if="nowEditMachine.isUpShelf === 0" type="primary" @click="submit(0)">提交并上架</el-button>
                </span>
    </el-dialog>

    <MachineShowDetailVertical v-if="showDetail.value" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine, modifyMachineUpShelf} from "../../api/machineApi";
import {modifyMachineQuality} from "../../api/machineApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";
import {getOperateTrace} from "../../api/operateTraceApi";
import MachineShowDetailVertical from "../../components/Machine/MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../api/machineTraceApi";

export default {
  name: "机器上架",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
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
      getOperateTrace(this.currentPage, this.size, {
        "operateEmpId": this.$store.state.userId,
        "operateType": 3
      }).then(resp => {
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
        let _this = this
        getMachine(1, 10, search).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有该机器")
              return
            }

            let machine = resp.data.obj.data[0]

            if (Object.keys(this.category).indexOf(resp.data.obj.data[0]['categoryId'] + '') === -1) {
              this.$message.error("该机器的类别不是手机、平板、手表")
              this.numberInput = ""
              return
            }

            getOperateTrace(1, 10, {
              "operateEmpId": this.$store.state.userId,
              "number": machine.number,
              "operateType": 3
            }).then(resp => {
              if (resp.data.code === 200) {
                if (resp.data.obj.total !== 0) {
                  let _this = this
                  setTimeout(function () {
                    _this.$message.error("该机器你已经提交过了");
                  }, 50);
                }
              }
            }).then(() => {
              dealMachineJudge(machine, this.$store, "upShelf").then(resp => {
                if (resp.code === -1) {
                  this.$message.error(resp.message);
                } else if (resp.code === 1) {
                  _this.nowEditMachine = machine
                  _this.numberInput = ""
                  _this.dialogVisible = true
                }
                if (resp.receiveDiagLog !== undefined) {
                  let _this = this
                  setTimeout(function () {
                    _this.$notify(resp.receiveDiagLog)
                  }, 50);
                }
              })
            })
          }
        })
      }
    },
    submit(type) {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
        modifyMachineUpShelf(temp, type).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功");
            this.numberInput = ""
            this.nowEditMachine = {}
            this.dialogVisible = false
            this.refresh()
          } else {
            this.$message.error("提交失败");
            this.initQualityDesc(this.nowEditMachine)
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
      getMachine(1, 10, {"number": row.number}).then(resp => {
        this.showDetailMachine = JSON.parse(JSON.stringify(resp.data.obj.data[0]));
        getMachineTrace({"number": row.number}).then(resp => {
          this.showMachineTrace = JSON.parse(JSON.stringify(resp.data.obj))
          this.showDetail.value = true
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

.routineQuality .el-checkbox-group {
  font-size: 1px;
}

.el-dialog {
  margin-top: 0 !important;
}
</style>
