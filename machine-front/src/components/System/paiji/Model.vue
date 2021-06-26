<template>
  <div>
    <el-form :model="form" label-width="2px" ref="form" class="model-form" :show-message="false">
      <el-table
          :data="form.modelContrast"
          style="width: 100%">
        <el-table-column
            prop="excelModel"
            label="excel"
            style="color: red"
            width="300">
          <template slot-scope="scope">
            <el-form-item :prop="'modelContrast[' + scope.$index +'].excelModel'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.excelModel" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column
            prop="paijiModel"
            label="拍机堂"
            width="500">
          <template slot-scope="scope">
            <el-form-item :prop="'modelContrast[' + scope.$index +'].paijiModel'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.paijiModel" placeholder="请输入内容"></el-input>
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
      <el-button type="primary" icon="el-icon-plus" @click="addModelContrast">添加
      </el-button>
      <el-button type="primary" icon="el-icon-upload" @click="update">更新
      </el-button>
      <el-button type="primary" icon="el-icon-refresh-left" @click="initModelContrast">重置
      </el-button>
    </div>
  </div>
</template>

<script>
import * as paijiApi from "../../../api/paijiApi"

export default {
  name: "Model",
  data() {
    return {
      form: {
        modelContrast: [],
      }
    }
  },
  mounted() {
    this.initModelContrast()
  },
  methods: {
    initModelContrast() {
      paijiApi.getModelContrast().then(resp => {
        if (resp.data.obj != null) {
          this.form.modelContrast = resp.data.obj
        }
      })
    },
    addModelContrast() {
      this.form.modelContrast.push({"excelModel": "", "paijiModel": ""})
    },
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          paijiApi.updateModelContrast(this.form.modelContrast).then(resp => {
            this.$message.success("更新成功")
            this.initModelContrast()
          })
        } else {
          this.$message.error("某个字段为空")
          return false;
        }
      });
    },
    handleDelete(row, index) {
      this.form.modelContrast.splice(index, 1)
    }
  }
}
</script>

<style>
.model-form .el-table--small td, .model-form .el-table--small td {
  padding: 0 !important;
}
</style>