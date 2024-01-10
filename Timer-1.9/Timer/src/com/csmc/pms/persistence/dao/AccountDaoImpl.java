package com.csmc.pms.persistence.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.csmc.db.DataSourceFactory;
import com.csmc.db.GenericSqlMapClientDaoSupport;
import com.csmc.pms.persistence.iface.AccountDao;

public class AccountDaoImpl extends GenericSqlMapClientDaoSupport implements AccountDao{
	public String getMailByGh(String accountNo) throws DataAccessException {		
		String mailAddress = (String)getTemplateByDataSource(
                DataSourceFactory.PMS_DATASOURCE).queryForObject("getMailByGh", accountNo);
		if (mailAddress != null && !mailAddress.equals("")) {
			mailAddress = mailAddress + ";";
		} else {
			mailAddress = "";
		}

        return mailAddress;
	}
	
	public String[] getSectionMailBySection(String section) throws DataAccessException {
		List mailAddresses = getTemplateByDataSource(DataSourceFactory.PMS_DATASOURCE)
			.queryForList("getSectionMailBySection", section);			
		if(mailAddresses != null && mailAddresses.size() > 0) {
			String mail=(String) mailAddresses.get(0);
			if(mail!=null){
				String [] mailArray=mail.split(",");
				return mailArray;
			}
		}
		return null;
	}

	public String[] getDeptLeaderMailsByDeptIndex(String deptIndex) throws DataAccessException {
    	List deptLeaderList=getTemplateByDataSource(DataSourceFactory.PMS_DATASOURCE)
    	          .queryForList("getDeptLeaderByDeptIndex", deptIndex);
    	if(deptLeaderList != null && deptLeaderList.size() > 0) {
			String deptLeader=(String) deptLeaderList.get(0);
			if(deptLeader!=null){
				String [] deptLeaderArray=deptLeader.split(",");
				String [] mails=new String[deptLeaderArray.length];
				for(int i=0;i<deptLeaderArray.length;i++){
					String leaderGH=deptLeaderArray[i];
					String mailAddress=getMailByGh(leaderGH);
					mails[i]=mailAddress;
				}
				return mails;
			}
		}
		return null;
    	
    }
}
