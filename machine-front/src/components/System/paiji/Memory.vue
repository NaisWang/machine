<template>
  <div>
    <el-form :model="form" label-width="2px" ref="form" class="model-form" :show-message="false">
      <el-table
          :data="form.memoryContrast"
          style="width: 100%">
        <el-table-column
            prop="machineName"
            label="excel表中机型"
            style="color: red"
            width="300">
          <template slot-scope="scope">
            <el-form-item :prop="'memoryContrast[' + scope.$index +'].machineName'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.machineName" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column
            prop="memory"
            label="内存"
            width="500">
          <template slot-scope="scope">
            <el-select v-model="scope.row.memory" filterable placeholder="请选择">
              <el-option
                  v-for="item in allMemory"
                  :key="item"
                  :label="item"
                  :value="item">
              </el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column
            prop="ahsCheckout"
            label="操作">
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.row, scope.$index)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-form>
    <div style="margin-top: 10px;">
      <el-button type="primary" icon="el-icon-plus" @click="addMemoryContrast">添加
      </el-button>
      <el-button type="primary" icon="el-icon-upload" @click="update">更新
      </el-button>
      <el-button type="primary" icon="el-icon-refresh-left" @click="initMemoryContrast">重置
      </el-button>
    </div>
  </div>
</template>

<script>
import * as paijiApi from "../../../api/paijiApi"

export default {
  name: "Memory",
  data() {
    return {
      form: {
        memoryContrast: [],
      },
      allMemory: ["2G+16G", "3G+32G", "4G+32G", "4G+64G", "6G+64G", "6G+128G", "8G+128G", "8G+256G", "10G+256G"]
    }
  },
  mounted() {
    this.initMemoryContrast()
  },
  methods: {
    initMemoryContrast() {
      paijiApi.getMemoryContrast().then(resp => {
        if (resp.data.obj != null) {
          this.form.memoryContrast = resp.data.obj
        }
      })
    },
    addMemoryContrast() {
      this.form.memoryContrast.push({"machineName": "", "memory": ""})
    },
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$confirm('是否确定要更新', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            paijiApi.updateMemoryContrast(this.form.memoryContrast).then(resp => {
              this.$message.success("更新成功")
              this.initMemoryContrast()
            })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消更新'
            });
          });
        } else {
          this.$message.error("某个字段为空")
          return false;
        }
      });
    },
    handleDelete(row, index) {
      this.form.memoryContrast.splice(index, 1)
    }
  }
}
</script>

<style>
.model-form .el-table--small td, .model-form .el-table--small td {
  padding: 0 !important;
}
</style>
