/*
 * Created on 2004-7-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.csmc.pms.webapp.security.model;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MenuModel {
	private String menuId;
	private String menuDesc;
	private String parentMenuId;
	private String menuType;
	private String functionType;
	private String actoinTag;
	private boolean hasPriv;
	
	public String getMenuId() {
		return menuId;
	}
	
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	
	public String getMenuDesc() {
		return menuDesc;
	}
	
	public void setMenuDesc(String menuDesc) {
		this.menuDesc = menuDesc;
	}

	public String getParentMenuId() {
		return parentMenuId;
	}
	
	public void setParentMenuId(String parentMenuId) {
		this.parentMenuId = parentMenuId;
	}

	public String getMenuType() {
		return menuType;
	}
	
	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public String getFunctionType() {
		return functionType;
	}
	
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	public String getActoinTag() {
		return actoinTag;
	}
	
	public void setActoinTag(String actoinTag) {
		this.actoinTag = actoinTag;
	}

	public boolean getHasPriv() {
		return hasPriv;
	}
	
	public void setHasPriv(boolean hasPriv) {
		this.hasPriv = hasPriv;
	}

}
