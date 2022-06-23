<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          销售单号：
          <el-input v-model="searchOrder.marketOrder"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入销售单号进行搜索..."
                    clearable></el-input>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>
    </div>


    <el-button type="primary" icon="el-icon-plus" @click="addReceiptDialogVisible = true">添加销售单</el-button>
    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <div>
      <el-table
          :data="allOrderInfo"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="marketOrder"
            label="销售订单单号"
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
            prop="channelId"
            label="销售渠道"
            width="130">
          <template #default="scope">
              <span>{{
                  $store.state.machineChannelCorr[scope.row.channelId]
                }}</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="sum"
            label="销售总数"
            width="170">
        </el-table-column>

        <el-table-column
            prop="saleSumPrice"
            label="销售金额"
            width="170">
        </el-table-column>

        <el-table-column
            prop="purchaseSumPrice"
            label="采购金额"
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
            label="备注"
            width="120">
          <template #default="scope">
            <el-popover
                placement="top-start"
                :width="200"
                trigger="hover"
                :content="scope.row.comment">
              <template #reference>
                <el-button style="width: 80px; text-overflow: ellipsis; overflow: hidden;" v-show="scope.row.comment">
                  {{
                    scope.row.comment
                  }}
                </el-button>
                <el-button style="width: 80px" v-show="!scope.row.comment">无</el-button>
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

        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button
                size="mini"
                type="success"
                @click="orderDetail(scope.row)">详情
            </el-button>

            <span v-if="scope.row.isRelease === 0">
              <el-button
                  size="mini"
                  type="primary"
                  @click="release(scope.row)">发布
              </el-button>

              <el-button
                  size="mini"
                  type="danger"
                  @click="edit(scope.row)">修改
              </el-button>

              <el-button
                  size="mini"
                  type="danger"
                  @click="handleDelete(scope.row)">删除
              </el-button>
            </span>

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

    <MachineEdit table-name="marketOrder" :machine="editReceipt" :edit-show="editShow"
                 @initShow="initAllOrderInfo"></MachineEdit>

    <el-dialog
        title="销售单信息"
        :visible.sync="addReceiptDialogVisible">

      <el-form ref="form" :model="newReceiptInfo" label-width="80px">

        <el-form-item label="销售渠道">
          <el-select clearable v-model="newReceiptInfo.channelId" size="mini" placeholder="采购渠道">
            <el-option
                v-for="id in Object.keys($store.state.machineChannelCorr).map(Number)"
                :label="$store.state.machineChannelCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
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
import {
  createMarketOrderReceipt,
  createMarketReturnReceipt,
  deleteMarketOrderReceipt,
  getMarketOrderReceipt,
  releaseMarketOrderReceipt
} from "../../../api/marketOrderApi"
import MachineEdit from "../../Machine/MachineEdit.vue";

export default {
  name: "MarketOrderShow",
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
      getMarketOrderReceipt(this.currentPage, this.size, this.searchOrder).then(resp => {
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
      this.$emit('func', 1, row.marketOrder, row.isRelease, row.operateEmpId)
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
        createMarketOrderReceipt(this.newReceiptInfo).then(resp => {
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
      this.editShow.value = true
      this.editReceipt = JSON.parse(JSON.stringify(row))
    },
    release(row) {
      if (row.sum === 0) {
        this.$message.error("该采购单为空，不能发布");
        return
      }
      this.$confirm('是否确定要发布该销售单', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        releaseMarketOrderReceipt(row.marketOrder).then(resp => {
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