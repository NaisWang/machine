<template>
  <div>
    <el-button @click="dialogVisible = true">下架机器</el-button>
    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <!--  <MachineShowDetail ref="child" :machines="machines" :paging="true"-->
    <!--                     :tableName="'addNotUpShelfTag'" :extra-not-show="[]"></MachineShowDetail>-->

    <el-table
        :data="machines"
        style="width: 100%">
      <el-table-column
          prop="machineNumber"
          label="物品编码"
          width="170">
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


    <el-dialog
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

      <MachineShowDetail :machines="scanMachines" paging="false" tableName="addNotUpShelfTag"
                         table-operate="add" :extra-not-show="[]"></MachineShowDetail>

      <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false"> 取 消</el-button>
        <el-button type="primary" @click="submit">提 交</el-button>
        </span>
    </el-dialog>

    <MachineShowDetailVertical v-if="showDetail.value" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :machine-detection="showMachineDetection"
                               :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine} from "../../api/machineApi";
import {addMachineRecall, getMachineRecall} from "../../api/machineRecall";
import MachineShowDetailVertical from "../../components/Machine/MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../api/machineTraceApi";
import {addMachineDownShelf, getMachineDownShelf} from "../../api/machineDownShelfApi";
import {getMachineDetection} from "../../api/machineDetection";

export default {
  name: "下架召回",
  data() {
    return {
      showDetail: {"value": false},
      showMachineTrace: {},
      showMachineDetection: {},
      showDetailMachine: {},
      scanMachines: [],
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
      getMachineDownShelf(this.current, this.size, {"nowOperateEmpId": this.$store.state.userId}).then((resp) => {
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
              this.$message.error("这机器现位于" + this.$store.state.subStorageLocationIdToNameCorr[machine.storageLocationId] + ",而你不能管理这个库位的机器");
              return
            }

            if (machine.statusId !== 11) {
              this.$message.error("该机器不是没有上架！！！")
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
        addMachineDownShelf(this.scanMachines).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("添加成功");
            this.numberInput = ""
            this.dialogVisible = false
            this.scanMachines = []
            this.refresh()
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
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}

</style>
