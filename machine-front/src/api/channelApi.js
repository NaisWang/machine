import myAxios from "../utils/myAxios";
import qs from 'qs'

const BASE_URL = '/system/channel'

//获取渠道以及金钱
export function getChannelAndMoney(currentPage, size, channel) {
  let test = Object.assign(channel, {currentPage: currentPage, size: size})
  return myAxios({
    url: BASE_URL + '/',
    method: 'get',
    params: test,
    paramsSerializer: function (params) {
      return qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

//添加渠道
export function addChannel(channel) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'post',
    data: channel
  })
}

//修改渠道
export function modifyChannel(channel) {
  return myAxios({
    url: BASE_URL + '/',
    method: 'put',
    data: channel
  })
}


//删除渠道
export function deleteChannel(channelId) {
  return myAxios({
    url: BASE_URL + '/?channelId=' + channelId,
    method: 'delete',
  })
}
