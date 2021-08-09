package com.example.server.utils;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.server.pojo.DeliverMachine;
import com.example.server.pojo.DeliverReceipt;
import com.example.server.pojo.Machine;
import com.example.server.service.impl.DeliverMachineServiceImpl;
import com.example.server.service.impl.DeliverReceiptServiceImpl;
import com.example.server.service.impl.MachineServiceImpl;
import com.example.server.service.impl.StockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author : whz
 */
@Component
public class JudgeCompleteDeliverIntention {

	@Autowired
	private DeliverReceiptServiceImpl deliverReceiptService;
	@Autowired
	private DeliverMachineServiceImpl deliverMachineService;
	@Autowired
	private MachineServiceImpl machineService;

	private static DeliverMachineServiceImpl staticDeliverMachineService;
	private static DeliverReceiptServiceImpl staticDeliverReceiptService;
	private static MachineServiceImpl staticMachineService;

	@PostConstruct
	public void init() {
		staticDeliverMachineService = deliverMachineService;
		staticDeliverReceiptService = deliverReceiptService;
		staticMachineService = machineService;
	}

	public static boolean judgeIsComplete(List<Machine> machines, Integer deliverIntentionId) {
		for (Machine machine : machines) {
			if (machine.getNeedCompleteDeliverReceiptId() == 0) {
				continue;
			}
			DeliverReceipt deliverReceipt = staticDeliverReceiptService.getById(machine.getNeedCompleteDeliverReceiptId());
			if (!deliverReceipt.getDeliverIntentionId().equals(deliverIntentionId)) {
				continue;
			}
			if (staticDeliverMachineService.update(new DeliverMachine(), new UpdateWrapper<DeliverMachine>().eq("deliver_receipt_id", machine.getNeedCompleteDeliverReceiptId()).eq("machine_number", machine.getNumber()).set("is_complete", 1))) {
				if (staticMachineService.update(new Machine(), new UpdateWrapper<Machine>().eq("number", machine.getNumber()).set("need_complete_deliver_receipt_id", 0))) {
					continue;
				}
			}
			return false;
		}
		return true;
	}
}
