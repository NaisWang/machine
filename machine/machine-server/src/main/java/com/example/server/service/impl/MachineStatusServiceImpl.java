package com.example.server.service.impl;

import com.example.server.pojo.MachineStatus;
import com.example.server.mapper.MachineStatusMapper;
import com.example.server.service.IMachineStatusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-30
 */
@Service
public class MachineStatusServiceImpl extends ServiceImpl<MachineStatusMapper, MachineStatus> implements IMachineStatusService {

}
