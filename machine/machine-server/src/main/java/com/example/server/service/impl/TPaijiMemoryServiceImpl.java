package com.example.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.mapper.TPaijiMemoryMapper;
import com.example.server.pojo.TPaijiMemory;
import com.example.server.service.ITPaijiMemoryService;
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
public class TPaijiMemoryServiceImpl extends ServiceImpl<TPaijiMemoryMapper, TPaijiMemory> implements ITPaijiMemoryService {

  @Autowired
  private TPaijiMemoryMapper paijiMemoryMapper;

  @Override
  public void truncate() {
    paijiMemoryMapper.truncate();
  }
}
