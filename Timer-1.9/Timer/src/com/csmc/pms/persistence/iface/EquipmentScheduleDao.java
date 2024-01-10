package com.csmc.pms.persistence.iface;

import java.util.List;

import org.springframework.dao.DataAccessException;

public interface EquipmentScheduleDao {
	public List getDayUnFinishedPm() throws DataAccessException;
}
