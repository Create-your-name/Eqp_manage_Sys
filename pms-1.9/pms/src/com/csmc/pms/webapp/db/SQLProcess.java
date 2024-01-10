/*
 * Created on 2004-7-14 
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.csmc.pms.webapp.db;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.*;
import java.sql.*;
import java.util.*;
   
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;

/**
 * @author Sky
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SQLProcess {
    public static final String module = SQLProcess.class.getName();
    
	private static Connection getConnection(String helperName)
		throws SQLException, GenericEntityException {
		return ConnectionFactory.getConnection(helperName);

	}
	
	public static int excuteSQLUpdate(String sql, GenericDelegator delegator)
		throws SQLProcessException {
		Iterator groups =
			UtilMisc.toIterator(
				delegator.getModelGroupReader().getGroupNames());
		String helperName =
			delegator.getGroupHelperName(groups.next().toString());
		Connection conn = null;
		Statement st = null;
		Debug.logInfo("SQL=[" + sql + "]",module);		
		try {
			conn = getConnection(helperName);
			st = conn.createStatement();
			int i = st.executeUpdate(sql);
			return i;
		} catch (GenericEntityException e) {
			throw new SQLProcessException(
				"SQL Exception while get the connection:" + sql,
				e);
		} catch (SQLException e) {
			throw new SQLProcessException(
				"SQL Exception while executing the following:" + sql,
				e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	//查询gui db
	public static List excuteSQLQueryGui(String sql, GenericDelegator delegator)
	        throws SQLProcessException {
	    String helperName = "localoracle";
	    Connection conn = null;
	    Statement st = null;
	    ResultSet rs = null;
	    Debug.logInfo("SQL=[" + sql + "]",module);
	    try {
	        conn = getConnection(helperName);
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
	    } catch (GenericEntityException e) {
	        e.printStackTrace();
	        throw new SQLProcessException(
	                "SQL Exception while get the connection:" + sql,
	                e);
	    } catch (SQLException e) {
	        e.printStackTrace();  
	        throw new SQLProcessException(
	                "SQL Exception while executing the following:" + sql,
	                e);
	    } finally {
	        if (rs != null) {
	            try {
	                rs.close();
	            } catch (SQLException e1) {
	                // TODO Auto-generated catch block
	                rs = null;
	                e1.printStackTrace();
	            }
	        }
	        if (st != null) {
	            try {
	                st.close();
	            } catch (SQLException e1) {
	                // TODO Auto-generated catch block
	                st = null;
	                e1.printStackTrace();
	            }
	        }
	        if (conn != null) {
	            try {
	                conn.close();
	            } catch (SQLException e1) {
	                // TODO Auto-generated catch block
	                conn = null;
	                e1.printStackTrace();
	            }
	        }
	    }
	}
	public static List excuteSQLQuery(String sql, GenericDelegator delegator)
		throws SQLProcessException {
		Iterator groups =
			UtilMisc.toIterator(
				delegator.getModelGroupReader().getGroupNames());
		String helperName =
		        delegator.getGroupHelperName(groups.next().toString());
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		Debug.logInfo("SQL=[" + sql + "]",module);
		try {
			conn = getConnection(helperName);
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
		} catch (GenericEntityException e) {
			e.printStackTrace();
			throw new SQLProcessException(
				"SQL Exception while get the connection:" + sql,
				e);
		} catch (SQLException e) {
			e.printStackTrace();  
			throw new SQLProcessException(
				"SQL Exception while executing the following:" + sql,
				e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					rs = null;
					e1.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					st = null;
					e1.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					conn = null;
					e1.printStackTrace();
				}
			}
		}
	}

	public static int excuteStoreProcudureUpdate(
		String sql,
		GenericDelegator delegator)
		throws SQLProcessException {
		Iterator groups =
			UtilMisc.toIterator(
				delegator.getModelGroupReader().getGroupNames());
		String helperName =
			delegator.getGroupHelperName(groups.next().toString());
		Connection conn = null;
		CallableStatement st = null;
		try {
			conn = getConnection(helperName);
			st = conn.prepareCall(sql);
			int i = st.executeUpdate(sql);

			return i;
		} catch (GenericEntityException e) {
			throw new SQLProcessException(
				"SQL Exception while get the connection:" + sql,
				e);
		} catch (SQLException e) {
			throw new SQLProcessException(
				"SQL Exception while executing the following:" + sql,
				e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static List excuteStoreProcudureQuery(
		String sql,
		GenericDelegator delegator)
		throws SQLProcessException {
		Iterator groups =
			UtilMisc.toIterator(
				delegator.getModelGroupReader().getGroupNames());
		String helperName =
			delegator.getGroupHelperName(groups.next().toString());
		Connection conn = null;
		CallableStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection(helperName);
			st = conn.prepareCall(sql);
			rs = st.executeQuery(sql);
			List list = new ArrayList();
			ResultSetMetaData rm = rs.getMetaData();

			while (rs.next()) {
				Map map = new HashMap();
				for (int i = 0; i < rm.getColumnCount(); i++) {
					map.put(rm.getColumnName(i), rs.getString(i));
				}
				list.add(map);
			}
			return list;
		} catch (GenericEntityException e) {
			throw new SQLProcessException(
				"SQL Exception while get the connection:" + sql,
				e);
		} catch (SQLException e) {
			throw new SQLProcessException(
				"SQL Exception while executing the following:" + sql,
				e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	private static Connection getConnectionByDriver(String connect_string) throws SQLException {
//		String connect_string ="jdbc:oracle:thin:csmc/csmc@192.1.88.5:1521:guip1";
//		String connect_string ="jdbc:oracle:thin:csmc/csmc@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.1.88.5)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.1.88.6)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=guip)(failover_mode=(type=select)))(FAILOVER=on)(LOAD_BALANCE=on)) ";
//		String connect_string = "jdbc:oracle:thin:mask_new/masknewpw@192.1.1.210:1521:dbre";

		// Load the JDBC driver
		DriverManager.registerDriver(new oracle.jdbc.OracleDriver());  
		Connection conn = DriverManager.getConnection(connect_string);
		return conn;
	}
	
	public static int excuteSQLUpdate(String sql,String connectionStr)
		throws SQLException {
		Connection conn = null;
		Statement st = null;
		try {
			conn = getConnectionByDriver(connectionStr);
			st = conn.createStatement();
			int i = st.executeUpdate(sql);
			return i;
		}  finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static List excuteSQLQuery(String sql, String connectionStr)
	throws SQLException {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = getConnectionByDriver(connectionStr);
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
				try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	//非service操作增加commit语句
	public static int excuteSQLUpdateForQc(String sql, GenericDelegator delegator)
		throws SQLProcessException {
		Iterator groups =
			UtilMisc.toIterator(
				delegator.getModelGroupReader().getGroupNames());
		String helperName =
			delegator.getGroupHelperName(groups.next().toString());
		Connection conn = null;
		Statement st = null;
		Debug.logInfo("SQL=[" + sql + "]",module);		
		try {
			conn = getConnection(helperName);
			conn.setAutoCommit(false);
			st = conn.createStatement();
			int i = st.executeUpdate(sql);
			conn.commit();
			return i;
		} catch (GenericEntityException e) {
			throw new SQLProcessException(
				"SQL Exception while get the connection:" + sql,
				e);
		} catch (SQLException e) {
			throw new SQLProcessException(
				"SQL Exception while executing the following:" + sql,
				e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}	
	
	public static List excuteSQLQueryByDsName(String sql, String helperName)
		throws SQLProcessException {

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
//		Debug.logInfo("SQL=[" + sql + "]",module);
		try {
			conn = getConnection(helperName);
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
		} catch (GenericEntityException e) {
			e.printStackTrace();
			throw new SQLProcessException(
				"SQL Exception while get the connection:" + sql,
				e);
		} catch (SQLException e) {
			e.printStackTrace();  
			throw new SQLProcessException(
				"SQL Exception while executing the following:" + sql,
				e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}
