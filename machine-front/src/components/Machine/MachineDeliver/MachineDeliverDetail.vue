<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px"
         class="machineDeliverDetail">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          采购退货单号：
          <el-input v-model="searchMachine.marketOrderId"
                    disabled
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入物品编号进行搜索..."
                    clearable></el-input>
        </el-col>
        <el-col :span="8" style="margin-right: 10px;">
          物品编号：
          <el-input v-model="searchMachine.number"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入物品编号进行搜索..."
                    clearable></el-input>
        </el-col>
        <el-col :span="8" style="margin-right: 10px;">
          IMEI号：
          <el-input v-model="searchMachine.imei"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入IMEI号进行搜索..."
                    clearable></el-input>
        </el-col>
        <el-col :span="4" style="margin-right: 10px;">
          品类：
          <el-select clearable v-model="searchMachine.categoryId" size="mini" placeholder="品类">
            <el-option
                v-for="id in Object.keys($store.state.machineCategoryCorr).map(Number)"
                :label="$store.state.machineCategoryCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="3" style="margin-right: 10px;">
          品牌：
          <el-select clearable v-model="searchMachine.brandId" size="mini" placeholder="品牌">
            <el-option
                v-for="id in Object.keys($store.state.machineBrandCorr).map(Number)"
                :label="$store.state.machineBrandCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row style="margin-top: 15px;">
        <el-col :span="4" style="margin-right: 10px;">
          <div>购入渠道：</div>
          <el-select clearable v-model="searchMachine.purchasingChannelId" size="mini" placeholder="购入渠道">
            <el-option
                v-for="id in Object.keys($store.state.machineChannelCorr).map(Number)"
                :label="$store.state.machineChannelCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="4" style="margin-right: 10px;">
          <div>库位：</div>
          <el-input v-model="searchMachine.stockLocation"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="请输入库位"
                    clearable></el-input>
        </el-col>
        <el-col :span="14" style="margin-right: 10px;">
          <div>中标日期：</div>
          <el-date-picker
              v-model="bidDateScope"
              type="daterange"
              size="mini"
              unlink-panels
              value-format="yyyy-MM-dd"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期">
          </el-date-picker>
        </el-col>
      </el-row>
      <el-row style="margin-top: 10px;">
        <el-col :span="7" :offset="17">
          <el-button size="mini" @click="cancelAdvSearch">取消</el-button>
          <el-button size="mini" icon="el-icon-search" type="primary" @click="initMachine">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <AddMachineByScan v-if="isEdit === 0" :machines="machines" table-name="machineDeliver"
                      :receiptId="receiptDetailNumber" @initShow="initDeliverMachines"></AddMachineByScan>

    <div>
      <el-table
          :data="machines"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="machineNumber"
            label="物品编码"
            width="170">
        </el-table-column>

        <el-table-column
            prop="status"
            label="接收状态"
            width="170">
          <template #default="scope">
            <span v-if="scope.row.status === 0">待转交</span>
            <span v-else-if="scope.row.status === 1">转交中</span>
            <span v-else>已接收</span>
          </template>
        </el-table-column>

        <el-table-column
            prop="receiveEmpId"
            label="接收人"
            width="170">
          <template #default="scope">
            <span>{{ $store.state.employeeNameCorr[scope.row.receiveEmpId] }}</span>
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
                @click="detail(scope.row)">详情
            </el-button>

            <el-button
                v-if="isEdit === 0 || scope.row.status === 1"
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


    <MachineShowDetailVertical v-if="showDetail.value" :table-name="tableName" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :show-detail="showDetail"></MachineShowDetailVertical>


    <!--    <el-dialog-->
    <!--        title="机器信息"-->
    <!--        :visible.sync="dialogVisible"-->
    <!--        width="80%">-->

    <!--      <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"-->
    <!--                v-model="numberInput"></el-input>-->

    <!--      <MachineShowDetailVertical v-if="showMachineTable" ref="child" :machine="addMachineInfo"-->
    <!--                                 table-name="storageSearch"></MachineShowDetailVertical>-->

    <!--      <div v-if="showMachineTable">-->
    <!--        <hr>-->
    <!--        <span>备注：</span>-->
    <!--        <el-input type="text" style="width: 70%" v-model="addDeliverMachineInfo.comment"-->
    <!--                  placeholder="请填写添加这台机器到转交单的备注信息"></el-input>-->
    <!--      </div>-->

    <!--      <span slot="footer" class="dialog-footer">-->
    <!--                              <el-button @click="dialogVisible = false">取 消</el-button>-->
    <!--                              <el-button type="primary" @click="add">添 加</el-button>-->
    <!--                            </span>-->
    <!--    </el-dialog>-->
  </div>
</template>

<script>
import initMachineCorr from "../../../utils/machineCorr";
import {getDeliverMachine} from "../../../api/deliverMachineApi";
import {getMachine} from "../../../api/machineApi";
import {addDeliverMachine} from "../../../api/deliverMachineApi";
import {deleteDeliverMachine} from "../../../api/deliverMachineApi";
import AddMachineByScan from "../AddMachineByScan.vue";
import MachineShowDetailVertical from "../MachineShowDetailVertical.vue";
import {getMachineTrace} from "../../../api/machineTraceApi";

export default {
  name: "MachineDeliverDetail",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      searchMachine: {},
      machines: [],
      currentPage: 1,
      size: 10,
      total: null,
      addMachineInfo: {},
      dialogVisible: false,
      numberInput: "",
      showMachineTable: false,
      addDeliverMachinesInfo: [],
      addDeliverMachineInfo: {},
    }
  },
  props: ['receiptDetailNumber', 'isEdit'],
  mounted() {
    this.searchMachine.deliverReceiptId = this.receiptDetailNumber;
    this.initDeliverMachines();
    initMachineCorr(this.$store)
  },
  methods: {
    initDeliverMachines() {
      getDeliverMachine(this.currentPage, this.size, this.searchMachine).then(resp => {
        if (resp.data.obj) {
          this.totol = resp.data.obj.total;
          this.machines = resp.data.obj.data;
        }
      })
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initDeliverMachines();
    },
    sizeChange(size) {
      this.size = size;
      this.initDeliverMachines();
    },
    addMachine() {
      this.dialogVisible = true
    },
    addMachineByScan() {
      if (this.numberInput !== "") {
        let _this = this.$refs
        getMachine(1, 10, {"number": this.numberInput}).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
              return
            }
            if (resp.data.obj.data[0]['deliverMachineId'] !== 0) {
              this.$message.error("该机器正处于转交中!!!")
              return
            }
            this.addMachineInfo = resp.data.obj.data[0]
            this.showMachineTable = true
            this.numberInput = ""
            _this.child.initMachineTable()
          }
        })
      }
    },
    handleDelete(row) {
      this.$confirm('是否确定要删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log(row)
        deleteDeliverMachine(row.machineNumber, this.receiptDetailNumber).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("删除成功");
            this.initDeliverMachines()
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    detail(row) {
      getMachine(1, 10, {"number": row.machineNumber}).then(resp => {
        this.showDetailMachine = JSON.parse(JSON.stringify(resp.data.obj.data[0]));
        getMachineTrace({"number": row.machineNumber}).then(resp => {
          this.showMachineTrace = JSON.parse(JSON.stringify(resp.data.obj))
          this.showDetail.value = true
        })
      })
    }
  },
  components: {
    AddMachineByScan,
    MachineShowDetailVertical,
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>
