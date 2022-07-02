package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.pojo.TPaijiColor;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2022-07-01
 */
public interface ITPaijiColorService extends IService<TPaijiColor> {

  void truncate();
}
