import myAxios from "../utils/myAxios";


export function getPaiji() {
  return myAxios({
    url: "/machine/price/field",
    method: 'get',
  })
}

export function getPaijiBackup() {
  return myAxios({
    url: "/machine/price/fieldBackup",
    method: 'get',
  })
}

export function getModelContrast() {
  return myAxios({
    url: "/machine/price/model-contrast/",
    method: 'get'
  })
}

export function updatePaijiField() {
  return myAxios({
    url: '/system/paiji/updateField',
    method: 'get'
  })
}

export function updatePaijiTable(paiji) {
  return myAxios({
    url: '/system/paiji/updateTable',
    method: 'put',
    data: paiji
  })
}

export function updateModelContrast(modelsContrast) {
  return myAxios({
    url: "/machine/price/model-contrast/",
    method: 'post',
    data: modelsContrast
  })
}

export function getModelGuaranteeBattery() {
  return myAxios({
    url: "/machine/price/model-guarantee-battery/",
    method: "get"
  })
}

export function getGuarantee() {
  return myAxios({
    url: "/machine/price/guarantee/",
    method: 'get'
  })
}

export function getBattery() {
  return myAxios({
    url: "/machine/price/battery/",
    method: 'get'
  })
}

export function updateModelGuaranteeBattery(modelGuaranteeBattery) {
  return myAxios({
    url: "/machine/price/model-guarantee-battery/",
    method: "post",
    data: modelGuaranteeBattery
  })
}

export function getXdCombinationPriceField() {
  return myAxios({
    url: "http://120.79.195.87:5000/xd_combination_price_field",
    method: 'get'
  })
}

export function get_paiji_combination_price_field() {
  return myAxios({
    url: "http://120.79.195.87:5000/paiji_combination_price_field",
    method: 'get'
  })
}

export function getPaijiField() {
  return myAxios({
    url: "http://120.79.195.87:5000/paiji-field",
    method: 'get'
  })
}

export function getPriceCombination() {
  return myAxios({
    url: "/machine/price/price-combination/",
    method: 'get'
  })
}

export function updatePriceCombination(priceCombination) {
  return myAxios({
    url: "/machine/price/price-combination/",
    method: 'post',
    data: priceCombination
  })
}

export function getPaijiUser() {
  return myAxios({
    url: "/machine/price/paiji-user/",
    method: "get"
  })
}

export function updatePaijiUser(paijiUser) {
  return myAxios({
    url: "/machine/price/paiji-user/",
    method: 'post',
    data: paijiUser
  })
}

export function testLogin(username, password) {
  return myAxios({
    url: "http://120.79.195.87:5000/testLogin?username=" + username + "&password=" + password,
    method: 'get'
  })
}

export function stopSearchPrice() {
  return myAxios({
    url: 'http://120.79.195.87:5000/stop-search-price',
    method: 'get'
  })
}

