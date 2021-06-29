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

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "几成新")
	private String grade;

	@ApiModelProperty(value = "屏幕外观描述")
	private String screenAppearance;

	@ApiModelProperty(value = "边框背板")
	private String iframeBack;

	@ApiModelProperty(value = "屏幕显示")
	private String screenDisplay;

	@ApiModelProperty(value = "价格一组合")
	private String price1;

	@ApiModelProperty(value = "价格二组合")
	private String price2;

	@ApiModelProperty(value = "价格三组合")
	private String price3;


}
