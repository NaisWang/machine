<template>
  <div>
    <div>
      <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>
    </div>

    <el-card class="box-card" v-for="(item, index) in myStatistics" :key="index">
      <div>{{ item.sum }}</div>
      <div>{{ $store.state.machineStatusCorr[item.statusId] }}</div>
    </el-card>

    <!--    <AllStatistics></AllStatistics>-->
    <MachineShowDetail v-if="dialogVisible" :machines="machines"></MachineShowDetail>
  </div>
</template>

<script>
import {getAllStatistics, getOneStatistics} from "../api/statisticsApi";
import MachineShowDetail from "../components/Machine/MachineShowDetail.vue";
import AllStatistics from "../components/Statistics/AllStatistics.vue";

export default {
  name: "首页",
  components: {MachineShowDetail, AllStatistics},
  data() {
    return {
      myStatistics: [],
      machines: [],
      dialogVisible: false
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      this.initMyStatistics();
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initMyStatistics() {
      getOneStatistics().then(resp => {
        if (resp.data.code === 200) {
          this.myStatistics = resp.data.obj
        }
      })
    },
    detail(search) {

    }
  }
}
</script>

<style scoped>

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 10px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.box-card {
  display: inline-block;
  width: 24%;
}
</style>