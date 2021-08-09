<template>
  <div>
    <el-button @click="addNotUpShelfTag">添加‘不上架’标签</el-button>
    <el-button @click="removeNotUpShelfTag">去掉‘不上架’标签</el-button>
    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <MachineShowDetail ref="child" :machines="machines" :paging="true"
                       :tableName="'addNotUpShelfTag'" :extra-not-show="[]"></MachineShowDetail>

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
  </div>

</template>

<script>
import AddMachineByScan from "../../components/Machine/AddMachineByScan.vue";
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine, modifyCanUpShelf} from "../../api/machineApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";

export default {
  name: "不上架机器",
  data() {
    return {
      scanRemoveNotUpShelfTagMachines: [],
      scanAddNotUpShelfTagMachines: [],
      scanMachines: [],
      machines: [],
      method: 1,
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
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initNotUpShelfMachines()
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    removeNotUpShelfTag() {
      this.numberInput = ""
      this.method = 0
      this.scanMachines = this.scanRemoveNotUpShelfTagMachines;
      this.dialogVisible = true;
    },
    addNotUpShelfTag() {
      this.numberInput = ""
      this.method = 1
      this.scanMachines = this.scanAddNotUpShelfTagMachines;
      this.dialogVisible = true;
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initMachinesByApi(this.searchMachine);
    },
    sizeChange(size) {
      this.size = size;
      this.initMachinesByApi(this.searchMachine);
    },
    initNotUpShelfMachines() {
      this.$refs.child.initMachinesByApi({"isUpShelf": 1})
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
              this.$message.error("该机器已添加了")
              return
            }

            let machine = resp.data.obj.data[0]

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
        modifyCanUpShelf(this.scanMachines, this.method).then(resp => {
          if (resp.data.code === 200) {
            this.numberInput = ""
            if (this.method === 0) {
              this.scanRemoveNotUpShelfTagMachines = []
            } else {
              this.scanAddNotUpShelfTagMachines = []
            }
            this.dialogVisible = false
            this.$message.success("添加成功");
            this.refresh()
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消保存'
        });
      });
    }
  },
  components: {
    AddMachineByScan,
    MachineShowDetail
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>