<template>
  <div>
    <el-form :model="form" label-width="2px" ref="form" class="model-form" :show-message="false">

      <div v-for="item in Object.keys(form.machineDescTable)"
           :key="item">
        <el-table
            :data="form.machineDescTable[item]"
            style="width: 100%">
          <el-table-column
              prop="name"
              label="类别"
              style="color: red"
              width="300">
            <template slot-scope="scope">
              <el-form-item :prop="'machineDescTable['+item +'][' + scope.$index +'].name'"
                            style="margin: 0;"
                            :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
                <el-input v-model="scope.row.name" placeholder="请输入内容"></el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column
              prop="value"
              label="值"
              width="300">
            <template slot-scope="scope">
              <el-form-item :prop="'machineDescTable['+item +'][' + scope.$index +'].value'"
                            style="margin: 0;"
                            :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
                <el-input v-model="scope.row.value" placeholder="请输入内容"></el-input>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column
              prop="phone"
              label="手机"
              width="160">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.phone" active-color="#13ce66"
                         inactive-color="#ff4949" active-text="启用"
                         inactive-text="禁用" :active-value="0" :inactive-value="1"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column
              prop="tablet"
              label="平板"
              width="160">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.tablet" active-color="#13ce66"
                         inactive-color="#ff4949" active-text="启用"
                         inactive-text="禁用" :active-value="0" :inactive-value="1"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column
              prop="watch"
              label="手表"
              width="160">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.watch" active-color="#13ce66"
                         inactive-color="#ff4949" active-text="启用"
                         inactive-text="禁用" :active-value="0" :inactive-value="1"
              ></el-switch>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" icon="el-icon-plus" @click="addModelContrast(item)">添加
        </el-button>
      </div>

    </el-form>
    <div style="margin-top: 10px;">
      <el-button type="primary" icon="el-icon-upload" @click="update">更新
      </el-button>
      <el-button type="primary" icon="el-icon-refresh-left" @click="initMachineDescTable">重置
      </el-button>
    </div>
  </div>
</template>

<script>
import {getMachineDescTable} from "../../api/machineDesc";
import {updateMachineDesc} from "../../api/machineDesc";

export default {
  name: "机器描述设置",
  data() {
    return {
      form: {
        machineDescTable: [],
      }
    }
  },
  mounted() {
    this.initMachineDescTable();
  },
  methods: {
    initMachineDescTable() {
      getMachineDescTable().then(resp => {
        if (resp.data.obj) {
          this.form.machineDescTable = resp.data.obj
        }
      })
    },
    addModelContrast(item) {
      this.form.machineDescTable[item].push({'parentValue': item});
    },
    update() {
      this.$confirm('是否确定要更新', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateMachineDesc(this.form.machineDescTable).then(resp => {
          console.log(resp)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消更新'
        });
      });
    }
  }
}
</script>

<style>
.el-table--small td, .desc .el-table--small td {
  padding: 0 !important;
}
</style>