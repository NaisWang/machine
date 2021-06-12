package com.example.server.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : whz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespPageBean {
	private Long total;
	private List<?> data;
}
