/*
 * �������� 2006-8-9
 *
 * @author laol
 */
package com.csmc.db;

import org.apache.commons.dbcp.BasicDataSource;

public class GenericDataSource extends BasicDataSource {
	
	private String dsName;

	/**
	 * @return ���� dsName��
	 */
	public String getDsName() {
		return dsName;
	}

	/**
	 * @param dsName Ҫ���õ� dsName��
	 */
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	
}
