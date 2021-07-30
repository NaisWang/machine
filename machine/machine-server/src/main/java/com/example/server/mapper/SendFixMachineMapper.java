package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.SendFixMachine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author whz
 * @since 2021-07-25
 */
public interface SendFixMachineMapper extends BaseMapper<SendFixMachine> {

	/**
	 * 获取维修单中的机器
	 */
	IPage<SendFixMachine> getSendFixMachine(Page<SendFixMachine> page, @Param("sendFixMachine") SendFixMachine sendFixMachine, @Param("localDateScope") LocalDate[] localDateScope);
}
