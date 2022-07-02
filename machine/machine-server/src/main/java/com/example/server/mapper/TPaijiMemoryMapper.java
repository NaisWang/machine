package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.pojo.TPaijiMemory;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2022-07-01
 */
public interface TPaijiMemoryMapper extends BaseMapper<TPaijiMemory> {

  @Update("delete from t_paiji_memory")
  void truncate();
}
