import myAxios from "../utils/myAxios";

const BASE_URL = "/home/menu"

export function getAllMenuWithChildren() {
  return myAxios({
    url: BASE_URL + '/',
    method: 'get'
  })
}