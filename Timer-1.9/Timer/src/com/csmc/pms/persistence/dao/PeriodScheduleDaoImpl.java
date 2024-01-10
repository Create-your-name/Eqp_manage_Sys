/*
 * 创建日期 2007-9-7
 *
 * @author dinghh
 */
package com.csmc.pms.persistence.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.csmc.db.DataSourceFactory;
import com.csmc.db.GenericSqlMapClientDaoSupport;
import com.csmc.pms.persistence.iface.PeriodScheduleDao;

public class PeriodScheduleDaoImpl extends GenericSqlMapClientDaoSupport implements PeriodScheduleDao{
    
    public static final String module = PeriodScheduleDaoImpl.class.getName();
    
    public List getDayUnFinishedPc(String pcStyleName) throws DataAccessException {
        List list = (List)getTemplateByDataSource(
                DataSourceFactory.PMS_DATASOURCE).queryForList("getDayUnFinishedPc", pcStyleName);
        return list;
    }
    
    public List getWeekUnFinishedPc(String pcStyleName) throws DataAccessException {
        List list = (List)getTemplateByDataSource(
                DataSourceFactory.PMS_DATASOURCE).queryForList("getWeekUnFinishedPc", pcStyleName);
        return list;
    }
}
