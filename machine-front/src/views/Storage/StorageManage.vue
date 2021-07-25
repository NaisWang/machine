<template>
  <div>
    <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
              v-model="numberInput"></el-input>

    <MachineShowDetail :machines="scanMachine" :paging="false"
                       :tableName="'storageSearch'" table-operate="add" :extra-not-show="[]"></MachineShowDetail>

    <el-dialog
        v-if="Object.keys(nowEditMachine).length !== 0"
        title="机器信息"
        :visible.sync="dialogVisible"
        width="80%">

      <MachineShowDetail :machines="[nowEditMachine]" :paging="false"
                         :tableName="'storageSearch'" table-operate="add"
                         :extra-not-show="['qualityDesc', 'featureDesc', 'rankDesc', 'needFix', 'fixed', 'notFixed', 'fixToBad ','footer', 'operate']"></MachineShowDetail>

      <hr style="margin-top: 15px">

      <el-form label-width="80px">
        <el-form-item label="成色检测">
          <div>
              <span v-for="item in (nowEditMachine.qualityDesc === null ? '' : nowEditMachine.qualityDesc.split(','))"
                    :key="item"
                    v-if="item !== ''"
                    style="border: 1px solid black">
                {{ $store.state.machineIdToDesc[item] }}
              </span>
          </div>
        </el-form-item>
        <el-form-item label="功能检测">
          <div>
              <span v-for="item in (nowEditMachine.featureDesc === null ? '' : nowEditMachine.featureDesc.split(','))"
                    :key="item"
                    v-if="item !== ''"
                    style="border: 1px solid black">
                {{ $store.state.machineIdToDesc[item] }}
              </span>
          </div>
        </el-form-item>
        <el-form-item label="等级检测">
          <div>
            <span>{{ nowEditMachine.rankDesc }}</span>
          </div>
        </el-form-item>
        <el-form-item label="需要维修项">
          <div>
              <span v-for="item in (nowEditMachine.needFix === null ? '' : nowEditMachine.needFix.split(','))"
                    :key="item"
                    v-if="item !== ''"
                    style="border: 1px solid black">
                {{ $store.state.machineIdToDesc[item] }}
              </span>
          </div>
        </el-form-item>
        <el-form-item label="修好项">
          <div>
              <span v-for="item in (nowEditMachine.fixed === null ? '' : nowEditMachine.fixed.split(','))"
                    :key="item"
                    v-if="item !== ''"
                    style="border: 1px solid black">
                {{ $store.state.machineIdToDesc[item] }}
              </span>
          </div>
        </el-form-item>
        <el-form-item label="没有修好项">
          <div>
              <span v-for="item in (nowEditMachine.notFixed === null ? '' : nowEditMachine.notFixed.split(','))"
                    :key="item"
                    v-if="item !== ''"
                    style="border: 1px solid black">
                {{ $store.state.machineIdToDesc[item] }}
              </span>
          </div>
        </el-form-item>
        <el-form-item label="修坏项">
          <div>
              <span v-for="item in (nowEditMachine.fixToBad === null ? '' : nowEditMachine.fixToBad.split(','))"
                    :key="item"
                    v-if="item !== ''"
                    style="border: 1px solid black">
                {{ $store.state.machineIdToDesc[item] }}
              </span>
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <span>{{ nowEditMachine.comment }}</span>
        </el-form-item>
      </el-form>

      <span class="dialog-footer" slot="footer">
                  <el-button @click="cancel">取 消</el-button>
                  <el-button type="primary" @click="detection">检 测</el-button>
                  <el-button type="primary" @click="market">销 售</el-button>
                  <el-button type="primary" @click="fix">维 修</el-button>
                  <el-button type="primary" @click="fix">待 上 架</el-button>
                  <el-button type="primary" @click="returnMachine">退 货</el-button>
                </span>
    </el-dialog>


  </div>
</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine} from "../../api/machineApi";
import {modifyMachineStatusTo5} from "../../api/machineApi";

export default {
  name: "库存处理",
  data() {
    return {
      numberInput: "",
      scanMachine: "",
      nowEditMachine: {}
    }
  },
  methods: {
    addMachineByScan() {
      if (this.numberInput !== "") {
        getMachine(1, 10, {"number": this.numberInput}).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")
              return
            }

            this.nowEditMachine = resp.data.obj.data[0]

            //状态判断
            if (this.nowEditMachine.statusId !== 2 && this.nowEditMachine.statusId !== 5) {
              this.$message.error("物品编号为:" + this.numberInput + "的机器状态是" + this.$store.state.machineStatusCorr[this.nowEditMachine.statusId] + ", 不能进行该操作!!!")
              return
            }

            if (this.nowEditMachine.statusId === 2) {
              modifyMachineStatusTo5([{'id': this.nowEditMachine['id']}]).then(resp => {
                this.$message.success("该机器的状态已库存中变为处理中");
              })
            }
            this.numberInput = ""
            this.showHangUp = true
            this.dialogVisible = true
          }
        })
      }
    },
    detection() {

    }

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