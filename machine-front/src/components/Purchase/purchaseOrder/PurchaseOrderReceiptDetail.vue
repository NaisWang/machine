<template>
  <div>
    <MachineSearch @searchMachines="initMachines" @cancelAdvSearch="cancelAdvSearch"
                   :search-machine="searchMachine"></MachineSearch>

    <div v-if="isRelease === 0 && $store.state.userId === operateEmpId">
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

    <!--
        <MachineShowDetail ref="child" :machines="machines" :paging="true" :tableName="'purchaseOrder'"
                           :is-release="isRelease" :extra-not-show="[]"
                           :receipt-id="receiptDetailNumber"></MachineShowDetail>
    -->

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
            prop="machineSku"
            label="SKU"
            width="170">
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
                  <el-button @click="excelDialogVisible = false">取 消</el-button>
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
                  <el-button @click="manualDialogVisible = false">取 消</el-button>
                  <el-button type="primary" @click="addMachines('manual')">提 交</el-button>
                </span>
    </el-dialog>


    <MachineShowDetailVertical v-if="showDetail.value" :machine="showDetailMachine"
                               :machine-trace="showMachineTrace" :machine-detection="showMachineDetection"
                               :show-detail="showDetail"></MachineShowDetailVertical>

  </div>
</template>

<script>
import MachineShowDetailVertical from "../../Machine/MachineShowDetailVertical.vue";
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import {getCorrIndexByName} from "../../../utils/machineCorr";
import {addMachineToPurchaseReceipt, getPurchaseOrderReceiptToMachine} from "../../../api/purchaseOrderReceipt";
import * as XLSX from "xlsx"
import MachineSearch from "../../Machine/MachineSearch.vue";
import {getMachine} from "../../../api/machineApi";
import {getMachineTrace} from "../../../api/machineTraceApi";
import {getMachineDetection} from "../../../api/machineDetection";

export default {
  name: "PurchaseOrderReceiptDetail",
  data() {
    return {
      showDetail: {"value": false},
      showDetailMachine: {},
      showMachineTrace: {},
      showMachineDetection: {},
      searchMachine: {},
      needAddMachine: [],
      needAddMachineByManual: {},
      excelDialogVisible: false,
      manualDialogVisible: false,
      machines: [],
      fileList: [],
      currentPage: 1,
      sizes: 10,
      total: null,
      purchaseOrderReceiptField: [{"zh": "物品编号", "en": "number"}, {"zh": "IMEI", "en": "imei"}, {
        "zh": "品类",
        "en": "categoryId"
      }, {"zh": "品牌", "en": "brandId"}, {"zh": "型号", "en": "type"}, {
        "zh": "sku",
        "en": "sku"
      }, {"zh": "等级", "en": "rank"}, {"zh": "采购价", "en": "purchasePrice"}, {
        "zh": "描述",
        "en": "describe"
      }, {"zh": "采购人员", "en": "purchaseEmpId"}, {"zh": "备注", "en": "comment"}, {"zh": "质检方", "en": "qualityInspector"}],
    }
  },
  props: ['receiptDetailNumber', 'isRelease', 'operateEmpId'],
  mounted() {
    this.initMachines();
  },
  methods: {
    initMachines() {
      this.searchMachine.purchaseOrderId = this.receiptDetailNumber;
      //this.$refs.child.initMachinesByApi(this.searchMachine)
      getPurchaseOrderReceiptToMachine(this.currentPage, this.sizes, this.receiptDetailNumber).then(resp => {
        if (resp.data.code === 200) {
          this.total = resp.data.obj.total;
          this.machines = resp.data.obj.data;
          if (this.machines === undefined) {
            this.machines = []
          }
        }
      })
    },
    cancelAdvSearch() {
      this.searchMachine = {}
      this.searchMachine.purchaseOrderId = this.receiptDetailNumber;
      getPurchaseOrderReceiptToMachine(this.receiptDetailNumber).then(resp => {
        if (resp.data.code === 200) {
          this.machines = resp.data.obj;
        }
      })
    },
    handleChange(file, fileList) {
      this.fileList = [fileList[fileList.length - 1]]
      this.readWorkbookFromLocalFile(file)
    },
    handleRemove(file, fileList) {
      this.fileList = fileList;
      this.needAddMachine = []
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.initMachines();
    },
    sizeChange(size) {
      this.sizes = size;
      this.initMachines();
    },
    detail(row) {
      getMachine(1, 10, {"id": row.machineId}).then(resp => {
        this.showDetailMachine = JSON.parse(JSON.stringify(resp.data.obj.data[0]));
        getMachineTrace({"machineId": row.machineId}).then(resp => {
          this.showMachineTrace = JSON.parse(JSON.stringify(resp.data.obj))
          getMachineDetection({"machineId": row.machineId}).then(resp => {
            this.showMachineDetection = JSON.parse(JSON.stringify(resp.data.obj))
            this.showDetail.value = true
          })
        })
      })
    },
    addPurchaseOrderReceiptMachinesByExcel(machineExcelInfo, ExcelTitleIndexNumber) {
      let ans1 = {code: 0, message: ""};
      let length = machineExcelInfo.length;
      let transform = {
        "categoryId": this.$store.state.machineCategoryCorr,
        "brandId": this.$store.state.machineBrandCorr,
        "purchaseEmpId": this.$store.state.employeeNameCorr
      }
      console.log("fjksdfjaskfdjdaskfjaskj")
      console.log(this.$store.state.machineBrandCorr);
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
            }
            if (index === -1) {
              ans1.code = 1
              ans1.message += machineInfo[item] + '、';
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
      return ans1;
    },
    getMachineExcelTitleIndex(machineExcelTitleRow, ExcelTitleIndexNumber) {
      let ans = {code: 0, message: ""}
      this.purchaseOrderReceiptField.forEach(item => {
        let index = -1;
        machineExcelTitleRow.forEach((item1, i) => {
          if (item1.toLowerCase() === item['zh'].toLowerCase()) {
            index = i
            return
          }
        })
        if (index === -1) {
          ans.code = 1;
          ans.message += item['zh'] + '、';
          return
        }
        ExcelTitleIndexNumber[item['zh']] = index
      })
      return ans;
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
        let ans = _this.getMachineExcelTitleIndex(jsonArr[0], ExcelTitleIndexNumber);
        if (ans.code === 1) {
          _this.$message.error("该表缺少如下列：" + ans.message);
          return
        }
        let ans1 = _this.addPurchaseOrderReceiptMachinesByExcel(jsonArr, ExcelTitleIndexNumber)
        if (ans1.code === 1) {
          _this.$message.error("该表缺少如下值：" + ans1.message);
          return
        }
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
              //this.$refs.child.initMachinesByApi(this.searchMachine)
              this.initMachines();
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
    MachineSearch,
    MachineShowDetail,
    MachineShowDetailVertical
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>
