import {getAllMenuWithChildren} from "../api/menuApi";

function resolveMenu(data) {
  if (data == null) {
    return null;
  }
  let res = [];
  data.forEach(item => {
    let menus = {};
    menus.path = item.path;
    menus.component = getComponent(item.component);
    menus.name = item.name;
    menus.iconcls = item.iconcls;
    menus.children = resolveMenu(item.children);
    res.push(menus);
  })
  return res;
}

function getComponent(component) {
  if (component.startsWith("Home")) {
    return () => import("../views/" + component + ".vue")
  }
  if (component.startsWith("Client")) {
    return () => import("../views/Client/" + component + ".vue")
  }
  if (component.startsWith("Mach")) {
    return () => import("../views/Machine/" + component + ".vue")
  }
  if (component.startsWith("Per")) {
    return () => import("../views/Personnel/" + component + ".vue")
  }
  if (component.startsWith("Sta")) {
    return () => import("../views/Statistics/" + component + ".vue")
  }
  if (component.startsWith("Sys")) {
    return () => import("../views/System/" + component + ".vue")
  }
}

export function receiveMenu(router, store) {
  return getAllMenuWithChildren().then(resp => {
    let data = resp.data.obj;
    console.log(data)
    let rou = resolveMenu(data);
    console.log(rou)
    store.commit("initMenuRoutes", rou);
    router.addRoutes(rou);
  })
}