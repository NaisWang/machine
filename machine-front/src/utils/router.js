import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'

let router = new VueRouter({
  routes: [
    {path: '/', hidden: true, redirect: '/login'},
    {path: '/login', hidden: true, component: Login},
    {path: '/home', hidden: true, component: Home},
  ]
})

export default router
