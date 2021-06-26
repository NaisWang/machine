<template>
  <div>
    <el-form :model="form" label-width="2px" ref="form" class="model-form" :show-message="false">
      <el-table
          :data="form.modelGuaranteeBattery"
          style="width: 100%">
        <el-table-column
            prop="model"
            label="机型"
            style="color: red"
            width="300">
          <template slot-scope="scope">
            <el-form-item :prop="'modelGuaranteeBattery[' + scope.$index +'].model'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.model" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>

        <el-table-column
            prop="excelGuarantee"
            label="excel表中保修"
            width="300">
          <template slot-scope="scope">
            <el-form-item :prop="'modelGuaranteeBattery[' + scope.$index +'].excelGuarantee'"
                          style="margin: 0;"
                          :rules="{ required: true, message: '不能为空', trigger: 'blur' }">
              <el-input v-model="scope.row.excelGuarantee" placeholder="请输入内容"></el-input>
            </el-form-item>
          </template>
        </el-table-column>

        <el-table-column
            prop="paijiGuaranteeId"
            label="拍机堂中的保修"
            width="200">
          <template slot-scope="scope">
            <el-select v-model="scope.row.paijiGuaranteeId" placeholder="请选择">
              <el-option
                  v-for="id in Object.keys(guaranteeCorr).map(Number)"
                  :key="id"
                  :label="guaranteeCorr[id]"
                  :value="id">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column
            prop="batteryId"
            label="拍机堂中的电池效率"
            width="200">
          <template slot-scope="scope">
            <el-select v-model="scope.row.batteryId" placeholder="请选择">
              <el-option
                  v-for="id in Object.keys(batteryCorr).map(Number)"
                  :key="id"
                  :label="batteryCorr[id]"
                  :value="id">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column
            prop="guaranteeDefaultId"
            label="默认拍机堂中的保修"
            width="200">
          <template slot-scope="scope">
            <el-select v-model="scope.row.guaranteeDefaultId" placeholder="请选择">
              <el-option
                  v-for="id in Object.keys(guaranteeCorr).map(Number)"
                  :key="id"
                  :label="guaranteeCorr[id]"
                  :value="id">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column
            prop="guaranteeDefaultEnable"
            label="默认保修描述是否启用"
            width="200">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.guaranteeDefaultEnable" active-color="#13ce66"
                       inactive-color="#ff4949" active-text="启用"
                       inactive-text="禁用" :active-value="1" :inactive-value="0"
            ></el-switch>
          </template>
        </el-table-column>

        <el-table-column
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
      <el-button type="primary" icon="el-icon-refresh-left" @click="initModelGuaranteeBattery">重置
      </el-button>
    </div>
  </div>
</template>

<script>
import * as paijiApi from "../../../api/paijiApi"

export default {
  name: "Guarantee",
  data() {
    return {
      form: {
        modelGuaranteeBattery: [],
      },
      guaranteeCorr: {},
      batteryCorr: {}
    }
  },
  mounted() {
    this.initModelGuaranteeBattery()
    this.initBatteryCorr();
    this.initGuaranteeCorr();
  },
  methods: {
    initModelGuaranteeBattery() {
      paijiApi.getModelGuaranteeBattery().then(resp => {
        this.form.modelGuaranteeBattery = resp.data.obj;
        console.log(this.form.modelGuaranteeBattery)
      })
    },
    initGuaranteeCorr() {
      paijiApi.getGuarantee().then(resp => {
        this.guaranteeCorr = resp.data.obj;
      })
    },
    initBatteryCorr() {
      paijiApi.getBattery().then(resp => {
        this.batteryCorr = resp.data.obj;
      })
    },
    handleDelete(row, index) {
      this.form.modelGuaranteeBattery.splice(index, 1)
    },
    update() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          paijiApi.updateModelGuaranteeBattery(this.form.modelGuaranteeBattery).then(resp => {
            this.$message.success("更新成功")
            this.initModelGuaranteeBattery()
          })
        } else {
          this.$message.error("某个字段为空")
          return false;
        }
      });
    },
    addModelContrast() {
      this.form.modelGuaranteeBattery.push({
        "model": "",
        "excelGuarantee": "",
        "paijiGuaranteeId": "",
        "batteryId": "",
        "guaranteeDefaultId": "",
        "guaranteeDefaultEnable": ""
      })
    }
  }
}
</script>

<style scoped>

</style>