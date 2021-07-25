<template>
  <div>

    <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
              v-model="numberInput"></el-input>

    <MachineShowDetail :machines="scanMachine" :paging="false"
                       :tableName="'machineFix'" table-operate="add"></MachineShowDetail>

    <el-button>提交</el-button>

  </div>

</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine} from "../../api/machineApi";

export default {
  name: "机器维修",
  data() {
    return {
      scanMachine: [],
      numberInput: "",
    }
  },
  methods: {
    addMachineByScan() {
      if (this.numberInput !== "") {
        getMachine(1, 10, {"number": this.numberInput}).then(resp => {
          console.log(resp.data.obj)
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
            resp.data.obj.data[0].qualityDesc = resp.data.obj.data[0].qualityDesc === null ? "" : resp.data.obj.data[0].qualityDesc.split(",");
            resp.data.obj.data[0].featureDesc = resp.data.obj.data[0].featureDesc === null ? "" : resp.data.obj.data[0].featureDesc.split(",");
            resp.data.obj.data[0].needFix = resp.data.obj.data[0].needFix === null ? "" : resp.data.obj.data[0].needFix.split(",");
            resp.data.obj.data[0].fixed = resp.data.obj.data[0].fixed === null ? "" : resp.data.obj.data[0].fixed.split(",");
            resp.data.obj.data[0].notFixed = resp.data.obj.data[0].notFixed === null ? "" : resp.data.obj.data[0].notFixed.split(",");
            resp.data.obj.data[0].fixToBad = resp.data.obj.data[0].fixToBad === null ? "" : resp.data.obj.data[0].fixToBad.split(",");
            this.scanMachine.push(resp.data.obj.data[0])
            this.numberInput = ""
          }
        })
      }
    },
    //判断编号为number的机器是否已经添加到单据中
    judgeIsAdd(number) {
      let flag = 1
      this.scanMachine.forEach(item => {
        console.log("itemNum" + item.number)
        console.log("num" + number)
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
