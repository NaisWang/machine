<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          上架单号：
          <el-input v-model="searchOrder.upShelfOrder"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入上架单号进行搜索..."
                    @clear="initAllOrderInfo"
                    clearable></el-input>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>

    </div>

    <el-button type="primary" icon="el-icon-plus" @click="addReceipt">添加上架单号</el-button>
    <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>

    <div>
      <el-table
          :data="allOrderInfo"
          style="width: 100%">
        <el-table-column
            prop="upShelfOrder"
            label="上架单号"
            width="170">
        </el-table-column>

        <el-table-column
            prop="upShelfDate"
            label="上架日期"
            width="170">
        </el-table-column>

        <el-table-column
            prop="sum"
            label="上架总数"
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

        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button
                size="mini"
                type="success"
                @click="orderDetail(scope.row)">详情
            </el-button>
          </template>
        </el-table-column>

      </el-table>

    </div>
  </div>
</template>

<script>
import {getUpShelfReceipt} from "../../../api/upShelfReceiptApi";

export default {
  name: "RoutineShelfShow",
  data() {
    return {
      searchOrder: {},
      allOrderInfo: [],
      currentPage: 1,
      size: 10,
      total: null,
    }
  },
  mounted() {
    this.refresh();
  },
  methods: {
    refresh(type) {
      this.initAllOrderInfo();
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initAllOrderInfo() {
      getUpShelfReceipt(this.currentPage, this.size, this.searchOrder).then(resp => {
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
      this.$emit('func', 1, row.purchaseReturnOrder)
    },
    addReceipt() {
      this.$emit('func', 2)
    }
  }
}
</script>

<style scoped>

</style>
