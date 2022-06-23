<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          客户名字：
          <el-input v-model="searchOrder.name"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入员工号进行搜索..."
                    clearable></el-input>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>
    </div>

    <el-button type="primary" icon="el-icon-plus" @click="addReceiptDialogVisible = true">添加客户</el-button>
    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <div>
      <el-table
          :data="clientAll"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="id"
            label="客户id"
            width="170">
        </el-table-column>

        <el-table-column
            prop="name"
            label="客户名字"
            width="170">
        </el-table-column>

        <el-table-column
            prop="purchaseSumPrice"
            label="采购金钱"
            width="170">
        </el-table-column>

        <el-table-column
            prop="marketSellPrice"
            label="销售金额"
            width="170">
        </el-table-column>

        <el-table-column
            prop="marketRefundPrice"
            label="销售退货"
            width="170">
        </el-table-column>

        <el-table-column
            prop="comment"
            label="备注"
            width="170">
        </el-table-column>

        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button
                size="mini"
                type="danger"
                v-if=""
                @click="edit(scope.row)">修改
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
        title="编辑渠道信息"
        :visible.sync="editEmpDialogVisible">

      <el-form ref="form" :model="nowEditEmp" label-width="80px">

        <el-form-item label="姓名">
          <el-input type="text" v-model="nowEditEmp.name" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="text" v-model="newReceiptInfo.comment" size="mini"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
                              <el-button @click="editEmpDialogVisible = false">取 消</el-button>
                              <el-button type="primary" @click="submit">提 交</el-button>
                            </span>
    </el-dialog>

    <el-dialog
        title="客户信息"
        :visible.sync="addReceiptDialogVisible">

      <el-form ref="form" :model="newReceiptInfo" label-width="80px">

        <el-form-item label="姓名">
          <el-input type="text" v-model="newReceiptInfo.name" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="text" v-model="newReceiptInfo.comment" size="mini"></el-input>
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
import {addChannel, deleteChannel, getChannelAndMoney, modifyChannel} from "../../api/channelApi";

export default {
  name: "客户管理",
  data() {
    return {
      editEmpDialogVisible: false,
      addReceiptDialogVisible: false,
      newReceiptInfo: {},
      searchOrder: {},
      clientAll: [],
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
      console.log("fff");
      getChannelAndMoney(this.currentPage, this.size, this.searchOrder).then(resp => {
        if (resp.data.obj) {
          console.log(resp.data);
          this.clientAll = resp.data.obj.data
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
        addChannel(this.newReceiptInfo).then(resp => {
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
    edit(row) {
      this.nowEditEmp = JSON.parse(JSON.stringify(row));
      this.editEmpDialogVisible = true
    },
    submit() {
      this.$confirm('是否确定要修改', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        modifyChannel(this.nowEditEmp).then(resp => {
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
        deleteChannel(row.id).then(resp => {
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
