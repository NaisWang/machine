package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.MachineRecall;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-30
 */
public interface MachineRecallMapper extends BaseMapper<MachineRecall> {

	/**
	 * 获取机器召回信息
	 */
	IPage<MachineRecall> getMachineRecall(Page<MachineRecall> page, @Param("machineRecall") MachineRecall machineRecall, @Param("bidDateScope") LocalDate[] bidDateScope);
}
