package com.example.server.controller;


import com.example.server.pojo.PaijiModel;
import com.example.server.pojo.TPaijiColor;
import com.example.server.service.ITPaijiColorService;
import com.example.server.service.impl.TPaijiColorServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author whz
 * @since 2022-07-01
 */
@RestController
@RequestMapping("/paiji/color")
public class TPaijiColorController {

  @Autowired
  private TPaijiColorServiceImpl paijiColorService;

  @ApiOperation("获取机型颜色对照表")
  @GetMapping("/")
  public RespBean getColorContrast() {
    return RespBean.success("获取成功", paijiColorService.list(null));
  }

  @ApiOperation("更新颜色对照表")
  @PostMapping("/")
  @Transactional
  public RespBean updateColorContrast(@RequestBody List<TPaijiColor> paijiColorList) {
    paijiColorService.truncate();
    try {
      if (paijiColorService.saveBatch(paijiColorList)) {
        return RespBean.success("更新成功");
      }
    } catch (Exception e) {
      throw new RuntimeException("更新失败");
    }
    return RespBean.error("更新失败");
  }

  @ApiOperation("删除颜色对照表某数据")
  @DeleteMapping("/")
  public RespBean deleteColorContrast(Integer id) {
    if (paijiColorService.removeById(id)) {
      return RespBean.success("删除成功");
    }
    return RespBean.error("删除失败");
  }
}
