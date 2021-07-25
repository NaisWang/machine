import myAxios from "../utils/myAxios";

let BASE_URL = '/machine/table/field'

//获取机器详细界面表格所要展示的数据
export function getMachineTableField() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}
