package com.example.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.mapper.PaijiModelMapper;
import com.example.server.mapper.TPaijiColorMapper;
import com.example.server.pojo.TPaijiColor;
import com.example.server.service.ITPaijiColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2022-07-01
 */
@Service
public class TPaijiColorServiceImpl extends ServiceImpl<TPaijiColorMapper, TPaijiColor> implements ITPaijiColorService {

  @Autowired
  private TPaijiColorMapper paijiColorMapper;

  /**
   * 清空表
   */
  @Override
  public void truncate() {
    paijiColorMapper.truncate();
  }

}
