package com.example.server.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.DeliverMachine;
import com.example.server.pojo.DeliverReceipt;
import com.example.server.pojo.Machine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : whz
 */
@Component
public class MachineReceive {

	//@Autowired
	//private

	///**
	// * 判断是否可以接收
	// *
	// * @return
	// */
	//public boolean judgeReceive(Integer MachinId) {
	//	Machine machine = machineService.getById(machineId);
	//	Integer deliverMachineId = machine.getDeliverMachineId();
	//	if (deliverMachineId == 0) {
	//		return RespBean.error("该机器已经接收");
	//	}
	//	if (machineService.update(new Machine(), new UpdateWrapper<Machine>().eq("id", machineId).set("deliver_machine_id", 0))) {

	//		Map<String, Integer> queryMap = new HashMap<>();
	//		queryMap.put("machine_id", machineId);
	//		queryMap.put("id", deliverMachineId);

	//		LocalDateTime now = LocalDateTime.now();

	//		DeliverMachine deliverMachine = deliverMachineService.getOne(new QueryWrapper<DeliverMachine>().allEq(queryMap));
	//		deliverMachine.setReceiveEmpId(empId);
	//		deliverMachine.setStatus(2);
	//		deliverMachine.setReceiveDate(now);

	//		DeliverReceipt deliverReceipt = deliverReceiptService.getById(deliverMachine.getDeliverReceiptId());
	//		if (!Arrays.asList(deliverReceipt.getReceiveEmpIds().split(",")).contains(empId + "")) {
	//			return RespBean.error("你不能接收该机器");
	//		}

	//		if (deliverMachineService.update(deliverMachine, new QueryWrapper<DeliverMachine>().allEq(queryMap))) {
	//			logService.save(new Log(empId, "接收机器", "机器id为" + machineId, now, 0));
	//			return RespBean.success("接收成功");
	//		}
	//	}
	//}
}
