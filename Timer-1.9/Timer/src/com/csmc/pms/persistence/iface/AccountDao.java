package com.csmc.pms.persistence.iface;

import org.springframework.dao.DataAccessException;

public interface AccountDao {
	public String getMailByGh(String accountNo) throws DataAccessException;
	public String[] getSectionMailBySection(String section) throws DataAccessException;
	public String[] getDeptLeaderMailsByDeptIndex(String deptIndex) throws DataAccessException;
}
