package com.example.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.pojo.DeliverMachine;
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
 * @since 2021-07-22
 */
public interface DeliverMachineMapper extends BaseMapper<DeliverMachine> {


	/**
	 * 分页获取转交单中的详情
	 */
	IPage<DeliverMachine> getDeliverMachine(Page<DeliverMachine> page, @Param("deliverMachine") DeliverMachine deliverMachine, @Param("localDateScope") LocalDate[] localDateScope);
}
