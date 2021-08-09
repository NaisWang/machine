<template>
  <div>
    <!--    <el-card class="box-card">-->
    <!--      <div slot="header" class="clearfix">-->
    <!--        <span>当天</span>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(2) !== -1" class="text item">-->
    <!--        成色检测数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['1'][0]["qualityDetection"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(3) !== -1" class="text item">-->
    <!--        功能检测数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['1'][0]["featureDetection"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(8) !== -1" class="text item">-->
    <!--        上架数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['1'][0]["upShelf"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(6) !== -1" class="text item">-->
    <!--        维修完数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['1'][0]["fixed"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--    </el-card>-->

    <!--    <el-card class="box-card">-->
    <!--      <div slot="header" class="clearfix">-->
    <!--        <span>一周</span>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(2) !== -1" class="text item">-->
    <!--        成色检测数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['2'][0]["qualityDetection"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(3) !== -1" class="text item">-->
    <!--        功能检测数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['2'][0]["featureDetection"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(8) !== -1" class="text item">-->
    <!--        上架数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['2'][0]["upShelf"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(6) !== -1" class="text item">-->
    <!--        维修完数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['2'][0]["fixed"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--    </el-card>-->

    <!--    <el-card class="box-card">-->
    <!--      <div slot="header" class="clearfix">-->
    <!--        <span>一月</span>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(2) !== -1" class="text item">-->
    <!--        成色检测数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['3'][0]["qualityDetection"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(3) !== -1" class="text item">-->
    <!--        功能检测数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['3'][0]["featureDetection"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(8) !== -1" class="text item">-->
    <!--        上架数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['3'][0]["upShelf"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(6) !== -1" class="text item">-->
    <!--        维修完数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['3'][0]["fixed"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--    </el-card>-->

    <!--    <el-card class="box-card">-->
    <!--      <div slot="header" class="clearfix">-->
    <!--        <span>全部</span>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(2) !== -1" class="text item">-->
    <!--        成色检测数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['4'][0]["qualityDetection"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(3) !== -1" class="text item">-->
    <!--        功能检测数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['4'][0]["featureDetection"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(8) !== -1" class="text item">-->
    <!--        上架数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['4'][0]["upShelf"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--      <div v-if="$store.state.empIdToRoleIdsCorr[$store.state.userId].indexOf(6) !== -1" class="text item">-->
    <!--        维修完数：-->
    <!--        <el-tag>-->
    <!--          {{ statistics['4'][0]["fixed"] + "个" }}-->
    <!--        </el-tag>-->
    <!--      </div>-->
    <!--    </el-card>-->

    <AllStatistics></AllStatistics>

    <MachineShowDetail v-if="dialogVisible" :machines="machines"></MachineShowDetail>

  </div>
</template>

<script>
import {getAllStatistics, getStatistics} from "../api/statisticsApi";
import MachineShowDetail from "../components/Machine/MachineShowDetail.vue";
import AllStatistics from "../components/Statistics/AllStatistics.vue";

export default {
  name: "首页",
  components: {MachineShowDetail, AllStatistics},
  data() {
    return {
      statistics: {},
      machines: [],
      dialogVisible: false
    }
  },
  mounted() {
    this.refresh()
  },
  methods: {
    refresh(type) {
      //let statusIds = []
      ////成色检测人员
      //if (this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(2)) {
      //  statusIds.push(8);
      //}
      ////功能检测人员
      //if (this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(3)) {
      //  statusIds.push(9);
      //}
      ////上架人员
      //if (this.$store.state.empIdToRoleIdsCorr[this.$store.state.userId].indexOf(8)) {
      //  statusIds.push(9);
      //  statusIds.push()
      //}
      //this.initStatistics();
      //if (type === 1) {
      //  this.$message.success("刷新成功");
      //}
      this.initStatistics()
    },
    //initStatistics() {
    //  getStatistics(this.$store.state.userId).then(resp => {
    //    if (resp.data.code === 200) {
    //      this.statistics = resp.data.obj
    //    }
    //  })
    //},

    initStatistics() {
      getAllStatistics([this.$store.state.userId], Object.keys(this.$store.state.machineStatusCorr).map(Number), [0, 1, 2, 3]).then(resp => {
        if (resp.data.code === 200) {
          console.log(resp)
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