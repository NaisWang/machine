import myAxios from "../utils/myAxios";

const BASE_URL = '/individual/all';

export function getIndividualInfo() {
  return myAxios({
    url: BASE_URL + "/",
    method: 'get'
  })
}