package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

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
 * @since 2021-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_storage_location")
@ApiModel(value = "StorageLocation对象", description = "")
public class StorageLocation implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "库位id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "库位名字")
	private String name;

	@ApiModelProperty(value = "操作人ids")
	private String operateEmpIds;

	@ApiModelProperty(value = "子库数")
	@TableField(exist = false)
	private Integer subStorageSum;

	@ApiModelProperty(value = "机器数")
	@TableField(exist = false)
	private Integer machineSum;

	@ApiModelProperty(value = "备注")
	private String comment;
}
