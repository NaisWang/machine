<template>
  <div class="routineQuality">
    <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
              v-model="numberInput"></el-input>

    <MachineShowDetail :machines="scanMachine" :paging="false"
                       :tableName="'machineShelf'" table-operate="add" :extra-not-show="[]"></MachineShowDetail>

    <el-dialog
        style="margin-top: -8vh;"
        v-if="Object.keys(nowEditMachine).length !== 0"
        title="机器上架"
        :before-close="handleClose"
        :visible.sync="dialogVisible"
        width="90%">

      <MachineShowDetail :machines="[nowEditMachine]" :paging="false"
                         :tableName="'machineShelf'" table-operate="add"
                         :extra-not-show="['rankDesc','onePrice' , 'goodPrice', 'bagNumber', 'bidPrice', 'footer', 'operate']"></MachineShowDetail>
      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">机器上架信息设置：</h2>
      <el-form label-width="80px">
        <el-form-item label="一口价">
          <el-input v-model="onePrice"></el-input>
        </el-form-item>
        <el-form-item label="最高价">
          <el-input v-model="nowEditMachine.bidPrice"></el-input>
        </el-form-item>
        <span v-if="nowEditMachine.bidPrice">预测利润： {{
            nowEditMachine.bidPrice * 0.03 < 50 ? nowEditMachine.bidPrice * 0.97 - nowEditMachine.purchasePrice : nowEditMachine.bidPrice - 50 - nowEditMachine.purchasePrice
          }}</span>
        <el-form-item label="好的价格">
          <el-input v-model="nowEditMachine.goodPrice"></el-input>
        </el-form-item>
        <el-form-item label="机器等级">
          <el-input v-model="nowEditMachine.rankDesc"></el-input>
        </el-form-item>
        <el-form-item label="袋子编码">
          <el-input v-model="nowEditMachine.bagNumber"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="nowEditMachine.comment"></el-input>
        </el-form-item>
      </el-form>

      <span class="dialog-footer" slot="footer">
                  <el-button @click="cancel">取 消</el-button>
                  <el-button type="primary" @click="submit">提 交</el-button>
                </span>
    </el-dialog>


  </div>
</template>


<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine} from "../../api/machineApi";

export default {
  name: "机器上架",
  data() {
    return {
      scanMachine: [],
      nowEditMachine: {},
      numberInput: "",
      dialogVisible: false,
    }
  },
  methods: {
    addMachineByScan() {
      if (this.numberInput !== "") {
        let _this = this
        getMachine(1, 10, {"number": this.numberInput}).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
              return
            }
            if (resp.data.obj.total >= 2) {
              this.$message.error("物品编号为:" + this.numberInput + "的机器在库存中超过2个")
              return
            }
            if (this.judgeIsAdd(resp.data.obj.data[0].number) === -1) {
              this.$message.error("物品编号为:" + this.numberInput + "的机器已添加到该单据中了")
              return
            }
            _this.nowEditMachine = resp.data.obj.data[0]
            console.log("aaa")
            console.log(_this.nowEditMachine)
            _this.numberInput = ""
            this.showHangUp = true
            _this.dialogVisible = true
          }
        })
      }
    },
    //判断编号为number的机器是否已经添加到单据中
    judgeIsAdd(number) {
      let flag = 1
      this.scanMachine.forEach(item => {
        if (item.number === number) {
          flag = -1
        }
      })
      return flag
    },
  },
  components: {
    MachineShowDetail
  }
}
</script>

<style scoped>

</style>
