<template>
  <div>
    <div>
      <el-button type="primary" icon="el-icon-plus" @click="addReceiptDialogVisible = true">添加库位</el-button>
      <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>
    </div>

    <div>
      <el-table
          :data="allOrderInfo"
          style="width: 100%">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>

        <el-table-column
            prop="name"
            label="库位"
            width="170">
        </el-table-column>

        <el-table-column
            prop="subStorageSum"
            label="子库数"
            width="170">
        </el-table-column>

        <el-table-column
            prop="machineSum"
            label="机器数"
            width="170">
        </el-table-column>

        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button
                size="mini"
                type="success"
                @click="orderDetail(scope.row)">详情
            </el-button>

            <el-button
                size="mini"
                type="info"
                @click="edit(scope.row)">修改
            </el-button>

          </template>
        </el-table-column>

      </el-table>

    </div>

    <el-dialog
        title="编辑库位信息"
        :visible.sync="nowEditStorageDialogVisible">

      <el-form ref="form" :model="nowEditStorage" label-width="80px">
        <el-form-item label="名称" :prop="'name'" :rules="{ required: true,trigger: 'blur' }"
                      :show-message="false">
          <el-input type="text" v-model="nowEditStorage.name" size="mini"></el-input>
        </el-form-item>

        <el-form-item label="备注" :prop="'comment'">
          <el-input type="text" v-model="nowEditStorage.comment" size="mini"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
                              <el-button @click="nowEditStorageDialogVisible = false">取 消</el-button>
                              <el-button type="primary" @click="editStorage">提 交</el-button>
                            </span>
    </el-dialog>


    <el-dialog
        title="库位信息"
        :visible.sync="addReceiptDialogVisible">

      <el-form ref="form1" :model="newReceiptInfo" label-width="80px">

        <el-form-item label="名称" :prop="'name'" :rules="{ required: true,trigger: 'blur' }"
                      :show-message="false">
          <el-input type="text" v-model="newReceiptInfo.name" size="mini"></el-input>
        </el-form-item>

        <!--        <el-form-item label="库管门卫" :prop="'gateEmpId'" :rules="{ required: true,trigger: 'blur' }"-->
        <!--                      :show-message="false">-->
        <!--          <el-select clearable v-model="newReceiptInfo.gateEmpId" size="mini">-->
        <!--            <el-option-->
        <!--                v-for="id in $store.state.roleIdToEmpIdsCorr[10]"-->
        <!--                :label="$store.state.employeeNameCorr[id]"-->
        <!--                :value="id"-->
        <!--                :key="id">-->
        <!--            </el-option>-->
        <!--          </el-select>-->
        <!--        </el-form-item>-->

        <el-form-item label="备注" :prop="'comment'">
          <el-input type="text" v-model="newReceiptInfo.comment" size="mini"></el-input>
        </el-form-item>

      </el-form>


      <span slot="footer" class="dialog-footer">
                              <el-button @click="dialogVisible = false">取 消</el-button>
                              <el-button type="primary" @click="addReceiptInfo">添 加</el-button>
                            </span>
    </el-dialog>


  </div>
</template>

<script>
import {addStorageLocation, getStorageLocation, modifyStorageLocation} from "../../../api/storageLocationApi";
import {initStorageLocation} from "../../../utils/initialStoreInformation";


export default {
  name: "SysStorageLocationShow",
  data() {
    return {
      dialogVisible: false,
      nowEditStorage: {},
      allOrderInfo: [],
      addReceiptDialogVisible: false,
      newReceiptInfo: {},
      nowEditStorageDialogVisible: false
    }
  },
  mounted() {
    this.refresh();
  },
  methods: {
    refresh(type) {
      this.initAllOrderInfo()
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initAllOrderInfo() {
      getStorageLocation().then(resp => {
        if (resp.data.obj) {
          this.allOrderInfo = resp.data.obj;
        }
      })
    },
    orderDetail(row) {
      console.log("id" + row.id)
      this.$emit('func', 1, row.id)
    },
    addReceiptInfo() {
      this.$refs['form1'].validate((valid) => {
        if (valid) {
          this.$confirm('是否确定要添加', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            addStorageLocation(this.newReceiptInfo).then(resp => {
              if (resp.data.code === 200) {
                this.$message.success("创建成功");
                this.initAllOrderInfo();
                this.addReceiptDialogVisible = false;
                initStorageLocation(this.$store);
              }
            })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消添加'
            });
          });
        } else {
          this.$message.error("数据有错误");
          return false;
        }
      });
    },
    editStorage() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          modifyStorageLocation(this.nowEditStorage).then(resp => {
            if (resp.data.code === 200) {
              this.$message.success("修改成功")
              this.nowEditStorageDialogVisible = false;
              this.nowEditStorage = {}
              this.initAllOrderInfo()
              initStorageLocation(this.$store);
            }
          })
        } else {
          this.$message.error("数据有错误");
          return false;
        }
      });

    },
    edit(row) {
      this.nowEditStorage = JSON.parse(JSON.stringify(row))
      this.nowEditStorageDialogVisible = true;
    },
  }

}
</script>

<style scoped>

</style>
