<template>
  <div class="desc">
    <div>
      <el-button type="primary" icon="el-icon-search" @click="updatePaijiTable()">更新拍机堂对照表数据
      </el-button>
      <el-button type="primary" icon="el-icon-search" @click="updatePaijiField()">更新拍机堂字段数据
      </el-button>

      <!--      <div>-->
      <!--        <el-row>-->
      <!--          <el-col :span="8" style="margin-right: 10px;">-->
      <!--            拍机堂字段：-->
      <!--            <el-input style="width: 300px; margin-right: 10px;" v-model="searchMachine.number"-->
      <!--                      prefix-icon="el-icon-search"-->
      <!--                      placeholder=""-->
      <!--                      clearable-->
      <!--                      @clear="initMachine"></el-input>-->
      <!--          </el-col>-->
      <!--          <el-col :span="8" style="margin-right: 10px;">-->
      <!--            对应描述字段：-->
      <!--            <el-input style="width: 300px; margin-right: 10px;" v-model="searchMachine.number"-->
      <!--                      prefix-icon="el-icon-search"-->
      <!--                      placeholder=""-->
      <!--                      :disabled="showAdvanceSearchVisible"-->
      <!--                      clearable-->
      <!--                      @clear="initMachine"></el-input>-->
      <!--          </el-col>-->
      <!--          <el-col :span="8" style="margin-right: 10px;">-->
      <!--            <el-button type="primary" icon="el-icon-search" @click="doSearch()" :disabled="showAdvanceSearchVisible">搜索-->
      <!--            </el-button>-->
      <!--          </el-col>-->
      <!--        </el-row>-->
      <!--      </div>-->
    </div>
    <template>
      <el-table
          v-loading="loading"
          element-loading-text="拼命加载中"
          element-loading-spinner="el-icon-loading"
          element-loading-background="rgba(0, 0, 0, 0.8)"
          :span-method="objectSpanMethod"
          :data="paiji"
          style="width: 100%">
        <el-table-column width="200" label="归类" prop="name" align="center"></el-table-column>
        <el-table-column
            prop="value"
            disabled
            label="拍机堂"
            style="color: red"
            width="300">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 2 || scope.row.status === 1"
                  style="color: red">{{ scope.row.value }}</span>
            <span v-else>{{ scope.row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column
            prop="xdCheckout"
            label="小当"
            width="500">
          <template slot-scope="scope">
            <el-input v-model="scope.row.xdCheckout" placeholder="请输入内容"></el-input>
          </template>
        </el-table-column>
        <el-table-column
            prop="ahsCheckout"
            label="采货侠"
            width="500">
          <template slot-scope="scope">
            <el-input v-model="scope.row.ahsCheckout" placeholder="请输入内容"></el-input>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </div>
</template>

<script>
import * as paijiApi from "../../../api/paijiApi"

export default {
  name: "Describe",
  data() {
    return {
      paiji: [],
      // 合并的长度
      colSpanArr: [],
      // 合并开始的位置
      rowIndexArr: [],
      loading: false,
    }
  },
  mounted() {
    this.initPaiji()
  },
  methods: {
    initPaiji() {
      paijiApi.getPaiji().then(resp => {
        if (resp) {
          console.log(resp)
          this.paiji = resp.data.obj;
          this.getColSpanData("name", this.paiji);
        }
      })
    },
    //  获取合并行的位置和合并长度
    getColSpanData(prop, data) {
      let colLength = 1;
      data.forEach((element, index) => {
        if (index == 0) {
          // rowIndex开始合并的位置为0,合并单元格数量默认为1
          this.rowIndexArr.push(0);
          colLength = 1;
        } else {

          if (element[prop] == data[index - 1][prop]) {
            // 当相邻两条数据相等时，rowspan+1,即colLength+1
            colLength += 1;
            // 当最后一条数据与倒数第2条不等式，在colSpanArr中push(1)
            if (index == data.length - 1) {
              this.colSpanArr.push(colLength);
            }
          } else {
            //  相邻两条不等时push index和合并的长度
            this.colSpanArr.push(colLength);
            this.rowIndexArr.push(index);
            if (index == data.length - 1) {
              this.colSpanArr.push(1);
            }
            colLength = 1;
          }
        }
      });
      this.rowIndexArr.push(data.length);
      console.log(this.rowIndexArr, this.colSpanArr)
      //  结果分别是：
      //this.rowIndexArr:[0, 2, 5, 7, 9，10]
      //this.colSpanArr:[2, 3, 2, 2, 1]
    },
    // 合并单元格
    objectSpanMethod({row, column, rowIndex, columnIndex}) {
      if (columnIndex === 0) {
        for (let i = 0; i < this.colSpanArr.length; i++) {
          //如果rowIndex和this.rowIndexArr[i]相等则开始合并，合并数量对应为this.colSpanArr[i]
          if (rowIndex === this.rowIndexArr[i]) {
            return {
              rowspan: this.colSpanArr[i],
              colspan: 1
            };
          } else {
            if (
                rowIndex > this.rowIndexArr[i] &&
                rowIndex < this.rowIndexArr[i + 1]
            ) {
              return {
                //被合并行则返回rowspan: 0, colspan: 0，
                //这里被合并行为1,3,4,6,8
                rowspan: 0,
                colspan: 0
              };
            }
          }
        }
      }
    },
    updatePaijiField() {
      this.loading = true
      paijiApi.updatePaijiField().then(resp => {
        this.loading = false;
        this.$message.success("拍机堂字段给更新完成")
        this.initPaiji()
      })
    },
    updatePaijiTable() {
      paijiApi.updatePaijiTable(this.paiji).then(resp => {
        this.$message.success("更新成功")
        this.initPaiji()
      })
    }
  }
}
</script>

<style>
.desc .el-table--small td, .desc .el-table--small td {
  padding: 0 !important;
}
</style>
