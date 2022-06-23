<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          采购单号：
          <el-input v-model="searchOrder.purchaseOrder"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入采购单号进行搜索..."
                    @clear="initAllOrderInfo"
                    clearable></el-input>
        </el-col>
        <el-col :span="4" style="margin-right: 10px;">
          采购渠道：
          <el-select clearable v-model="searchOrder.purchaseChannelId" size="mini" placeholder="采购渠道"
                     @clear="initAllOrderInfo">
            <el-option
                v-for="id in Object.keys($store.state.machineChannelCorr).map(Number)"
                :label="$store.state.machineChannelCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="3" style="margin-right: 10px;">
          采购单状态：
          <el-select clearable v-model="searchOrder.purchaseOrderStatus" size="mini" placeholder="采购单状态"
                     @clear="initAllOrderInfo">
            <el-option
                v-for="(item, index) in purchaseOrderStatus"
                :label="item"
                :value="index+1"
                :key="index">
            </el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>
    </div>

    <div>
      <el-button type="primary" icon="el-icon-plus" @click="addReceiptDialogVisible = true">添加采购单</el-button>
      <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>
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
            prop="purchaseOrder"
            label="采购单号"
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
            prop="purchaseChannelId"
            label="采购渠道"
            width="130">
          <template #default="scope">
            <div>
              <el-select size="mini" v-show="isEdit"
                         v-model="scope.row.purchaseChannelId"
                         @change="initUpdateMachine(scope.row, 'purchaseChannelId')">
                <el-option
                    v-for="index in Object.keys($store.state.machineChannelCorr).map(Number)"
                    :key="index"
                    :label="$store.state.machineChannelCorr[index]"
                    :value="index">
                </el-option>
              </el-select>
              <span v-show="!isEdit">{{
                  $store.state.machineChannelCorr[scope.row.purchaseChannelId]
                }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            prop="sum"
            label="采购数量"
            width="70">
        </el-table-column>

        <el-table-column
            prop="sumPrice"
            label="采购金额"
            width="80">
        </el-table-column>

        <el-table-column
            prop="notEnterPurchasePrice"
            label="未入库金额"
            width="80">
        </el-table-column>

        <el-table-column
            prop="enterPurchaseCount"
            label="已入库数量"
            width="70">
        </el-table-column>

        <el-table-column
            prop="notEnterPurchaseCount"
            label="未入库数量"
            width="80">
        </el-table-column>

        <el-table-column
            prop="purchaseOrderStatus"
            label="采购单状态"
            width="70">
          <template #default="scope">
              <span>{{
                  purchaseOrderStatus[scope.row.purchaseOrderStatus]
                }}</span>
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
                v-if="scope.row.isDelete === 0"
                size="mini"
                type="danger"
                @click="handleDelete(scope.row)">删除
            </el-button>

            <el-button
                v-if="scope.row.isRelease === 1"
                size="mini"
                type="primary"
                :disabled="scope.row.purchaseOrderStatus !== 0"
                @click="dialogVisible = true; purchaseOrderForOneKey = scope.row.purchaseOrder">
              一键入库
            </el-button>
          </template>
        </el-table-column>

      </el-table>

    </div>

    <MachineEdit table-name="purchaseOrderReceipt" :machine="editReceipt" :edit-show="editShow"
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
        title="提示"
        :visible.sync="dialogVisible"
        width="70%"
        :before-close="handleClose">
      <div
          style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
        <el-form>
          <el-form-item label="库位">
            <el-select clearable v-model="batchEnterStorageInfo.storageLocationId"
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
            <el-input v-model="batchEnterStorageInfo.comment"
                      size="mini"
                      placeholder="备注信息"
                      clearable></el-input>
          </el-form-item>
        </el-form>
      </div>

      <span slot="footer" class="dialog-footer">
                  <el-button @click="dialogVisible = false">取 消</el-button>
                  <el-button type="primary" @click="batchEnterStorage1()">确 定</el-button>
                </span>
    </el-dialog>

    <el-dialog
        title="采购单信息"
        :visible.sync="addReceiptDialogVisible">

      <el-form ref="form" :model="newReceiptInfo" label-width="80px">

        <el-form-item label="采购渠道">
          <el-select clearable v-model="newReceiptInfo.purchaseChannelId" size="mini" placeholder="采购渠道">
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
import * as purchaseOrder from "../../../api/purchaseOrderReceipt"
import initMachineCorr from "../../../utils/machineCorr";
import * as tab from "../../../utils/tab"
import {createEnterStorageReceiptByOneKey} from "../../../api/enterStorageApi";
import {
  createPurchaseReceipt,
  deletePurchaseOrderReceipt,
  releasePurchaseReceipt
} from "../../../api/purchaseOrderReceipt";
import MachineEdit from "../../Machine/MachineEdit.vue";
import {showDeleteReceiptJudge} from "../../../utils/showDeleteJudge";


export default {
  name: "PurchaseOrderReceiptShow",
  components: {MachineEdit},
  data() {
    return {
      editShow: {"value": false},
      editReceipt: {},
      searchOrder: {},
      allOrderInfo: [],
      purchaseOrderStatus: ["待入库", "已入库", "部分入库"],
      originalValue: {},
      editMachines: [],
      currentPage: 1,
      size: 10,
      total: null,
      drawer: false,
      direction: 'ttb',
      batchEnterStorageInfo: {},
      purchaseOrderForOneKey: null,
      dialogVisible: false,
      addReceiptDialogVisible: false,
      newReceiptInfo: {}
    }
  },
  mounted() {
    this.refresh();
  },
  methods: {
    refresh(type) {
      initMachineCorr(this.$store)
      this.initAllOrderInfo()
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initAllOrderInfo() {
      purchaseOrder.getPurchaseOrderInfo(this.currentPage, this.size, this.searchOrder).then(resp => {
        if (resp.data.obj) {
          this.allOrderInfo = resp.data.obj.data;
          this.total = resp.data.obj.total
        }
      })
    },
    /**
     * 添加修改了的字段到this.editMachines中
     */
    initUpdateMachine(row, fieldName) {
      let flag = 1;
      for (let i = 0; i < this.editMachines.length; i++) {
        let item = this.editMachines[i];
        if (item.id === row.id) {
          if (this.originalValue[row.id][fieldName] + '' === row[fieldName]) {
            delete item[fieldName]
            if (Object.keys(item).length === 1) {
              this.editMachines.splice(i, 1);
              i--;
            }
          } else {
            item[fieldName] = row[fieldName];
          }
          flag = 0;
        }
      }
      if (flag) {
        let machine = {id: row.id}
        machine[fieldName] = row[fieldName];
        this.editMachines.push(machine);
      }
    },
    initOriginValue() {
      this.machines.forEach(item => {
        this.originalValue[item.id] = Object.assign({}, item)
      })
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initAllOrderInfo()
    },
    sizeChange(size) {
      this.size = size;
      this.initAllOrderInfo()
    },
    doSearch() {
      this.currentPage = 1
      this.initAllOrderInfo()
    },
    orderDetail(row) {
      this.$emit('changeComponentFlag', 1, row.purchaseOrder, row.isRelease, row.operateEmpId)
    },
    addPurchaseOrderReceipt() {
      this.$emit('changeComponentFlag', 2)
    },
    batchEnterStorage1() {
      if (this.purchaseOrderForOneKey === null) {
        this.$message.error("提交失败")
        return
      }
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        createEnterStorageReceiptByOneKey(this.batchEnterStorageInfo, this.purchaseOrderForOneKey).then(resp => {
          this.purchaseOrderForOneKey = null
          if (resp.data.code === 200) {
            //this.$emit('func', 0);
            this.dialogVisible = false;
            this.purchaseOrderForOneKey = null
            this.initAllOrderInfo();
            this.$message.success("提交成功")
            return
          }
          this.$message.error("提交失败")
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消保存'
        });
      });
    },
    batchEnterStorage(row) {
      console.log(row);
      if (row.purchaseOrderStatus === 0) {
        this.$router.push("/machine/storage/enter/?oneKeyEnterStoragePurchaseOrder=" + row.purchaseOrder)
        let tabItem = {
          title: "库存入库",
          name: "库存入库",
          path: "/machine/storage/enter/?oneKeyEnterStoragePurchaseOrder=" + row.purchaseOrder
        }
        console.log("aaaaaaaaaaaaaaaaa")
        tab.addTab(tabItem, this.$router, this.$store)
        //getMachine(1, 99999, {"purchaseOrderId": row.purchaseOrderId}).then(resp => {
        //  if(resp.data.obj.data){
        //    this.$router.push("/machine/storage/enter/?oneKeyEnterStorage=1")
        //  }else {
        //    this.$message.error("一键入库失败")
        //  }
        //})
      }
    },
    addReceiptInfo() {
      this.$confirm('是否确定要添加', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        createPurchaseReceipt(this.newReceiptInfo).then(resp => {
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
        releasePurchaseReceipt(row.purchaseOrder).then(resp => {
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
        deletePurchaseOrderReceipt(row.purchaseOrder).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("删除成功");
            this.initAllOrderInfo();
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

<style scoped>

</style>
