<template>
  <div>
    <template>
      <el-breadcrumb separator="/" style="margin-bottom: 15px">
        <el-breadcrumb-item><a @click="toPurchaseOrder">库位</a></el-breadcrumb-item>
        <el-breadcrumb-item v-if="componentFlag === 0">库位</el-breadcrumb-item>
        <el-breadcrumb-item v-else-if="componentFlag === 1">库位详情</el-breadcrumb-item>
      </el-breadcrumb>
    </template>

    <SysStorageLocationShow @func="changeComponentFlag" v-if="componentFlag === 0"></SysStorageLocationShow>
    <SysStorageLocationDetail v-else :storageLocationId="storageLocationId"></SysStorageLocationDetail>
  </div>
</template>

<script>
import SysStorageLocationShow from "../../components/System/SysStorageLocation/SysStorageLocationShow.vue";
import SysStorageLocationDetail from "../../components/System/SysStorageLocation/SysStorageLocationDetail.vue";


export default {
  name: "库位设置",
  data() {
    return {
      componentFlag: 0,
      storageLocationId: undefined,
    }
  },
  methods: {
    changeComponentFlag(flag, storageLocationId) {
      this.componentFlag = flag
      this.storageLocationId = storageLocationId
    },
    toPurchaseOrder() {
      this.componentFlag = 0
    }
  },
  components: {
    SysStorageLocationShow,
    SysStorageLocationDetail
  }
}
</script>

<style scoped>

</style>
