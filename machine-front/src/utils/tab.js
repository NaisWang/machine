import store from "./store";

export function removeTab(targetName, router, store) {  // 关闭当前页签，并将缓存数组中缓存当前页签的项去除
  console.log("fffaa")
  let tabs = store.state.editableTabs;
  let activeName = store.state.editableTabsValue
  let componentName;

  if (activeName === targetName) {  // 删除的是当前选中的页签
    tabs.forEach((tab, index) => {
      if (tab.name === targetName) {
        // 关闭当前页签后，如果有下一个页签则显示下一个，否则显示前一个
        let nextTab = tabs[index + 1] || tabs[index - 1];
        if (nextTab) {
          activeName = nextTab.name;
          router.push(nextTab.path)
        } else {
          router.push("/home")
        }
      }
    });
  }
  store.commit('initEditableTabsValue', activeName);
  store.commit('initEditableTabs', tabs.filter(tab1 => tab1.name !== targetName))
  store.commit('initIncludeList', store.state.includeList.filter(item => item !== targetName))
}

export function removeTabUrlQuery(tabName, store) {
  store.state.editableTabs.forEach(item => {
    if (item.name === tabName) {
      item.path = item.substring(0, item.indexOf("?"))
    }
  })
}

export function addTab(item, router, store) {
  // 当前新增的页签是否存在页签数组中的判断
  let hasSame = store.state.editableTabs.filter(item1 => item1.name === item.name)
  if (!hasSame.length) {
    store.commit('editableTabsPush', item)
    store.commit('includeListPush', item.name)
    store.commit('initEditableTabsValue', item.name)
    router.push(item.path)
  }
  store.commit('initEditableTabsValue', item.name)
  router.push(item.path)
}

export function chooseTab(tab, router, route, store) {
  console.log("all")
  console.log(router)
  console.log(tab)
  console.log(route)
  if (tab.name !== route.name) {
    console.log("")
    router.push(store.state.editableTabs[tab.index].path)
  }
}