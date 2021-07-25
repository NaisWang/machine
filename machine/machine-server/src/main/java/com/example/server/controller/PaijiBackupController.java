package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.Paiji;
import com.example.server.pojo.PaijiBackup;
import com.example.server.service.impl.PaijiBackupServiceImpl;
import com.example.server.utils.RequestUtil;
import com.example.server.utils.RespBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-07-06
 */
@RestController
public class PaijiBackupController {

	@Autowired
	private PaijiBackupServiceImpl paijiBackupService;

	@ApiOperation("获取paiji备份对照表数据")
	@GetMapping("/machine/price/fieldBackup")
	public RespBean getPaiji(PaijiBackup paijiBackup) {
		List<PaijiBackup> paijiList = paijiBackupService.listOrder(paijiBackup);
		if (paijiList != null) {
			return RespBean.success("获取成功", paijiList);
		}
		return RespBean.error("获取失败");
	}

	public void updatePaijiField(List<PaijiBackup> paijiList) {
		HashMap<Integer, List<PaijiBackup>> paijiMap = new HashMap<>();
		for (PaijiBackup paijiBackup : paijiList) {
			Integer status = judgeFieldStatus(paijiBackup);
			if (paijiMap.containsKey(status)) {
				List<PaijiBackup> paijiList1 = paijiMap.get(status);
				paijiBackup.setStatus(status);
				paijiList1.add(paijiBackup);
			} else {
				paijiBackup.setStatus(status);
				List<PaijiBackup> paijiList1 = new LinkedList<>();
				paijiList1.add(paijiBackup);
				paijiMap.put(status, paijiList1);
			}
		}
		if (paijiMap.get(0) != null) {
			paijiBackupService.updateBatchById(paijiMap.get(0));
		}
		if (paijiMap.get(1) != null) {
			paijiBackupService.updateBatchById(paijiMap.get(1));
		}
		if (paijiMap.get(2) != null) {
			paijiBackupService.saveBatch(paijiMap.get(2));
		}
	}

	private Integer judgeFieldStatus(PaijiBackup paijiBackup) {
		List<PaijiBackup> paijiList = paijiBackupService.list(new QueryWrapper<PaijiBackup>().eq("id", paijiBackup.getId()));
		System.out.println(paijiList);
		if (paijiList.size() == 0) {
			System.out.println("添加了该字段");
			return 2;
		}
		List<PaijiBackup> paijiList1 = paijiBackupService.list(new QueryWrapper<PaijiBackup>().eq("id", paijiBackup.getId()).eq("value", paijiBackup.getValue()));
		System.out.println(paijiList1);
		if (paijiList1.size() == 0) {
			System.out.println("修改了该字段");
			return 1;
		}
		return 0;
	}
}
