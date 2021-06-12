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
 * @since 2021-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_purchase_order_modify")
@ApiModel(value="PurchaseOrderModify对象", description="")
public class PurchaseOrderModify implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "角色id")
    @TableField("role_id")
    private Integer roleId;

    @ApiModelProperty(value = "物品编号")
    private Integer number;

    @ApiModelProperty(value = "IMEI号")
    private Integer imei;

    @ApiModelProperty(value = "品类")
    private Integer category;

    @ApiModelProperty(value = "型号")
    private Integer type;

    @ApiModelProperty(value = "品牌")
    private Integer brand;

    @ApiModelProperty(value = "sku名称")
    private Integer sku;

    @ApiModelProperty(value = "等级")
    private Integer rank;

    @ApiModelProperty(value = "库位")
    @TableField("stock_location")
    private Integer stockLocation;

    @ApiModelProperty(value = "购买渠道")
    @TableField("purchasing_channel")
    private Integer purchasingChannel;

    @ApiModelProperty(value = "录入批次")
    @TableField("enter_batch")
    private Integer enterBatch;

    @ApiModelProperty(value = "采购价")
    @TableField("purchase_price")
    private Integer purchasePrice;

    @ApiModelProperty(value = "中标日期")
    @TableField("bidding_date")
    private Integer biddingDate;

    @ApiModelProperty(value = "操作了这个机器的所有操作人")
    private Integer operatedName;

    @ApiModelProperty(value = "正在操作这个机器的操作人")
    private Integer operatingName;

    @ApiModelProperty(value = "备注")
    private Integer describe;


}
