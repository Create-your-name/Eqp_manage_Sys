package com.csmc.pms.webapp.pda.model;

/**
 * equipItem
 * 
 * @author shaoaj
 * @2007-10-31
 */
public class FormItem {
	// 表单名
	private String formName;

	// 文件名
	private String fileName;

	// job文件名
	private String flowName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	/**
	 * @return the flowName
	 */
	public String getFlowName() {
		return flowName;
	}

	/**
	 * @param flowName the flowName to set
	 */
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}
}
