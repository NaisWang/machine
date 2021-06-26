package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.PaijiUser;
import com.example.server.service.impl.PaijiUserServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-06-20
 */
@RestController
@RequestMapping("/machine/price/paiji-user")
public class PaijiUserController {

	@Autowired
	private PaijiUserServiceImpl paijiUserService;

	@ApiOperation("获取拍机堂中查价格用户")
	@GetMapping("/")
	public RespBean getPaijiUser() {
		List<PaijiUser> paijiUsers = paijiUserService.list(null);
		if (paijiUsers != null) {
			return RespBean.success("获取成功", paijiUsers);
		}
		return RespBean.error("获取失败");
	}

	@ApiOperation("更新拍机堂中查价格用户")
	@PostMapping("/")
	@Transactional
	public RespBean updatePaijiUser(@RequestBody List<PaijiUser> paijiUsers) {
		try {
			paijiUserService.remove(new QueryWrapper<>());
			paijiUserService.saveBatch(paijiUsers);
			return RespBean.success("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("更新失败");
		}
	}

}

