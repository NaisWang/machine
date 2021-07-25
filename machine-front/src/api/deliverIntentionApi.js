import myAxios from "../utils/myAxios";

let BASE_URL = '/deliver/intention'

export function getDeliverIntention() {
  return myAxios({
    url: BASE_URL + "/",
    method: 'get'
  })
}