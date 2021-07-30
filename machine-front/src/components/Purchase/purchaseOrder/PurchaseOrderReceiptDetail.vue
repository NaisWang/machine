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
    </div>

    <div v-if="isRelease === 0">
      <el-upload
          class="upload-demo"
          ref="upload"
          :on-remove="handleRemove"
          :on-change="handleChange"
          :file-list="fileList"
          accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"
          action=""
          :auto-upload="false">
        <template #trigger>
          <el-button size="small" type="primary">选取文件</el-button>
        </template>
        <template #tip>
          <div class="el-upload__tip">
            导表添加机器
          </div>
        </template>
      </el-upload>

      <el-button @click="manualDialogVisible = true">手动添加机器</el-button>
    </div>

    <MachineShowDetail ref="child" :machines="machines" :paging="true" :tableName="'purchaseOrder'"
                       :is-release="isRelease" :extra-not-show="[]"></MachineShowDetail>


    <el-dialog
        title="excel表格中的机器，是否确定要添加 ?"
        :visible.sync="excelDialogVisible"
        width="90%">
      <span>当前机器总数：</span>
      <el-tag>{{ needAddMachine.length }}</el-tag>
      <MachineShowDetail :machines="needAddMachine" :paging="false"
                         :tableName="'purchaseOrder'"
                         :extraNotShow="['statusId', 'purchaseChannelId','purchaseTime']"
                         table-operate="excel"></MachineShowDetail>

      <span slot="footer" class="dialog-footer">
                  <el-button @click="dialogVisible = false">取 消</el-button>
                  <el-button type="primary" @click="addMachines('excel')">提 交</el-button>
                </span>
    </el-dialog>


    <el-dialog
        title="添加机器"
        :visible.sync="manualDialogVisible"
        width="50%">
      <div
          style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
        <el-form :model="needAddMachineByManual" status-icon ref="form" label-width="100px" class="demo-ruleForm">

          <el-form-item :prop="'number'"
                        label="物品编号"
                        style="margin: 0;"
                        :show-message="false"
                        :rules="{ required: true,trigger: 'blur' }">
            <el-input type="text" v-model="needAddMachineByManual.number" placeholder="请输入物品编号"></el-input>
          </el-form-item>

          <el-form-item :prop="'imei'"
                        label="imei"
                        style="margin: 0;"
                        :show-message="false"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="needAddMachineByManual.imei" placeholder="请输入imei"></el-input>
          </el-form-item>

          <el-form-item :prop="'categoryId'"
                        label="品类"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'change' }">
            <el-select size="mini"
                       v-model="needAddMachineByManual.categoryId">
              <el-option
                  v-for="index in Object.keys($store.state.machineCategoryCorr).map(Number)"
                  :key="index"
                  :label="$store.state.machineCategoryCorr[index]"
                  :value="index">
              </el-option>
            </el-select>
          </el-form-item>


          <el-form-item :prop="'brandId'"
                        label="品牌"
                        style="margin: 0;"
                        :show-message="false"
                        :rules="{ required: true,trigger: 'change' }">
            <el-select size="mini"
                       v-model="needAddMachineByManual.brandId">
              <el-option
                  v-for="index in Object.keys($store.state.machineBrandCorr).map(Number)"
                  :key="index"
                  :label="$store.state.machineBrandCorr[index]"
                  :value="index">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item :prop="'type'"
                        label="型号"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'change' }">
            <el-select size="mini"
                       v-model="needAddMachineByManual.type">
              <el-option
                  v-for="index in Object.keys($store.state.machineBrandCorr).map(Number)"
                  :key="index"
                  :label="$store.state.machineBrandCorr[index]"
                  :value="index">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item :prop="'sku'"
                        label="sku"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="needAddMachineByManual.sku" placeholder="请输入sku"></el-input>
          </el-form-item>

          <el-form-item :prop="'rank'"
                        label="等级"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="needAddMachineByManual.rank" placeholder="请输入等级"></el-input>
          </el-form-item>


          <el-form-item :prop="'purchasePrice'"
                        label="采购价"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="needAddMachineByManual.purchasePrice" placeholder="请输入采购价"></el-input>
          </el-form-item>


          <el-form-item :prop="'describe'"
                        label="描述"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="needAddMachineByManual.describe" placeholder="请输入描述"></el-input>
          </el-form-item>

          <el-form-item :prop="'purchaseEmpId'"
                        label="采购人员"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'change' }">
            <el-select size="mini"
                       v-model="needAddMachineByManual.purchaseEmpId">
              <el-option
                  v-for="index in Object.keys($store.state.employeeNameCorr).map(Number)"
                  :key="index"
                  :label="$store.state.employeeNameCorr[index]"
                  :value="index">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item :prop="'comment'"
                        label="备注"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="needAddMachineByManual.comment" placeholder="请输入备注"></el-input>
          </el-form-item>

        </el-form>
      </div>

      <span slot="footer" class="dialog-footer">
                  <el-button @click="dialogVisible = false">取 消</el-button>
                  <el-button type="primary" @click="addMachines('manual')">提 交</el-button>
                </span>
    </el-dialog>

  </div>
</template>

<script>
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import initMachineCorr from "../../../utils/machineCorr";
import {getCorrIndexByName} from "../../../utils/machineCorr";
import {addMachineToPurchaseReceipt} from "../../../api/purchaseOrderReceipt";
import * as XLSX from "xlsx"

export default {
  name: "PurchaseOrderReceiptDetail",
  data() {
    return {
      searchMachine: {},
      needAddMachine: [],
      needAddMachineByManual: {},
      excelDialogVisible: false,
      manualDialogVisible: false,
      machines: [],
      fileList: [],
      purchaseOrderReceiptField: [{"zh": "物品编号", "en": "number"}, {"zh": "IMEI", "en": "imei"}, {
        "zh": "品类",
        "en": "categoryId"
      }, {"zh": "品牌", "en": "brandId"}, {"zh": "型号", "en": "type"}, {
        "zh": "sku",
        "en": "sku"
      }, {"zh": "等级", "en": "rank"}, {"zh": "采购价", "en": "purchasePrice"}, {
        "zh": "描述",
        "en": "describe"
      }, {"zh": "采购人员", "en": "purchaseEmpId"}, {"zh": "备注", "en": "comment"}],
    }
  },
  props: ['receiptDetailNumber', 'isRelease'],
  mounted() {
    this.searchMachine.purchaseOrderId = this.receiptDetailNumber;
    this.$refs.child.initMachinesByApi(this.searchMachine)
    initMachineCorr(this.$store)
  },
  methods: {
    handleChange(file, fileList) {
      this.fileList = [fileList[fileList.length - 1]]
      this.readWorkbookFromLocalFile(file)
    },
    handleRemove(file, fileList) {
      this.fileList = fileList;
      this.needAddMachine = []
    },

    addPurchaseOrderReceiptMachinesByExcel(machineExcelInfo, ExcelTitleIndexNumber) {
      let length = machineExcelInfo.length;
      let transform = {
        "categoryId": this.$store.state.machineCategoryCorr,
        "brandId": this.$store.state.machineBrandCorr,
        "purchaseEmpId": this.$store.state.employeeNameCorr
      }
      let checkoutFlag = true
      for (let i = 1; i < length; i++) {
        let machineInfo = {}
        if (checkoutFlag) {
          this.purchaseOrderReceiptField.forEach(item => {
            machineInfo[item['en']] = machineExcelInfo[i][ExcelTitleIndexNumber[item['zh']]]
          })
          Object.keys(transform).forEach(item => {
            let index
            try {
              index = getCorrIndexByName(transform[item], machineInfo[item].toLowerCase())
            } catch (e) {
              console.log(e)
              console.log(machineInfo[item] + "ffff")
            }
            if (index === -1) {
              this.$message.error(machineInfo[item] + "不存在")
              checkoutFlag = false
            }
            machineInfo[item] = index
          })
          if (checkoutFlag) {
            this.needAddMachine.push(machineInfo)
          }
        }
      }
      if (!checkoutFlag) {
        this.needAddMachine = []
      }
    },
    getMachineExcelTitleIndex(machineExcelTitleRow, ExcelTitleIndexNumber) {
      this.purchaseOrderReceiptField.forEach(item => {
        let index = -1;
        machineExcelTitleRow.forEach((item1, i) => {
          if (item1.toLowerCase() === item['zh'].toLowerCase()) {
            index = i
            return
          }
        })
        if (index === -1) {
          this.$message.error("该文件中缺少" + item['zh'] + "列")
          return
        }
        ExcelTitleIndexNumber[item['zh']] = index
      })
    },
    readWorkbookFromLocalFile(file) {
      let _this = this;
      let f = file.raw;
      let reader = new FileReader();
      reader.onload = function (e) {
        let data = e.target.result;
        let workbook = XLSX.read(data, {
          type: 'binary'
        });
        let first_worksheet = workbook.Sheets[workbook.SheetNames[0]];
        let jsonArr = XLSX.utils.sheet_to_json(first_worksheet, {header: 1});
        console.log(jsonArr);
        let ExcelTitleIndexNumber = {}
        if (jsonArr.length === 0) {
          _this.$message.error("该表为空")
          return
        }
        _this.getMachineExcelTitleIndex(jsonArr[0], ExcelTitleIndexNumber)
        _this.addPurchaseOrderReceiptMachinesByExcel(jsonArr, ExcelTitleIndexNumber)
        _this.excelDialogVisible = true
      };
      reader.readAsBinaryString(f);
    },
    addMachines(type) {
      let canAdd = true;
      if (type === 'manual') {
        this.$refs['form'].validate((valid) => {
          if (valid) {
          } else {
            canAdd = false
            this.$message.error("输入有误");
            return false;
          }
        });
      }
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (canAdd) {
          let addMachine = []
          if (type === 'excel') {
            addMachine = this.needAddMachine
          } else if (type === 'manual') {
            addMachine = [this.needAddMachineByManual]
          }
          addMachineToPurchaseReceipt(addMachine, this.receiptDetailNumber).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("添加成功");
              if (type === 'excel') {
                this.needAddMachine = []
              } else if (type === 'manual') {
                this.needAddMachineByManual = {}
              }
              this.needAddMachine = []
              this.$refs.child.initMachinesByApi(this.searchMachine)
              this.excelDialogVisible = false;
              this.manualDialogVisible = false;
            }
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消'
        });
      });
    },
  },
  components: {
    MachineShowDetail
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>
