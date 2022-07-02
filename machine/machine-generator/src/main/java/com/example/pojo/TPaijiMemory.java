package com.example.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_paiji_memory")
@ApiModel(value="TPaijiMemory对象", description="")
public class TPaijiMemory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @TableField("machine_name")
    private String machineName;

    private String memory;


}
