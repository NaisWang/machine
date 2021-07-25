<template>
  <div>
    <el-form :model="form" label-width="2px" ref="form" class="model-form" :show-message="false">
      <el-table
          :data="form.userInfo"
          style="width: 100%">
        <el-table-column
            prop="username"
            label="电话号码"
            style="color: red"
            width="300">
          <template slot-scope="scope">
            <el-form-item :prop="'userInfo[' + scope.$index +'].username'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.username" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>

        <el-table-column
            prop="password"
            label="密码"
            style="color: red"
            width="300">
          <template slot-scope="scope">
            <el-form-item :prop="'userInfo[' + scope.$index +'].password'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input type="password" v-model="scope.row.password" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>

        <el-table-column
            label="操作">
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.row, scope.$index)">删除
            </el-button>

            <el-button
                size="mini"
                type="primary"
                @click="testConnect(scope.row, scope.$index)">测试连接
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
      <el-button type="primary" icon="el-icon-refresh-left" @click="initUserInfo">重置
      </el-button>
    </div>

  </div>
</template>


<script>
import * as paijiApi from "../../../api/paijiApi";

export default {
  name: "User",
  data() {
    return {
      form: {
        userInfo: [],
      }
    }
  },
  mounted() {
    this.initUserInfo()
  },
  methods: {
    initUserInfo() {
      paijiApi.getPaijiUser().then(resp => {
        if (resp['data']['obj']) {
          this.form.userInfo = resp['data']['obj']
        }
      })
    },
    addModelContrast() {
      this.form.userInfo.push({"username": "", "password": ""})
    },
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$confirm('是否确定要更新', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            paijiApi.updatePaijiUser(this.form.userInfo).then(resp => {
              this.$message.success("更新成功")
              this.initUserInfo()
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
      this.form.userInfo.splice(index, 1)
    },
    testConnect(row) {
      paijiApi.testLogin(row.username, row.password).then(resp => {
        if (resp['data'] === 1) {
          this.$message.success("连接成功")
          return
        }
        this.$message.error("连接失败")
      })
    }
  }

}
</script>

<style scoped>

</style>