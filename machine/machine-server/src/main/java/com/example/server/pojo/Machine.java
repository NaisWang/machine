package com.example.server.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.server.config.converter.DateConverter;
import com.example.server.config.converter.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author whz
 * @since 2021-07-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_machine")
@ApiModel(value = "Machine对象", description = "")
public class Machine implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@ApiModelProperty(value = "采购单号id")
	@TableField("purchase_order_id")
	private Integer purchaseOrderId;

	@ApiModelProperty(value = "入库单据id")
	@TableField("enter_storage_receipt_id")
	private Integer enterStorageReceiptId;

	@ApiModelProperty(value = "销退入库单据id")
	@TableField("market_return_enter_storage_receipt_id")
	private Integer marketReturnEnterStorageReceiptId;

	@ApiModelProperty(value = "上架入库单据id")
	@TableField("up_shelf_enter_storage_id")
	private Integer upShelfEnterStorageId;

	@ApiModelProperty(value = "采购退货单据id")
	@TableField("purchase_return_receipt_id")
	private Integer purchaseReturnReceiptId;

	@ApiModelProperty(value = "销售单据id")
	@TableField("market_order_id")
	private Integer marketOrderId;

	@ApiModelProperty(value = "销售退货单据id")
	@TableField("market_return_receipt_id")
	private Integer marketReturnReceiptId;

	@ApiModelProperty(value = "袋子编号")
	@TableField("bag_number")
	private String BagNumber;

	@ApiModelProperty(value = "物品编号")
	@TableField("`number`")
	private String number;

	@ApiModelProperty(value = "IMEI号")
	private String imei;

	@ApiModelProperty(value = "拍机堂条码")
	private String paijiBarcode;

	@ApiModelProperty(value = "品类id")
	@TableField("category_id")
	private Integer categoryId;

	@ApiModelProperty(value = "品牌id")
	@TableField("brand_id")
	private Integer brandId;

	@ApiModelProperty(value = "型号")
	private String type;

	@ApiModelProperty(value = "sku名称")
	private String sku;

	@ApiModelProperty(value = "等级")
	@TableField("`rank`")
	private String rank;

	@ApiModelProperty(value = "一口价")
	@TableField("one_price")
	private Float onePrice;

	@ApiModelProperty(value = "竞拍价")
	@TableField("bid_price")
	private Float bidPrice;

	@ApiModelProperty(value = "采购价")
	@TableField("purchase_price")
	private Float purchasePrice;

	@ApiModelProperty(value = "好的价格")
	@TableField("good_price")
	private Float goodPrice;

	@ApiModelProperty(value = "出库价")
	@TableField("sale_price")
	private Float salePrice;

	@ApiModelProperty(value = "维修价")
	@TableField("fix_price")
	private Float fixPrice;

	@ApiModelProperty(value = "描述")
	@TableField("`describe`")
	private String describe;

	@ApiModelProperty(value = "库位Id")
	@TableField("storage_location_id")
	private Integer storageLocationId;

	@ApiModelProperty(value = "上一个机器状态id")
	@TableField("previous_status_id")
	private Integer previousStatusId;

	@ApiModelProperty(value = "机器状态id")
	@TableField("status_id")
	private Integer statusId;

	@ApiModelProperty(value = "出货渠道id")
	@TableField("sale_channel_id")
	private Integer saleChannelId;

	@ApiModelProperty(value = "采购渠道id")
	@TableField(exist = false)
	private Integer purchaseChannelId;

	@ApiModelProperty(value = "处理人id")
	@TableField("operate_emp_id")
	private Integer operateEmpId;

	@ApiModelProperty(value = "处理时间")
	@TableField("operate_date")
	private LocalDateTime operateDate;

	@ApiModelProperty(value = "采购时间")
	@TableField(exist = false)
	private LocalDateTime purchaseTime;

	@ApiModelProperty(value = "采购人员id")
	@TableField("purchase_emp_id")
	private Integer purchaseEmpId;

	@ApiModelProperty(value = "质检方")
	@TableField("quality_inspector")
	private String qualityInspector;

	@ApiModelProperty(value = "入库人员id")
	@TableField("enter_storage_emp_id")
	private Integer enterStorageEmpId;

	@ApiModelProperty(value = "入库日期")
	@TableField("enter_storage_date")
	private LocalDateTime enterStorageDate;

	@ApiModelProperty(value = "出库日期")
	@TableField("out_storage_date")
	private LocalDateTime outStorageDate;

	@ApiModelProperty(value = "出库批次")
	@TableField("out_storage_batch")
	private Integer outStorageBatch;

	@ApiModelProperty(value = "成色检测描述")
	@TableField("quality_desc")
	private String qualityDesc;

	@ApiModelProperty(value = "功能检测描述")
	@TableField("feature_desc")
	private String featureDesc;

	@ApiModelProperty(value = "等级检测描述")
	@TableField("rank_desc")
	private String rankDesc;

	@ApiModelProperty(value = "需要修改的项")
	@TableField("need_fix")
	private String needFix;

	@ApiModelProperty(value = "已修好的项")
	@TableField("fixed")
	private String fixed;

	@ApiModelProperty(value = "未修好的项")
	@TableField("not_fixed")
	private String notFixed;

	@ApiModelProperty(value = "修坏项")
	@TableField("fix_to_bad")
	private String fixToBad;

	@ApiModelProperty(value = "对应转机机器表中的id")
	@TableField("deliver_receipt_id")
	private Integer deliverReceiptId;

	@ApiModelProperty(value = "是否完成转交单指定的指标，为0，表示完成了，反之，表示没有完成")
	@TableField("need_complete_deliver_receipt_id")
	private Integer needCompleteDeliverReceiptId;

	@ApiModelProperty(value = "是否能上架")
	@TableField("is_up_shelf")
	private Integer isUpShelf;

	@ApiModelProperty(value = "备注")
	@TableField("comment")
	private String comment;

}
