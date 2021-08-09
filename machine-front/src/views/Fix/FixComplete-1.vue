<template>
  <div class="">
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
                       :tableName="'machineFix'" table-operate="add" :extra-not-show="[]"></MachineShowDetail>

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

        <el-form-item label="需要维修项"
                      style="margin: 0;">
          <el-tag
              type="warning"
              v-for="item in (nowEditMachine.needFix === null ? '' : nowEditMachine.needFix.split(','))"
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
      <h2 style="color: #409EFF">维修项：</h2>
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane v-for="(item, index) in tabs" :key="index" :label="item.label" :name="item.prop">
          <template>
            <div
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
          </template>
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
                  <el-button @click="cancel">取 消</el-button>
                  <el-button type="primary" @click="hangUp" v-if="showHangUp">挂 起</el-button>
                  <el-button type="primary" @click="submit">提 交</el-button>
                </span>
    </el-dialog>


  </div>

</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine, modifyFixComplete, modifyFixItem} from "../../api/machineApi";
import {modifyMachineFeature} from "../../api/machineApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";

export default {
  name: "维修完成",
  data() {
    return {
      scanMachine: [],
      numberInput: "",
      test: null,
      dialogVisible: false,
      nowEditMachine: {},
      category: {1: 'phone', 2: 'tablet', 3: '手表'},
      showHangUp: false,
      tabs: [
        {'label': "已修好项", 'prop': 'fixed', 'editProp': 'editFixed'},
        {'label': "未修好项", 'prop': 'notFixed', 'editProp': 'editNotFixed'},
        {'label': "修坏项", 'prop': 'fixToBad', 'editProp': 'editFixToBad'},
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
      searchMethod: "number"
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
              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
              return
            }

            let machine = resp.data.obj.data[0]

            if (Object.keys(this.category).indexOf(machine['categoryId'] + '') === -1) {
              this.$message.error("该机器的类别不是手机、平板、手表")
              this.numberInput = ""
              return
            }

            let ans = this.judgeIsSubmit(machine.number)
            if (ans.type === -1) {
              let _this = this
              this.editMachine(ans.value);
              setTimeout(function () {
                _this.$message.error("该机器已经确定了维修项，且提交了");
              }, 50);
              return
            } else if (ans.type === 0) {
              this.editMachine(ans.value);
              return
            }

            dealMachineJudge(machine, this.$store, "fixComplete").then(resp => {
              if (resp.code === -1) {
                this.$message.error(resp.message);
              } else if (resp.code === 1) {
                machine.editFixed = []
                machine.editNotFixed = []
                machine.editFixToBad = []
                _this.nowEditMachine = machine
                _this.initFeatureDesc(machine)
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
    //判断编号为number的机器是否已经添加到单据中
    judgeIsSubmit(number) {
      let ans = {type: 1}
      this.scanMachine.forEach(item => {
        if (item.number === number) {
          if (item.statusId === 32) {
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
        temp.fixed = temp.editFixed.toString()
        temp.notFixed = temp.editNotFixed.toString()
        temp.fixToBad = temp.editFixToBad.toString()
        modifyFixComplete(temp ).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功");
            this.nowEditMachine.statusId = resp.data.obj.statusId;
            this.nowEditMachine.operateEmpId = resp.data.obj.operateEmpId;
            this.nowEditMachine.operateDate = resp.data.obj.operateDate;
            this.nowEditMachine.comment = resp.data.obj.comment;

            this.nowEditMachine.editComment = ""

            this.nowEditMachine.fixed = temp.fixed
            this.nowEditMachine.notFixed = temp.notFixed
            this.nowEditMachine.fixToBad = temp.fixToBad
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
            this.initFeatureDesc(this.nowEditMachine)
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
      this.nowEditMachine.fixed = this.nowEditMachine.editFixed.toString();
      this.nowEditMachine.notFixed = this.nowEditMachine.editNotFixed.toString();
      this.nowEditMachine.fixToBad = this.nowEditMachine.editFixToBad.toString();
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
      this.initFeatureDesc(machine)
      this.showHangUp = false
      this.dialogVisible = true
    },
    cancel() {
      this.nowEditMachine.fixed = this.nowEditMachine.editFixed.toString();
      this.nowEditMachine.notFixed = this.nowEditMachine.editNotFixed.toString();
      this.nowEditMachine.fixToBad = this.nowEditMachine.editFixToBad.toString();
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
.el-dialog {
  margin-top: 0 !important;
}
</style>
