/*
 * 创建日期 2006-8-9
 *
 * @author laol
 */
package com.csmc.db;

import org.apache.commons.dbcp.BasicDataSource;

public class GenericDataSource extends BasicDataSource {
	
	private String dsName;

	/**
	 * @return 返回 dsName。
	 */
	public String getDsName() {
		return dsName;
	}

	/**
	 * @param dsName 要设置的 dsName。
	 */
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	
}
