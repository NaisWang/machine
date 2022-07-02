<template>
  <div>
    <el-form :model="form" label-width="2px" ref="form" class="model-form" :show-message="false">
      <el-table
          :data="form.colorContrast"
          style="width: 100%">
        <el-table-column
            prop="machineName"
            label="excel表中机型"
            style="color: red"
            width="300">
          <template slot-scope="scope">
            <el-form-item :prop="'colorContrast[' + scope.$index +'].machineName'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.machineName" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column
            prop="excelColor"
            label="excel表中颜色"
            width="500">
          <template slot-scope="scope">
            <el-form-item :prop="'colorContrast[' + scope.$index +'].excelColor'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.excelColor" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column
            prop="paijiColor"
            label="拍机堂中颜色"
            width="500">
          <template slot-scope="scope">
            <el-form-item :prop="'colorContrast[' + scope.$index +'].paijiColor'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.paijiColor" placeholder="请输入内容"></el-input>
            </el-form-item>
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
      <el-button type="primary" icon="el-icon-plus" @click="addColorContrast">添加
      </el-button>
      <el-button type="primary" icon="el-icon-upload" @click="update">更新
      </el-button>
      <el-button type="primary" icon="el-icon-refresh-left" @click="initColorContrast">重置
      </el-button>
    </div>
  </div>
</template>

<script>
import * as paijiApi from "../../../api/paijiApi"

export default {
  name: "Color",
  data() {
    return {
      form: {
        colorContrast: [],
      }
    }
  },
  mounted() {
    this.initColorContrast()
  },
  methods: {
    initColorContrast() {
      paijiApi.getColorContrast().then(resp => {
        if (resp.data.obj != null) {
          this.form.colorContrast = resp.data.obj
        }
      })
    },
    addColorContrast() {
      this.form.colorContrast.push({"machineName": "", "excelColor": "", "paijiColor": ""})
    },
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$confirm('是否确定要更新', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            paijiApi.updateColorContrast(this.form.colorContrast).then(resp => {
              this.$message.success("更新成功")
              this.initColorContrast()
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
      this.form.colorContrast.splice(index, 1)
    }
  }
}
</script>

<style>
.model-form .el-table--small td, .model-form .el-table--small td {
  padding: 0 !important;
}
</style>
