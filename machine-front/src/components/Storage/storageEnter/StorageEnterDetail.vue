<template>
  <div>
    <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <AddMachineByScan v-if="isRelease === 0 && $store.state.userId === operateEmpId" table-name="storageEnter"
                      operate-name="addMachineToEnterStorage"
                      :machines="machines"
                      :receiptId="storageEnterOrderNumber" @initShow="initMachines"></AddMachineByScan>

    <!--
        <MachineShowDetail ref="child" :machines="machines" :paging="true" :tableName="'storageEnter'"
                           :is-release="isRelease" :extra-not-show="[]"
                           :receipt-id="storageEnterOrderNumber"></MachineShowDetail>
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

    <MachineShowDetailVertical v-if="showDetail.value" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :machine-detection="showMachineDetection"
                               :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import initMachineCorr from "../../../utils/machineCorr";
import {addMachineToEnterStorageReceipt, getEnterStorageReceiptToMachine} from "../../../api/enterStorageApi";
import MachineShowDetailVertical from "../../Machine/MachineShowDetailVertical.vue";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import MachineSearch from "../../Machine/MachineSearch.vue";
import {getMachine} from "../../../api/machineApi";
import {getMachineTrace} from "../../../api/machineTraceApi";
import {getMachineDetection} from "../../../api/machineDetection";

export default {
  name: "StorageEnterDetail",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      showMachineDetection: {},
      searchMachine: {},
      machines: [],
      dialogVisible: false,
      showMachineTable: false,
      numberInput: "",
      addMachineInfo: {},
      currentPage: 1,
      sizes: 10,
      total: null,
    }
  },
  props: ['storageEnterOrderNumber', 'isRelease', 'operateEmpId'],
  mounted() {
    this.initMachines()
  },
  methods: {
    initMachines() {
      //this.searchMachine.enterStorageReceiptId = this.storageEnterOrderNumber;
      getEnterStorageReceiptToMachine(this.currentPage, this.sizes, this.storageEnterOrderNumber).then(resp => {
        if (resp.data.code === 200) {
          this.total = resp.data.obj.total
          this.machines = resp.data.obj.data;
        }
      })
      //this.$refs.child.initMachinesByApi(this.searchMachine)
    },
    cancelAdvSearch() {
      this.searchMachine = {}
      this.initMachines()
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initMachines();
    },
    sizeChange(size) {
      this.sizes = size;
      this.initMachines();
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
    //addMachineByScan() {
    //  let _this = this
    //  if (this.numberInput !== "") {
    //    getMachine(1, 10, {"number": this.numberInput}).then(resp => {
    //      if (resp.data.obj) {
    //        if (resp.data.obj.total === 0) {
    //          this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
    //          return
    //        }

    //        //转交判断
    //        if (resp.data.obj.data[0]['deliverReceiptId'] > 0) {
    //          //this.$message.error("该机器正处于转交中!!!")
    //          let deliverJudgeResult = {"code": "", "message": "", "deliverReceipt": {}}
    //          deliverJudge(resp.data.obj.data[0], this.$store, deliverJudgeResult).then(() => {
    //            if (deliverJudgeResult.code === 1) {
    //              receiveDeliverMachine(resp.data.obj.data[0].id).then(resp => {
    //                if (resp.data.code === 200) {
    //                  this.$message.success("接收成功");
    //                  this.numberInput = ""
    //                  this.initAllOrderInfo()
    //                  _this.deal(resp)
    //                }
    //              })
    //            } else {
    //              this.dialogVisible = false
    //              this.$message.error(deliverJudgeResult.message);
    //            }
    //          })
    //        } else {
    //          this.deal(resp)
    //        }
    //      }
    //    })
    //  }
    //},
    //deal(resp) {
    //  //状态判断
    //  let statusId = resp.data.obj.data[0]['statusId']
    //  if ([1, 3, 4].indexOf(statusId) === -1) {
    //    this.$message.error("该机器的状态是" + this.$store.state.machineStatusCorr[statusId] + "不能入库")
    //    return
    //  }
    //  if (statusId === 3) {
    //    this.$confirm('该机器的状态是退货中，是否确定入库', '提示', {
    //      confirmButtonText: '确定',
    //      cancelButtonText: '取消',
    //      type: 'warning'
    //    }).then(() => {
    //      this.addMachineInfo = resp.data.obj.data[0]
    //      this.showMachineTable = true
    //      this.$refs.child1.initMachineTable()
    //      this.numberInput = ""
    //    }).catch(() => {
    //      this.$message({
    //        type: 'info',
    //        message: '已取消入库'
    //      });
    //    });
    //  } else {
    //    this.addMachineInfo = resp.data.obj.data[0]
    //    this.showMachineTable = true
    //    this.$refs.child1.initMachineTable()
    //    this.numberInput = ""
    //  }

    //},
    //addMachines() {
    //  addMachineToEnterStorageReceipt([this.addMachineInfo.id], this.storageEnterOrderNumber).then(resp => {
    //    if (resp.data.code === 200) {
    //      this.$message.success("添加成功");
    //      this.$refs.child.initMachinesByApi(this.searchMachine)
    //      this.numberInput = ""
    //      this.addMachineInfo = {}
    //      this.showMachineTable = false
    //      this.dialogVisible = false;
    //    }
    //  })
    //},
  },
  components: {
    AddMachineByScan,
    MachineShowDetail,
    MachineShowDetailVertical,
    MachineSearch
  }
}
</script>

<style scoped>

</style>