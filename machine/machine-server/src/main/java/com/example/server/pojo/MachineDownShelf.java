package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
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
 * @since 2021-08-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_machine_down_shelf")
@ApiModel(value="MachineDownShelf对象", description="")
public class MachineDownShelf implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Integer machineId;

    @ApiModelProperty(value = "机器物品编码")
    @TableField("machine_number")
    private String machineNumber;

    @ApiModelProperty(value = "下架召回时间")
    @TableField("recall_time")
    private LocalDateTime recallTime;

    @ApiModelProperty(value = "操作人")
    @TableField("operate_emp_id")
    private Integer operateEmpId;


}
