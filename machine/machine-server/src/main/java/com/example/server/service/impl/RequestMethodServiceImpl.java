package com.example.server.service.impl;

import com.example.server.pojo.RequestMethod;
import com.example.server.mapper.RequestMethodMapper;
import com.example.server.service.IRequestMethodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whz
 * @since 2021-05-27
 */
@Service
public class RequestMethodServiceImpl extends ServiceImpl<RequestMethodMapper, RequestMethod> implements IRequestMethodService {

	@Autowired
	private RequestMethodMapper requestMethodMapper;

}
