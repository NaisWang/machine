<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="4" style="margin-right: 10px;">
          员工：
          <el-select clearable v-model="searchLog.empId" size="mini" placeholder="员工">
            <el-option
                v-for="id in Object.keys($store.state.employeeNameCorr).map(Number)"
                :label="$store.state.employeeNameCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="14" style="margin-right: 10px;">
          <div>日志日期：</div>
          <el-date-picker
              v-model="logTimeScope"
              type="daterange"
              size="mini"
              unlink-panels
              value-format="yyyy-MM-dd"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期">
          </el-date-picker>
        </el-col>
      </el-row>
      <el-row style="margin-top: 10px;">
        <el-col :span="7" :offset="17">
          <el-button size="mini" icon="el-icon-search" type="primary" @click="initLog">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <el-table :data="logs"
              style="width: 100%">
      <el-table-column
          prop="empId"
          label="工号"
          width="80">
      </el-table-column>
      <el-table-column
          prop="empName"
          label="员工名"
          width="100">
      </el-table-column>
      <el-table-column
          prop="urlDesc"
          label="操作"
          width="200">
      </el-table-column>
      <el-table-column
          prop="parameter"
          label="参数">
      </el-table-column>
      <el-table-column
          prop="time"
          label="time"
          width="200">
      </el-table-column>
      <el-table-column
          prop="result"
          label="是否成功"
          width="50">
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: flex-end">
      <el-pagination
          background
          layout="sizes, prev, pager, next, ->, total"
          @current-change="currentChange"
          @size-change="sizeChange"
          :page-size="20"
          :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {getLog} from "../../api/logApi";

export default {
  name: "操作日志",
  data() {
    return {
      searchLog: {},
      currentPage: 1,
      size: 20,
      logTimeScope: [],
      logs: [],
      total: null
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initLog();
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initLog() {
      getLog(this.currentPage, this.size, this.searchLog, this.logTimeScope).then(resp => {
        if (resp.data.obj) {
          this.total = resp.data.obj.total;
          this.logs = resp.data.obj.data;
        }
      })
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initLog();
    },
    sizeChange(size) {
      this.size = size;
      this.initLog()
    },
  }

}
</script>

<style>
.el-table--small td, .el-table--small td {
  padding: 0 !important;
}
</style>