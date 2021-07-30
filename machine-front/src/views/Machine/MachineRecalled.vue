<template>
  <div>

    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <el-table
        :data="machines"
        style="width: 100%">
      <el-table-column
          prop="number"
          label="物品编码"
          width="170">
      </el-table-column>

      <el-table-column
          prop="nowOperateEmpId"
          label="召回操作人"
          width="170">
        <template #default="scope">
          {{ $store.state.employeeNameCorr[scope.row.nowOperateEmpId] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="statusId"
          label="召回时的状态"
          width="170">
        <template #default="scope">
          {{ $store.state.machineStatusCorr[scope.row.statusId] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="storageLocationId"
          label="召回时库位"
          width="170">
        <template #default="scope">
          {{ $store.state.subStorageLocationIdToNameCorr[scope.row.storageLocationId] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="recallTime"
          label="时间"
          width="170">
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

    <div style="display: flex; justify-content: flex-end">
      <el-pagination
          background
          layout="sizes, prev, pager, next, ->, total"
          @current-change="currentChange"
          @size-change="sizeChange"
          :total="total">
      </el-pagination>
    </div>

    <MachineShowDetailVertical v-if="showDetail.value" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :show-detail="showDetail"></MachineShowDetailVertical>
  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine} from "../../api/machineApi";
import {addMachineRecall, getMachineRecall} from "../../api/machineRecall";
import MachineShowDetailVertical from "../../components/Machine/MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../api/machineTraceApi";

export default {
  name: "机器被召回",
  data() {
    return {
      showDetail: {"value": false},
      showMachineTrace: {},
      scanMachines: [],
      showDetailMachine: {},
      machines: [],
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
      searchMethod: "number",
      numberInput: "",
      currentPage: 1,
      size: 10,
      total: null
    }
  },
  components: {
    MachineShowDetail,
    MachineShowDetailVertical
  },
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
      getMachineRecall(this.current, this.size, {"preOperateEmpId": this.$store.state.userId}).then((resp) => {
        if (resp.data.obj) {
          this.machines = resp.data.obj.data
          this.total = resp.data.obj.total
        }
      })
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initMachinesByApi(this.searchMachine);
    },
    sizeChange(size) {
      this.size = size;
      this.initMachinesByApi(this.searchMachine);
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
            if (resp.data.obj.total === 0) {
              this.$message.error("没有该机器")
              return
            }
            if (resp.data.obj.total !== 1) {
              this.$message.error("该机器在库存中超过2个")
              return
            }
            let machine = resp.data.obj.data[0]

            if (machine.storageLocationId === null) {
              this.$message.error("该机器还没入库!!!")
              return
            }
            if (this.judgeIsAdd(machine.number) === -1) {
              this.$message.error("该机器已添加了")
              return
            }

            if (Object.keys(this.$store.state.empIdToStorageLocationIdsForGateCorr).indexOf(this.$store.state.userId + '') === -1) {
              this.$message.error("你不是库管门卫, 没有权力使用召回功能")
              return
            }

            if (this.$store.state.empIdToStorageLocationIdsForGateCorr[this.$store.state.userId].indexOf(machine.storageLocationId) === -1) {
              this.$message.error("这机器现位于" + this.$store.state.storageLocationIdToEmpIdsCorr[machine.storageLocationId] + ",而你不能管理这个库位的机器");
              return
            }

            if (machine.operateEmpId === this.$store.state.userId) {
              this.$message.error("该机器的现在操作人就是你，你无需接回！！！")
              return
            }

            this.scanMachines.push(machine);
            this.numberInput = ""
          }
        })
      }
    },
    judgeIsAdd(number) {
      let flag = 1
      this.scanMachines.forEach(item => {
        if (item.number === number) {
          flag = -1
        }
      })
      return flag
    },
    submit() {
      if (this.scanMachines.length === 0) {
        this.$message.error("你还没有添加机器!!!")
        return
      }
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        addMachineRecall(this.scanMachines).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("添加成功");
            this.numberInput = ""
            this.dialogVisible = false
            this.scanMachines = []
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消保存'
        });
      });
    },
    detail(row) {
      getMachine(1, 10, {"number": row.number}).then(resp => {
        this.showDetailMachine = JSON.parse(JSON.stringify(resp.data.obj.data[0]));
        getMachineTrace({"number": row.number}).then(resp => {
          this.showMachineTrace = JSON.parse(JSON.stringify(resp.data.obj))
          this.showDetail.value = true
        })
      })
    },
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}

</style>
