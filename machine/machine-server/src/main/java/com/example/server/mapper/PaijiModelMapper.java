package com.example.server.mapper;

import com.example.server.pojo.PaijiModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-06-23
 */
public interface PaijiModelMapper extends BaseMapper<PaijiModel> {

	@Update("delete from t_paiji_model")
	void truncate();

}
