package com.example.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.server.pojo.TPaijiMemory;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whz
 * @since 2022-07-01
 */
public interface ITPaijiMemoryService extends IService<TPaijiMemory> {

  void truncate();
}
