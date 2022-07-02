package com.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.server.pojo.TPaijiColor;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2022-07-01
 */
public interface TPaijiColorMapper extends BaseMapper<TPaijiColor> {

  @Update("delete from t_paiji_color")
  void truncate();

}
