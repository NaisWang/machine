<template>
  <div>
    <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <AddMachineByScan v-if="isRelease === 0 && $store.state.userId === operateEmpId" table-name="upShelfEnterStorage"
                      operate-name="addMachineToUpShelfEnterStorage" :machines="machines"
                      :receiptId="receiptId" @initShow="initMachines"></AddMachineByScan>

    <!--
        <MachineShowDetail ref="child" :machines="machines" :paging="true" :tableName="'upShelfEnterStorage'"
                           :is-release="isRelease" :extra-not-show="[]" :receipt-id="receiptId"></MachineShowDetail>
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
import {addMachineToEnterStorageReceipt} from "../../../api/enterStorageApi";
import MachineShowDetailVertical from "../../Machine/MachineShowDetailVertical.vue";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import MachineSearch from "../../Machine/MachineSearch.vue";
import {getMachine} from "../../../api/machineApi";
import {getMachineTrace} from "../../../api/machineTraceApi";
import {getMachineDetection} from "../../../api/machineDetection";
import {getUpShelfEnterStorageToMachine} from "../../../api/upShelfEnterStorageApi";

export default {
  name: "StorageUpShelfDetail",
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
      total: null
    }
  },
  props: ['receiptId', 'isRelease', 'operateEmpId'],
  mounted() {
    this.initMachines()
  },
  methods: {
    initMachines() {
      this.searchMachine.upShelfEnterStorageId = this.receiptId;
      getUpShelfEnterStorageToMachine(this.currentPage, this.sizes, this.receiptId).then(resp => {
        if (resp.data.code === 200) {
          this.total = resp.data.obj.total
          this.machines = resp.data.obj.data
          if (this.machines === undefined) {
            this.machines = []
          }
        }
      })
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
