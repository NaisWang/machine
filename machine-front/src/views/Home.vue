<template>
  <div>
    <el-container>

      <el-header class="homeHeader">
        <div class="title">网上办公系统</div>
        <el-dropdown class="userInfo" @command="handleCommand">
          <span class="el-dropdown-link">
            {{ user.name }}<i><img :src="user.userFace"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="userinfo">个人中心</el-dropdown-item>
            <el-dropdown-item command="setting">设置</el-dropdown-item>
            <el-dropdown-item command="logout">注销登陆</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-header>

      <el-container>
        <el-aside width="150px">
          <el-menu @select="menuClick">
            <el-submenu
                :index="index+''"
                v-for="(item, index) in menuRoutes"
                :key="index"
                v-if="!item.hidden">
              <template slot="title"><i :class="item.iconcls"></i>{{ item.name }}
              </template>
              <el-menu-item
                  :index="JSON.stringify(children)"
                  v-for="(children, index1) in item.children"
                  :key="index1">
                {{ children.name }}
              </el-menu-item>
            </el-submenu>
          </el-menu>
        </el-aside>

        <el-main>

          <el-tabs v-model="$store.state.editableTabsValue" type="card" closable @tab-click='chooseTab'
                   @tab-remove="removeTab">
            <el-tab-pane
                v-for="(item, index) in $store.state.editableTabs"
                :key="item.name"
                :label="item.title"
                :name="item.name"
            >
            </el-tab-pane>
          </el-tabs>

          <div class="homeWelcome" v-if="this.$route.path === '/home'">欢迎来到网上办公系统</div>

          <keep-alive :include="$store.state.includeList">
            <router-view class="homeRouterView" @func="addTab"></router-view>
          </keep-alive>

        </el-main>


      </el-container>
    </el-container>

  </div>
</template>

<script>
import * as tab from "../utils/tab"

export default {
  name: "Home",
  data() {
    return {
      user: JSON.parse(window.sessionStorage.getItem("user")),
      isCollapse: true
    };
  },
  mounted() {
    let tabItem = {
      title: this.$route.name,
      name: this.$route.name,
      path: this.$route.path,
    }
    this.addTab(tabItem);
  },
  methods: {
    menuClick(index) {
      index = JSON.parse(index);
      let tabItem = {
        title: index.name,
        name: index.name,
        path: index.path,
      }
      this.addTab(tabItem);
    },
    handleCommand(command) {
      if (command === "logout") {
        this.$confirm('此操作将退出登陆, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          //清空用户信息
          window.sessionStorage.removeItem("tokenStr");
          window.sessionStorage.removeItem("user");
          this.$store.commit('initRoutes', []);
          //跳转到登录页
          this.$router.replace("/");
          this.$message({
            type: 'success',
            message: '退出成功!'
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消退出'
          })
        });
      }
    },

    removeTab(targetName) {  // 关闭当前页签，并将缓存数组中缓存当前页签的项去除
      tab.removeTab(targetName, this.$router, this.$store)
    },
    addTab(item) {
      tab.addTab(item, this.$router, this.$store)
    },
    chooseTab(tab1) {
      tab.chooseTab(tab1, this.$router, this.$route, this.$store)
    },
  },
  computed: {
    menuRoutes() {
      return this.$store.state.menuRoutes;
    },
  }
};
</script>

<style scoped lang="scss">
.homeHeader {
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;

  .title {
    font-size: 30px;
    color: white;
  }

  .userInfo {
    cursor: pointer;
  }
}

.el-dropdown-link {
  img {
    width: 48px;
    height: 48px;
    border-radius: 24px;
    margin-left: 8px;
  }
}

.homeWelcome {
  text-align: center;
  font-size: 30px;
  color: #409eff;
  padding-top: 50px;
}

.homeRouterView {
  margin-top: 10px;
}

.el-aside li {
  min-width: 120px !important;
}
</style>
