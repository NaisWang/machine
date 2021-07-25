package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.Paiji;
import com.example.server.pojo.PaijiBackup;
import com.example.server.service.impl.PaijiServiceImpl;
import com.example.server.utils.RequestUtil;
import com.example.server.utils.RespBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.apache.xmlbeans.impl.xb.xmlschema.impl.SpaceAttributeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-06-16
 */
@RestController
public class PaijiController {

	@Autowired
	private PaijiServiceImpl paijiService;
	@Autowired
	private PaijiBackupController paijiBackupController;

	@ApiOperation("获取paiji对照表数据")
	@GetMapping("/machine/price/field")
	public RespBean getPaiji(Paiji paiji) {
		List<Paiji> paijiList = paijiService.listOrder(paiji);
		if (paijiList != null) {
			return RespBean.success("获取成功", paijiList);
		}
		return RespBean.error("获取失败");
	}

	@ApiOperation("根据拍机堂中的某个字段，返回这个字段所属的类别的所有的字段以及其id")
	@GetMapping("/machine/price/getCategoryCorrByField")
	public RespBean getCategoryCorrByField(String field) {
		System.out.println("aaa" + field);
		List<Paiji> paijiList = paijiService.list(new QueryWrapper<Paiji>().like("value", field));
		System.out.println(paijiList);
		String categoryName = paijiList.get(0).getName();
		List<Paiji> paijiList1 = paijiService.list(new QueryWrapper<Paiji>().eq("name", categoryName));
		return RespBean.success("查询成功", paijiList1);
	}

	@ApiOperation("获取保修对应表")
	@GetMapping("/machine/price/guarantee/")
	public RespBean getGuarantee() {
		Map<Integer, String> map = new HashMap<>();
		List<Paiji> paijiList = paijiService.list(new QueryWrapper<Paiji>().eq("name", "苹果保修期时长"));
		for (Paiji paiji : paijiList) {
			map.put(paiji.getId(), paiji.getValue());
		}
		if (paijiList != null) {
			return RespBean.success("获取成功", map);
		}
		return RespBean.error("获取失败");
	}

	@ApiOperation("获取电池对应表")
	@GetMapping("/machine/price/battery/")
	public RespBean getBattery() {
		Map<Integer, String> map = new HashMap<>();
		List<Paiji> paijiList = paijiService.list(new QueryWrapper<Paiji>().eq("name", "电池健康度"));
		for (Paiji paiji : paijiList) {
			map.put(paiji.getId(), paiji.getValue());
		}
		if (paijiList != null) {
			return RespBean.success("获取成功", map);
		}
		return RespBean.error("获取失败");
	}

	@ApiOperation("更新拍机堂字段")
	@GetMapping("/system/paiji/updateField")
	@Transactional
	public RespBean updatePaijiField() {
		try {
			List<Integer> ids = new LinkedList<>();
			String result = RequestUtil.getRequest("http://127.0.0.1:5000/update");
			if (result != null) {
				System.out.println(result);
				ObjectMapper objectMapper = new ObjectMapper();
				List<Paiji> paijiList = objectMapper.readValue(result, new TypeReference<List<Paiji>>() {
				});
				if (paijiList.size() == 0) {
					return RespBean.error("更新失败, 请稍候在尝试");
				}
				List<PaijiBackup> paijiBackups = objectMapper.readValue(result, new TypeReference<List<PaijiBackup>>() {
				});
				HashMap<Integer, List<Paiji>> paijiMap = new HashMap<>();
				for (Paiji paiji : paijiList) {
					Integer status = judgeFieldStatus(paiji);
					ids.add(paiji.getId());
					if (paijiMap.containsKey(status)) {
						List<Paiji> paijiList1 = paijiMap.get(status);
						paiji.setStatus(status);
						paijiList1.add(paiji);
					} else {
						paiji.setStatus(status);
						List<Paiji> paijiList1 = new LinkedList<>();
						paijiList1.add(paiji);
						paijiMap.put(status, paijiList1);
					}
				}
				if (paijiMap.get(0) != null) {
					paijiService.updateBatchById(paijiMap.get(0));
				}
				if (paijiMap.get(1) != null) {
					paijiService.updateBatchById(paijiMap.get(1));
				}
				if (paijiMap.get(2) != null) {
					paijiService.saveBatch(paijiMap.get(2));
				}
				paijiBackupController.updatePaijiField(paijiBackups);
				paijiService.remove(new QueryWrapper<Paiji>().notIn("id", ids));
			}
			return RespBean.success("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("更新失败");
		}
	}

	/**
	 * 判断拍机堂字段与上一个版本的区别状态
	 * 0: 表示没有变化
	 * 1: 表示发生了变化
	 * 2: 增加了该字段
	 */
	private Integer judgeFieldStatus(Paiji paiji) {
		List<Paiji> paijiList = paijiService.list(new QueryWrapper<Paiji>().eq("id", paiji.getId()));
		System.out.println(paijiList);
		if (paijiList.size() == 0) {
			System.out.println("添加了该字段");
			return 2;
		}
		List<Paiji> paijiList1 = paijiService.list(new QueryWrapper<Paiji>().eq("id", paiji.getId()).eq("value", paiji.getValue()));
		System.out.println(paijiList1);
		if (paijiList1.size() == 0) {
			System.out.println("修改了该字段");
			return 1;
		}
		return 0;
	}

	@ApiOperation("更新对照表")
	@PutMapping("/system/paiji/updateTable")
	public RespBean updateTable(@RequestBody List<Paiji> paijiList) {
		System.out.println(paijiList);
		if (paijiService.updateBatchById(paijiList)) {
			return RespBean.success("更新成功");
		}
		return RespBean.error("更新失败");
	}

}
