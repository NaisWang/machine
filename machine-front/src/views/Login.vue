<template>
  <div>
    <el-form ref="form" :model="form" class="login-box" :rules="rules">
      <h3 class="login-title">欢迎登陆</h3>
      <el-form-item label="账号" prop="username">
        <el-input
            type="text"
            placeholder="请输入用户名"
            v-model="form.username"
        ></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
            type="password"
            placeholder="请输入密码"
            v-model="form.password"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button
            style="width: 100%"
            type="primary"
            @click="submitForm()">登陆
        </el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import * as loginApi from '../api/loginApi'
import {receiveMenu} from "../utils/menu";
import {getIndividualInfo} from "../api/IndividualApi";
import initMachineCorr from "../utils/machineCorr";


export default {
  name: "Login",
  data() {
    return {
      form: {
        username: "",
        password: "",
      },
      rules: {
        username: [
          {required: true, message: "请输入用户名", trigger: "blur"},
          {min: 2, max: 8, message: "长度在 2 到 6 个字符", trigger: "blur"},
        ],
        password: [{required: true, message: "请输入密码", trigger: "blur"}],
      },
    }
  },
  methods: {
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          loginApi.login(this.form).then(resp => {
            let data = resp.data;
            const tokenStr = data.obj.tokenHead + " " + data.obj.token;
            window.sessionStorage.setItem("tokenStr", tokenStr);
            receiveMenu(this.$router, this.$store).then(() => {
              getIndividualInfo().then(resp => {
                window.sessionStorage.setItem("user", JSON.stringify(resp.data.obj));
                this.$store.commit('initUserId', resp.data.obj['id'])
                initMachineCorr(this.$store)
                this.$message.success(data.message);
                this.$router.push('/home');
              })
            });
          })
        } else {
          this.$message.error("输入有错");
        }
      })
    }
  }
}
</script>
<style scoped>
.login-box {
  width: 350px;
  margin: 120px auto;
  border: 1px solid #decfe6;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 0 30px #decfe6;
}

.login-title {
  text-align: center;
}
</style>
