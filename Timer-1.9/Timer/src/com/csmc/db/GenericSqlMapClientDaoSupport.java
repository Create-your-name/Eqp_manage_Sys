/*
 * 创建日期 2006-8-21
 *
 * @author laol
 */
package com.csmc.db;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

public class GenericSqlMapClientDaoSupport extends SqlMapClientDaoSupport{
	
	public SqlMapClientTemplate getTemplateByDataSource(String dsName) {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		template.setDataSource(DataSourceFactory.getDataSource(dsName));
		return template;
	}
}
