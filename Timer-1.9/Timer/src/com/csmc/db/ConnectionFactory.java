package com.csmc.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;


public class ConnectionFactory {

    public static final String module = ConnectionFactory.class.getName();

    public static synchronized Connection getConnection(String dsName) throws SQLException {
        DataSource dataSource = DataSourceFactory.getDataSource(dsName);

        if (dataSource != null) {
            return dataSource.getConnection();
        }
		return null;
    }
}
