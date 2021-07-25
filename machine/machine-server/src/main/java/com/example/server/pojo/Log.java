package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author whz
 * @since 2021-07-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_log")
@ApiModel(value = "Log对象", description = "")
public class Log implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "员工id")
	@NonNull
	private Integer empId;

	@ApiModelProperty(value = "员工名字")
	@TableField(exist = false)
	private String empName;

	@ApiModelProperty(value = "请求描述")
	@NonNull
	private String urlDesc;

	@ApiModelProperty(value = "请求参数")
	@NonNull
	private String parameter;

	@ApiModelProperty(value = "请求时间")
	@NonNull
	private LocalDateTime time;

	@ApiModelProperty(value = "是否执行成功")
	@NonNull
	private Integer result;


}
