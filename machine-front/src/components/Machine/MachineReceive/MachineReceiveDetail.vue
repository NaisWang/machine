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

    <div>
      <el-table
          :data="machines"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="machineId"
            label="机器id"
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
                @click="orderDetail(scope.row)">详情
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

  </div>
</template>

<script>
import initMachineCorr from "../../../utils/machineCorr";
import {getDeliverMachine} from "../../../api/deliverMachineApi";
import {getMachine} from "../../../api/machineApi";
import {addDeliverMachine} from "../../../api/deliverMachineApi";
import {deleteDeliverMachine} from "../../../api/deliverMachineApi";

export default {
  name: "MachineReceiveDetail",
  data() {
    return {
      searchMachine: {},
      machines: [],
      currentPage: 1,
      size: 10,
      total: null,
    }
  },
  props: ['receiptDetailNumber'],
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
  },
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>
