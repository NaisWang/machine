<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          转交单号：
          <el-input v-model="searchOrder.deliverReceiptId"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入转交单号进行搜索..."
                    clearable></el-input>
        </el-col>
      </el-row>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()">搜索
      </el-button>
    </div>

    <el-button type="primary" icon="el-icon-plus" @click="addReceipt">添加转交单</el-button>
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
            label="是否完成"
            width="80">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.completeSum !== scope.row.sum">没有</el-tag>
            <el-tag v-else-if="scope.row.completeSum === scope.row.sum">完成</el-tag>
          </template>
        </el-table-column>

        <el-table-column
            prop="deliverReceiptId"
            label="转交订单单号"
            width="170">
        </el-table-column>

        <el-table-column
            prop="deliverIntentionId"
            label="转交单类别"
            width="170">
          <template #default="scope">
              <span>{{
                  $store.state.deliverIntentionCorr[scope.row.deliverIntentionId]
                }}</span>
          </template>
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
            label="转交机器总数"
            width="170">
        </el-table-column>

        <el-table-column
            prop="notReceiveSum"
            label="转交未接收机器总数"
            width="170">
        </el-table-column>

        <el-table-column
            prop="completeSum"
            label="完成指标数"
            width="170">
        </el-table-column>

        <el-table-column
            prop="operateEmpId"
            label="发起人"
            width="170">
          <template #default="scope">
            <span>{{ $store.state.employeeNameCorr[scope.row.operateEmpId] }}</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="receiveEmpIds"
            label="接收人(组)"
            width="170">
          <template #default="scope">
              <span v-for="item in (scope.row.receiveEmpIds === null ? '' : scope.row.receiveEmpIds.split(','))"
                    :key="item">{{
                  $store.state.employeeNameCorr[item] + '、'
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

            <el-button
                size="mini"
                type="primary"
                v-if="scope.row.enableEdit === 0"
                @click="release(scope.row)">发布
            </el-button>

            <el-button
                size="mini"
                type="danger"
                v-if="scope.row.isDelete === 0"
                @click="handleDelete(scope.row)">删除
            </el-button>

            <el-button
                v-if="scope.row.enableEdit === 1"
                size="mini"
                type="primary"
                disabled>已发布
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
        title="转交单信息"
        :visible.sync="addReceiptDialogVisible">

      <el-form ref="form" :model="newDeliverReceiptInfo" label-width="80px">

        <el-form-item label="类别">
          <el-select clearable v-model="newDeliverReceiptInfo.deliverIntentionId" size="mini" placeholder="类别"
                     @change="typeChange">
            <el-option
                v-for="id in deliverIntention"
                :label="$store.state.deliverIntentionCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="接收人">
          <el-select v-model="newDeliverReceiptInfo.receiveEmpIds" multiple placeholder="请选择">
            <el-option
                v-for="item in canSelectReceiveEmpIds"
                :key="item"
                :label="$store.state.employeeNameCorr[item]"
                :value="item">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="备注">
          <el-input type="text" v-model="newDeliverReceiptInfo.comment" size="mini"></el-input>
        </el-form-item>

        <!--        <el-form-item label="当前库位">-->
        <!--          <el-tag>{{-->
        <!--              $store.state.storageLocationCorr[originStorageLocationId]-->
        <!--            }}-->
        <!--          </el-tag>-->
        <!--        </el-form-item>-->

        <!--        <el-form-item label="目标库位">-->
        <!--          <el-tag v-if="targetStorageLocationId != null">-->
        <!--            {{ $store.state.storageLocationCorr[targetStorageLocationId] }}-->
        <!--          </el-tag>-->
        <!--        </el-form-item>-->

      </el-form>

      <span slot="footer" class="dialog-footer">
                              <el-button @click="addReceiptDialogVisible= false">取 消</el-button>
                              <el-button type="primary" @click="addReceiptInfo">添 加</el-button>
                            </span>
    </el-dialog>

  </div>
</template>

<script>
import {getDeliverReceipt} from "../../../api/deliverReceiptApi";
import {createDeliverReceipt} from "../../../api/deliverReceiptApi";
import {deleteDeliverReceipt} from "../../../api/deliverReceiptApi";
import {releaseDeliverReceipt} from "../../../api/deliverReceiptApi";

export default {
  name: "MachineDeliverShow",
  data() {
    return {
      searchOrder: {},
      allOrderInfo: [],
      currentPage: 1,
      size: 10,
      total: null,
      newDeliverReceiptInfo: {},
      addReceiptDialogVisible: false,
      deliverIntention: [],
      deliverTypeRoleCorr: {
        // 转交单类型id - 能接收该转交单的角色id
        // 采购退货
        1: [9],
        //检测
        3: [2, 3],
        //成色检测
        4: [2],
        //成色检测
        12: [2],
        //功能检测
        5: [3],
        //上架
        6: [8],
        // 销售
        9: [1],
        // 维修
        10: [6],
        //退回
        11: [10],
        //库存入库
        2: [10],
        //上架入库
        7: [10],
        // 销退入库
        8: [10],
        // 送外修
        13: [13]
      },
      deliverTypeLaunchCorr: {
        // 转交单类型id - 能发起该转交单的角色id
        // 采购退货
        1: [9, 4, 10, 11],
        //检测
        3: [2, 3, 4, 8, 10, 11],
        //成色检测
        4: [2, 3, 4, 8, 10, 11],
        //成色检测(不上架)
        12: [2, 3, 4, 8, 10, 11],
        //功能检测
        5: [2, 3, 4, 8, 10, 11],
        //上架
        6: [2, 3, 4, 8, 10, 11],
        // 销售
        9: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
        // 维修
        10: [1, 4, 5, 6, 8, 10, 11],
        //退回
        11: [1, 2, 3, 8],
        //库存入库
        2: [10, 11, 6],
        //上架入库
        7: [10, 11],
        // 销退入库
        8: [10, 11],
        //送外修
        13: [6]
      },
      //targetStorageLocationId: null,
      canSelectReceiveEmpIds: [],
      //canAddReceipt: true,
      //originStorageLocationId: this.$store.state.empIdToStorageLocationIdCorr[JSON.parse(window.sessionStorage.getItem('user'))['id']]
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initAllOrderInfo();
      this.initDeliverIntention();
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initDeliverIntention() {
      //当前用户所拥有的角色
      let nowUserRoles = this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId]
      console.log("bbb")
      console.log(nowUserRoles)

      Object.keys(this.deliverTypeLaunchCorr).forEach(key => {
        let flag = true
        this.deliverTypeLaunchCorr[key].forEach(roleId => {
          if (flag && nowUserRoles.indexOf(roleId) !== -1) {
            flag = false
            this.deliverIntention.push(key);
          }
        })
      })
      console.log("aaaadfd")
      console.log(this.deliverIntention)
    },
    initAllOrderInfo() {
      getDeliverReceipt(this.currentPage, this.size, this.searchOrder, 0).then(resp => {
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
      this.$emit('func', 1, row.deliverReceiptId, row.enableEdit, row.operateEmpId)
    },
    addReceipt() {
      this.addReceiptDialogVisible = true
    },
    addReceiptInfo() {
      //if (!this.canAddReceipt) {
      //  console.log("aaa")
      //  return;
      //}
      if (this.newDeliverReceiptInfo.receiveEmpIds.length === 0) {
        this.$message.error("接收人为空");
        return
      }
      this.$confirm('是否确定要添加', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        createDeliverReceipt(this.newDeliverReceiptInfo).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("创建成功");
            this.initAllOrderInfo();
            this.newDeliverReceiptInfo = {}
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
    handleDelete(row) {
      this.$confirm(row.sum !== 0 ? '该转交单中添加了机器，是否还要删除转交单' : '是否要删除转交单', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteDeliverReceipt(row.deliverReceiptId).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("删除成功");
            this.initAllOrderInfo();
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    release(row) {
      this.$confirm('是否确定要发布该转交单', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        releaseDeliverReceipt(row.deliverReceiptId).then(resp => {
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
    typeChange() {
      this.newDeliverReceiptInfo.receiveEmpIds = []
      //this.targetStorageLocationId = null

      //获取变化后的角色id
      let roleIds = this.deliverTypeRoleCorr[this.newDeliverReceiptInfo.deliverIntentionId]
      //通过角色id获取对应的员工ids,
      let empIds = []
      roleIds.forEach(roleId => {
        let tempEmpIds = this.$store.state.roleIdToEmpIdsCorr[roleId];
        tempEmpIds.forEach(empId => {
          if (empIds.indexOf(empId) === -1) {
            empIds.push(empId);
          }
        })
      })
      console.log("ffff")
      console.log(this.$store.state.roleIdToEmpIdsCorr)

      this.canSelectReceiveEmpIds = empIds;

      // 判断是否能跨库，然后赋值给canSelectReceiveEmpIds
      //如果是处理人员，则可以进行跨库
      //let deliverEmpId = JSON.parse(window.sessionStorage.getItem('user'))['id']; //发起转发订单的发起人的id
      //console.log(this.$store.state.roleIdToEmpIdsCorr)
      //if (this.$store.state.roleIdToEmpIdsCorr[7] !== undefined && this.$store.state.roleIdToEmpIdsCorr[7].indexOf(deliverEmpId) !== -1) {
      //  this.canSelectReceiveEmpIds = empIds
      //} else {
      //  this.canSelectReceiveEmpIds = []
      //  empIds.forEach(empId => {
      //    if (this.$store.state.empIdToStorageLocationIdCorr[deliverEmpId] === this.$store.state.empIdToStorageLocationIdCorr[empId]) {
      //      this.canSelectReceiveEmpIds.push(empId);
      //    }
      //  })
      //}
    },
    //receiveEmpChange() {
    //  if (this.newDeliverReceiptInfo.receiveEmpIds.length !== 0) {
    //    let storageLocationId = this.$store.state.empIdToStorageLocationIdCorr[this.newDeliverReceiptInfo.receiveEmpIds[0]];
    //    this.newDeliverReceiptInfo.receiveEmpIds.forEach(empId => {
    //      if (this.$store.state.empIdToStorageLocationIdCorr[empId] !== storageLocationId) {
    //        this.$message.error("接收人：" + this.$store.state.employeeNameCorr[this.newDeliverReceiptInfo.receiveEmpIds[0]] + "与" + this.$store.state.employeeNameCorr[empId] + "是不同库位的人");
    //        this.canAddReceipt = false
    //        return
    //      }
    //    })
    //    this.targetStorageLocationId = storageLocationId
    //    console.log("c")
    //    this.canAddReceipt = true
    //  } else {
    //    console.log("b")
    //    this.canAddReceipt = false
    //  }
    //}
  }
}
</script>

<style scoped>

</style>
