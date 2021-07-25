package com.example.server.controller;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.server.pojo.*;
import com.example.server.service.impl.*;
import com.example.server.utils.MachineUtil;
import com.example.server.utils.RespBean;
import com.example.server.utils.RespPageBean;
import com.sun.tools.jconsole.JConsoleContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whz
 * @since 2021-05-26
 */
@RestController
@RequestMapping("")
public class PurchaseOrderController {

	@Autowired
	private CategoryServiceImpl categoryService;
	@Autowired
	private ChannelServiceImpl channelService;
	@Autowired
	private BrandServiceImpl brandService;


	//public RespBean getPurchaseOrderDetail(@RequestParam(defaultValue = "1") Integer currentPage,
	//																			 @RequestParam(defaultValue = "10") Integer size,
	//																			 @){
	//	RespPageBean purchaseOrderMachine = machineService.getPurchaseOrderMachine(currentPage, size, )
	//}

	/**
	 * 获取所有采购单
	 */
	//@ApiOperation("获取/搜索采购单中的所有机器信息, 分页")
	//@GetMapping("/")
	//public RespBean getAllPurchaseOrder(@RequestParam(defaultValue = "1") Integer currentPage,
	//																		@RequestParam(defaultValue = "10") Integer size,
	//																		PurchaseOrder purchaseOrder,
	//																		LocalDate[] bidDateScope) {
	//	System.out.println("e");
	//	System.out.println(Arrays.toString(bidDateScope));
	//	RespPageBean Machines = purchaseOrderService.getMachineByPage(currentPage, size, purchaseOrder, bidDateScope);
	//	return RespBean.success("查询成功", Machines);
	//}

	/**
	 * 手动向采购单中添加机器
	 */
	//@ApiOperation("手动向采购单中添加机器")
	//@PostMapping("/")
	//public RespBean manualAddMachine(@RequestBody PurchaseOrder purchaseOrder) {
	//	if (purchaseOrderService.manualAddMachine(purchaseOrder) == 1) {
	//		return RespBean.success("添加成功");
	//	}
	//	return RespBean.error("添加失败");
	//}

	///**
	// * 修改采购单中的数据
	// */
	//@ApiOperation("修改采购单中的数据")
	//@PutMapping("/")
	//public RespBean modifyMachine(@RequestBody PurchaseOrder[] purchaseOrder) {
	//	System.out.println(Arrays.toString(purchaseOrder));
	//	if (purchaseOrderService.updateBatchById(Arrays.asList(purchaseOrder))) {
	//		return RespBean.success("修改成功");
	//	}
	//	return RespBean.error("修改失败");
	//}

	//@ApiOperation("删除采购单中的数据")
	//@DeleteMapping("/")
	//public RespBean deleteMachine(Integer id) {
	//	if (purchaseOrderService.removeById(id)) {
	//		return RespBean.success("删除成功");
	//	}
	//	return RespBean.error("删除失败");
	//}

	//@ApiOperation("通过excel形式导入数据")
	//@PostMapping("/import")
	//public RespBean importEmployee(MultipartFile file, Principal principal, Authentication authentication) {
	//	List<Category> categoryList = categoryService.list(null);
	//	Map<String, Integer> categoryMap = new HashMap<>();
	//	for (Category category : categoryList) {
	//		categoryMap.put(category.getName(), category.getId());
	//	}

	//	List<Channel> channelList = channelService.list(null);
	//	Map<String, Integer> channelMap = new HashMap<>();
	//	for (Channel channel : channelList) {
	//		channelMap.put(channel.getName(), channel.getId());
	//	}

	//	List<Brand> brandList = brandService.list(null);
	//	Map<String, Integer> brandMap = new HashMap<>();
	//	for (Brand brand : brandList) {
	//		brandMap.put(brand.getName(), brand.getId());
	//	}

	//	Employee employee = (Employee) authentication.getPrincipal();

	//	ImportParams params = new ImportParams();
	//	params.setTitleRows(1);
	//	try {
	//		List<PurchaseOrder> purchaseOrderList = ExcelImportUtil.importExcel(file.getInputStream(), PurchaseOrder.class, params);
	//		for (PurchaseOrder purchaseOrder : purchaseOrderList) {
	//			purchaseOrder.setCategoryId(categoryMap.get(purchaseOrder.getCategory().getName()));
	//			purchaseOrder.setPurchasingChannelId(channelMap.get(purchaseOrder.getChannel().getName()));
	//			purchaseOrder.setBrandId(brandMap.get(purchaseOrder.getBrand().getName()));
	//			purchaseOrder.setOperatedIds(employee.getId().toString());
	//			purchaseOrder.setOperatingId(employee.getId());
	//			if (MachineUtil.judgePurchaseHaveMachine(purchaseOrder) == 1) {
	//				return RespBean.error("导入失败, 物品编号为：" + purchaseOrder.getNumber() + "的机器已导入采购单中");
	//			}
	//			if (MachineUtil.judgePurchaseHaveMachine(purchaseOrder) == 2) {
	//				return RespBean.error("导入失败, IMEI号为：" + purchaseOrder.getImei() + "的机器已导入采购单中");
	//			}
	//		}
	//		if (purchaseOrderService.saveBatch(purchaseOrderList)) {
	//			return RespBean.success("导入成功");
	//		}
	//	} catch (Exception e) {
	//		e.printStackTrace();
	//	}
	//	return RespBean.error("导入失败");
	//}

	//@ApiOperation("数据初始化")
	//@GetMapping("/postMost")
	//public RespBean test() {
	//	HashMap<Integer, String> category = new HashMap<>();
	//	category.put(1, "手机");
	//	category.put(2, "平板");
	//	category.put(3, "笔记本");
	//	category.put(4, "相机");
	//	category.put(5, "数据线");
	//	category.put(6, "充电器");
	//	category.put(7, "耳机");

	//	HashMap<Integer, String> brand = new HashMap<>();
	//	brand.put(1, "苹果");
	//	brand.put(2, "锤子");
	//	brand.put(3, "华为");
	//	brand.put(4, "vivo");
	//	brand.put(5, "oppo");
	//	brand.put(6, "一加");
	//	brand.put(7, "魅族");
	//	brand.put(8, "小米");

	//	HashMap<Integer, String> channel = new HashMap<>();
	//	channel.put(1, "找靓机");
	//	channel.put(2, "锤子");
	//	channel.put(3, "淘宝");
	//	channel.put(4, "京东");
	//	channel.put(5, "爱回收");

	//	HashMap<Integer, String> operateName = new HashMap<>();
	//	operateName.put(1, "张三");
	//	operateName.put(2, "李四");
	//	operateName.put(3, "王五");
	//	operateName.put(4, "赵六");

	//	HashMap<Integer, String> rank = new HashMap<>();
	//	rank.put(1, "A1");
	//	rank.put(2, "A+");
	//	rank.put(3, "A+1");
	//	rank.put(4, "A+2");
	//	rank.put(5, "B1");
	//	rank.put(6, "B+");
	//	rank.put(7, "B+1");
	//	rank.put(8, "B+2");


	//	List<PurchaseOrder> employees = new LinkedList<>();
	//	for (int i = 0; i < 10000; i++) {
	//		PurchaseOrder pu = new PurchaseOrder();
	//		String number = new DecimalFormat("00000").format(i);
	//		pu.setNumber("201910151925492" + number);
	//		pu.setImei("8688230312" + number);

	//		Random r = new Random();

	//		int categoryId = r.nextInt(6) + 1;
	//		pu.setCategoryId(categoryId);

	//		int brandId = r.nextInt(7) + 1;
	//		pu.setBrandId(brandId);

	//		pu.setType(brand.get(brandId) + category.get(categoryId));
	//		pu.setSku(brand.get(brandId) + category.get(categoryId));

	//		pu.setRank(rank.get(r.nextInt(7) + 1));
	//		pu.setStockLocation(r.nextInt(8) + "");
	//		pu.setPurchasingChannelId(r.nextInt(4) + 1);
	//		pu.setEnterBatch(r.nextInt(8));
	//		pu.setPurchasePrice(Float.parseFloat(new DecimalFormat(".00").format(r.nextInt(10000) + r.nextFloat())));

	//		pu.setBiddingDate(LocalDate.of(r.nextInt(2) + 2018, r.nextInt(11) + 1, r.nextInt(27) + 1));

	//		int opId = r.nextInt(3) + 1;
	//		pu.setOperatedIds(opId + "");
	//		pu.setOperatingId(opId);
	//		employees.add(pu);
	//	}

	//	if (purchaseOrderService.saveBatch(employees))
	//		return RespBean.success("添加成功");
	//	return RespBean.error("添加成功");
	//}

}

