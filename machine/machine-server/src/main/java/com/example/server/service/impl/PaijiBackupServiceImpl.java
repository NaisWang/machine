package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.mapper.PaijiMapper;
import com.example.server.pojo.Paiji;
import com.example.server.pojo.PaijiBackup;
import com.example.server.mapper.PaijiBackupMapper;
import com.example.server.service.IPaijiBackupService;
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
 * @since 2021-07-06
 */
@Service
public class PaijiBackupServiceImpl extends ServiceImpl<PaijiBackupMapper, PaijiBackup> implements IPaijiBackupService {

	@Autowired
	private PaijiBackupMapper paijiBackupMapper;

	/**
	 * 排序获取所有数据
	 */
	public List<PaijiBackup> listOrder(PaijiBackup paijiBackup) {
		QueryWrapper<PaijiBackup> queryWrapper = new QueryWrapper<>();
		queryWrapper.orderByAsc("name").notIn("name", "小型号", "网络制式", "购买渠道", "机身颜色", "内存", "存储容量");
		if (paijiBackup != null) {
			if (paijiBackup.getValue() != null) {
				queryWrapper.eq("value", paijiBackup.getValue());
			}
			if (paijiBackup.getXdCheckout() != null) {
				queryWrapper.eq("xd_checkout", paijiBackup.getXdCheckout());
			}
			if (paijiBackup.getAhsCheckout() != null) {
				queryWrapper.eq("ahs_checkout", paijiBackup.getAhsCheckout());
			}
		}
		return paijiBackupMapper.selectList(queryWrapper);
	}

}
