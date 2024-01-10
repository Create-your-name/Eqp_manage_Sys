package com.csmc.pms.persistence.iface;

import java.util.List;

import org.springframework.dao.DataAccessException;

public interface PeriodScheduleDao {

    public List getDayUnFinishedPc(String pcStyleName) throws DataAccessException;
    
    public List getWeekUnFinishedPc(String pcStyleName) throws DataAccessException;

}
