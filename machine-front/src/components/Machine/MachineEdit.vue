<template>
  <el-dialog
      title="修改机器信息"
      :visible.sync="editShow.value"
      width="50%">
    <div
        style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-form :model="machine" status-icon ref="form" label-width="100px" class="demo-ruleForm">

        <el-form-item :prop="'number'"
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
                      label="物品编号"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">
          <el-input type="text" v-model="machine.number" placeholder="请输入物品编号"></el-input>
        </el-form-item>

        <el-form-item :prop="'imei'"
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
                      label="imei"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true, trigger: 'blur' }">
          <el-input type="text" v-model="machine.imei" placeholder="请输入imei"></el-input>
        </el-form-item>

        <el-form-item label="采购渠道"
                      prop="purchaseChannelId"
                      v-if="['purchaseOrderReceipt'].indexOf(tableName) !== -1 "
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true, trigger: 'blur' }">
          <div>
            <el-select size="mini"
                       v-model="machine.purchaseChannelId">
              <el-option
                  v-for="index in Object.keys($store.state.machineChannelCorr).map(Number)"
                  :key="index"
                  :label="$store.state.machineChannelCorr[index]"
                  :value="index">
              </el-option>
            </el-select>
          </div>
        </el-form-item>

        <el-form-item :prop="'categoryId'"
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
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
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
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
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
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
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
                      label="sku"
                      :show-message="false"
                      style="margin: 0;"
                      :rules="{ required: true, trigger: 'blur' }">
          <el-input type="text" v-model="machine.sku" placeholder="请输入sku"></el-input>
        </el-form-item>

        <el-form-item :prop="'rank'"
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
                      label="等级"
                      :show-message="false"
                      style="margin: 0;"
                      :rules="{ required: true, trigger: 'blur' }">
          <el-input type="text" v-model="machine.rank" placeholder="请输入等级"></el-input>
        </el-form-item>


        <el-form-item :prop="'purchasePrice'"
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
                      label="采购价"
                      :show-message="false"
                      style="margin: 0;"
                      :rules="{ required: true, trigger: 'blur' }">
          <el-input type="text" v-model="machine.purchasePrice" placeholder="请输入采购价"></el-input>
        </el-form-item>


        <el-form-item :prop="'describe'"
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
                      label="描述"
                      :show-message="false"
                      style="margin: 0;"
                      :rules="{ required: true, trigger: 'blur' }">
          <el-input type="text" v-model="machine.describe" placeholder="请输入描述"></el-input>
        </el-form-item>

        <el-form-item :prop="'purchaseEmpId'"
                      v-if="['purchaseOrder'].indexOf(tableName) !== -1"
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
                      v-if="['purchaseOrder', 'purchaseReturn','purchaseOrderReceipt', 'enterStorageReceipt', 'marketOrder'].indexOf(tableName) !== -1"
                      label="备注"
                      :show-message="false"
                      style="margin: 0;"
                      :rules="{ required: true, trigger: 'blur' }">
          <el-input type="text" v-model="machine.comment" placeholder="请输入备注"></el-input>
        </el-form-item>

      </el-form>
    </div>

    <span slot="footer" class="dialog-footer">
                  <el-button @click="editShow.value = false">取 消</el-button>
                  <el-button type="primary" @click="submit">提 交</el-button>
                </span>
  </el-dialog>
</template>

<script>
import {modifyMachineForPurchaseOrder, modifyPurchaseOrderReceipt} from "../../api/purchaseOrderReceipt";
import {modifyEnterStorageReceipt} from "../../api/enterStorageApi";
import {modifyPurchaseReturnReceipt} from "../../api/purchaseReturnApi";
import {modifyMarketReturnReceipt} from "../../api/marketOrderApi";

export default {
  name: "MachineEdit",
  data() {
    return {}
  },
  props: ['machine', 'tableName', 'editShow'],
  methods: {
    submit() {
      this.$confirm('是否确定要修改', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.tableName === 'purchaseOrder') {
          modifyMachineForPurchaseOrder(this.machine).then(resp => {
            if (resp.data.code === 200) {
              this.afterSubmit()
            }
          })
        } else if (this.tableName === 'purchaseOrderReceipt') {
          modifyPurchaseOrderReceipt(this.machine).then(resp => {
            if (resp.data.code === 200) {
              this.afterSubmit()
            }
          })
        } else if (this.tableName === 'enterStorageReceipt') {
          modifyEnterStorageReceipt(this.machine).then(resp => {
            if (resp.data.code === 200) {
              this.afterSubmit()
            }
          })
        } else if (this.tableName === 'purchaseReturn') {
          modifyPurchaseReturnReceipt(this.machine).then(resp => {
            if (resp.data.code === 200) {
              this.afterSubmit()
            }
          })
        } else if (this.tableName === 'marketOrder') {
          modifyMarketReturnReceipt(this.machine).then(resp => {
            if (resp.data.code === 200) {
              this.afterSubmit()
            }
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消修改'
        });
      });
    },
    afterSubmit() {
      this.$message.success("更新成功")
      this.editShow.value = false
      this.$emit('initShow');
    }
  }
}
</script>

<style scoped>

</style>