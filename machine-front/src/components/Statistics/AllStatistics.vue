<template>
  <div>
    <el-form style="display: inline-block">
      <el-form-item label="员工" style="display: inline-block">
        <el-select clearable multiple v-model="empIds" size="mini" placeholder="员工">
          <el-option label='全选' value='全选' @click.native='selectAllEmpIds'></el-option>
          <el-option
              v-for="index in Object.keys($store.state.employeeNameCorr).map(Number)"
              :key="index"
              :label="$store.state.employeeNameCorr[index]"
              :value="index">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="状态" style="display: inline-block">
        <el-select clearable multiple v-model="statusIds" size="mini">
          <el-option label='全选' value='全选' @click.native='selectAllStatusIds'></el-option>
          <el-option
              v-for="index in Object.keys($store.state.machineStatusCorr).map(Number)"
              :key="index"
              :label="$store.state.machineStatusCorr[index]"
              :value="index">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="天数" style="display: inline-block">
        <el-select clearable multiple v-model="dateScope" size="mini">
          <el-option label='全选' value='全选' @click.native='selectAllDateScope'></el-option>
          <el-option
              v-for="index in Object.keys(dateScopeCorr).map(Number)"
              :key="index"
              :label="dateScopeCorr[index]"
              :value="index">
          </el-option>
        </el-select>
      </el-form-item>

    </el-form>

    <el-button @click="search">查询</el-button>

    <el-table
        :data="allStatistics"
        style="width: 100%">
      <el-table-column
          prop="operateEmpId"
          label="员工"
          width="170">
        <template #default="scope">
          {{ $store.state.employeeNameCorr[scope.row.operateEmpId] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="statusId"
          label="状态"
          width="170">
        <template #default="scope">
          {{ $store.state.machineStatusCorr[scope.row.statusId] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="dateScope"
          label="天数"
          width="170">
        <template #default="scope">
          {{ dateScopeCorr[scope.row.dateScope] }}
        </template>
      </el-table-column>

      <el-table-column
          prop="sum"
          label="数量"
          width="170">
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import {getAllStatistics} from "../../api/statisticsApi";

export default {
  name: "数据报表",
  data() {
    return {
      empIds: [],
      statusIds: [],
      dateScope: [],
      dateScopeCorr: {
        0: "当天",
        1: "一周",
        2: "一月",
        3: "全部"
      },
      allStatistics: [],
    }
  },
  methods: {
    initAllStatistics() {

    },
    search() {
      if (this.empIds.length === 0) {
        this.$message.success("请选择员工")
        return
      }
      if (this.statusIds.length === 0) {
        this.$message.success("请选择状态")
        return
      }
      if (this.dateScope.length === 0) {
        this.$message.success("请选择天数")
        return
      }
      getAllStatistics(this.empIds, this.statusIds, this.dateScope).then(resp => {
        if (resp.data.code === 200) {
          this.allStatistics = resp.data.obj
        }
      })
    },
    selectAllEmpIds() {
      this.empIds = []
      Object.keys(this.$store.state.employeeNameCorr).map(Number).forEach(item => {
        this.empIds.push(item);
      })
    },
    selectAllStatusIds() {
      this.statusIds = []
      Object.keys(this.$store.state.machineStatusCorr).map(Number).forEach(item => {
        this.statusIds.push(item);
      })
    },
    selectAllDateScope() {
      this.dateScope = [0, 1, 2, 3]
    }
  }
}
</script>

<style scoped>

</style>
