/**
 * 
 */
package com.csmc.pms.webapp.util.jakartaFileUpload;

import java.util.List;
import java.util.Map;

/**
 * @author shaoaj
 * 
 */
public class JfileItemInfo {
	private List fileUpLoadState = null;

	private Map fileValue = null;

	private List fileNameList=null;
	
	private List contentTypeList=null;
	/**
	 * @return the fileUpLoadState
	 */
	public List getFileUpLoadState() {
		return fileUpLoadState;
	}

	/**
	 * @param fileUpLoadState
	 *            the fileUpLoadState to set
	 */
	public void setFileUpLoadState(List fileUpLoadState) {
		this.fileUpLoadState = fileUpLoadState;
	}

	/**
	 * @return the fileValue
	 */
	public Map getFileValue() {
		return fileValue;
	}

	/**
	 * @param fileValue
	 *            the fileValue to set
	 */
	public void setFileValue(Map fileValue) {
		this.fileValue = fileValue;
	}

	/**
	 * @return the fileNameList
	 */
	public List getFileNameList() {
		return fileNameList;
	}

	/**
	 * @param fileNameList the fileNameList to set
	 */
	public void setFileNameList(List fileNameList) {
		this.fileNameList = fileNameList;
	}

	/**
	 * @return the contentTypeList
	 */
	public List getContentTypeList() {
		return contentTypeList;
	}

	/**
	 * @param contentTypeList the contentTypeList to set
	 */
	public void setContentTypeList(List contentTypeList) {
		this.contentTypeList = contentTypeList;
	}
}
