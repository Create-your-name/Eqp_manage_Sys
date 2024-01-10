package com.csmc.pms.webapp.pda.model;

import java.util.ArrayList;
import java.util.List;

/**
 * IndexÄ¿Â¼Àà
 * 
 * @author shaoaj
 * @2007-10-31
 */
public class FormLib {
	private String sheetTyp = "";

	private List downLoadList;

	private List equipmentGroupList = new ArrayList();

	public List getEquipmentGroupList() {
		return equipmentGroupList;
	}

	public void setEquipmentGroupList(List equipmentGroupList) {
		this.equipmentGroupList = equipmentGroupList;
	}

	public List getDownLoadList() {
		return downLoadList;
	}

	public void setDownLoadList(List downLoadList) {
		this.downLoadList = downLoadList;
	}

	public String getSheetTyp() {
		return sheetTyp;
	}

	public void setSheetTyp(String sheetTyp) {
		this.sheetTyp = sheetTyp;
	}

}
