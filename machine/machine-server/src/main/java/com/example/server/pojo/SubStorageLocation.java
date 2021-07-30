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
 * @since 2021-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sub_storage_location")
@ApiModel(value = "SubStorageLocation对象", description = "")
public class SubStorageLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "父库位id")
	@TableField("parent_storage_location_id")
	private Integer parentStorageLocationId;

	@ApiModelProperty(value = "库名")
	private String name;

	@ApiModelProperty(value = "库管门卫")
	@TableField("gate_emp_id")
	private String gateEmpId;

	@ApiModelProperty(value = "机器数")
	@TableField(exist = false)
	private Integer machineSum;

	@ApiModelProperty(value = "备注")
	private String comment;


}
