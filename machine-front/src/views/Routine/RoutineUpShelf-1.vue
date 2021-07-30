<!--<template>-->
<!--  <div class="routineQuality">-->
<!--    <el-input clearable placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"-->
<!--              v-model="numberInput"></el-input>-->

<!--    <MachineShowDetail :machines="scanMachine" :paging="false"-->
<!--                       :tableName="'machineShelf'" table-operate="add"  :extra-not-show="[]"></MachineShowDetail>-->

<!--    <el-dialog-->
<!--        v-if="Object.keys(nowEditMachine).length !== 0"-->
<!--        title="机器上架"-->
<!--        :before-close="handleClose"-->
<!--        :visible.sync="dialogVisible"-->
<!--        width="90%">-->

<!--      <MachineShowDetail :machines="[nowEditMachine]" :paging="false"-->
<!--                         :tableName="'machineShelf'" table-operate="add"-->
<!--                         :extra-not-show="['rankDesc','onePrice' , 'goodPrice', 'bagNumber', 'bidPrice', 'footer', 'operate']"></MachineShowDetail>-->
<!--      <hr style="margin-top: 15px">-->
<!--      <h2 style="color: #409EFF">机器上架信息设置：</h2>-->
<!--      <el-form label-width="80px">-->
<!--        <el-form-item label="一口价">-->
<!--          <el-input v-model="onePrice"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="最高价">-->
<!--          <el-input v-model="nowEditMachine.bidPrice"></el-input>-->
<!--        </el-form-item>-->
<!--        <span v-if="nowEditMachine.bidPrice">预测利润： {{-->
<!--            nowEditMachine.bidPrice * 0.03 < 50 ? nowEditMachine.bidPrice * 0.97 - nowEditMachine.purchasePrice : nowEditMachine.bidPrice - 50 - nowEditMachine.purchasePrice-->
<!--          }}</span>-->
<!--        <el-form-item label="好的价格">-->
<!--          <el-input v-model="nowEditMachine.goodPrice"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="机器等级">-->
<!--          <el-input v-model="nowEditMachine.rankDesc"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="袋子编码">-->
<!--          <el-input v-model="nowEditMachine.bagNumber"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="备注">-->
<!--          <el-input v-model="nowEditMachine.comment"></el-input>-->
<!--        </el-form-item>-->
<!--      </el-form>-->

<!--      <span class="dialog-footer" slot="footer">-->
<!--                  <el-button @click="cancel">取 消</el-button>-->
<!--                  <el-button type="primary" @click="submit">提 交</el-button>-->
<!--                </span>-->
<!--    </el-dialog>-->


<!--  </div>-->
<!--</template>-->


<!--<script>-->
<!--import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";-->
<!--import {getMachine} from "../../api/machineApi";-->

<!--export default {-->
<!--  name: "机器上架",-->
<!--  data() {-->
<!--    return {-->
<!--      scanMachine: [],-->
<!--      nowEditMachine: {},-->
<!--      numberInput: "",-->
<!--      dialogVisible: false,-->
<!--    }-->
<!--  },-->
<!--  methods: {-->
<!--    addMachineByScan() {-->
<!--      if (this.numberInput !== "") {-->
<!--        let _this = this-->
<!--        getMachine(1, 10, {"number": this.numberInput}).then(resp => {-->
<!--          if (resp.data.obj) {-->
<!--            if (resp.data.obj.total === 0) {-->
<!--              this.$message.error("没有物品编号为:" + this.numberInput + "的机器")-->
<!--              return-->
<!--            }-->
<!--            if (resp.data.obj.total >= 2) {-->
<!--              this.$message.error("物品编号为:" + this.numberInput + "的机器在库存中超过2个")-->
<!--              return-->
<!--            }-->
<!--            if (this.judgeIsAdd(resp.data.obj.data[0].number) === -1) {-->
<!--              this.$message.error("物品编号为:" + this.numberInput + "的机器已添加到该单据中了")-->
<!--              return-->
<!--            }-->
<!--            _this.nowEditMachine = resp.data.obj.data[0]-->
<!--            console.log("aaa")-->
<!--            console.log(_this.nowEditMachine)-->
<!--            _this.numberInput = ""-->
<!--            this.showHangUp = true-->
<!--            _this.dialogVisible = true-->
<!--          }-->
<!--        })-->
<!--      }-->
<!--    },-->
<!--    //判断编号为number的机器是否已经添加到单据中-->
<!--    judgeIsAdd(number) {-->
<!--      let flag = 1-->
<!--      this.scanMachine.forEach(item => {-->
<!--        if (item.number === number) {-->
<!--          flag = -1-->
<!--        }-->
<!--      })-->
<!--      return flag-->
<!--    },-->
<!--  },-->
<!--  components: {-->
<!--    MachineShowDetail-->
<!--  }-->
<!--}-->
<!--</script>-->

<!--<style>-->
<!--</style>-->

<template>
  <div class="">
    <el-input clearable style="width: 89%" placeholder="请输入物品编号" @keydown.enter.native="addMachineByScan"
              v-model="numberInput"></el-input>
    <el-select style="width: 9%" v-model="searchMethod" placeholder="请选择">
      <el-option
          v-for="item in searchOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value">
      </el-option>
    </el-select>

    <MachineShowDetail :machines="scanMachine" :paging="false"
                       @func="editMachine"
                       :tableName="'machineShelf'" table-operate="add" :extra-not-show="[]"></MachineShowDetail>

    <el-dialog
        :modal-append-to-body='false'
        v-if="Object.keys(nowEditMachine).length !== 0"
        title="确定上架信息"
        :before-close="handleClose"
        :visible.sync="dialogVisible"
        width="90%">
      <MachineShowDetail :machines="[nowEditMachine]" :paging="false"
                         :tableName="'machineShelf'" table-operate="add"
                         :extra-not-show="['qualityDesc', 'featureDesc', 'onePrice', 'bidPrice','goodPrice','bagNumber','rankDesc','footer', 'operate']"></MachineShowDetail>

      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">检测情况项：</h2>

      <el-form status-icon label-width="100px" class="demo-ruleForm">
        <el-form-item label="成色检测情况"
                      style="margin: 0;">
          <el-tag
              v-for="item in (nowEditMachine.qualityDesc=== null ? '' : nowEditMachine.qualityDesc.split(','))"
              :key="item" v-if="item !== ''">
            {{ $store.state.machineIdToDesc[item] }}
          </el-tag>
        </el-form-item>

        <el-form-item label="功能检测情况"
                      style="margin: 0;">
          <el-tag
              v-for="item in (nowEditMachine.featureDesc === null ? '' : nowEditMachine.featureDesc.split(','))"
              :key="item" v-if="item !== ''">
            {{ $store.state.machineIdToDesc[item] }}
          </el-tag>
        </el-form-item>

        <el-form-item label="机器备注"
                      style="margin: 0;">
          <el-input disabled :value="nowEditMachine.comment"></el-input>
        </el-form-item>
      </el-form>


      <hr style="margin-top: 15px">
      <h2 style="color: #409EFF">上架项：</h2>

      <el-form :model="nowEditMachine" status-icon ref="form" label-width="100px" class="demo-ruleForm">

        <el-form-item :prop="'goodPrice'"
                      label="好的价格"
                      style="margin: 0;"
                      :show-message="false">

          <el-input type="text" v-model="nowEditMachine.editGoodPrice" placeholder="请输入价格"></el-input>
        </el-form-item>

        <el-form-item :prop="'editOnePrice'"
                      label="一口价"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">
          <el-input type="text" v-model="nowEditMachine.editOnePrice" placeholder="请输入价格"></el-input>
        </el-form-item>

        <el-form-item :prop="'editBidPrice'"
                      label="最高价"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">
          <el-input type="text" v-model="nowEditMachine.editBidPrice" placeholder="请输入价格"></el-input>
        </el-form-item>

        <el-form-item :prop="'editRankDesc'"
                      label="机器等级"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">

          <el-input type="text" v-model="nowEditMachine.editRankDesc" placeholder="请输入机器等级检测"></el-input>
        </el-form-item>

        <el-form-item :prop="'editBagNumber'"
                      label="袋子编码"
                      style="margin: 0;"
                      :show-message="false"
                      :rules="{ required: true,trigger: 'blur' }">

          <el-input type="text" v-model="nowEditMachine.editBagNumber" placeholder="请输入袋子编码"></el-input>
        </el-form-item>

        <el-form-item :prop="'editComment'"
                      label="备注"
                      style="margin: 0;"
                      :show-message="false">
          <el-input type="text" v-model="nowEditMachine.editComment" placeholder="请输入备注"></el-input>
        </el-form-item>

      </el-form>


      <span class="dialog-footer" slot="footer">
                  <el-button @click="cancel">取 消</el-button>
                  <el-button type="primary" @click="hangUp" v-if="showHangUp">挂 起</el-button>
                  <el-button type="primary" @click="submit">提 交</el-button>
                </span>
    </el-dialog>


  </div>

</template>

<script>
import MachineShowDetail from "../../components/Machine/MachineShowDetail.vue";
import {getMachine, modifyFixItem, modifyMachineUpShelf} from "../../api/machineApi";
import {modifyMachineFeature} from "../../api/machineApi";
import {dealMachineJudge} from "../../utils/dealMachineJudge";

export default {
  name: "机器上架",
  data() {
    return {
      scanMachine: [],
      numberInput: "",
      test: null,
      dialogVisible: false,
      nowEditMachine: {},
      category: {1: 'phone', 2: 'tablet', 3: '手表'},
      showHangUp: false,
      searchOptions: [{
        value: 'number',
        label: '物品编码'
      }, {
        value: 'imei',
        label: 'imei号'
      }, {
        value: 'paijiBarCode',
        label: '拍机堂条码'
      }],
      searchMethod: "number"
    }
  },
  methods: {
    addMachineByScan() {
      if (this.numberInput !== "") {
        let _this = this
        let search = {}
        if (this.searchMethod === 'number') {
          search['number'] = this.showMachineDetailNumber
        } else if (this.searchMethod === 'imei') {
          search['imei'] = this.showMachineDetailNumber
        } else if (this.searchMethod === 'paijiBarCode') {
          search['paijiBarcode'] = this.showMachineDetailNumber
        }
        getMachine(1, 10, search).then(resp => {
          if (resp.data.obj) {
            if (resp.data.obj.total === 0) {
              this.$message.error("没有该机器")
              return
            }

            let machine = resp.data.obj.data[0]

            if (Object.keys(this.category).indexOf(machine['categoryId'] + '') === -1) {
              this.$message.error("该机器的类别不是手机、平板、手表")
              this.numberInput = ""
              return
            }

            let ans = this.judgeIsSubmit(machine.number)
            if (ans.type === -1) {
              let _this = this
              this.editMachine(ans.value);
              setTimeout(function () {
                _this.$message.error("该机器已经确定了维修项，且提交了");
              }, 50);
              return
            } else if (ans.type === 0) {
              this.editMachine(ans.value);
              return
            }

            dealMachineJudge(machine, this.$store, "upShelf").then(resp => {
              if (resp.code === -1) {
                this.$message.error(resp.message);
              } else if (resp.code === 1) {
                _this.nowEditMachine = machine
                _this.numberInput = ""
                _this.showHangUp = true
                _this.dialogVisible = true
              }
              if (resp.receiveDiagLog !== undefined) {
                let _this = this
                setTimeout(function () {
                  _this.$notify(resp.receiveDiagLog)
                }, 50);
              }
            })
          }
        })
      }
    },
    //判断编号为number的机器是否已经添加到单据中
    judgeIsSubmit(number) {
      let ans = {type: 1}
      this.scanMachine.forEach(item => {
        if (item.number === number) {
          if (item.operate !== undefined && item.operate.submitted === 1) {
            ans = {type: -1, value: item}
          } else {
            ans = {type: 0, value: item}
          }
        }
      })
      return ans
    },
    submit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$confirm('是否确定要提交', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            modifyMachineUpShelf(this.nowEditMachine).then(resp => {
              if (resp.data.code === 200) {
                this.$message.success("提交成功");
                this.nowEditMachine.statusId = resp.data.obj.statusId;
                this.nowEditMachine.operateEmpId = resp.data.obj.operateEmpId;
                this.nowEditMachine.operateDate = resp.data.obj.operateDate;

                this.nowEditMachine.goodPrice = resp.data.obj.goodPrice;
                this.nowEditMachine.onePrice = resp.data.obj.onePrice;
                this.nowEditMachine.bidPrice = resp.data.obj.bidPrice;
                this.nowEditMachine.rankDesc = resp.data.obj.rankDesc;
                this.nowEditMachine.bagNumber = resp.data.obj.bagNumber;
                this.nowEditMachine.comment = resp.data.obj.comment;

                this.nowEditMachine.editGoodPrice = ""
                this.nowEditMachine.editOnePrice = ""
                this.nowEditMachine.editBidPrice = ""
                this.nowEditMachine.editComment = ""
                this.nowEditMachine.editRankDesc = ""
                this.nowEditMachine.editBagNumber = ""


                this.nowEditMachine.operate = {}
                this.nowEditMachine.operate.submitted = 1
                this.nowEditMachine.operate.edit = 0
                if (this.showHangUp) {
                  this.scanMachine.push(this.nowEditMachine)
                }
                this.nowEditMachine = {}
                this.dialogVisible = false
              } else {
                this.$message.error("提交失败");
              }
            })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消提交'
            });
          });
        } else {
          this.$message.error("输入有误");
          return false;
        }
      });
    },
    handleClose(done) {
      this.$confirm('确认关闭？')
          .then(_ => {
            done();
            this.showHangUp = false
          })
          .catch(_ => {
          });
    },
    hangUp() {
      this.nowEditMachine.operate = {}
      this.nowEditMachine.operate.submitted = 0
      this.nowEditMachine.operate.notSubmitted = 1
      this.nowEditMachine.operate.edit = 1
      this.scanMachine.push(this.nowEditMachine)
      this.nowEditMachine = {}
      this.dialogVisible = false
    },
    editMachine(machine) {
      this.nowEditMachine = machine
      this.showHangUp = false
      this.dialogVisible = true
    },
    cancel() {
      this.nowEditMachine = {}
      this.dialogVisible = false
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
