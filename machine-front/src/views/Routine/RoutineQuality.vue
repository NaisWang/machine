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
        v-if="Object.keys(nowEditMachine).length !== 0"
        title="机器成色检测"
        :before-close="handleClose"
        :visible.sync="dialogVisible"
        width="90%">
      <MachineShowDetail :machines="[nowEditMachine]" :paging="false"
                         :tableName="'qualityDesc'" table-operate="add"
                         :extra-not-show="['qualityDesc', 'footer', 'operate']"></MachineShowDetail>

      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">描述：</h2>
      <el-form status-icon label-width="100px" class="demo-ruleForm">
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
      <h2 style="color: #409EFF">成色情况检测：</h2>
      <template>
        <div
            v-for="(group,index) in Object.keys($store.state.machineDesc[category[nowEditMachine['categoryId']]]['qualityInfos'])"
            :key="index">
          <el-row>
            <el-col :span="2">
              <span style="margin-right: 8px; font-size: 12px; font-weight: bold;">{{ group }}</span>
            </el-col>
            <el-col :span="22">
              <el-checkbox-group
                  size="mini"
                  :max="1"
                  v-model="nowEditMachine.editQualityDesc[index]">
                <el-checkbox
                    border
                    style="margin-right: 5px; margin-top: 1px; margin-left: 0"
                    v-for="item in $store.state.machineDesc[category[nowEditMachine['categoryId']]]['qualityInfos'][group]"
                    :key="item.id"
                    :label="item.id + ''">{{ item.value }}
                </el-checkbox>
              </el-checkbox-group>
            </el-col>
          </el-row>
        </div>
      </template>

      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">其他信息设置：</h2>
      <el-form label-width="80px">
        <el-form-item label="是否可以上架">
          <el-radio v-model="nowEditMachine.isUpShelf" :label="0">可以</el-radio>
          <el-radio v-model="nowEditMachine.isUpShelf" :label="1">不可以</el-radio>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="nowEditMachine.qualityComment"></el-input>
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

export default {
  name: "成色检测",
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
      getOperateTrace(this.currentPage, this.size, {
        "operateEmpId": this.$store.state.userId,
        "operateType": 1
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
              "operateType": 1
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
              dealMachineJudge(machine, this.$store, "qualityDetection").then(resp => {
                if (resp.code === -1) {
                  this.$message.error(resp.message);
                } else if (resp.code === 1) {
                  machine.editQualityDesc = []
                  _this.nowEditMachine = machine
                  _this.initQualityDesc(machine)
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
    initQualityDesc(data) {
      let length = Object.keys(this.$store.state.machineDesc[this.category[this.nowEditMachine['categoryId']]]['qualityInfos']).length
      if (data.qualityDesc === null) {
        data.editQualityDesc = []
        Object.keys(this.$store.state.machineDesc[this.category[this.nowEditMachine['categoryId']]]['qualityInfos']).forEach(item => {
          data.editQualityDesc.push([])
        })
      } else {
        let temp = data.qualityDesc.split(",")
        data.editQualityDesc = []
        temp.forEach(item => {
          if (item === "") {
            data.editQualityDesc.push([]);
          } else {
            data.editQualityDesc.push([item]);
          }
        })
        for (let i = data.editQualityDesc.length; i < length; i++) {
          data.editQualityDesc.push([]);
        }
      }
      return true
    },
    submit() {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
        temp.qualityDesc = temp.editQualityDesc.toString()
        modifyMachineQuality(temp).then(resp => {
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

.routineQuality .el-checkbox-group {
  font-size: 1px;
}

.el-dialog {
  margin-top: 0 !important;
}
</style>
