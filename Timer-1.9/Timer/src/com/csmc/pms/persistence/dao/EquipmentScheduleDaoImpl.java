/*
 * 创建日期 2007-9-10
 *
 * @author dinghh
 */
package com.csmc.pms.persistence.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.csmc.db.DataSourceFactory;
import com.csmc.db.GenericSqlMapClientDaoSupport;
import com.csmc.pms.persistence.iface.EquipmentScheduleDao;

public class EquipmentScheduleDaoImpl extends GenericSqlMapClientDaoSupport implements EquipmentScheduleDao{
public static final String module = PeriodScheduleDaoImpl.class.getName();
    
    public List getDayUnFinishedPm() throws DataAccessException {
        List list = (List)getTemplateByDataSource(
                DataSourceFactory.PMS_DATASOURCE).queryForList("getDayUnFinishedPm", null);
        return list;
    }
}
