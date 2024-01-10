/*
 * Created on 2004-7-14 
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.csmc.db;

import java.sql.*;
import java.util.*;

public class SQLProcess {
    public static final String module = SQLProcess.class.getName();

    private static Connection getConnection(String dsName) throws SQLException {
    	Connection conn = ConnectionFactory.getConnection(dsName);
    	return conn;
    }
    
	public static List excuteSQLQuery(String sql,String dsName)
	throws SQLException {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection(dsName);
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			List list = new ArrayList();
			ResultSetMetaData rm = rs.getMetaData();

			while (rs.next()) {
				Map map = new HashMap();
				for (int i = 0; i < rm.getColumnCount(); i++) {
					map.put(rm.getColumnName(i+1), rs.getString(i+1));
				}
				list.add(map);
			}  
			return list;
		} finally {
			if (rs != null) {
				try { rs.close(); } catch (SQLException e1) { }
			}
			if (st != null) {
				try { st.close(); } catch (SQLException e1) { }
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException e1) { }
			}
		}
	}
	
	public static int excute(String sql)
	throws SQLException {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = null;
			st = conn.createStatement();
			int i = st.executeUpdate(sql);
			return i;

		} finally {
			if (rs != null) {
				try { rs.close(); } catch (SQLException e1) { }
			}
			if (st != null) {
				try { st.close(); } catch (SQLException e1) { }
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException e1) { }
			}
		}
	}
}
