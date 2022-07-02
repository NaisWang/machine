<template>
  <div>
    <el-upload
        class="upload-demo"
        ref="upload"
        :on-remove="handleRemove1"
        :on-change="handleChange1"
        :file-list="fileList1"
        accept="application/vnd.ms-excel"
        action=""
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
        :multiple="true"
        style="margin-top: 20px;"
        class="upload-demo"
        ref="upload"
        accept="application/vnd.ms-excel"
        :on-remove="handleRemove2"
        :on-change="handleChange2"
        action=""
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
        <el-button size="small" v-if="importDataDisabled" type="success" @click="stopSearchPrice"
                   icon="el-icon-video-pause">停止
        </el-button>
      </el-col>
    </el-row>

    <div style="margin-top: 15px; font-size: 8px">
      <div>- 单元格背景色为黄色, 表示表格中缺少必须要有的字段描述, 或该字段搭配没有价格</div>
      <div>- 单元格背景色为红色, 表示机型搜索不出来</div>
      <div>- 单元格背景色为蓝色, 表示没有对应的保修或电池效率</div>
      <div>- 单元格背景色为绿色, 表示excel表的机况描述字段在对照表中没有</div>
      <div>- 单元格背景色为灰色, 表示缺少拍机堂中的某个字段描述</div>
    </div>

    <div style="display: flex; justify-content: space-between">
      <div style="margin-top:12px">
        <audio id="player" controls loop="loop" src="http://120.79.195.87:8081/empImg/test-music.mp3"></audio>
      </div>
      <div style="width: 50%; margin: auto">
        <template>
          <div class="demo-progress">
            <el-progress :text-inside="true" :stroke-width="26" :percentage="completePercent"/>
          </div>
        </template>
      </div>
    </div>

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
import * as paijiApi from "../../api/paijiApi"

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
      searchPriceMethod: 1,
      completePercent: 0,
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
      this.fileList1 = [fileList[fileList.length - 1]]
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
      $http.post('http://120.79.195.87:5000/price_excel/import', param, config)
          //    $http.post('http://127.0.0.1:5000/price_excel/import', param, config)
          .then(resp => {
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
            this.onSuccess()
            new Promise(resolve =>
                setTimeout(resolve, 3000)
            ).then(() => {
              this.stopTimer = false;
            })
          }, () => {
            this.onError()
            this.stopTimer = false;
          })
    },
    get_log() {
      this.stopTimer = true;
      this.log = []
      let that = this;
      let player = document.getElementById("player")
      setTimeout(function fn() {
        if (that.stopTimer) {
          try {
            $http.post("http://120.79.195.87:5000/log", {}, {timeout: 1000 * 60 * 5}).then(resp => {
              //       $http.get("http://127.0.0.1:5000/log").then(resp => {
              that.log = resp.data
              if (that.log.authCode === 1) {
                player.play()
              }
              that.completePercent = that.log.completeRows / that.log.allRows * 100;
              setTimeout(fn, 1000);
            }).catch((e) => {
              setTimeout(fn, 1000);
            })
          } catch (e) {
            setTimeout(fn, 1000);
          }
        }
      }, 1000)
    },
    stopSearchPrice() {
      let that = this;
      setTimeout(function fn() {
        paijiApi.stopSearchPrice().then(resp => {
          that.$message.success("暂停成功")
        }).catch((e) => {
          setTimeout(fn, 1000)
        })
      }, 1000)
    }
  }
}
</script>

<style scoped>

</style>