import axios from 'axios'
import {Message} from 'element-ui'

import global from './global.js'

const service = axios.create({
  baseURL: global.BASE_URL
})

service.interceptors.request.use(
    config => {
      if (window.sessionStorage.getItem("tokenStr")) {
        config.headers['Authorization'] = window.sessionStorage.getItem('tokenStr');
      }
      return config;
    },
    error => {
      console.log(error);
    }
)

service.interceptors.response.use(
    success => {
      if (success.status === 200) {
        if (success.data.code === 403 || success.data.code === 500) {
          Message.error({
            message: success.data.message
          })
          return Promise.reject(success);
        }
        return Promise.resolve(success);
      }
      return Promise.reject(success)
    },
    error => {
      console.log(error);
      if (error.response.status === 404) {
        Message.error({
          message: "网络请求不存在"
        })
      }
    }
)

export default service;