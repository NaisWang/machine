package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.Paiji;
import com.example.server.mapper.PaijiMapper;
import com.example.server.service.IPaijiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-06-16
 */
@Service
public class PaijiServiceImpl extends ServiceImpl<PaijiMapper, Paiji> implements IPaijiService {

	@Autowired
	private PaijiMapper paijiMapper;

	/**
	 * 排序获取所有数据
	 */
	@Override
	public List<Paiji> listOrder(Paiji paiji) {
		QueryWrapper<Paiji> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByAsc("name").notIn("name", "小型号", "网络制式", "购买渠道", "机身颜色", "内存", "存储容量");
		if (paiji != null) {
			if (paiji.getValue() != null) {
				queryWrapper.eq("value", paiji.getValue());
			}
			if (paiji.getXdCheckout() != null) {
				queryWrapper.eq("xd_checkout", paiji.getXdCheckout());
			}
			if (paiji.getAhsCheckout() != null) {
				queryWrapper.eq("ahs_checkout", paiji.getAhsCheckout());
			}
		}
		return paijiMapper.selectList(queryWrapper);
	}
}
