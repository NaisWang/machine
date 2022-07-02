package com.example.server.controller;


import com.example.server.pojo.TPaijiColor;
import com.example.server.pojo.TPaijiMemory;
import com.example.server.service.impl.TPaijiMemoryServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2022-07-01
 */
@RestController
@RequestMapping("/paiji/memory")
public class TPaijiMemoryController {

  @Autowired
  private TPaijiMemoryServiceImpl paijiMemoryService;

  @ApiOperation("获取内存对照表")
  @GetMapping("/")
  public RespBean getMemoryContrast() {
    return RespBean.success("获取成功", paijiMemoryService.list(null));
  }

  @ApiOperation("更新内存对照表")
  @PostMapping("/")
  @Transactional
  public RespBean updateMemoryContrast(@RequestBody List<TPaijiMemory> paijiMemoryList) {
    paijiMemoryService.truncate();
    try {
      if (paijiMemoryService.saveBatch(paijiMemoryList)) {
        return RespBean.success("更新成功");
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("更新失败");
    }
    return RespBean.error("更新失败");
  }

  @ApiOperation("删除内存对照表某数据")
  @DeleteMapping("/")
  public RespBean deleteMemoryContrast(Integer id) {
    if (paijiMemoryService.removeById(id)) {
      return RespBean.success("删除成功");
    }
    return RespBean.error("删除失败");
  }
}
