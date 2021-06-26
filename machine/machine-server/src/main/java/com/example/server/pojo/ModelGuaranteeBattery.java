package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

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
 * @since 2021-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_model_guarantee_battery")
@ApiModel(value = "ModelGuaranteeBattery对象", description = "")
public class ModelGuaranteeBattery implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "机型")
	private String model;

	@ApiModelProperty(value = "excel中保修的描述")
	@TableField("excel_guarantee")
	private String excelGuarantee;

	@ApiModelProperty(value = "拍机堂中保修的描述的id")
	@TableField("paiji_guarantee_id")
	private Integer paijiGuaranteeId;

	@ApiModelProperty(value = "拍机堂中的电池效率id")
	@TableField("battery_id")
	private Integer batteryId;

	@ApiModelProperty(value = "当excel中没有对保修进行描述时，所要选的值id")
	@TableField("guarantee_default_id")
	private Integer guaranteeDefaultId;

	@ApiModelProperty(value = "默认保修描述是否启用")
	@TableField("guarantee_default_enable")
	private Integer guaranteeDefaultEnable;

}
