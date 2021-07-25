<template>
  <div class="purchaseOrderReceipt">
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
          请选择采购订单表
        </div>
      </template>

    </el-upload>
    <el-button @click="addMachineDialogVisible = true">手动添加机器</el-button>

    <el-dialog
        title="添加机器"
        :visible.sync="addMachineDialogVisible"
        width="50%">
      <div
          style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
        <el-form :model="machine" status-icon ref="form" label-width="100px" class="demo-ruleForm">

          <el-form-item :prop="'number'"
                        label="物品编号"
                        style="margin: 0;"
                        :show-message="false"
                        :rules="{ required: true,trigger: 'blur' }">
            <el-input type="text" v-model="machine.number" placeholder="请输入物品编号"></el-input>
          </el-form-item>

          <el-form-item :prop="'imei'"
                        label="imei"
                        style="margin: 0;"
                        :show-message="false"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="machine.imei" placeholder="请输入imei"></el-input>
          </el-form-item>

          <el-form-item :prop="'categoryId'"
                        label="品类"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'change' }">
            <el-select size="mini"
                       v-model="machine.categoryId">
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
                       v-model="machine.brandId">
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
                       v-model="machine.type">
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
            <el-input type="text" v-model="machine.sku" placeholder="请输入sku"></el-input>
          </el-form-item>

          <el-form-item :prop="'rank'"
                        label="等级"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="machine.rank" placeholder="请输入等级"></el-input>
          </el-form-item>


          <el-form-item :prop="'purchasePrice'"
                        label="采购价"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="machine.purchasePrice" placeholder="请输入采购价"></el-input>
          </el-form-item>


          <el-form-item :prop="'describe'"
                        label="描述"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'blur' }">
            <el-input type="text" v-model="machine.describe" placeholder="请输入描述"></el-input>
          </el-form-item>

          <el-form-item :prop="'purchaseEmpId'"
                        label="采购人员"
                        :show-message="false"
                        style="margin: 0;"
                        :rules="{ required: true, trigger: 'change' }">
            <el-select size="mini"
                       v-model="machine.purchaseEmpId">
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
            <el-input type="text" v-model="machine.comment" placeholder="请输入备注"></el-input>
          </el-form-item>

        </el-form>
      </div>

      <span slot="footer" class="dialog-footer">
                  <el-button @click="dialogVisible = false">取 消</el-button>
                  <el-button type="primary" @click="addMachine">提 交</el-button>
                </span>
    </el-dialog>


    <MachineShowDetail ref="child" :machines="purchaseOrderReceiptMachines" :paging="false"
                       tableName="purchaseOrder" :tableOperate="'add'"></MachineShowDetail>

    <el-button @click="dialogVisible = true">提交</el-button>

    <el-dialog
        title="采购订单单据信息"
        :visible.sync="dialogVisible"
        width="50%">
      <div
          style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
        <el-row>
          <el-col :span="4" style="margin-right: 10px;">
            采购渠道：
            <el-select clearable v-model="purchaseOrderReceiptMainInfo.purchaseChannelId" size="mini"
                       placeholder="采购渠道">
              <el-option
                  v-for="id in Object.keys($store.state.machineChannelCorr).map(Number)"
                  :label="$store.state.machineChannelCorr[id]"
                  :value="id"
                  :key="id">
              </el-option>
            </el-select>
          </el-col>
          <el-col :span="3" style="margin-right: 10px;">
            备注：
            <el-input v-model="purchaseOrderReceiptMainInfo.comment"
                      size="mini"
                      prefix-icon="el-icon-search"
                      placeholder="备注信息"
                      clearable></el-input>
          </el-col>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
                  <el-button @click="dialogVisible = false">取 消</el-button>
                  <el-button type="primary" @click="submit">提 交</el-button>
                </span>
    </el-dialog>


  </div>
</template>

<script>
import * as purchaseOrderReceiptApi from "../../../api/purchaseOrderReceipt"
import * as XLSX from "xlsx"
import {getCorrIndexByName} from "../../../utils/machineCorr"
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";

export default {
  name: "AddPurchaseOrderReceipt",
  data() {
    return {
      purchaseOrderReceiptMainInfo: {},
      purchaseOrderReceiptMachines: [],
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
      dialogVisible: false,
      addMachineDialogVisible: false,
      machine: {}
    }
  },
  methods: {
    handleChange(file, fileList) {
      this.fileList = [fileList[fileList.length - 1]]
      this.readWorkbookFromLocalFile(file)
    },
    handleRemove(file, fileList) {
      this.fileList = fileList;
      this.purchaseOrderReceiptMachines = []
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
              console.log(machineInfo[item] + "ffff")
            }
            if (index === -1) {
              this.$message.error(machineInfo[item] + "不存在")
              checkoutFlag = false
            }
            machineInfo[item] = index
          })
          if (checkoutFlag) {
            this.purchaseOrderReceiptMachines.push(machineInfo)
          }
        }
      }
      if (!checkoutFlag) {
        this.purchaseOrderReceiptMachines = []
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
      };
      reader.readAsBinaryString(f);
    },
    handleDelete(index) {
      this.$confirm('是否确定要删除', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.purchaseOrderReceiptMachines.splice(index, 1)
        this.$message.success("删除成功")
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    submit() {
      if (!this.purchaseOrderReceiptMainInfo.purchaseChannelId) {
        this.$message.error("请选择采购渠道")
        return
      }
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        purchaseOrderReceiptApi.createPurchaseReceipt(this.purchaseOrderReceiptMainInfo, this.purchaseOrderReceiptMachines).then(resp => {
          if (resp.data.code === 200) {
            this.$message.success("提交成功")
            this.$emit('changeComponentFlag', 0)
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
    addMachine() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$confirm('是否确定要添加', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.purchaseOrderReceiptMachines.push(this.machine)
            this.addMachineDialogVisible = false
            this.machine = {}
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消添加'
            });
          });
        } else {
          this.$message.error("某个字段为空")
          return false;
        }
      });
    },
  },
  components: {
    MachineShowDetail
  }
}
</script>

<style>
.purchaseOrderReceipt .el-table--small td, .purchaseOrderReceipt .el-table--small td {
  padding: 0 !important;
}

</style>