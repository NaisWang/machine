package com.example.server.service.impl;

import com.example.server.pojo.PurchaseOrder;
import com.example.server.mapper.PurchaseOrderMapper;
import com.example.server.service.IPurchaseOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements IPurchaseOrderService {

}
