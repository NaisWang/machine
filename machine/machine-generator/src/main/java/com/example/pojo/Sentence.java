package com.example.pojo;

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
 * @since 2022-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Sentence对象", description="")
public class Sentence implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @TableField("section_key")
    private Integer sectionKey;

    @TableField("unit_key")
    private Integer unitKey;

    private String sentence;

    private String keyword;

    private String chinese;


}
