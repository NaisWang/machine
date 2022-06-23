package com.example.server.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.EnterStorageReceiptToMachine;
import com.example.server.service.impl.EnterStorageReceiptToMachineServiceImpl;
import com.example.server.utils.RespBean;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-08-17
 */
@RestController
@RequestMapping("/machine/storage/enter-storage/machine")
public class EnterStorageReceiptToMachineController {
}
