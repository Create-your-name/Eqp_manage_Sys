package com.csmc.pms.webapp.period.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.ConnectionFactory;

import com.csmc.pms.webapp.db.SQLProcessException;

public class YearPeriod implements Runnable{
	public static final String module = YearPeriod.class.getName();
	private String accountNo;
	private Long nextYear;
	private Long seqIndex;
	private GenericDelegator delegator;
	
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public GenericDelegator getDelegator() {
		return delegator;
	}

	public void setDelegator(GenericDelegator delegator) {
		this.delegator = delegator;
	}

	public Long getNextYear() {
		return nextYear;
	}

	public void setNextYear(Long nextYear) {
		this.nextYear = nextYear;
	}

	public Long getSeqIndex() {
		return seqIndex;
	}

	public void setSeqIndex(Long seqIndex) {
		this.seqIndex = seqIndex;
	}

	public void run() {
		try {
			Iterator groups = UtilMisc.toIterator(delegator.getModelGroupReader().getGroupNames());
			String helperName = delegator.getGroupHelperName(groups.next().toString());
			Connection conn = null;
			CallableStatement st = null;
			String errorMsg = "";
			String sql = "{Call pms_next_year_period(?,?,?,?,?)}";
			try {
				conn = ConnectionFactory.getConnection(helperName);
				st = conn.prepareCall(sql);
				st.setString(1, accountNo);
				st.setLong(2, nextYear.longValue());
				st.setLong(3, seqIndex.longValue());
				st.registerOutParameter(4, Types.VARCHAR);
				st.registerOutParameter(5, Types.VARCHAR);
				st.executeUpdate();
				errorMsg = st.getString(5);
				if (errorMsg != null && !"".equals(errorMsg)) {
					throw new SQLProcessException(errorMsg);
				}
			} catch (GenericEntityException e) {
				throw new SQLProcessException(
					"SQL Exception while get the connection:" + sql, e);
			} catch (SQLException e) {
				throw new SQLProcessException(
					"SQL Exception while executing the following:" + sql, e);
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e1) {
						Debug.logError(e1, module);
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e1) {
						Debug.logError(e1, module);
					}
				}
			}
		} catch(Exception e) {
			Debug.logError(e, module);
		}
	}
}
