package com.csmc.pms.webapp.workflow.model;

import org.ofbiz.base.util.Debug;

import com.csmc.pms.webapp.util.Constants;

public class ActionItem {
	
	public static final String module = ActionItem.class.getName();
	
	//项目Index
	private long itemIndex;
	//项目名
	private String itemName;
	//项目描述
	private String itemDescription;
	//项目序列号
	private int itemOrder;
	//项目类别
	private int itemType;
	//预设值
	private String defaultValue;
	//项目单位
	private String itemUnit;
	//项目上界
	private Double itemUpperSpec;
	//项目下界
	private Double itemLowerSpec;
	//项目值
	private String itemValue;
	//选择列表
	private String itemOption;
	//数据点Index
	private long pointIndex;
	//项目备注
	private String itemNode;
	//是否超规范
	private boolean isOOS;
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public long getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(long itemIndex) {
		this.itemIndex = itemIndex;
	}
	public Double getItemLowerSpec() {
		return itemLowerSpec;
	}
	public void setItemLowerSpec(Double itemLowerSpec) {
		this.itemLowerSpec = itemLowerSpec;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getItemOrder() {
		return itemOrder;
	}
	public void setItemOrder(int itemOrder) {
		this.itemOrder = itemOrder;
	}
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	public String getItemUnit() {
		return itemUnit;
	}
	public void setItemUnit(String itemUnit) {
		this.itemUnit = itemUnit;
	}
	public Double getItemUpperSpec() {
		return itemUpperSpec;
	}
	public void setItemUpperSpec(Double itemUpperSpec) {
		this.itemUpperSpec = itemUpperSpec;
	}
	public String getItemOption() {
		return itemOption;
	}
	public void setItemOption(String itemOption) {
		this.itemOption = itemOption;
	}
	public String getItemValue() {
		return itemValue;
	}
	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}
	public long getPointIndex() {
		return pointIndex;
	}
	public void setPointIndex(long pointIndex) {
		this.pointIndex = pointIndex;
	}
	public String getItemNode() {
		return itemNode;
	}
	public void setItemNode(String itemNode) {
		this.itemNode = itemNode;
	}
	
	public boolean isOOS() {
		isOOS = false;
		
		try {
			if(Constants.NUMBER_ITEM == this.getItemType()) {
				if(this.itemValue != null && !"NONE".equalsIgnoreCase(this.itemValue)) {
					double value = Double.parseDouble(this.itemValue);
					
					if(this.itemUpperSpec == null && this.itemLowerSpec != null) {//单边大于
						if(value < this.itemLowerSpec.doubleValue()) {
							this.isOOS = true;
						}
					} else if(this.itemUpperSpec != null && this.itemLowerSpec == null) {//单边小于
						if(value > this.itemUpperSpec.doubleValue()) {
							this.isOOS = true;
						}
					} else if(this.itemUpperSpec != null && this.itemLowerSpec != null) {//双边规范
						if(value > this.itemUpperSpec.doubleValue() ||
								value < this.itemLowerSpec.doubleValue()) {
							this.isOOS = true;
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e.toString() + " itemIndex:" + itemIndex, module);
		}
		
		return isOOS;
	}
	
}
