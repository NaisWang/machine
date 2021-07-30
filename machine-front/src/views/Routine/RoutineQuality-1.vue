<template>
  <div class="routineQuality">
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
    <MachineShowDetail :machines="scanMachine" :paging="false"
                       @func="editMachine"
                       :tableName="'qualityDesc'" table-operate="add" :extra-not-show="['comment']"></MachineShowDetail>

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
        <el-form-item label="备注">
          <el-input v-model="nowEditMachine.editComment"></el-input>
        </el-form-item>
      </el-form>

      <span class="dialog-footer" slot="footer">
                  <el-button @click="cancel">取 消</el-button>
                  <el-button type="primary" @click="hangUp" v-if="showHangUp">挂 起</el-button>
                  <el-button type="primary" @click="submit">提 交</el-button>
                </span>
    </el-dialog>


  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine} from "../../api/machineApi";
import {modifyMachineQuality} from "../../api/machineApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";

export default {
  name: "成色检测",
  data() {
    return {
      scanMachine: [],
      test: null,
      numberInput: "",
      dialogVisible: false,
      nowEditMachine: {},
      category: {1: 'phone', 2: 'tablet', 3: '手表'},
      showHangUp: false,
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
      searchMethod: "number"
    }
  },
  activated() {
    console.log(this.$route.query)
    if (this.$route.query.numberInput !== undefined) {
      this.numberInput = this.$route.query.numberInput
      this.addMachineByScan();
      this.$route.query.numberInput = undefined;
    }
  },
  mounted() {
    console.log(this.$route.query)
    if (this.$route.query.numberInput !== undefined) {
      this.numberInput = this.$route.query.numberInput
      this.addMachineByScan();
    }
  },
  methods: {
    addMachineByScan() {
      if (this.numberInput !== "") {
        let search = {}
        if (this.searchMethod === 'number') {
          search['number'] = this.showMachineDetailNumber
        } else if (this.searchMethod === 'imei') {
          search['imei'] = this.showMachineDetailNumber
        } else if (this.searchMethod === 'paijiBarCode') {
          search['paijiBarcode'] = this.showMachineDetailNumber
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

            let ans = this.judgeIsSubmit(machine.number)
            if (ans.type === -1) {
              let _this = this
              this.editMachine(ans.value);
              setTimeout(function () {
                _this.$message.error("该机器已经成色检测完毕，且提交了");
              }, 50);
              return
            } else if (ans.type === 0) {
              this.editMachine(ans.value);
              return
            }

            dealMachineJudge(machine, this.$store, "qualityDetection").then(resp => {
              if (resp.code === -1) {
                this.$message.error(resp.message);
              } else if (resp.code === 1) {
                machine.editQualityDesc = []
                _this.nowEditMachine = machine
                _this.initQualityDesc(machine)
                _this.numberInput = ""
                _this.showHangUp = true
                _this.dialogVisible = true
              }
              if (resp.receiveDiagLog !== undefined) {
                let _this = this
                setTimeout(function () {
                  _this.$notify(resp.receiveDiagLog)
                }, 50);
              }
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
    //判断编号为number的机器是否已经添加到单据中
    judgeIsSubmit(number) {
      let ans = {type: 1}
      this.scanMachine.forEach(item => {
        if (item.number === number) {
          if (item.operate !== undefined && item.operate.submitted === 1) {
            ans = {type: -1, value: item}
          } else {
            ans = {type: 0, value: item}
          }
        }
      })
      return ans
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
            this.nowEditMachine.statusId = resp.data.obj.statusId;
            this.nowEditMachine.operateEmpId = resp.data.obj.operateEmpId;
            this.nowEditMachine.operateDate = resp.data.obj.operateDate;
            this.nowEditMachine.comment = resp.data.obj.comment;

            this.nowEditMachine.editComment = resp.data.obj.comment;

            this.nowEditMachine.qualityDesc = temp.qualityDesc
            this.nowEditMachine.operate = {}
            this.nowEditMachine.operate.submitted = 1
            this.nowEditMachine.operate.edit = 0
            if (this.showHangUp) {
              this.scanMachine.push(this.nowEditMachine)
            }
            this.nowEditMachine = {}
            this.dialogVisible = false
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
    handleClose(done) {
      this.$confirm('确认关闭？')
          .then(_ => {
            done();
            this.showHangUp = false
          })
          .catch(_ => {
          });
    },
    hangUp() {
      this.nowEditMachine.qualityDesc = this.nowEditMachine.editQualityDesc.toString();
      this.nowEditMachine.operate = {}
      this.nowEditMachine.operate.submitted = 0
      this.nowEditMachine.operate.notSubmitted = 1
      this.nowEditMachine.operate.edit = 1
      this.scanMachine.push(this.nowEditMachine)
      this.nowEditMachine = {}
      this.dialogVisible = false
    },
    editMachine(machine) {
      this.nowEditMachine = machine
      this.initQualityDesc(machine);
      this.showHangUp = false
      this.dialogVisible = true
    },
    cancel() {
      this.nowEditMachine.qualityDesc = this.nowEditMachine.editQualityDesc.toString();
      this.nowEditMachine = {}
      this.dialogVisible = false
    }
  },
  components: {
    MachineShowDetail
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