<template>
  <div>
    <el-form :model="form" label-width="2px" ref="form" class="model-form" :show-message="false">
      <el-table
          :data="form.priceCombination"
          style="width: 100%">
        <el-table-column
            prop="name"
            label="名称"
            width="130">
          <template slot-scope="scope">
            <el-form-item :prop="'priceCombination[' + scope.$index +'].name'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.name" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>

        <el-table-column
            prop="grade"
            label="级别&属性"
            width="130">
          <template slot-scope="scope">
            <el-form-item :prop="'priceCombination[' + scope.$index +'].grade'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'change' }">
              <el-select v-model="scope.row.grade" multiple placeholder="请选择">
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
            prop="screenAppearance"
            label="屏幕外观"
            width="230">
          <template slot-scope="scope">
            <el-form-item :prop="'priceCombination[' + scope.$index +'].screenAppearance'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'change' }">
              <el-select v-model="scope.row.screenAppearance" multiple filterable placeholder="请选择">
                <el-option
                    v-for="item in xdCombinationPriceField['屏幕外观']"
                    :title="item"
                    style="width: 400px"
                    :key="item"
                    :label="item"
                    :value="item">
                </el-option>
              </el-select>
            </el-form-item>
          </template>
        </el-table-column>

        <el-table-column
            prop="iframeBack"
            label="边框背板"
            width="230">
          <template slot-scope="scope">
            <el-form-item :prop="'priceCombination[' + scope.$index +'].iframeBack'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'change' }">
              <el-select v-model="scope.row.iframeBack" multiple filterable placeholder="请选择">
                <el-option
                    v-for="item in xdCombinationPriceField['边框背板']"
                    :title="item"
                    style="width: 400px"
                    :key="item"
                    :label="item"
                    :value="item">
                </el-option>
              </el-select>
            </el-form-item>
          </template>
        </el-table-column>


        <el-table-column
            prop="screenDisplay"
            label="屏幕显示"
            width="230">
          <template slot-scope="scope">
            <el-form-item :prop="'priceCombination[' + scope.$index +'].screenDisplay'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'change' }">
              <el-select v-model="scope.row.screenDisplay" multiple filterable placeholder="请选择">
                <el-option
                    v-for="item in xdCombinationPriceField['屏幕显示']"
                    :title="item"
                    style="width: 400px"
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
      <el-button type="primary" icon="el-icon-refresh-left" @click="initPriceCombination">重置
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
      paijiField: [],
      xdCombinationPriceField: {}
    }
  },
  mounted() {
    this.initPriceCombination()
    this.initXdField()
    this.initXdCombinationPriceField()
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
    initXdField() {
      paijiApi.get_paiji_combination_price_field().then(resp => {
        if (resp['data']['data']) {
          this.paijiField = resp['data']['data']
        }
      })
    },
    initXdCombinationPriceField() {
      paijiApi.getXdCombinationPriceField().then(resp => {
        if (resp['data']) {
          this.xdCombinationPriceField = resp['data']
        }
      })
    },
    addModelContrast() {
      this.form.priceCombination.push({
        "name": "",
        "grade": "",
        "screenAppearance": "",
        "iframeBack": "",
        "screenDisplay": "",
        "price1": "",
        "price2": "",
        "price3": ""
      })
    },
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.$confirm('是否确定要更新', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.form.priceCombination.forEach(item => {
              item["grade"] = this.arrayToString(item["grade"])
              item['screenAppearance'] = this.arrayToString(item["screenAppearance"])
              item['iframeBack'] = this.arrayToString(item["iframeBack"])
              item['screenDisplay'] = this.arrayToString(item["screenDisplay"])
              item['price1'] = this.arrayToString(item["price1"])
              item['price2'] = this.arrayToString(item["price2"])
              item['price3'] = this.arrayToString(item["price3"])
            })
            console.log(this.form.priceCombination)
            paijiApi.updatePriceCombination(this.form.priceCombination).then(resp => {
              this.initPriceCombination()
              this.$message.success("更新成功")
            })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消更新'
            });
          });
        } else {
          this.$message.error("某个字段为空")
          return false;
        }
      });
    },
    arrayToString(items) {
      let ans = ""
      items.forEach(item => {
        ans += item + "、"
      })
      if (ans !== "") {
        ans = ans.substr(0, ans.length - 1);
      }
      return ans
    },
    stringToArray() {
      this.form.priceCombination.forEach(item => {
        item['grade'] = item['grade'].split("、")
        item['screenAppearance'] = item['screenAppearance'].split("、")
        item['iframeBack'] = item['iframeBack'].split("、")
        item['screenDisplay'] = item['screenDisplay'].split("、")
        for (let i = 1; i <= 3; i++) {
          if (item['price' + i] === "") {
            item['price' + i] = []
          } else {
            item['price' + i] = item['price' + i].split("、")
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

<style>
.el-select__tags-text {
  display: inline-block;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.el-select .el-tag__close.el-icon-close {
  top: -7px;
}
</style>