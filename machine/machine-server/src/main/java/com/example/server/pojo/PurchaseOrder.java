package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
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
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_purchase_order")
@ApiModel(value="PurchaseOrder对象", description="")
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "物品编号")
    private String number;

    @ApiModelProperty(value = "IMEI号")
    private String imei;

    @ApiModelProperty(value = "品类id")
    @TableField("category_id")
    private Integer categoryId;

    @ApiModelProperty(value = "型号")
    private String type;

    @ApiModelProperty(value = "sku名称")
    private String sku;

    @ApiModelProperty(value = "等级")
    private String rank;

    @ApiModelProperty(value = "库位")
    @TableField("stock_location")
    private String stockLocation;

    @ApiModelProperty(value = "购买渠道id")
    @TableField("purchasing_channel_id")
    private Integer purchasingChannelId;

    @ApiModelProperty(value = "录入批次")
    @TableField("enter_batch")
    private Integer enterBatch;

    @ApiModelProperty(value = "采购价")
    @TableField("purchase_price")
    private Float purchasePrice;

    @ApiModelProperty(value = "中标日期")
    @TableField("bidding_date")
    private LocalDate biddingDate;

    @ApiModelProperty(value = "操作了这个机器的所有操作人id")
    @TableField("operated_ids")
    private String operatedIds;

    @ApiModelProperty(value = "正在操作这个机器的操作人id")
    @TableField("operating_id")
    private Integer operatingId;

    @ApiModelProperty(value = "备注")
    private String describe;


}
