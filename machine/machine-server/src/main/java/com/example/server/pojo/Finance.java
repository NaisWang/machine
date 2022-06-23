package com.example.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : whz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Finance {
	private Float purchasePrice;
	private Float purchaseReturnPrice;
	private Float sellPrice;
	private Float refundPrice;
	private Float fixPrice;
}
