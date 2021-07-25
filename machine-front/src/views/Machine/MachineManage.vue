<template>
  <div>
    <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
              v-model="numberInput"></el-input>

    <MachineShowDetail :machines="scanMachine" :paging="false"
                       :tableName="'storageSearch'" table-operate="add" :extra-not-show="[]"></MachineShowDetail>

    <el-dialog
        v-if="Object.keys(nowEditMachine).length !== 0"
        :title="dialogTitle"
        :before-close="handleClose"
        :visible.sync="dialogVisible"
        width="90%">
      <!--      <MachineShowDetail :machines="[nowEditMachine]" :paging="false"-->
      <!--                         :tableName="'storageSearch'" table-operate="add"-->
      <!--                         :extra-not-show="['qualityDesc', 'featureDesc', 'rankDesc', 'needFix', 'fixed', 'notFixed', 'fixToBad ','footer', 'operate']"></MachineShowDetail>-->
      <MachineShowDetailVertical table-name="storageSearch" :machine="nowEditMachine"></MachineShowDetailVertical>

      <span class="dialog-footer" slot="footer">
                  <el-button @click="cancel">取 消</el-button>
                  <el-button type="primary" @click="detection">接收，但不做任何处理</el-button>
                  <el-button type="primary" @click="detection">确定维修项</el-button>
                  <el-button type="primary" @click="routineQuality">成 色 检 测</el-button>
                  <el-button type="primary" @click="detection">功 能 检 测</el-button>
                  <el-button type="primary" @click="market">销 售</el-button>
                  <el-button type="primary" @click="fix">维 修</el-button>
                  <el-button type="primary" @click="fix">待 上 架</el-button>
                  <el-button type="primary" @click="returnMachine">退 货</el-button>
                </span>
    </el-dialog>


  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import MachineShowDetailVertical from "../../components/Machine/MachineShowDetailVertical.vue";
import {getMachine} from "../../api/machineApi";
import {modifyMachineStatusTo5} from "../../api/machineApi";
import {deliverJudge} from "../../utils/deliverJudge";
import * as tab from "../../utils/tab"

export default {
  name: "机器处理",
  data() {
    return {
      numberInput: "",
      scanMachine: "",
      nowEditMachine: {},
      dialogTitle: "",
      dialogVisible: false
    }
  },
  methods: {
    addMachineByScan() {
      if (this.numberInput !== "") {
        getMachine(1, 10, {"number": this.numberInput}).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
              return
            }

            this.nowEditMachine = resp.data.obj.data[0]

            let deliverJudgeResult = {"code": "", "message": "", "deliverReceipt": {}}
            if (this.nowEditMachine.deliverMachineId === 0) {
              //状态判断
              if (this.otherJudge()) return
              this.dialogVisible = true
              this.dialogTitle = "机器基本信息";
            } else {
              //转交判断
              deliverJudge(this.nowEditMachine, this.$store, deliverJudgeResult).then(() => {
                if (deliverJudgeResult.code === 1) {
                  if (this.otherJudge()) return
                  this.dialogVisible = true
                  let receiveEmp = ""
                  deliverJudgeResult.deliverReceipt['receiveEmpIds'].split(',').forEach(item => {
                    receiveEmp += this.$store.state.employeeNameCorr[item] + '、'
                  })
                  this.dialogTitle = "该机器由" + this.$store.state.employeeNameCorr[deliverJudgeResult.deliverReceipt['operateEmpId']] + "转交给" + receiveEmp + ", 你是否要接收?";
                } else {
                  this.dialogVisible = false
                  this.$message.error(deliverJudgeResult.message);
                }
              })
            }
          }
        })
      }
    },
    otherJudge() {
      //状态判断
      if (this.nowEditMachine.statusId !== 2 && this.nowEditMachine.statusId !== 5) {
        this.$message.error("物品编号为:" + this.numberInput + "的机器状态是" + this.$store.state.machineStatusCorr[this.nowEditMachine.statusId] + ", 不能进行该操作!!!")
        return false
      }
      return true
    },
    handleClose() {
      this.dialogVisible = false
    },
    routineQuality() {
      //this.$router.push("/machine/storage/enter/?oneKeyEnterStoragePurchaseOrder=" + row.purchaseOrder)
      let tabItem = {
        title: "成色检测",
        name: "成色检测",
        path: "/machine/routine/quality/?numberInput=" + this.numberInput
      }

      tab.addTab(tabItem, this.$router, this.$store)
      tab.removeTabUrlQuery("成色检测", this.$store)
    },
    detection() {

    }

  },
  components: {
    MachineShowDetail,
    MachineShowDetailVertical
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>
