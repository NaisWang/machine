package com.example.service.impl;

import com.example.pojo.Sentence;
import com.example.mapper.SentenceMapper;
import com.example.service.ISentenceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author whz
 * @since 2022-04-25
 */
@Service
public class SentenceServiceImpl extends ServiceImpl<SentenceMapper, Sentence> implements ISentenceService {

}
