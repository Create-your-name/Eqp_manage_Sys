package com.csmc.pms.webapp.workflow.model;

import org.ofbiz.base.util.Debug;

import com.csmc.pms.webapp.util.Constants;

public class ActionItem {
	
	public static final String module = ActionItem.class.getName();
	
	//��ĿIndex
	private long itemIndex;
	//��Ŀ��
	private String itemName;
	//��Ŀ����
	private String itemDescription;
	//��Ŀ���к�
	private int itemOrder;
	//��Ŀ���
	private int itemType;
	//Ԥ��ֵ
	private String defaultValue;
	//��Ŀ��λ
	private String itemUnit;
	//��Ŀ�Ͻ�
	private Double itemUpperSpec;
	//��Ŀ�½�
	private Double itemLowerSpec;
	//��Ŀֵ
	private String itemValue;
	//ѡ���б�
	private String itemOption;
	//���ݵ�Index
	private long pointIndex;
	//��Ŀ��ע
	private String itemNode;
	//�Ƿ񳬹淶
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
					
					if(this.itemUpperSpec == null && this.itemLowerSpec != null) {//���ߴ���
						if(value < this.itemLowerSpec.doubleValue()) {
							this.isOOS = true;
						}
					} else if(this.itemUpperSpec != null && this.itemLowerSpec == null) {//����С��
						if(value > this.itemUpperSpec.doubleValue()) {
							this.isOOS = true;
						}
					} else if(this.itemUpperSpec != null && this.itemLowerSpec != null) {//˫�߹淶
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
