package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.example.server.config.converter.ArrayToStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author whz
 * @since 2021-06-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_price_combination")
@ApiModel(value = "PriceCombination对象", description = "")
public class PriceCombination implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "几成新")
	private String grade;

	@ApiModelProperty(value = "excel描述")
	private String excelDesc;


	@ApiModelProperty(value = "价格一组合")
	private String price1;

	@ApiModelProperty(value = "价格二组合")
	private String price2;

	@ApiModelProperty(value = "价格三组合")
	private String price3;


}
