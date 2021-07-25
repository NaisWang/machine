<template>
  <div>
    <div style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
      <el-row>
        <el-col :span="3" style="margin-right: 10px;">
          备注：
          <el-input v-model="receiptMainInfo.comment"
                    size="mini"
                    prefix-icon="el-icon-search"
                    placeholder="备注信息"
                    clearable></el-input>
        </el-col>
      </el-row>
    </div>

    <AddMachineByScan :machines="machines" table-name="storageEnter"></AddMachineByScan>

    <MachineShowDetail ref="child" :machines="machines" :tableOperate="'add'"
                       table-name="storageEnter" extra-not-show="[]"></MachineShowDetail>

    <el-button @click="dialogVisible = true">提交</el-button>

    <el-dialog
        title="销售退货单据信息"
        :visible.sync="dialogVisible"
        width="50%">
      <div
          style="border: 1px solid #409eff; border-radius: 5px; box-sizing: border-box; padding: 5px; margin: 10px 0px">
        <el-row>
          <el-col :span="3" style="margin-right: 10px;">
            备注：
            <el-input v-model="receiptMainInfo.comment"
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
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import {createEnterStorageReceipt} from "../../../api/enterStorageApi";
import {getMachine} from "../../../api/machineApi";

export default {
  name: "StorageEnterAdd",
  data() {
    return {
      receiptMainInfo: {},
      numberInput: "",
      machines: [],
      nowPurchaseOrderNumber: null
    }
  },
  props: ['purchaseOrderNumber'],
  mounted() {
    this.init()
  },
  activated() {
    this.init()
  },
  methods: {
    init() {
      console.log("now" + this.nowPurchaseOrderNumber)
      console.log("no" + this.purchaseOrderNumber)
      if (this.machines.length === 0) {
        console.log("now" + this.purchaseOrderNumber)
        getMachine(1, 99999, {"purchaseOrderId": parseInt(this.purchaseOrderNumber)}).then(resp => {
          if (resp.data.obj.data) {
            this.nowPurchaseOrderNumber = this.purchaseOrderNumber
            this.machines = resp.data.obj.data
          }
        })
      } else if (this.purchaseOrderNumber !== this.nowPurchaseOrderNumber) {
        this.$confirm('当前入库单没有保存, 是否确定要覆盖', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.machines = []
          getMachine(1, 99999, {"purchaseOrderId": parseInt(this.purchaseOrderNumber)}).then(resp => {
            if (resp.data.obj.data) {
              this.nowPurchaseOrderNumber = this.purchaseOrderNumber
              this.machines = resp.data.obj.data
            }
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消保存'
          });
        });
      }
    },
    submit() {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        createEnterStorageReceipt(this.receiptMainInfo, this.machines).then(resp => {
          if (resp.data.code === 200) {
            this.$emit('func', 0);
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
    }
  },
  components: {
    AddMachineByScan,
    MachineShowDetail
  }
}
</script>

<style scoped>

</style>