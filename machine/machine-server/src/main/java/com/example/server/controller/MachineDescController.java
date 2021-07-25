package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.Machine;
import com.example.server.pojo.MachineDesc;
import com.example.server.pojo.Paiji;
import com.example.server.service.ICategoryService;
import com.example.server.service.impl.MachineDescServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-16
 */
@RestController
@RequestMapping("/machine/desc")
public class MachineDescController {

	@Autowired
	private MachineDescServiceImpl machineDescService;

	@ApiOperation("获取机器描述")
	@GetMapping("/")
	public RespBean getMachineDesc() {
		HashMap<String, HashMap<String, HashMap<String, List<MachineDesc>>>> ans = new HashMap<>();

		String[] types = {"phone", "tablet", "watch"};

		for (String type : types) {
			List<MachineDesc> machineDescList = machineDescService.list(new QueryWrapper<MachineDesc>().eq(type, 0));
			HashMap<String, HashMap<String, List<MachineDesc>>> map = new HashMap<>();

			HashMap<String, List<MachineDesc>> functionInfos = new HashMap<>();
			HashMap<String, List<MachineDesc>> qualityInfos = new HashMap<>();

			for (MachineDesc machineDesc : machineDescList) {
				if (machineDesc.getParentValue().equals("functionInfos")) {
					if (!functionInfos.containsKey(machineDesc.getName())) {
						List<MachineDesc> temp = new ArrayList<>();
						temp.add(machineDesc);
						functionInfos.put(machineDesc.getName(), temp);
					} else {
						functionInfos.get(machineDesc.getName()).add(machineDesc);
					}
				} else if (machineDesc.getParentValue().equals("qualityInfos")) {
					if (!qualityInfos.containsKey(machineDesc.getName())) {
						List<MachineDesc> temp = new ArrayList<>();
						temp.add(machineDesc);
						qualityInfos.put(machineDesc.getName(), temp);
					} else {
						qualityInfos.get(machineDesc.getName()).add(machineDesc);
					}
				}
			}

			map.put("functionInfos", functionInfos);
			map.put("qualityInfos", qualityInfos);
			ans.put(type, map);
		}

		return RespBean.success("获取成功", ans);
	}

	@ApiOperation("获取机器描述表")
	@GetMapping("/getTable/")
	public RespBean getMachineDescTable() {
		List<MachineDesc> machineDescList = machineDescService.list(null);

		List<MachineDesc> functionInfos = new ArrayList<>();
		List<MachineDesc> qualityInfos = new ArrayList<>();

		for (MachineDesc machineDesc : machineDescList) {
			if (machineDesc.getParentValue().equals("functionInfos")) {
				functionInfos.add(machineDesc);
			} else if (machineDesc.getParentValue().equals("qualityInfos")) {
				qualityInfos.add(machineDesc);
			}
		}
		HashMap<String, List<MachineDesc>> ans = new HashMap<>();
		ans.put("functionInfos", functionInfos);
		ans.put("qualityInfos", qualityInfos);
		return RespBean.success("获取成功", ans);
	}

	@ApiOperation("更新机器描述表")
	@PutMapping("/")
	@Transactional
	public RespBean updateMachineDesc(@RequestBody MachineDesc[] machineDesc) {
		try {
			if (machineDescService.saveOrUpdateBatch(Arrays.asList(machineDesc))) {
				return RespBean.success("更新成功");
			}
			throw new RuntimeException("更新失败");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("更新失败");
		}
	}
}
