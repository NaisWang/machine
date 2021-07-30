<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="8" style="margin-right: 10px;">
          采购单号：
          <el-input v-model="searchMachine.purchaseOrderId"
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

    <AddMachineByScan v-if="isRelease === 0" table-name="upShelfEnterStorage" operate-name="addMachineToUpShelfEnterStorage" :machines="machines"
                      :receiptId="receiptId" @initShow="initMachine"></AddMachineByScan>

    <MachineShowDetail ref="child" :machines="machines" :paging="true" :tableName="'upShelfEnterStorage'"
                       :is-release="isRelease" :extra-not-show="[]"></MachineShowDetail>
  </div>
</template>

<script>
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import initMachineCorr from "../../../utils/machineCorr";
import {addMachineToEnterStorageReceipt} from "../../../api/enterStorageApi";
import MachineShowDetailVertical from "../../Machine/MachineShowDetailVertical.vue";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";

export default {
  name: "StorageUpShelfDetail",
  data() {
    return {
      searchMachine: {},
      machines: [],
      dialogVisible: false,
      showMachineTable: false,
      numberInput: "",
      addMachineInfo: {}
    }
  },
  props: ['receiptId', 'isRelease'],
  mounted() {
    this.searchMachine.upShelfEnterStorageId = this.receiptId;
    this.$refs.child.initMachinesByApi(this.searchMachine)
    initMachineCorr(this.$store)
  },
  methods: {
    initMachine() {
      this.$refs.child.initMachinesByApi(this.searchMachine)
    },
  },
  components: {
    AddMachineByScan,
    MachineShowDetail,
    MachineShowDetailVertical
  }
}
</script>

<style scoped>

</style>
