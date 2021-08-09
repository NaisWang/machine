<template>
  <div>
    <template>

      <el-button @click="dialogVisible = true">扫码添加</el-button>

      <el-dialog
          v-if="['fixSend'].indexOf(tableName) !== -1"
          title="送外修"
          :before-close="handleClose"
          :visible.sync="dialogVisible"
          width="90%">

        <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
                  v-model="numberInput"></el-input>
        <el-select style="width: 100px" v-model="searchMethod" placeholder="请选择">
          <el-option
              v-for="item in searchOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>

        <MachineShowDetail :machines="[scanMachine[0]]" :paging="false"
                           :tableName="'storageSearch'" table-operate="add"
                           :extra-not-show="['qualityDesc', 'featureDesc', 'needFix', 'fixed', 'notFixed','fixToBad','rankDesc','onePrice' , 'goodPrice', 'bagNumber', 'bidPrice', 'footer', 'operate']"></MachineShowDetail>

        <div v-if="scanMachine.length === 1">

          <hr style="margin-top: 15px">
          <h2 style="color: #409EFF">检测情况项：</h2>

          <el-form status-icon label-width="100px" class="demo-ruleForm">
            <el-form-item label="成色检测情况"
                          style="margin: 0;">
              <el-tag
                  v-for="item in (scanMachine[0].qualityDesc=== null ? '' : scanMachine[0].qualityDesc.split(','))"
                  :key="item" v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </el-form-item>

            <el-form-item label="功能检测情况"
                          style="margin: 0;">
              <el-tag
                  v-for="item in (scanMachine[0].featureDesc === null ? '' : scanMachine[0].featureDesc.split(','))"
                  :key="item" v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </el-form-item>

            <el-form-item label="需要维修项"
                          style="margin: 0;">
              <el-tag
                  type="warning"
                  v-for="item in (scanMachine[0].needFix === null ? '' : scanMachine[0].needFix.split(','))"
                  :key="item" v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </el-form-item>

            <el-form-item label="已修好项"
                          style="margin: 0;">
              <el-tag
                  type="success"
                  v-for="item in (scanMachine[0].fixed === null ? '' : scanMachine[0].fixed.split(','))"
                  :key="item" v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </el-form-item>

            <el-form-item label="未修好项"
                          style="margin: 0;">
              <el-tag
                  type="danger"
                  v-for="item in (scanMachine[0].notFixed === null ? '' : scanMachine[0].notFixed.split(','))"
                  :key="item" v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </el-form-item>

            <el-form-item label="修坏项"
                          style="margin: 0;">
              <el-tag
                  type="danger"
                  v-for="item in (scanMachine[0].fixToBad === null ? '' : scanMachine[0].fixToBad.split(','))"
                  :key="item" v-if="item !== ''">
                {{ $store.state.machineIdToDesc[item] }}
              </el-tag>
            </el-form-item>

            <el-form-item label="机器备注"
                          style="margin: 0;">
              <el-input disabled :value="scanMachine[0].comment"></el-input>
            </el-form-item>
          </el-form>


          <hr style="margin-top: 15px">
          <h2 style="color: #409EFF">送外修信息设置：</h2>
          <el-form label-width="80px">
            <el-form-item label="维修当口">
              <el-input v-model="scanMachine[0].sendDestination"></el-input>
            </el-form-item>
            <el-form-item label="维修故障">
              <el-input v-model="scanMachine[0].fixItem"></el-input>
            </el-form-item>
            <el-form-item label="是否为返修">
              <el-switch v-model="scanMachine[0].isRepair" active-color="#ff4949"
                         inactive-color="#13ce66" active-text="是"
                         inactive-text="否" :active-value="1" :inactive-value="0"
              ></el-switch>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="scanMachine[0].fixComment"></el-input>
            </el-form-item>
          </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="submit">提 交</el-button>
          </span>
      </el-dialog>

      <el-dialog
          v-else
          title="提示"
          :visible.sync="dialogVisible"
          width="80%"
          :before-close="handleClose">
        <el-input style="width:  89%" clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
                  v-model="numberInput"></el-input>
        <el-select style="width: 9%" v-model="searchMethod" placeholder="请选择">
          <el-option
              v-for="item in searchOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>

        <MachineShowDetail :machines="scanMachine" :paging="false" :table-name="tableName"
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
import {addMachineToMarketReturnReceipt} from "../../api/marketReturnReceiptApi";
import {addMachineToMarketReturnEnterStorageReceipt} from "../../api/marketReturnEnterStorageApi";
import {addMachineToFixSendReceipt} from "../../api/fixSendMachineApi";
import {addMachineToUpShelfEnterStorage} from "../../api/upShelfEnterStorageApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";

export default {
  name: "AddMachineByScan",
  data() {
    return {
      scanMachine: [],
      numberInput: "",
      nowEditMachine: {},
      dialogVisible: false,
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
  props: ['machines', 'tableName', 'receiptId', 'operateName'],
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
        let search = {}
        if (this.searchMethod === 'number') {
          search['number'] = this.numberInput
        } else if (this.searchMethod === 'imei') {
          search['imei'] = this.numberInput
        } else if (this.searchMethod === 'paijiBarCode') {
          search['paijiBarcode'] = this.numberInput
        }
        getMachine(1, 10, search).then(resp => {
          if (resp.data.obj) {
            console.log(resp.data.obj)
            if (resp.data.obj.total === 0) {
              this.$message.error("没有该机器")
              return
            }
            if (resp.data.obj.total !== 1) {
              this.$message.error("该机器在库存中超过2个")
              return
            }
            if (this.judgeIsAdd(resp.data.obj.data[0].number) === -1) {
              this.$message.error("该机器已添加到该单据中了")
              return
            }

            let machine = resp.data.obj.data[0]

            dealMachineJudge(machine, this.$store, this.operateName).then(resp => {
              if (resp.code === -1) {
                this.$message.error(resp.message);
              } else if (resp.code === 0) {
                this.$confirm(resp.message, '提示', {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  type: 'warning'
                }).then(() => {
                  this.numberInput = ""
                  if (['addMachineToSendFix'].indexOf(this.operateName) !== -1) {
                    this.scanMachine = [machine]
                  } else {
                    this.scanMachine.push(machine)
                  }
                }).catch(() => {
                  this.$message({
                    type: 'info',
                    message: '已取消入库'
                  });
                });
              } else if (resp.code === 1) {
                this.numberInput = ""
                if (['addMachineToSendFix'].indexOf(this.operateName) !== -1) {
                  this.scanMachine = [machine]
                } else {
                  this.scanMachine.push(machine)
                }
              }
              if (resp.receiveDiagLog !== undefined) {
                let _this = this
                setTimeout(function () {
                  _this.$notify(resp.receiveDiagLog)
                }, 50);
              }
            })

            //if (this.tableName === "storageEnter") {
            //  if (this.storageEnterReceiptJudge(machine) === -1) {
            //    return
            //  }
            //} else if (this.tableName === "purchaseReturn") {
            //  if (this.purchaseReturnReceiptJudge(machine) === -1) {
            //    return
            //  }
            //} else if (this.tableName === 'marketOrder') {
            //  if (this.marketOrderReceiptJudge(machine) === -1) {
            //    return
            //  }
            //} else if (this.tableName === 'marketReturn') {
            //  if (this.marketReturnReceiptJudge(machine) === -1) {
            //    return
            //  }
            //}
            //转交判断
            //if (machine['deliverReceiptId'] > 0) {
            //  console.log(machine['deliverReceiptId'])
            //  let deliverJudgeResult = {"code": "", "message": "", "deliverReceipt": {}}
            //  deliverJudge(machine, this.$store, deliverJudgeResult).then(() => {
            //    if (deliverJudgeResult.code === 1) {
            //      receiveDeliverMachine(machine.id).then(resp1 => {
            //        if (resp1.data.code === 200) {
            //          this.$message.success("接收成功");
            //          this.numberInput = ""
            //          if (['fixSend'].indexOf(this.tableName) !== -1) {
            //            this.scanMachine = [machine]
            //          } else {
            //            this.scanMachine.push(machine)
            //          }
            //        }
            //      })
            //    } else {
            //      this.dialogVisible = false
            //      this.$message.error(deliverJudgeResult.message);
            //    }
            //  })
            //} else {
            //  if (['fixSend'].indexOf(this.tableName) !== -1) {
            //    this.scanMachine = [machine]
            //  } else {
            //    this.scanMachine.push(machine)
            //  }
            //  this.numberInput = ""
            //}

          }
        })
      }
    },
    //将机器添加到入库单时，判断该机器是否合法
    storageEnterReceiptJudge(machine) {
      //if (machine.enterStorageReceiptId != null && machine.enterStorageReceiptId !== 0) {
      //  this.$message.error("物品编号为:" + this.numberInput + "的机器已经在单号为--" + machine.enterStorageReceiptId + "--的入库单据中了")
      //  return -1
      //}
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
      //if (machine.purchaseReturnOrderId != null && machine.purchaseReturnOrderId !== "") {
      //  this.$message.error("物品编号为:" + this.numberInput + "的机器已经在单号为--" + machine.purchaseReturnOrderId + "--的采购退货单据中了")
      //  return -1
      //}
      //return 1
    },

    //将机器添加到销售单时，判断该机器是否合法
    marketOrderReceiptJudge(machine) {
      //if (machine.marketOrderId != null && machine.marketOrderId !== 0) {
      //  this.$message.error("物品编号为:" + this.numberInput + "的机器已经在单号为--" + machine.marketOrderId + "--的销售单据中了")
      //  return -1
      //}

      //if (machine.statusId !== 26) {
      //  this.$message.error("该机器的状态不是已上架、待出库状态！不能添加到销售单中")
      //  return -1
      //}

      //return 1
    },

    //将机器添加到销售单时，判断该机器是否合法
    marketReturnReceiptJudge(machine) {
      //if (machine.marketReturnReceiptId != null && machine.marketReturnReceiptId !== 0) {
      //  this.$message.error("物品编号为:" + this.numberInput + "的机器已经在单号为--" + machine.marketReturnReceiptId + "--的销售退货单据中了")
      //  return -1
      //}

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
        } else if (this.tableName === 'marketReturn') {
          addMachineToMarketReturnReceipt(ids, this.receiptId).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.machines.push.apply(this.machines, this.scanMachine);
              this.$emit('initShow')
            }
          })
        } else if (this.tableName === 'marketReturnEnterStorage') {
          addMachineToMarketReturnEnterStorageReceipt(ids, this.receiptId).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.machines.push.apply(this.machines, this.scanMachine);
              this.$emit('initShow')
            }
          })
        } else if (this.tableName === 'upShelfEnterStorage') {
          addMachineToUpShelfEnterStorage(ids, this.receiptId).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("提交成功");
              this.machines.push.apply(this.machines, this.scanMachine);
              this.$emit('initShow')
            }
          })
        } else if (this.tableName === 'fixSend') {
          addMachineToFixSendReceipt(this.scanMachine[0], this.receiptId).then(resp => {
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