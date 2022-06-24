<template>
  <div>
    <div style="display: flex; justify-content: space-between">
      <el-input style="width: 300px; margin-right: 10px;" v-model="searchMachine.number" prefix-icon="el-icon-search"
                placeholder="请输入物品编号进行搜索..."
                :disabled="showAdvanceSearchVisible"
                clearable
                @clear="initMachine"></el-input>
      <el-button type="primary" icon="el-icon-search" @click="doSearch()" :disabled="showAdvanceSearchVisible">搜索
      </el-button>
      <el-button type="primary" @click="showAdvanceSearchVisible = !showAdvanceSearchVisible">
        <i :class="showAdvanceSearchVisible ?'fa fa-angle-double-up':'fa fa-angle-double-down'" aria-hidden="true"></i>
        高级搜索
      </el-button>
      <el-upload action="http://120.79.195.87:8080/machine/purchase/import" :show-file-list="false"
                 :before-upload="beforeUpload"
                 :headers="headers"
                 :on-success="onSuccess"
                 :disabled="importDataDisabled"
                 :on-error="onError">
        <el-button type="success" :icon="importDataBtnIcon" :disabled="importDataDisabled">
          {{ importDataBtnText }}
        </el-button>
      </el-upload>

      <el-button type="primary" icon="el-icon-plus">添加机器</el-button>
    </div>

    <div v-show="showAdvanceSearchVisible"
         style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
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
          <el-button size="mini" icon="el-icon-search" type="primary" @click="doSearch()">搜索</el-button>
        </el-col>
      </el-row>
    </div>
    <div>
      <el-button
          size="mini"
          :disabled="isEdit"
          @click="handleEdit()">编辑当前页
      </el-button>
      <span v-show="isEdit">
        <el-button
            size="mini"
            @click="cancelEdit()">取消编辑
        </el-button>
        <el-button
            size="mini"
            @click="completeEdit()">编辑完成
        </el-button>
      </span>
    </div>
    <div>
      <el-table
          :data="machines"
          @selection-change="handleSelectionChange"
          style="width: 100%">
        <el-table-column
            fixed
            type="selection"
            width="55">
        </el-table-column>
        <el-table-column
            fixed
            prop="number"
            label="物品编号"
            width="170">
        </el-table-column>
        <el-table-column
            prop="imei"
            label="IMEI号"
            width="130">
        </el-table-column>
        <el-table-column
            prop="categoryId"
            label="品类"
            width="70">
          <template #default="scope">
            <span>{{ $store.state.machineCategoryCorr[scope.row.categoryId] }}</span>
          </template>
        </el-table-column>
        <el-table-column
            prop="type"
            label="型号"
            width="90">
        </el-table-column>
        <el-table-column
            prop="brandId"
            label="品牌"
            width="70">
          <template #default="scope">
            <span>{{ $store.state.machineBrandCorr[scope.row.brandId] }}</span>
          </template>
        </el-table-column>
        <el-table-column
            prop="sku"
            label="sku名称"
            width="90">
        </el-table-column>
        <el-table-column
            prop="rank"
            label="等级"
            width="70">
        </el-table-column>
        <el-table-column
            prop="stockLocation"
            label="库位"
            width="50">
        </el-table-column>
        <el-table-column
            prop="purchasingChannelId"
            label="购买渠道"
            width="110">
          <template #default="scope">
            <div>
              <el-select size="mini" v-show="isEdit && isModifyField.purchasingChannel"
                         v-model="scope.row.purchasingChannelId"
                         @change="initUpdateMachine(scope.row, 'purchasingChannelId')">
                <el-option
                    v-for="index in Object.keys($store.state.machineChannelCorr).map(Number)"
                    :key="index"
                    :label="$store.state.machineChannelCorr[index]"
                    :value="index">
                </el-option>
              </el-select>
              <span v-show="!isEdit || (isEdit && isModifyField.purchasingChannel === 0)">{{
                  $store.state.machineChannelCorr[scope.row.purchasingChannelId]
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            prop="enterBatch"
            label="录入批次"
            width="70">
        </el-table-column>
        <el-table-column
            prop="purchasePrice"
            label="采购价"
            width="110">
          <template #default="scope">
            <div>
              <el-input maxlength="9" v-show="isEdit && isModifyField.purchasePrice"
                        v-model="scope.row.purchasePrice"
                        @change="initUpdateMachine(scope.row, 'purchasePrice')"></el-input>
              <span v-show="!isEdit || (isEdit && isModifyField.purchasePrice === 0)">{{
                  scope.row.purchasePrice
                }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            prop="biddingDate"
            label="中标日期"
            width="160">
          <template #default="scope">
            <div>
              <el-date-picker
                  :clearable="false"
                  v-show="isEdit && isModifyField.biddingDate"
                  v-model="scope.row.biddingDate"
                  type="date"
                  @change="initUpdateMachine(scope.row, 'biddingDate')"
                  value-format="yyyy-MM-dd"
                  :placeholder="scope.row.biddingDate" style="width: 130px">
              </el-date-picker>
              <span v-show="!isEdit || (isEdit && isModifyField.biddingDate === 0)">{{ scope.row.biddingDate }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
            prop="operatingId"
            label="操作人"
            width="80">
          <template #default="scope">
            <span>{{ $store.state.employeeNameCorr[scope.row.operatingId] }}</span>
          </template>
        </el-table-column>
        <el-table-column
            prop="describe"
            label="备注"
            width="120">
          <template #default="scope">
            <el-popover
                placement="top-start"
                :width="200"
                trigger="hover"
                :content="scope.row.describe">
              <template #reference>
                <el-button style="width: 80px; text-overflow: ellipsis; overflow: hidden;">
                  <span v-show="!scope.row.describe">无</span>
                  <span v-show="scope.row.describe">{{ scope.row.describe }}</span>
                </el-button>
              </template>
              <el-input
                  type="textarea"
                  autosize
                  @change="initUpdateMachine(scope.row, 'describe')"
                  :disabled="!isEdit || isModifyField.describe === 0"
                  :value="scope.row.describe"
                  v-model="scope.row.describe">
              </el-input>
            </el-popover>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" width="80">
          <template #default="scope">
            <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除
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
import * as purchaseOrderApi from "../../../api/purchaseOrderApi"
import * as purchaseOrderModifyApi from "../../../api/purchaseOrderModifyApi"
import initMachineCorr from "../../../utils/machineCorr";

export default {
  name: "采购订单",
  data() {
    return {
      machines: [],
      currentPage: 1,
      size: 10,
      bidDateScope: [],
      searchMachine: {},
      originalValue: {},
      editMachines: [],
      isEdit: false,
      isModifyField: {},
      showAdvanceSearchVisible: false,
      importDataBtnIcon: 'el-icon-upload2',
      importDataDisabled: false,
      importDataBtnText: '导入数据',
      headers: {
        Authorization: window.sessionStorage.getItem("tokenStr")
      },
      total: null,
      StopTimer: true,
      multipleSelection: []
    }
  },
  mounted() {
    this.initMachine();
    this.initIsModifyField();
    initMachineCorr(this.$store)
  },
  activated() {
    this.initMachine();
    this.StopTimer = true;
    let that = this;
    setTimeout(function fn() {
      if (that.StopTimer) {
        that.initMachine();
        setTimeout(fn, 1000 * 10);
      }
    }, 1000 * 10)
  },
  deactivated() {
    this.StopTimer = false;
  },
  methods: {
    initMachine() {
      purchaseOrderApi.getAllPurchaseOrder(this.currentPage, this.size, this.searchMachine, this.bidDateScope).then(resp => {
        let obj = resp.data.obj;
        this.machines = obj.data;
        this.total = obj.total;
      })
    },
    initIsModifyField() {
      purchaseOrderModifyApi.isModifyFiled().then(resp => {
        this.isModifyField = resp.data.obj;
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
    handleEdit() {
      this.isEdit = true;
      this.editMachines = [];
      this.initOriginValue();
    },
    cancelEdit() {
      this.initMachine()
      this.isEdit = false;
      this.originalValue = {};
    },
    completeEdit() {
      if (this.editMachines.length !== 0) {
        purchaseOrderApi.modifyMachine(this.editMachines).then(resp => {
          this.initMachine();
        })
      }
      this.isEdit = false;
      this.editMachines = [];
      this.originalValue = {};
    },
    beforeUpload() {
      this.importDataBtnIcon = 'el-icon-loading';
      this.importDataBtnText = '正在导入';
      this.importDataDisabled = true;
    },
    onSuccess(resp) {
      if (resp.code === 200) {
        this.$message.success(resp.message)
      } else {
        this.$message.error(resp.message)
      }
      this.importDataBtnIcon = 'el-icon-upload2';
      this.importDataBtnText = '导入数据';
      this.importDataDisabled = false;
      this.initMachine();
    },
    onError() {
      this.$message.error("导入失败")
      this.importDataBtnIcon = 'el-icon-upload2';
      this.importDataBtnText = '导入失败';
      this.importDataDisabled = false;
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initMachine();
    },
    sizeChange(size) {
      this.size = size;
      this.initMachine();
    },
    cancelAdvSearch() {
      this.showAdvanceSearchVisible = false;
      this.searchMachine = {};
      this.initMachine();
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    doSearch() {
      this.currentPage = 1;
      this.initMachine();
    }
  }
}
</script>

<style scoped>

</style>
