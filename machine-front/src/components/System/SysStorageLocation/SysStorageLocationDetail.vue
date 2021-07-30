<template>
  <div>
    <div>
      <el-button type="primary" icon="el-icon-plus" @click="addReceiptDialogVisible = true">添加子库位</el-button>
      <el-button type="primary" icon="el-icon-refresh" @click="refresh(1)">刷 新</el-button>
    </div>

    <el-table
        :data="allSubStorage"
        style="width: 100%">
      <el-table-column
          type="selection"
          width="55">
      </el-table-column>

      <el-table-column
          prop="name"
          label="库位名"
          width="170">
      </el-table-column>

      <el-table-column
          prop="gateEmpId"
          label="库管门卫"
          width="170">
        <template #default="scope">
          <el-tag v-for="item in  scope.row.gateEmpId === null ? [] : scope.row.gateEmpId.split(',')"
                  :key="item">{{
              $store.state.employeeNameCorr[item]
            }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
          prop="machineSum"
          label="机器数"
          width="170">
      </el-table-column>

      <el-table-column
          prop="comment"
          label="备注"
          width="170">
      </el-table-column>

      <el-table-column label="操作" fixed="right">
        <template #default="scope">
          <el-button
              size="mini"
              type="info"
              @click="edit(scope.row)">修改
          </el-button>

        </template>
      </el-table-column>
    </el-table>

    <el-dialog
        title="编辑库位信息"
        :visible.sync="nowEditStorageDialogVisible">

      <el-form ref="form" :model="nowEditStorage" label-width="80px">
        <el-form-item label="名称" :prop="'name'" :rules="{ required: true,trigger: 'blur' }"
                      :show-message="false">
          <el-input type="text" v-model="nowEditStorage.name" size="mini"></el-input>
        </el-form-item>

        <el-form-item label="库管门卫" :prop="'gateEmpId'">
          <el-select clearable v-model="nowEditStorage.editGateEmpId" multiple size="mini">
            <el-option
                v-for="id in $store.state.roleIdToEmpIdsCorr[10]"
                :label="$store.state.employeeNameCorr[id]"
                :value="id + ''"
                :key="id">
            </el-option>
          </el-select>
        </el-form-item>

        <!--        <el-form-item label="操作人员">-->
        <!--          <el-select multiple v-model="nowEditStorage.editEmpIds" size="mini">-->
        <!--            <el-option-->
        <!--                v-for="id in Object.keys($store.state.employeeNameCorr).map(Number)"-->
        <!--                :label="$store.state.employeeNameCorr[id]"-->
        <!--                :value="id + ''"-->
        <!--                :key="id+''">-->
        <!--            </el-option>-->
        <!--          </el-select>-->
        <!--        </el-form-item>-->

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

        <el-form-item label="库管门卫" :prop="'gateEmpId'">
          <el-select clearable v-model="newReceiptInfo.gateEmpId" multiple size="mini">
            <el-option
                v-for="id in $store.state.roleIdToEmpIdsCorr[10]"
                :label="$store.state.employeeNameCorr[id]"
                :value="id"
                :key="id">
            </el-option>
          </el-select>
        </el-form-item>


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
import {getAllEmp} from "../../../api/employeeApi";
import {
  addSubStorageLocation,
  getSubStorageLocation,
  modifySubStorageLocation
} from "../../../api/subStorageLocationApi";

export default {
  name: "SysStorageLocationDetail",
  data() {
    return {
      nowEditStorage: {},
      allSubStorage: [],
      addReceiptDialogVisible: false,
      newReceiptInfo: {},
      nowEditStorageDialogVisible: false
    }
  },
  props: ['storageLocationId'],
  mounted() {
    this.refresh();
    console.log(this.$store.state.roleIdToEmpIdsCorr)
  },
  methods: {
    refresh(type) {
      this.initSubStorageLocation();
      if (type === 1) {
        this.$message.success("刷新成功");
      }
    },
    initSubStorageLocation() {
      getSubStorageLocation(this.storageLocationId).then(resp => {
        if (resp.data.obj) {
          this.allSubStorage = resp.data.obj
        }
      })
    },
    addReceiptInfo() {
      this.$refs['form1'].validate((valid) => {
        if (valid) {
          this.$confirm('是否确定要添加', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.newReceiptInfo.parentStorageLocationId = this.storageLocationId;
            addSubStorageLocation(this.newReceiptInfo).then(resp => {
              if (resp.data.code === 200) {
                this.$message.success("创建成功");
                this.initSubStorageLocation();
                this.addReceiptDialogVisible = false;
                //initStorageLocation(this.$store);
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
          this.$confirm('是否确定要提交', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            modifySubStorageLocation(this.nowEditStorage).then(resp => {
              if (resp.data.code === 200) {
                this.$message.success("修改成功")
                this.nowEditStorageDialogVisible = false;
                this.nowEditStorage = {}
                this.initSubStorageLocation();


                //initStorageLocation(this.$store);
              }
            })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消提交'
            });
          });
        } else {
          this.$message.error("数据有错误");
          return false;
        }
      });

    },
    edit(row) {
      row.editGateEmpId = []
      this.nowEditStorage = JSON.parse(JSON.stringify(row))
      this.nowEditStorage.editGateEmpId = this.nowEditStorage.gateEmpId === null ? [] : this.nowEditStorage.gateEmpId.split(',')
      this.nowEditStorageDialogVisible = true;
    },
  }
}
</script>

<style>
.el-dialog {
  margin-top: 0 !important;
}
</style>
