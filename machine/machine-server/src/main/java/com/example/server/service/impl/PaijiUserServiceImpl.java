package com.example.server.service.impl;

import com.example.server.pojo.PaijiUser;
import com.example.server.mapper.PaijiUserMapper;
import com.example.server.service.IPaijiUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-06-20
 */
@Service
public class PaijiUserServiceImpl extends ServiceImpl<PaijiUserMapper, PaijiUser> implements IPaijiUserService {

}
