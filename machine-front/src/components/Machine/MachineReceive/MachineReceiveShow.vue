<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          转交单号：
          <el-input v-model="searchOrder.deliverReceiptId"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入转交单号进行搜索..."
                    clearable></el-input>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>
    </div>

    <div>
      <span>扫码接收</span>
      <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="receive"
                v-model="numberInput"></el-input>
      <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>
    </div>

    <div>
      <el-table
          :data="allOrderInfo"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="deliverReceiptId"
            label="转交订单单号"
            width="170">
        </el-table-column>

        <el-table-column
            prop="deliverIntentionId"
            label="转交单类别"
            width="170">
          <template #default="scope">
              <span>{{
                  $store.state.deliverIntentionCorr[scope.row.deliverIntentionId]
                }}</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="deliverDate"
            label="转交订单日期"
            width="170">
        </el-table-column>

        <el-table-column
            prop="sum"
            label="转交机器总数"
            width="170">
        </el-table-column>

        <el-table-column
            prop="notReceiveSum"
            label="转交未接收机器总数"
            width="170">
        </el-table-column>

        <el-table-column
            prop="receiveEmpIds"
            label="接收人(组)"
            width="170">
          <template #default="scope">
              <span v-for="item in (scope.row.receiveEmpIds === null ? '' : scope.row.receiveEmpIds.split(','))"
                    :key="item">{{
                  $store.state.employeeNameCorr[item] + '、'
                }}</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="operateEmpId"
            label="发起人"
            width="170">
          <template #default="scope">
            <span>{{ $store.state.employeeNameCorr[scope.row.operateEmpId] }}</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="comment"
            label="备注"
            width="120">
          <template #default="scope">
            <el-popover
                placement="top-start"
                :width="200"
                trigger="hover"
                :content="scope.row.comment">
              <template #reference>
                <el-button style="width: 80px; text-overflow: ellipsis; overflow: hidden;" v-show="scope.row.comment">
                  {{
                    scope.row.comment
                  }}
                </el-button>
                <el-button style="width: 80px" v-show="!scope.row.comment">无</el-button>
              </template>
              <el-input
                  type="textarea"
                  autosize
                  @change="initUpdateMachine(scope.row, 'describe')"
                  :disabled="!isEdit"
                  :value="scope.row.comment"
                  v-model="scope.row.comment">
              </el-input>
            </el-popover>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button
                size="mini"
                type="success"
                @click="orderDetail(scope.row)">详情
            </el-button>
          </template>
        </el-table-column>

      </el-table>

    </div>

    <div style="display: flex; justify-content: flex-end">
      <el-pagination
          background
          :current-page="currentPage"
          layout="sizes, prev, pager, next, ->, total"
          @current-change="currentChange"
          @size-change="sizeChange"
          :total="total">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import {getDeliverReceipt, getReceiveReceipt} from "../../../api/deliverReceiptApi";
import {getMachine} from "../../../api/machineApi";
import {deliverJudge} from "../../../utils/deliverJudge";
import {receiveDeliverMachine} from "../../../api/deliverMachineApi";
import {dealMachineJudge} from "../../../utils/dealMachineJudge";

export default {
  name: "MachineReceiveShow",
  data() {
    return {
      searchOrder: {},
      allOrderInfo: [],
      currentPage: 1,
      size: 10,
      total: null,
      numberInput: "",
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initAllOrderInfo();
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initAllOrderInfo() {
      getReceiveReceipt(this.currentPage, this.size, this.searchOrder, 1).then(resp => {
        if (resp.data.obj) {
          this.allOrderInfo = resp.data.obj.data
          this.total = resp.data.obj.total
        }
      })
    },
    doSearch() {
      this.currentPage = 1
      this.initAllOrderInfo()
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initAllOrderInfo();
    },
    sizeChange(size) {
      this.size = size;
      this.initAllOrderInfo();
    },
    orderDetail(row) {
      this.$emit('func', 1, row.deliverReceiptId, row.enableEdit)
    },
    receive() {
      if (this.numberInput !== "") {
        getMachine(1, 10, {"number": this.numberInput}).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
              return
            }

            //let nowEditMachine = resp.data.obj.data[0]
            let machine = resp.data.obj.data[0];

            if (machine.deliverReceiptId === 0) {
              this.$message.error("该机器状态不是转交状态")
              return
            }
            dealMachineJudge(machine, this.$store, '').then(resp => {
              if (resp.code === -1) {
                this.$message.error(resp.message);
              }
              if (resp.receiveDiagLog !== undefined) {
                let _this = this
                setTimeout(function () {
                  _this.$notify(resp.receiveDiagLog)
                }, 50);
              }
              //receiveDeliverMachine(machine.id).then(resp => {
              //  if (resp.data.code === 200) {
              //    this.$message.success("接收成功");
              //    this.numberInput = ""
              //    this.initAllOrderInfo()
              //  }
              //})
            })

            //let deliverJudgeResult = {"code": "", "message": "", "deliverReceipt": {}}
            //if (nowEditMachine.deliverMachineId === 0) {
            //  //状态判断
            //  this.$message.error("该机器状态不是转交状态")
            //} else {
            //  //转交判断
            //  deliverJudge(nowEditMachine, this.$store, deliverJudgeResult).then(() => {
            //    if (deliverJudgeResult.code === 1) {
            //      receiveDeliverMachine(nowEditMachine.id).then(resp => {
            //        if (resp.data.code === 200) {
            //          this.$message.success("接收成功");
            //          this.numberInput = ""
            //          this.initAllOrderInfo()
            //        }
            //      })
            //    } else {
            //      this.$message.error(deliverJudgeResult.message);
            //    }
            //  })
            //}
          }
        })
      }
    }
  },
}
</script>

<style scoped>

</style>