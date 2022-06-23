<template>
  <div>

    <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>
    <!--

        <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                       :search-machine="searchMachine"></MachineSearch>

        <AddMachineByScan v-if="isRelease === 0 && $store.state.userId === operateEmpId" table-name="marketOrder"
                          operate-name="addMachineToMarketOrder"
                          :machines="machines"
                          :receiptId="receiptDetailNumber" @initShow="initMachines"></AddMachineByScan>
    -->

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
    <!--
        <MachineShowDetail ref="child" :machines="machines" :paging="true"
                           :tableName="'marketOrder'" :is-release="isRelease" :extra-not-show="[]"
                           :receipt-id="receiptDetailNumber"></MachineShowDetail>

    -->

    <div>
      <el-table
          :data="machines"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="machineNumber"
            label="物品编码"
            width="170">
        </el-table-column>

        <el-table-column
            prop="machineSku"
            label="SKU"
            width="170">
        </el-table-column>

        <el-table-column
            prop="sellPrice"
            label="销售价格"
            width="170">
        </el-table-column>

        <el-table-column
            prop="operateTime"
            label="操作时间"
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
                @click="detail(scope.row)">详情
            </el-button>

            <el-button
                size="mini"
                type="success"
                @click="editDialogVisible = true; nowEditMachine = scope.row">修改
            </el-button>

            <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.row.machineId)">修改
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
    <el-dialog
        title="编辑销售机器信息"
        :before-close="handleClose"
        :visible.sync="editDialogVisible"
        width="90%">
      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">描述：</h2>
      <el-form status-icon label-width="100px" class="demo-ruleForm">
        <el-form-item label="销售价格"
                      style="margin: 0;">
          <el-input v-model="nowEditMachine.editSellPrice"></el-input>
        </el-form-item>
      </el-form>

      <span style="text-align: right; padding: 10px 20px 20px;     box-sizing: border-box;">
                  <el-button @click="handleClose">取 消</el-button>
                  <el-button type="primary" @click="edit">提交</el-button>
                </span>
    </el-dialog>

    <el-dialog
        title="销售机器信息"
        :before-close="handleClose"
        :visible.sync="dialogVisible"
        width="90%">
      <el-table
          :data="machineInfo"
          style="width: 100%">
        <el-table-column
            prop="name"
            label="属性"
            width="180">
        </el-table-column>

        <el-table-column
            prop="value"
            label="描述">
        </el-table-column>

      </el-table>

      <hr>

      <el-form status-icon label-width="100px" class="demo-ruleForm">
        <el-form-item label="销售价格">
          <el-input v-model="nowEditMachine.sellPrice"></el-input>
        </el-form-item>
      </el-form>

      <span style="text-align: right; padding: 10px 20px 20px;     box-sizing: border-box;">
                  <el-button @click="handleClose">取 消</el-button>
                  <el-button type="primary" @click="submit">提交</el-button>
                </span>
    </el-dialog>

    <MachineShowDetailVertical v-if="showDetail.value" :table-name="tableName" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :machine-detection="showMachineDetection"
                               :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import initMachineCorr from "../../../utils/machineCorr";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import MachineSearch from "../../Machine/MachineSearch.vue";
import {getMachine} from "../../../api/machineApi";
import {getMachineTrace} from "../../../api/machineTraceApi";
import {getMachineDetection} from "../../../api/machineDetection";
import {
  addMachineToMarketOrderReceipt, deleteMachineForMarketOrderReceipt,
} from "../../../api/marketOrderApi";
import {dealMachineJudge} from "../../../utils/dealMachineJudge";
import {getMarketOrderReceiptToMachine, modifyMarketOrderReceiptToMachine} from "../../../api/marketOrderToMachineApi";
import MachineShowDetailVertical from "../../Machine/MachineShowDetailVertical.vue";

export default {
  name: "MarketOrderDetail",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      showMachineDetection: {},
      searchMachine: {},
      machines: [],
      currentPage: 1,
      sizes: 10,
      total: null,
      dialogVisible: false,
      editDialogVisible: false,
      numberInput: "",
      nowEditMachine: {},
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
      machineInfo: []
    }
  },
  props: ['receiptDetailNumber', 'isRelease', 'operateEmpId'],
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initMachines()
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initMachines() {
      //this.searchMachine.marketOrderId = this.receiptDetailNumber;
      getMarketOrderReceiptToMachine(this.currentPage, this.size, this.receiptDetailNumber).then(resp => {
        if (resp.data.code === 200) {
          this.total = resp.data.obj.total;
          this.machines = resp.data.obj.data;
          if (this.machines === undefined) {
            this.machines = []
          }
        }
      })
    },
    initMachineInfo() {
      this.machineInfo = [
        {name: "物品编码", 'value': this.nowEditMachine.number},
        {name: "型号", 'value': this.nowEditMachine.type},
        {name: "IMEI", 'value': this.nowEditMachine.imei},
        {name: "等级", 'value': this.nowEditMachine.rank},
        {name: "采购价", 'value': this.nowEditMachine.purchasePrice},
        {name: "入库时间", 'value': this.nowEditMachine.enterStorageDate},
        {name: "机框描述", 'value': this.nowEditMachine.describe},
        {name: "机器备注", 'value': this.nowEditMachine.comment},
      ]
    },
    cancelAdvSearch() {
      this.searchMachine = {}
      this.searchMachine.purchaseOrderId = this.receiptDetailNumber;
      this.$refs.child.initMachinesByApi(this.searchMachine)
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initDeliverMachines();
    },
    sizeChange(size) {
      this.size = size;
      this.initDeliverMachines();
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
    },
    edit() {
      this.$confirm('是否确定要修改', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
        modifyMarketOrderReceiptToMachine({
          "receiptId": this.receiptDetailNumber,
          "machineId": this.nowEditMachine.machineId,
          "editSellPrice": this.nowEditMachine.editSellPrice
        }).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功");
            this.numberInput = ""
            this.nowEditMachine = {}
            this.editDialogVisible = false
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
    submit() {
      if (this.nowEditMachine.sellPrice.replaceAll(" ", '') === '') {
        this.$message.error("请输入销售价格");
        return
      }
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let temp = JSON.parse(JSON.stringify(this.nowEditMachine))
        addMachineToMarketOrderReceipt([this.nowEditMachine.id], this.receiptDetailNumber, this.nowEditMachine.sellPrice).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功");
            this.numberInput = ""
            this.nowEditMachine = {}
            this.machineInfo = []
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

            dealMachineJudge(machine, this.$store, "fixConfirm").then(resp => {
              if (resp.code === -1) {
                this.$message.error(resp.message);
              } else if (resp.code === 1) {
                machine.editFeatureDesc = []
                machine.editQualityDesc = []
                _this.nowEditMachine = machine
                _this.nowEditMachine.editSellPrice = ""
                _this.initMachineInfo();
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
          }
        })
      }
    },
    handleDelete(machineId) {
      this.$confirm('是否确定要删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMachineForMarketOrderReceipt(machineId, this.receiptDetailNumber).then(resp => {
          if (resp.data.code === 200) {
            this.refresh();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    }
  },
  components: {
    MachineSearch,
    MachineShowDetail,
    AddMachineByScan,
    MachineShowDetailVertical
  }
}
</script>

<style scoped>

</style>