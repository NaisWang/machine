<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          入库单号：
          <el-input v-model="searchOrder.purchaseOrder"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入采购单号进行搜索..."
                    clearable></el-input>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>
    </div>

    <el-button type="primary" icon="el-icon-plus" @click="addReceiptDialogVisible = true">添加入库单</el-button>
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
            prop="enterStorageOrder"
            label="入库单号"
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
            label="数量"
            width="170">
        </el-table-column>

        <el-table-column
            prop="sumPrice"
            label="总价格"
            width="170">
        </el-table-column>

        <el-table-column
            prop="storageLocationId"
            label="库位"
            width="170">
          <template #default="scope">
            {{
              scope.row.storageLocationId === null ? "" : $store.state.subStorageLocationIdToNameCorr[scope.row.storageLocationId]
            }}
          </template>
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

        <el-table-column label="操作" fixed="right" width="250px">
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
                type="danger"
                @click="eidt(scope.row)">修改
            </el-button>

            <el-button
                v-if="scope.row.isDelete === 0"
                size="mini"
                type="danger"
                @click="handleDelete(scope.row)">删除
            </el-button>
          </template>
        </el-table-column>

      </el-table>

    </div>

    <MachineEdit table-name="enterStorageReceipt" :machine="editReceipt" :edit-show="editShow"
                 @initShow="initAllOrderInfo"></MachineEdit>

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
        title="入库单信息"
        :visible.sync="addReceiptDialogVisible">

      <el-form ref="form" :model="newReceiptInfo" label-width="80px">

        <el-form-item label="库位">
          <el-select clearable v-model="newReceiptInfo.storageLocationId"
                     size="mini" placeholder="库位">
            <el-option
                v-for="id in $store.state.empIdToStorageLocationIdsForGateCorr[$store.state.userId]"
                :label="$store.state.subStorageLocationIdToNameCorr[id]"
                :value="id + ''"
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
import * as enterStorageApi from "../../../api/enterStorageApi"
import {
  createEnterStorageReceipt, deleteEnterStorageReceipt,
  deleteMachineForEnterStorageReceipt,
  releaseEnterStorageReceipt
} from "../../../api/enterStorageApi";
import MachineEdit from "../../Machine/MachineEdit.vue";

export default {
  name: "StorageEnterShow",
  data() {
    return {
      editShow: {"value": false},
      editReceipt: {},
      searchOrder: {},
      allOrderInfo: [],
      currentPage: 1,
      size: 10,
      total: null,
      newReceiptInfo: {},
      addReceiptDialogVisible: false
    }
  },
  mounted() {
    this.refresh()
    console.log("fff")
  },
  components: {
    MachineEdit
  },
  methods: {
    refresh(type) {
      this.initAllOrderInfo();
      if (type === 1) {
        this.$message.success("更新成功");
      }
    },
    initAllOrderInfo() {
      enterStorageApi.getEnterStorageReceipt(this.currentPage, this.size, this.searchOrder).then(resp => {
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
      this.$emit('func', 1, row.enterStorageOrder, row.isRelease, row.operateEmpId)
    },
    //addStorageEnterReceipt() {
    //  this.$emit('func', 2)
    //},
    addReceiptInfo() {
      if (this.newReceiptInfo.storageLocationId === "" || this.newReceiptInfo.storageLocationId === undefined) {
        this.$message.error("需要选择库位!!");
        return
      }
      this.$confirm('是否确定要添加', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        createEnterStorageReceipt(this.newReceiptInfo).then(resp => {
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
      this.$confirm('是否确定要发布该转交单', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        releaseEnterStorageReceipt(row.enterStorageOrder).then(resp => {
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
        deleteEnterStorageReceipt(row.enterStorageOrder).then(resp => {
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