<template>
  <div>
    <AddMachineByScan :machines="machines" table-name="purchaseReturn"></AddMachineByScan>

    <MachineShowDetail ref="child" :machines="machines" paging="false" :tableOperate="'add'"
                       table-name="purchaseReturn"></MachineShowDetail>

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
import AddMachineByScan from "../../Machine/AddMachineByScan.vue";
import MachineShowDetail from "../../Machine/MachineShowDetail.vue";
import {createPurchaseReturnReceipt} from "../../../api/purchaseReturnApi";

export default {
  name: "PurchaseReturnAdd",
  data() {
    return {
      receiptMainInfo: {},
      machines: [],
      dialogVisible: false
    }
  },
  methods: {
    submit() {
      this.$confirm('是否确定要提交', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        createPurchaseReturnReceipt(this.receiptMainInfo).then(resp => {
          if (resp.data.code === 200) {
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