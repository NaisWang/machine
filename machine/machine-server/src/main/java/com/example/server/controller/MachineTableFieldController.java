package com.example.server.controller;


import com.example.server.pojo.MachineTableField;
import com.example.server.service.impl.MachineTableFieldServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-15
 */
@RestController
@RequestMapping("/machine/table/field")
public class MachineTableFieldController {

	@Autowired
	private MachineTableFieldServiceImpl machineTableFieldService;

	@ApiOperation("获取机器详细界面表格所要展示的数据")
	@GetMapping("/")
	public RespBean getMachineTableField() {
		List<MachineTableField> tableFields = machineTableFieldService.list(null);
		HashMap<String, List<MachineTableField>> map = new HashMap<>();
		for (MachineTableField tableField : tableFields) {
			if (!map.containsKey(tableField.getType())) {
				List<MachineTableField> list = new ArrayList<>();
				list.add(tableField);
				map.put(tableField.getType(), list);
			} else {
				map.get(tableField.getType()).add(tableField);
			}
		}
		HashMap<String, HashMap<String, MachineTableField>> ansMap = new HashMap<>();

		for (String key : map.keySet()) {
			HashMap<String, MachineTableField> temp = new HashMap<>();
			for (MachineTableField machineTableField : map.get(key)) {
				temp.put(machineTableField.getNameEn(), machineTableField);
			}
			ansMap.put(key, temp);
		}

		return RespBean.success("获取成功", ansMap);
	}
}
