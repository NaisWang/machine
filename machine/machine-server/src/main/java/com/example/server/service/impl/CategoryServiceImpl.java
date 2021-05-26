package com.example.server.service.impl;

import com.example.server.pojo.Category;
import com.example.server.mapper.CategoryMapper;
import com.example.server.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("com.example.machineserver.mapper")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
