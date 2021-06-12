import Vue from 'vue'
import VueRouter from 'vue-router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'font-awesome/css/font-awesome.css'

import App from "./App.vue"

import router from './utils/router'
import store from './utils/store'
import {receiveMenu} from "./utils/menu";
import {getIndividualInfo} from "./api/IndividualApi";

Vue.use(VueRouter)
Vue.use(ElementUI, {size: 'small'})

router.beforeEach((to, from, next) => {
  if (to.fullPath.startsWith("/login")) {
    next();
  } else if (window.sessionStorage.getItem('tokenStr')) {
    receiveMenu(router, store).then(() => {
      if (!window.sessionStorage.getItem("user")) {
        //判断用户是否存在
        getIndividualInfo().then(resp => {
          let data = resp.data.obj;
          if (data) {
            //存入用户信息
            window.sessionStorage.setItem("user", JSON.stringify(data))
            next();
          }
        })
      }
    });
    next();
  } else {
    next("/login/?redirect=" + to.path);
  }
})

let vm = new Vue({
  el: "#app",
  render: c => c(App),
  router,
  store
})