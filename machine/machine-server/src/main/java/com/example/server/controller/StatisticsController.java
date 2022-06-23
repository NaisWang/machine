package com.example.server.controller;

import com.example.server.pojo.Employee;
import com.example.server.pojo.MachineTrace;
import com.example.server.pojo.Statistics;
import com.example.server.service.impl.MachineTraceServiceImpl;
import com.example.server.service.impl.OperateTraceServiceImpl;
import com.example.server.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

/**
 * @author : whz
 */
@Component
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

	@Autowired
	private OperateTraceServiceImpl operateTraceService;
	@Autowired
	private MachineTraceServiceImpl machineTraceService;

	@GetMapping("/")
	public RespBean getStatistics(Integer id) {
		//当天机器
		List<Statistics> dayStatistics = operateTraceService.getStatistics(id, 1);
		//一周机器
		List<Statistics> weekStatistics = operateTraceService.getStatistics(id, 2);
		//一月机器
		List<Statistics> monthStatistics = operateTraceService.getStatistics(id, 3);
		//全部机器
		List<Statistics> allStatistics = operateTraceService.getStatistics(id, 4);
		HashMap<Integer, List<Statistics>> ans = new HashMap<>();
		ans.put(1, dayStatistics);
		ans.put(2, weekStatistics);
		ans.put(3, monthStatistics);
		ans.put(4, allStatistics);
		return RespBean.success("获取成功", ans);
	}

	@GetMapping("/all")
	public RespBean getAllStatistics(Integer[] empIds, Integer[] statusIds, Integer[] dateScope) {
		List<Statistics> statistics = machineTraceService.getAllStatistics(empIds, statusIds, dateScope);
		return RespBean.success("获取成功", statistics);
	}

	//获取本人的记录
	@GetMapping("/one")
	public RespBean getOneStatistics(Authentication authentication) {
		Integer empId = ((Employee) authentication.getPrincipal()).getId();
		List<Statistics> statistics = machineTraceService.getOneStatistics(empId);
		return RespBean.success("获取成功", statistics);
	}

}
