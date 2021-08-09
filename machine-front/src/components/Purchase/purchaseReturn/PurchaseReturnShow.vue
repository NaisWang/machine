<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          退货单号：
          <el-input v-model="searchOrder.purchaseReturnOrder"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入退货单号进行搜索..."
                    @clear="initAllOrderInfo"
                    clearable></el-input>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>
    </div>

    <el-button type="primary" icon="el-icon-plus" @click="addReceiptDialogVisible = true">添加采购退货单</el-button>
    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <div>
      退货失败:
      <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="returnFail"
                v-model="numberInput"></el-input>
    </div>

    <div>
      <el-table
          :data="allOrderInfo"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="purchaseReturnOrder"
            label="退货单号"
            width="170">
        </el-table-column>

        <el-table-column
            prop="createTime"
            label="创建日期"
            width="170">
        </el-table-column>

        <el-table-column
            prop="releaseTime"
            label="发布日期"
            width="170">
        </el-table-column>

        <el-table-column
            prop="sum"
            label="退货总数"
            width="170">
        </el-table-column>

        <el-table-column
            prop="returnAndNotSend"
            label="退货未寄出"
            width="170">
        </el-table-column>

        <el-table-column
            prop="returnAndSend"
            label="退货已寄出"
            width="170">
        </el-table-column>

        <el-table-column
            prop="returnAndProceeds"
            label="退货已收款"
            width="170">
        </el-table-column>

        <el-table-column
            prop="operateEmpId"
            label="操作人"
            width="80">
          <template #default="scope">
              <span>{{
                  $store.state.employeeNameCorr[scope.row.operateEmpId]
                }}</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="comment"
            label="备注">
          <template #default="scope">
            <el-popover
                placement="top-start"
                :width="200"
                trigger="hover"
                :content="scope.row.comment">
              <template #reference>
                <el-button style="width: 90%; text-overflow: ellipsis; overflow: hidden;" v-show="scope.row.comment">
                  {{
                    scope.row.comment
                  }}
                </el-button>
                <el-button style="width: 90%; margin-left: 0;" v-show="!scope.row.comment">无</el-button>
              </template>
              <el-input
                  type="textarea"
                  autosize
                  @change="initUpdateMachine(scope.row, 'describe')"
                  :disabled="!isEdit"
                  :value="scope.row.comment"
                  v-model="scope.row.comment">
              </el-input>
            </el-popover>
          </template>
        </el-table-column>


        <el-table-column label="操作" fixed="right" width="300px">
          <template #default="scope">
            <el-button
                size="mini"
                type="success"
                @click="orderDetail(scope.row)">详情
            </el-button>

            <el-button
                v-if="scope.row.isRelease === 0"
                size="mini"
                type="primary"
                @click="release(scope.row)">发布
            </el-button>

            <el-button
                v-if="scope.row.isRelease === 0"
                size="mini"
                type="info"
                @click="eidt(scope.row)">修改
            </el-button>

            <el-button
                v-if="scope.row.isRelease === 0"
                size="mini"
                type="danger"
                @click="handleDelete(scope.row)">删除
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

    <MachineEdit table-name="purchaseReturn" :machine="editReceipt" :edit-show="editShow"
                 @initShow="initAllOrderInfo"></MachineEdit>

    <el-dialog
        title="采购退货单信息"
        :visible.sync="addReceiptDialogVisible">

      <el-form ref="form" :model="newReceiptInfo" label-width="80px">

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
import {
  createPurchaseReturnReceipt,
  deletePurchaseReturnReceipt,
  getPurchaseReturnReceipt, purchaseReturnError, releasePurchaseReturnReceipt
} from "../../../api/purchaseReturnApi";
import MachineEdit from "../../Machine/MachineEdit.vue";

export default {
  name: "PurchaseReturnShow",
  data() {
    return {
      editShow: {"value": false},
      editReceipt: {},
      addReceiptDialogVisible: false,
      newReceiptInfo: {},
      searchOrder: {},
      allOrderInfo: [],
      currentPage: 1,
      size: 10,
      total: null,
      numberInput: ""
    }
  },
  components: {
    MachineEdit
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
      getPurchaseReturnReceipt(this.currentPage, this.size, this.searchOrder).then(resp => {
        if (resp.data.obj) {
          this.allOrderInfo = resp.data.obj.data
          this.total = resp.data.obj.total
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
      this.$emit('func', 1, row.purchaseReturnOrder, row.isRelease, row.operateEmpId)
    },
    //addReceipt() {
    //  this.$emit('func', 2)
    //},
    addReceiptInfo() {
      this.$confirm('是否确定要添加', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        createPurchaseReturnReceipt(this.newReceiptInfo).then(resp => {
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
    release(row) {
      if (row.sum === 0) {
        this.$message.error("该采购单为空，不能发布");
        return
      }
      this.$confirm('是否确定要发布该退货转交单', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        releasePurchaseReturnReceipt(row.purchaseReturnOrder).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("发布成功");
            this.initAllOrderInfo();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消发布'
        });
      });
    },
    eidt(row) {
      this.editShow.value = true
      this.editReceipt = JSON.parse(JSON.stringify(row))
    },
    handleDelete(row) {
      this.$confirm('是否确定要删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deletePurchaseReturnReceipt(row.purchaseReturnOrder).then(resp => {
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

    },
    returnFail() {
      purchaseReturnError(this.numberInput).then(resp => {
        if (resp.data.code === 200) {
          this.$message.success("操作成功");
          this.initAllOrderInfo();
        }
      })
    }
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>