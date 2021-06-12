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

          <el-tabs v-model="editableTabsValue" type="card" closable @tab-click='chooseTab' @tab-remove="removeTab">
            <el-tab-pane
                v-for="(item, index) in editableTabs"
                :key="item.name"
                :label="item.title"
                :name="item.name"
            >
            </el-tab-pane>
          </el-tabs>

          <div class="homeWelcome" v-if="this.$route.path === '/home'">欢迎来到网上办公系统</div>

          <keep-alive :include="includeList">
            <router-view class="homeRouterView"></router-view>
          </keep-alive>

        </el-main>


      </el-container>
    </el-container>

  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      user: JSON.parse(window.sessionStorage.getItem("user")),
      editableTabsValue: null,
      editableTabs: [],
      includeList: [],
    };
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
      let tabs = this.editableTabs;
      let activeName = this.editableTabsValue;
      let componentName;

      if (activeName === targetName) {  // 删除的是当前选中的页签
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            // 关闭当前页签后，如果有下一个页签则显示下一个，否则显示前一个
            let nextTab = tabs[index + 1] || tabs[index - 1];
            if (nextTab) {
              activeName = nextTab.name;
              this.$router.push(nextTab.path)
            } else {
              this.$router.push("/home")
            }
          }
        });
      }
      this.editableTabsValue = activeName;
      this.editableTabs = tabs.filter(tab => tab.name !== targetName)
      this.includeList = this.includeList.filter(item => item !== targetName);
    },
    addTab(item) {
      // 当前新增的页签是否存在页签数组中的判断
      let hasSame = this.editableTabs.filter(item1 => item1.name === item.name)
      if (!hasSame.length) {
        this.editableTabs.push(item);
        this.includeList.push(item.name)
        this.editableTabsValue = item.name;
        this.$router.push(item.path)
      }
      this.editableTabsValue = item.name;
      this.$router.push(item.path)
    },
    chooseTab(tab) {
      this.$router.push(this.editableTabs[tab.index].path)
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
</style>
