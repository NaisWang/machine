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

      <el-tabs v-model="activeName" type="card" @tab-click="handleClick">


        <el-tab-pane label="成色情况检测"
                     v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(2) !== -1 || $store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(4) !== -1"
                     name="first">
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
          <h2 style="color: #409EFF">其他信息设置：</h2>
          <el-form label-width="80px">
            <el-form-item label="备注">
              <el-input v-model="nowEditMachine.editComment1"></el-input>
            </el-form-item>
          </el-form>

          <div style="text-align: right; padding: 10px 20px 20px; box-sizing: border-box;">
          <span style="text-align: right; padding: 10px 20px 20px;     box-sizing: border-box;">
                  <el-button @click="handleClose">取 消</el-button>
                  <el-button type="primary" @click="submit(1)">提 交</el-button>
                </span>
          </div>
        </el-tab-pane>


        <el-tab-pane label="功能情况检测"
                     v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(3) !== -1 || $store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(4) !== -1"
                     name="second">
          <h2 style="color: #409EFF">功能情况检测：</h2>
          <template>
            <div
                v-for="(group,index) in Object.keys($store.state.machineDesc[category[nowEditMachine['categoryId']]]['functionInfos'])"
                :key="index">
              <el-row>
                <el-col :span="2">
                  <span style="margin-right: 8px; font-size: 12px; font-weight: bold;">{{ group }}</span>
                </el-col>
                <el-col :span="22">
                  <el-checkbox-group
                      :max="1"
                      size="mini"
                      v-model="nowEditMachine.editFeatureDesc[index]">
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
          </template>
          <h2 style="color: #409EFF">其他信息设置：</h2>
          <el-form label-width="100px">
            <el-form-item label="备注" style="margin: 0">
              <el-input v-model="nowEditMachine.editComment2"></el-input>
            </el-form-item>
            <el-form-item label="拍机堂条码" style="margin: 0;">
              <el-input v-model="nowEditMachine.paijiBarcode"></el-input>
            </el-form-item>
          </el-form>
          <div style="text-align: right; padding: 10px 20px 20px; box-sizing: border-box;">
          <span style="text-align: right; padding: 10px 20px 20px;     box-sizing: border-box;">
                  <el-button @click="handleClose">取 消</el-button>
                  <el-button type="primary" @click="submit(2)">提 交</el-button>
                </span>
          </div>
        </el-tab-pane>


        <el-tab-pane label="机器上架"
                     v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(8) !== -1 || $store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(4) !== -1"
                     name="third">
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

          <div style="text-align: right; padding: 10px 20px 20px; box-sizing: border-box;">
          <span style="text-align: right; padding: 10px 20px 20px;     box-sizing: border-box;">
                  <el-button @click="handleClose">取 消</el-button>
                  <el-button type="primary" @click="submit(3,1)">提交并退回</el-button>
                  <el-button v-if="nowEditMachine.isUpShelf === 0" type="primary" @click="submit(3,0)">提交并上架</el-button>
                </span>
          </div>
        </el-tab-pane>

      </el-tabs>

    </el-dialog>

    <MachineShowDetailVertical v-if="showDetail.value" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine, modifyFixItem, modifyMachineFeature, modifyMachineUpShelf} from "../../api/machineApi";
import {modifyMachineQuality} from "../../api/machineApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";
import {getOperateTrace} from "../../api/operateTraceApi";
import MachineShowDetailVertical from "../../components/Machine/MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../api/machineTraceApi";

export default {
  name: "确定维修项",
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
      total: null,
      activeName: "first",
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
    initActiveName() {
      if (this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(2) !== -1 || this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(4) !== -1) {
        this.activeName = "first"
      } else if (this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(3) !== -1 || this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(4) !== -1) {
        this.activeName = "second"
      } else if (this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(8) !== -1 || this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(4) !== -1) {
        this.activeName = "third"
      }
    },
    initOperateTrace() {
      getOperateTrace(this.currentPage, this.size, {
        "operateEmpId": this.$store.state.userId
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
              dealMachineJudge(machine, this.$store, "fixConfirm").then(resp => {
                if (resp.code === -1) {
                  this.$message.error(resp.message);
                } else if (resp.code === 1) {
                  machine.editFeatureDesc = []
                  machine.editQualityDesc = []

                  _this.nowEditMachine = machine

                  _this.initFeatureDesc(machine)
                  _this.initQualityDesc(machine)

                  _this.numberInput = ""
                  this.initActiveName()
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
    initFeatureDesc(data) {
      let length = Object.keys(this.$store.state.machineDesc[this.category[this.nowEditMachine['categoryId']]]['functionInfos']).length
      if (data.featureDesc === null) {
        data.editFeatureDesc = []
        Object.keys(this.$store.state.machineDesc[this.category[this.nowEditMachine['categoryId']]]['functionInfos']).forEach(item => {
          data.editFeatureDesc.push([])
        })
      } else {
        let temp = data.featureDesc.split(",")
        data.editFeatureDesc = []
        temp.forEach(item => {
          if (item === "") {
            data.editFeatureDesc.push([]);
          } else {
            data.editFeatureDesc.push([item]);
          }
        })
        for (let i = data.editFeatureDesc.length; i < length; i++) {
          data.editFeatureDesc.push([]);
        }
      }
      return true
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
    submit(method, type) {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (method === 1) {
          let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
          temp.qualityDesc = temp.editQualityDesc.toString()
          modifyMachineQuality(temp).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.nowEditMachine.statusId = resp.data.obj.statusId
              //this.numberInput = ""
              //this.nowEditMachine = {}
            } else {
              this.$message.error("提交失败");
              this.initQualityDesc(this.nowEditMachine)
            }
          })
        } else if (method === 2) {
          let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
          temp.featureDesc = temp.editFeatureDesc.toString()
          modifyMachineFeature(temp).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.nowEditMachine.statusId = resp.data.obj.statusId
              //this.numberInput = ""
              //this.nowEditMachine = {}
            } else {
              this.$message.error("提交失败");
              this.initFeatureDesc(this.nowEditMachine)
            }
          })
        } else if (method === 3) {
          let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
          modifyMachineUpShelf(temp, type).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.nowEditMachine.statusId = resp.data.obj.statusId
              this.refresh()
              this.dialogVisible = false
              this.numberInput = ""
              this.nowEditMachine = {}
            } else {
              this.$message.error("提交失败");
              this.initQualityDesc(this.nowEditMachine)
            }
          })
        }
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
    },
    handleClose() {
      this.dialogVisible = false
      this.nowEditMachine = {}
      this.numberInput = ""
      this.refresh()
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
