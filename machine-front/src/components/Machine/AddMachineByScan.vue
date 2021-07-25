<template>
  <div>
    <template>
      <el-button @click="dialogVisible = true">扫码添加</el-button>
      <el-dialog
          title="提示"
          :visible.sync="dialogVisible"
          width="80%"
          :before-close="handleClose">
        <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
                  v-model="numberInput"></el-input>

        <MachineShowDetail :machines="scanMachine" paging="false" :table-name="tableName"
                           table-operate="add" extra-not-show="[]"></MachineShowDetail>

        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submit">提 交</el-button>
        </span>
      </el-dialog>
    </template>
  </div>
</template>


<script>
import MachineShowDetail from "./MachineShowDetail.vue";
import {getMachine} from "../../api/machineApi";
import {addMachineToEnterStorageReceipt} from "../../api/enterStorageApi";
import {addMachineToDeliverReceipt, receiveDeliverMachine} from "../../api/deliverMachineApi";
import {deliverJudge} from "../../utils/deliverJudge";
import {addMachineToPurchaseReturnReceipt} from "../../api/purchaseReturnApi";
import {addMachineToMarketOrderReceipt} from "../../api/marketOrderApi";

export default {
  name: "AddMachineByScan",
  data() {
    return {
      scanMachine: [],
      numberInput: "",
      dialogVisible: false
    }
  },
  props: ['machines', 'tableName', 'receiptId'],
  methods: {
    handleClose(done) {
      let _this = this
      this.$confirm('确认在没保存的情况下关闭吗？')
          .then(_ => {
            _this.scanMachine = []
            done()
          })
          .catch(_ => {
          });
    },
    addMachineByScan() {
      if (this.numberInput !== "") {
        getMachine(1, 10, {"number": this.numberInput}).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
              return
            }
            if (resp.data.obj.total !== 1) {
              this.$message.error("物品编号为:" + this.numberInput + "的机器在库存中超过2个")
              return
            }
            if (this.judgeIsAdd(resp.data.obj.data[0].number) === -1) {
              this.$message.error("物品编号为:" + this.numberInput + "的机器已添加到该单据中了")
              return
            }

            let machine = resp.data.obj.data[0]

            //机器加到表单中，但表单还没有提交
            if ([20, 21, 23].indexOf(machine.statusId) !== -1) {
              this.$message.error("该机器处于" + this.$store.state.machineStatusCorr[machine.statusId] + "状态，不能在对其入当前表单， 处理人是：" + this.$store.state.employeeNameCorr[machine.operateEmpId])
              return
            }

            if (this.tableName === "storageEnter") {
              if (this.storageEnterReceiptJudge(machine) === -1) {
                return
              }
            } else if (this.tableName === "purchaseReturn") {
              if (this.purchaseReturnReceiptJudge(machine) === -1) {
                return
              }
            } else if (this.tableName === 'marketOrder') {
              if (this.marketOrderReceiptJudge(machine) === -1) {
                return
              }
            }

            //转交判断
            if (machine['deliverReceiptId'] > 0) {
              console.log(machine['deliverReceiptId'])
              let deliverJudgeResult = {"code": "", "message": "", "deliverReceipt": {}}
              deliverJudge(machine, this.$store, deliverJudgeResult).then(() => {
                if (deliverJudgeResult.code === 1) {
                  receiveDeliverMachine(machine.id).then(resp1 => {
                    if (resp1.data.code === 200) {
                      this.$message.success("接收成功");
                      this.numberInput = ""
                      this.scanMachine.push(machine)
                    }
                  })
                } else {
                  this.dialogVisible = false
                  this.$message.error(deliverJudgeResult.message);
                }
              })
            } else {
              this.scanMachine.push(machine)
              this.numberInput = ""
            }

          }
        })
      }
    },
    //将机器添加到入库单时，判断该机器是否合法
    storageEnterReceiptJudge(machine) {
      if (machine.enterStorageReceiptId != null && machine.enterStorageReceiptId !== 0) {
        this.$message.error("物品编号为:" + this.numberInput + "的机器已经在单号为--" + machine.enterStorageReceiptId + "--的入库单据中了")
        return -1
      }
      let statusId = machine['statusId']
      if ([1, 3, 4].indexOf(statusId) === -1) {
        this.$message.error("该机器的状态是" + this.$store.state.machineStatusCorr[statusId] + "不能入库")
        return -1
      }
      if (statusId === 3) {
        let ans = 1
        this.$confirm('该机器的状态是退货中，是否确定入库', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {

        }).catch(() => {
          ans = -1
          this.$message({
            type: 'info',
            message: '已取消入库'
          });
        });
        return ans
      } else {
        return 1
      }
    },
    //将机器添加到采购退货单时，判断该机器是否合法
    purchaseReturnReceiptJudge(machine) {
      if (machine.purchaseReturnOrderId != null && machine.purchaseReturnOrderId !== "") {
        this.$message.error("物品编号为:" + this.numberInput + "的机器已经在单号为--" + machine.purchaseReturnOrderId + "--的采购退货单据中了")
        return -1
      }
      return 1
    },

    //将机器添加到销售单时，判断该机器是否合法
    marketOrderReceiptJudge(machine) {
      if (machine.marketOrderId != null && machine.marketOrderId !== 0) {
        this.$message.error("物品编号为:" + this.numberInput + "的机器已经在单号为--" + machine.marketOrderId + "--的采购退货单据中了")
        return -1
      }

      //if (machine.statusId !== 26) {
      //  this.$message.error("该机器的状态不是已上架、待出库状态！不能添加到销售单中")
      //  return -1
      //}

      return 1
    },
    //判断编号为number的机器是否已经添加到单据中
    judgeIsAdd(number) {
      let flag = 1
      this.scanMachine.forEach(item => {
        console.log("itemNum" + item.number)
        console.log("num" + number)
        if (item.number === number) {
          flag = -1
        }
      })
      this.machines.forEach(item => {
        if (item.number === number) {
          flag = -1
        }
      })
      return flag
    },
    submit() {
      if (this.scanMachine.length === 0) {
        this.$message.error("你还没有添加机器!!!")
        return
      }
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let ids = []
        this.scanMachine.forEach(item => {
          ids.push(item.id);
        })
        if (this.tableName === 'storageEnter') {
          addMachineToEnterStorageReceipt(ids, this.receiptId).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.machines.push.apply(this.machines, this.scanMachine);
              this.$emit('initShow')
            }
          })
        } else if (this.tableName === 'machineDeliver') {
          addMachineToDeliverReceipt(ids, this.receiptId).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.machines.push.apply(this.machines, this.scanMachine);
              this.$emit('initShow')
            }
          })
        } else if (this.tableName === 'purchaseReturn') {
          addMachineToPurchaseReturnReceipt(ids, this.receiptId).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.machines.push.apply(this.machines, this.scanMachine);
              this.$emit('initShow')
            }
          })
        } else if (this.tableName === 'marketOrder') {
          addMachineToMarketOrderReceipt(ids, this.receiptId).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.machines.push.apply(this.machines, this.scanMachine);
              this.$emit('initShow')
            }
          })
        }
        this.scanMachine = []
        this.dialogVisible = false
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消保存'
        });
      });
    }
  },
  components: {
    MachineShowDetail
  }
}
</script>

<style scoped>

</style>