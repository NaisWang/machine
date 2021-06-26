<template>
  <div>
    <el-upload
        class="upload-demo"
        ref="upload"
        :on-remove="handleRemove1"
        :on-change="handleChange1"
        :file-list="fileList1"
        accept="application/vnd.ms-excel"
        limit=1
        :auto-upload="false">
      <template #trigger>
        <el-button size="small" type="primary">选取文件</el-button>
      </template>
      <template #tip>
        <div class="el-upload__tip">
          请选择一个要查询价格的excel表
        </div>
      </template>
    </el-upload>

    <el-upload
        style="margin-top: 20px;"
        class="upload-demo"
        ref="upload"
        accept="application/vnd.ms-excel"
        :on-remove="handleRemove2"
        :on-change="handleChange2"
        :file-list="fileList2"
        :auto-upload="false">
      <template #trigger>
        <el-button size="small" type="primary">选取文件</el-button>
      </template>
      <template #tip>
        <div class="el-upload__tip">
          请选择对照表
        </div>
      </template>
    </el-upload>

    <el-row style="margin-top: 30px;">
      <el-col :span="5">
        <el-switch
            style="display: block"
            v-model="searchPriceMethod"
            active-color="#13ce66"
            inactive-color="#ff4949"
            active-text="小当"
            inactive-text="采货侠"
            :active-value="1"
            :inactive-value="0"
        >
        </el-switch>
      </el-col>
      <el-col :span="5">
        <el-button size="small" type="success" @click="update" :disabled="importDataDisabled"
                   :icon="importDataBtnIcon">{{ importDataBtnText }}
        </el-button>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="12">
        <h4>日志(查询成功)展示：</h4>
        <el-scrollbar height="800px" style="border: 1px solid black; height: 800px">
          <div v-for="(item, index) in log.log_success" :key="index">{{ item }}</div>
        </el-scrollbar>
      </el-col>

      <el-col :span="12">
        <h4>日志(查询失败)展示：</h4>
        <el-scrollbar height="800px" style="border: 1px solid black; height: 800px">
          <div v-for="(item, index) in log.log_error" :key="index">{{ item }}</div>
        </el-scrollbar>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import $http from "axios"

export default {
  name: "自动报价",
  data() {
    return {
      importDataBtnIcon: 'el-icon-upload2',
      importDataDisabled: false,
      importDataBtnText: '上传到服务器',
      stopTimer: true,
      log: {},
      uploadFiles: {},
      fileList1: [],
      fileList2: [],
      searchPriceMethod: 1
    }
  },
  methods: {
    beforeUpload() {
      this.importDataBtnIcon = 'el-icon-loading';
      this.importDataBtnText = '正在计算';
      this.importDataDisabled = true;
    },
    onSuccess() {
      this.importDataBtnIcon = 'el-icon-upload2';
      this.importDataBtnText = '上传到服务器';
      this.importDataDisabled = false;
    },
    onError() {
      this.importDataBtnIcon = 'el-icon-upload2';
      this.importDataBtnText = '上传到服务器';
      this.importDataDisabled = false;
      this.$message.error("导入失败")
    },
    handleChange1(e, fileList) {
      this.fileList1 = fileList;
    },
    handleRemove1(file, fileList) {
      this.fileList1 = fileList;
    },
    handleChange2(e, fileList) {
      this.fileList2 = fileList;
    },
    handleRemove2(file, fileList) {
      this.fileList2 = fileList;
    },
    update() {
      if (this.fileList1.length === 0) {
        this.$message.error("请选择一个要查询价格的excel表");
        return
      }
      this.beforeUpload()
      let param = new FormData();
      this.fileList1.forEach(item => {
        param.append('file', item.raw)

      })
      this.fileList2.forEach(item => {
        param.append('files', item.raw)
      })

      param.append('searchPriceMethod', this.searchPriceMethod)

      let config = {
        headers: {'Content-Type': 'multipart/form-data'},
        responseType: 'blob'
      };
      this.get_log()
      $http.post('http://127.0.0.1:5000/price_excel/import', param, config)
          .then(resp => {
            this.stopTimer = false;
            this.onSuccess()
            let data = resp.data;
            let blob = new Blob([data], {type: 'application/vnd.ms-excel'});
            let downloadElement = document.createElement('a');
            let href = window.URL.createObjectURL(blob);
            downloadElement.href = href;
            downloadElement.download = "价格表.xls"
            document.body.appendChild(downloadElement);
            downloadElement.click();
            document.body.removeChild(downloadElement);
            window.url.revokeObjectURL(href);
            this.onSuccess()
          }, () => {
            this.onError()
          })
    },
    get_log() {
      this.stopTimer = true;
      this.log = []
      let that = this;
      setTimeout(function fn() {
        if (that.stopTimer) {
          $http.get("http://127.0.0.1:5000/log").then(resp => {
            that.log = resp.data
            console.log(that.log)
            console.log(that.log.length)
            setTimeout(fn, 1000);
          })
        }
      }, 1000)
    }
  }
}
</script>

<style scoped>

</style>