package com.csmc.pms.webapp.pda.model;

import java.util.ArrayList;
import java.util.List;
/**
 * equipmentGroup
 * @author shaoaj
 * @2007-10-31
 */
public class EquipmentGroup {
	private String equipment="";
	private List formItemList=new ArrayList();
	public String getEquipment() {
		return equipment;
	}
	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}
	public List getFormItemList() {
		return formItemList;
	}
	public void setFormItemList(List formItemList) {
		this.formItemList = formItemList;
	}
}
