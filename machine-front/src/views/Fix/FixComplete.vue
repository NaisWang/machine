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
        title="确定维修项"
        :before-close="handleClose"
        :visible.sync="dialogVisible"
        width="90%">
      <MachineShowDetail :machines="[nowEditMachine]" :paging="false"
                         :tableName="'machineFix'" table-operate="add"
                         :extra-not-show="['featureDesc', 'paijiBarcode','qualityDesc','needFix','fixed','notFixed','fixToBad', 'footer', 'operate', 'comment']"></MachineShowDetail>

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

        <el-form-item label="需要维修项"
                      style="margin: 0;">
          <el-tag
              type="warning"
              v-for="item in (nowEditMachine.needFix === null ? '' : nowEditMachine.needFix.split(','))"
              :key="item" v-if="item !== ''">
            {{ $store.state.machineIdToDesc[item] }}
          </el-tag>
        </el-form-item>

        <el-form-item label="维修次数"
                      style="margin: 0;">
          <el-input disabled :value="nowEditMachine.fixTimes"></el-input>
        </el-form-item>

        <el-form-item label="机器备注"
                      style="margin: 0;">
          <el-input disabled :value="nowEditMachine.comment"></el-input>
        </el-form-item>
      </el-form>


      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">维修项：</h2>
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane v-for="(item, index) in tabs" :key="index" :label="item.label" :name="item.prop">
          <template>
            <div
                v-for="(group,index) in Object.keys($store.state.machineDesc[category[nowEditMachine['categoryId']]]['functionInfos'])"
                :key="index"
                v-show="nowEditMachine['editNeedFix'][index].length !== 0"
            >
              <el-row>
                <el-col :span="2">
                  <span style="margin-right: 8px; font-size: 10px; font-weight: bold;">{{ group }}</span>
                </el-col>
                <el-col :span="22">
                  <el-checkbox-group
                      size="mini"
                      :max="1"
                      v-model="nowEditMachine[item.editProp][index]">
                    <el-checkbox
                        border
                        style="margin-right: 5px; margin-top: 1px; margin-left: 0"
                        :key="item.id"
                        :label="nowEditMachine['editNeedFix'][index][0]">
                      {{ $store.state.machineIdToDesc[nowEditMachine['editNeedFix'][index][0]] }}
                    </el-checkbox>
                  </el-checkbox-group>
                </el-col>
              </el-row>
            </div>
          </template>

          <!--            <div
                          v-for="(group,index) in Object.keys($store.state.machineDesc[category[nowEditMachine['categoryId']]]['functionInfos'])"
                          :key="index">
                        <el-row>
                          <el-col :span="2">
                            <span style="margin-right: 8px; font-size: 10px; font-weight: bold;">{{ group }}</span>
                          </el-col>
                          <el-col :span="22">
                            <el-checkbox-group
                                size="mini"
                                :max="1"
                                v-model="nowEditMachine[item.editProp][index]">
                              <el-checkbox
                                  border
                                  style="margin-right: 5px; margin-top: 1px; margin-left: 0"
                                  v-for="item in $store.state.machineDesc[category[nowEditMachine['categoryId']]]['functionInfos'][group]"
                                  :key="item.id"
                                  :label="item.id + ''">{{ item.value }}
                              </el-checkbox>
                            </el-checkbox-group>
                          </el-col>
                        </el-row>
                      </div>
                    </template>-->
        </el-tab-pane>
      </el-tabs>

      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">其他信息设置：</h2>
      <el-form label-width="100px">
        <el-form-item label="维修价格">
          <el-input v-model="nowEditMachine.fixPrice"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="nowEditMachine.editComment"></el-input>
        </el-form-item>
      </el-form>

      <span class="dialog-footer" slot="footer">
                  <el-button @click="dialogVisible = false">取 消</el-button>
                  <el-button type="primary" @click="submit(1)">保 存</el-button>
                  <el-button type="primary" @click="submit(0)">维 修 完 成</el-button>
                </span>
    </el-dialog>

    <MachineShowDetailVertical v-if="showDetail.value" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :machine-detection="showMachineDetection"
                               :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine, modifyFixComplete, modifyFixItem, modifyMachineUpShelf} from "../../api/machineApi";
import {modifyMachineQuality} from "../../api/machineApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";
import {getOperateTrace} from "../../api/operateTraceApi";
import MachineShowDetailVertical from "../../components/Machine/MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../api/machineTraceApi";

export default {
  name: "维修完成",
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
      tabs: [
        {'label': "已修好项", 'prop': 'fixed', 'editProp': 'editFixed'},
        {'label': "不能修好项", 'prop': 'notFixed', 'editProp': 'editNotFixed'},
        //{'label': "修坏项", 'prop': 'fixToBad', 'editProp': 'editFixToBad'},
      ],
      activeName: 'fixed',
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
        "operateType": 5
      }).then(resp => {
        if (resp.data.code === 200) {
          this.allOperateMachines = resp.data.obj.data
          this.total = resp.data.obj.total
        }
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
              "operateType": 5,
              "afterStatusId": 33
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
              dealMachineJudge(machine, this.$store, "addMachineToFixComplete").then(resp => {
                if (resp.code === -1) {
                  this.$message.error(resp.message);
                } else if (resp.code === 1) {
                  machine.editFixed = []
                  machine.editNotFixed = []
                  machine.editFixToBad = []
                  _this.nowEditMachine = machine
                  _this.initFeatureDesc(machine)
                  _this.initEditNeedFix(_this.nowEditMachine)
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
    initEditNeedFix(data) {
      let length = Object.keys(this.$store.state.machineDesc[this.category[this.nowEditMachine['categoryId']]]['qualityInfos']).length
      if (data.needFix === null) {
        data.editNeedFix = []
        Object.keys(this.$store.state.machineDesc[this.category[this.nowEditMachine['categoryId']]]['qualityInfos']).forEach(item => {
          data.editNeedFix.push([])
        })
      } else {
        let temp = data.needFix.split(",")
        data.editNeedFix = []
        temp.forEach(item => {
          if (item === "") {
            data.editNeedFix.push([]);
          } else {
            data.editNeedFix.push([item]);
          }
        })
        for (let i = data.editNeedFix.length; i < length; i++) {
          data.editNeedFix.push([]);
        }
      }
      return true
    },
    initFeatureDesc(data) {
      let length = Object.keys(this.$store.state.machineDesc[this.category[this.nowEditMachine['categoryId']]]['functionInfos']).length
      this.tabs.forEach(item => {
        if (data[item["prop"]] === null) {
          data[item["editProp"]] = []
          Object.keys(this.$store.state.machineDesc[this.category[this.nowEditMachine['categoryId']]]['functionInfos']).forEach(item1 => {
            (data[item["editProp"]]).push([])
          })
        } else {
          let temp = data[item["prop"]].split(",")
          data[item["editProp"]] = []
          temp.forEach(item1 => {
            if (item1 === "") {
              data[item["editProp"]].push([]);
            } else {
              data[item["editProp"]].push([item1]);
            }
          })
          for (let i = data[item["editProp"]].length; i < length; i++) {
            data[item["editProp"]].push([]);
          }
        }
      })
      return true
    },
    submit(type) {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
        temp.fixed = temp.editFixed.toString()
        temp.notFixed = temp.editNotFixed.toString()
        temp.fixToBad = temp.editFixToBad.toString()
        modifyFixComplete(temp, type).then(resp => {
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
