<template>
  <div class="routineQuality">
    <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
              v-model="numberInput"></el-input>

    <MachineShowDetail :machines="scanMachine" :paging="false"
                       @func="editMachine"
                       :tableName="'qualityDesc'" table-operate="add" :extra-not-show="[]"></MachineShowDetail>

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
      <h2 style="color: #409EFF">成色情况检测：</h2>
      <template>
        <div
            v-for="(group,index) in Object.keys($store.state.machineDesc[category[nowEditMachine['categoryId']]]['qualityInfos'])"
            :key="index">
          <div style="margin-top: 10px;"></div>
          <el-row>
            <el-col :span="2">
              <span style="margin-right: 8px; font-size: 14px; font-weight: bold;">{{ group }}</span>
            </el-col>
            <el-col :span="22">
              <el-checkbox-group
                  :max="1"
                  v-model="nowEditMachine.editQualityDesc[index]">
                <el-checkbox
                    border
                    size="small"
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
          <el-input v-model="comment"></el-input>
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

export default {
  name: "成色检测",
  data() {
    return {
      scanMachine: [],
      test: null,
      numberInput: "",
      dialogVisible: false,
      nowEditMachine: {},
      category: {1: 'phone', 2: 'tablet', 8: '手表'},
      comment: "",
      showHangUp: false
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
        let _this = this
        getMachine(1, 10, {"number": this.numberInput}).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
              return
            }
            if (resp.data.obj.total >= 2) {
              this.$message.error("物品编号为:" + this.numberInput + "的机器在库存中超过2个")
              return
            }
            if (this.judgeIsAdd(resp.data.obj.data[0].number) === -1) {
              this.$message.error("物品编号为:" + this.numberInput + "的机器已添加到该单据中了")
              return
            }
            if (Object.keys(this.category).indexOf(resp.data.obj.data[0]['categoryId'] + '') === -1) {
              this.$message.error("该机器的类别不是手机、平板、手表")
              this.numberInput = ""
              return
            }
            resp.data.obj.data[0].editQualityDesc = []
            _this.nowEditMachine = resp.data.obj.data[0]
            this.initQualityDesc(resp.data.obj.data[0])
            _this.numberInput = ""
            this.showHangUp = true
            _this.dialogVisible = true
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
    judgeIsAdd(number) {
      let flag = 1
      this.scanMachine.forEach(item => {
        if (item.number === number) {
          flag = -1
        }
      })
      return flag
    },
    submit() {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
        temp.qualityDesc = temp.editQualityDesc.toString()
        if (temp.comment === null) {
          temp.comment = this.comment
        } else {
          temp.comment += this.comment
        }
        modifyMachineQuality([temp]).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功");
            this.nowEditMachine.statusId = resp.data.obj[0].statusId;
            this.nowEditMachine.operateEmpId = resp.data.obj[0].operateEmpId;
            this.nowEditMachine.operateDate = resp.data.obj[0].operateDate;

            this.nowEditMachine.qualityDesc = temp.qualityDesc
            this.nowEditMachine.comment = temp.comment
            this.nowEditMachine.operate = {}
            this.nowEditMachine.operate.submitted = 1
            this.nowEditMachine.operate.edit = 0
            if (this.showHangUp) {
              this.scanMachine.push(this.nowEditMachine)
            }
            this.nowEditMachine = {}
            this.comment = ""
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
      if (this.nowEditMachine.comment === null) {
        this.nowEditMachine.comment = this.comment
      } else {
        this.nowEditMachine.comment += this.comment
      }
      this.nowEditMachine.operate = {}
      this.nowEditMachine.operate.submitted = 0
      this.nowEditMachine.operate.notSubmitted = 1
      this.nowEditMachine.operate.edit = 1
      this.scanMachine.push(this.nowEditMachine)
      this.nowEditMachine = {}
      this.comment = ""
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
      if (this.nowEditMachine.comment === null) {
        this.nowEditMachine.comment = this.comment
      } else {
        this.nowEditMachine.comment += this.comment
      }
      this.nowEditMachine = {}
      this.comment = ""
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