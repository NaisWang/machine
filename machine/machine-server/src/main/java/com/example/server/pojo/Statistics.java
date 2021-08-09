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
public class Statistics {

	private Integer qualityDetection;
	private Integer featureDetection;
	private Integer upShelf;
	private Integer back;
	private Integer fixed;

	private Integer operateEmpId;
	private Integer statusId;
	private Integer sum;
	private Integer dateScope;

}
