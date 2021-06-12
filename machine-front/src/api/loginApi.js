import myAxios from "../utils/myAxios";

/**
 * ApiOperation(value = "登陆之后返回token")
 */
export function login(data) {
  return myAxios({
    url: '/login',
    method: 'post',
    data
  })
}

