<template>
  <div>
    <el-tabs type="border-card" v-model="activeName">
      <el-tab-pane label="描述对照表" name="Describe">
        <Describe></Describe>
      </el-tab-pane>
      <el-tab-pane label="描述对照表(备份)" name="DescBackup">
        <DescBackup></DescBackup>
      </el-tab-pane>
      <el-tab-pane label="机型对照表" name="Model">
        <Model></Model>
      </el-tab-pane>
      <el-tab-pane label="机型-保修-电池容量对照表" name="Guarantee">
        <Guarantee></Guarantee>
      </el-tab-pane>
      <el-tab-pane label="询价规则" name="PriceCombination">
        <PriceCombination></PriceCombination>
      </el-tab-pane>
      <el-tab-pane label="询价用户管理" name="User">
        <User></User>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>


<script>
import Describe from "../../components/System/paiji/Desc.vue"
import DescBackup from "../../components/System/paiji/DescBackup.vue";
import Model from "../../components/System/paiji/Model.vue"
import Guarantee from "../../components/System/paiji/Guarantee.vue";
import PriceCombination from "../../components/System/paiji/PriceCombination.vue";
import User from "../../components/System/paiji/User.vue";

export default {
  name: "拍机堂管理",
  data() {
    return {
      activeName: "Describe"
    }
  },
  components: {
    Describe,
    DescBackup,
    Model,
    Guarantee,
    PriceCombination,
    User
  }
}
</script>

<style scoped>

</style>

