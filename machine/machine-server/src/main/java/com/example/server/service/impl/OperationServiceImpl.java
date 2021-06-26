package com.example.server.service.impl;

import com.example.server.pojo.Operation;
import com.example.server.mapper.OperationMapper;
import com.example.server.service.IOperationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-06-22
 */
@Service
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Operation> implements IOperationService {

}
