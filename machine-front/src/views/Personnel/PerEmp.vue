<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          员工名字：
          <el-input v-model="searchOrder.marketOrder"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入员工号进行搜索..."
                    clearable></el-input>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>
    </div>

    <el-button type="primary" icon="el-icon-plus" @click="addReceiptDialogVisible = true">添加员工</el-button>
    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <div>
      <el-table
          :data="allEmps"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="id"
            label="员工id"
            width="170">
        </el-table-column>

        <el-table-column
            prop="name"
            label="员工名字"
            width="170">
        </el-table-column>

        <el-table-column
            prop="role"
            label="角色"
            width="170">
          <template #default="scope">
            <el-tag type="success" v-for="(role, index) in scope.row.roles" :key="index" style="margin-left: 5px;">
              {{ role.zhName }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button
                size="mini"
                type="danger"
                v-if=""
                @click="eidt(scope.row)">修改
            </el-button>
          </template>
        </el-table-column>

      </el-table>

    </div>

    <div style="display: flex; justify-content: flex-end">
      <el-pagination
          background
          :current-page="currentPage"
          layout="sizes, prev, pager, next, ->, total"
          @current-change="currentChange"
          @size-change="sizeChange"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog
        title="编辑用户信息"
        :visible.sync="editEmpDialogVisible">

      <el-form ref="form" :model="nowEditEmp" label-width="80px">

        <el-form-item label="姓名">
          <el-input type="text" v-model="nowEditEmp.name" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input type="text" v-model="nowEditEmp.username" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select clearable v-model="nowEditEmp.allRoleIds" size="mini" multiple placeholder="角色">
            <el-option
                v-for="id in Object.keys($store.state.rolesCorr).map(Number)"
                :label="$store.state.rolesCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
                              <el-button @click="editEmpDialogVisible = false">取 消</el-button>
                              <el-button type="primary" @click="submit">提 交</el-button>
                            </span>
    </el-dialog>

    <el-dialog
        title="员工信息"
        :visible.sync="addReceiptDialogVisible">

      <el-form ref="form" :model="newReceiptInfo" label-width="80px">

        <el-form-item label="姓名">
          <el-input type="text" v-model="newReceiptInfo.name" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input type="text" v-model="newReceiptInfo.username" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="newReceiptInfo.password" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select clearable v-model="newReceiptInfo.allRoleIds" size="mini" multiple placeholder="角色">
            <el-option
                v-for="id in Object.keys($store.state.rolesCorr).map(Number)"
                :label="$store.state.rolesCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
                              <el-button @click="addReceiptDialogVisible = false">取 消</el-button>
                              <el-button type="primary" @click="addReceiptInfo">添 加</el-button>
                            </span>
    </el-dialog>

  </div>
</template>

<script>
import {addEmp, getAllEmp, updateEmp} from "../../api/employeeApi";

export default {
  name: "员工资料",
  data() {
    return {
      editEmpDialogVisible: false,
      addReceiptDialogVisible: false,
      newReceiptInfo: {},
      searchOrder: {},
      allEmps: [],
      currentPage: 1,
      size: 10,
      total: null,
      keywords: "",
      nowEditEmp: {}
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initAllOrderInfo();
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initAllOrderInfo() {
      getAllEmp().then(resp => {
        if (resp.data.obj) {
          this.allEmps = resp.data.obj
        }
      })
    },
    doSearch() {
      this.currentPage = 1
      this.initAllOrderInfo()
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initAllOrderInfo();
    },
    sizeChange(size) {
      this.size = size;
      this.initAllOrderInfo();
    },
    orderDetail(row) {
      this.$emit('func', 1, row.marketOrder, row.isRelease)
    },
    addReceipt() {
      this.$emit('func', 2)
    },
    addReceiptInfo() {
      this.$confirm('是否确定要添加', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        addEmp(this.newReceiptInfo).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("创建成功");
            this.initAllOrderInfo();
            this.addReceiptDialogVisible = false;
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消添加'
        });
      });
    },
    eidt(row) {
      this.nowEditEmp = JSON.parse(JSON.stringify(row));
      this.nowEditEmp.allRoleIds = []
      let roles = row.roles;
      roles.forEach(r => {
        this.nowEditEmp.allRoleIds.push(r.id);
      })
      this.editEmpDialogVisible = true
    },
    submit() {
      this.$confirm('是否确定要修改', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateEmp(this.nowEditEmp).then(resp => {
          if (resp.data.code === 200) {
            this.nowEditEmp = {}
            this.$message.success("删除成功");
            this.initAllOrderInfo()
            this.editEmpDialogVisible = false
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消发布'
        });
      });
    },
    handleDelete(row) {
      this.$confirm('是否确定要删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMarketOrderReceipt(row.marketOrder).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("删除成功");
            this.initAllOrderInfo()
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消发布'
        });
      });

    }
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>
