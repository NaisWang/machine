package com.example.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.mapper.MachineDownShelfMapper;
import com.example.server.pojo.MachineDetection;
import com.example.server.mapper.MachineDetectionMapper;
import com.example.server.pojo.MachineDownShelf;
import com.example.server.service.IMachineDetectionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.server.utils.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-08-19
 */
@Service
public class MachineDetectionServiceImpl extends ServiceImpl<MachineDetectionMapper, MachineDetection> implements IMachineDetectionService {

}
