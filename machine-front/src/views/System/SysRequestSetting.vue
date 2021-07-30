<template>
  <div>
    <el-table
        :data="urls"
        style="width: 100%">
      <el-table-column
          prop="url"
          label="url"
          width="350">
      </el-table-column>

      <el-table-column
          prop="method"
          label="method"
          width="130">
      </el-table-column>

      <el-table-column
          prop="descr"
          label="desc"
          width="350">
      </el-table-column>

      <el-table-column
          prop="roleIds"
          label="角色">
        <template slot-scope="scope">
          <el-select clearable multiple v-model="scope.row.roleIds" size="mini" placeholder="请选择">
            <el-option
                v-for="id in Object.keys($store.state.rolesCorr).map(Number)"
                :label="$store.state.rolesCorr[id]"
                :value="id+''"
                :key="id">
            </el-option>
          </el-select>
        </template>
      </el-table-column>
    </el-table>

    <el-button @click="submit">提 交</el-button>
  </div>

</template>

<script>
import {getUrls, updateUrl} from "../../api/urlsApi";

export default {
  name: "请求设置",
  data() {
    return {
      urls: []
    }
  },
  mounted() {
    this.initUrls();
  },
  methods: {
    initUrls() {
      getUrls().then(resp => {
        this.urls = resp.data.obj
        this.urls.forEach(item => {
          if (item.roleIds === "") {
            item.roleIds = []
          } else {
            item.roleIds = item.roleIds.split(",");
          }
        })
      })
    },
    submit() {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateUrl(this.urls).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功");
            this.initAllOrderInfo();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消添加'
        });
      });
    }
  }
}
</script>

<style>
.el-table--small td, .el-table--small td {
  padding: 0 !important;
}
</style>