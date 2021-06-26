<template>
  <div>
    <el-form :model="form" label-width="2px" ref="form" class="model-form" :show-message="false">
      <el-table
          :data="form.priceCombination"
          style="width: 100%">
        <el-table-column
            prop="grade"
            label="级别&属性"
            width="130">
          <template slot-scope="scope">
            <el-form-item :prop="'priceCombination[' + scope.$index +'].grade'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'change' }">
              <el-select v-model="scope.row.grade" placeholder="请选择">
                <el-option
                    v-for="item in grade"
                    :key="item"
                    :label="item"
                    :value="item">
                </el-option>
              </el-select>
            </el-form-item>
          </template>
        </el-table-column>

        <el-table-column
            prop="excelDesc"
            label="描述"
            width="230">
          <template slot-scope="scope">
            <el-form-item :prop="'priceCombination[' + scope.$index +'].excelDesc'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'change' }">
              <el-select v-model="scope.row.excelDesc" multiple filterable placeholder="请选择">
                <el-option
                    v-for="item in xdField"
                    :key="item"
                    :label="item"
                    :value="item">
                </el-option>
              </el-select>
            </el-form-item>
          </template>
        </el-table-column>

        <el-table-column
            prop="price1"
            label="可能性1"
            key="index"
            width="220">
          <template slot-scope="scope">
            <el-select v-model="scope.row.price1" multiple filterable placeholder="请选择">
              <el-option
                  v-for="item in paijiField"
                  :key="item"
                  :label="item"
                  :value="item">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column
            prop="price2"
            label="可能性2"
            key="index"
            width="220">
          <template slot-scope="scope">
            <el-select v-model="scope.row.price2" multiple filterable placeholder="请选择">
              <el-option
                  v-for="item in paijiField"
                  :key="item"
                  :label="item"
                  :value="item">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column
            prop="price3"
            label="可能性3"
            key="index"
            width="220">
          <template slot-scope="scope">
            <el-select v-model="scope.row.price3" multiple filterable placeholder="请选择">
              <el-option
                  v-for="item in paijiField"
                  :key="item"
                  :label="item"
                  :value="item">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column
            prop="ahsCheckout"
            label="操作">
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.row, scope.$index)">删除
            </el-button>
          </template>
        </el-table-column>


      </el-table>
    </el-form>
    <div style="margin-top: 10px;">
      <el-button type="primary" icon="el-icon-plus" @click="addModelContrast">添加
      </el-button>
      <el-button type="primary" icon="el-icon-upload" @click="update">更新
      </el-button>
      <el-button type="primary" icon="el-icon-refresh-left" @click="initModelContrast">重置
      </el-button>
    </div>
  </div>
</template>

<script>
import * as paijiApi from "../../../api/paijiApi"

export default {
  name: "PriceCombination",
  data() {
    return {
      form: {
        priceCombination: [],
      },
      grade: ["95新", "9新", "8新", "8新以下"],
      xdField: [],
      paijiField: []
    }
  },
  mounted() {
    this.initPriceCombination()
    this.initExcelDesc()
    this.initXdField()
  },
  methods: {
    initPriceCombination() {
      paijiApi.getPriceCombination().then(resp => {
        if (resp['data']['obj']) {
          this.form.priceCombination = resp['data']['obj']
        }
        this.stringToArray()
        console.log(this.form.priceCombination)
      })
    },
    initExcelDesc() {
      paijiApi.getXdField().then(resp => {
        if (resp['data']['data']) {
          this.xdField = resp['data']['data']
        }
      })
    },
    initXdField() {
      paijiApi.getPaijiField().then(resp => {
        if (resp['data']['data']) {
          this.paijiField = resp['data']['data']
        }
      })
    },
    addModelContrast() {
      this.form.priceCombination.push({"grade": "", "excelDesc": "", "price1": "", "price2": "", "price3": ""})
    },
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.form.priceCombination.forEach(item => {
            item['excelDesc'] = item['excelDesc'].toString()
            item['price1'] = item['price1'].toString()
            item['price2'] = item['price2'].toString()
            item['price3'] = item['price3'].toString()
          })
          paijiApi.updatePriceCombination(this.form.priceCombination).then(resp => {
            this.initPriceCombination()
            this.$message.success("更新成功")
          })
        } else {
          this.$message.error("某个字段为空")
          return false;
        }
      });
    },
    stringToArray() {
      this.form.priceCombination.forEach(item => {
        item['excelDesc'] = item['excelDesc'].split(",")
        for (let i = 1; i <= 3; i++) {
          if (item['price' + i] === "") {
            item['price' + i] = []
          } else {
            item['price' + i] = item['price' + i].split(",")
          }
        }
      })
    },
    handleDelete(row, index) {
      this.form.priceCombination.splice(index, 1)
    }
  }
}
</script>

<style scoped>

</style>