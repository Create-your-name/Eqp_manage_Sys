package com.csmc.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.csmc.util.Log;

public class DataSourceFactory{

	public static final String module = DataSourceFactory.class.getName();
	
    private static Map dsCache = new HashMap();
    
    public static String PMS_DATASOURCE = "pms";
    
    private static FileSystemXmlApplicationContext ctx = 
        new FileSystemXmlApplicationContext("config/datasource-config.xml");

    public void init() {
        if(dsCache.isEmpty()) {
        	try {

                GenericDataSource pmsds = (GenericDataSource) ctx.getBean("pmsDataSource");
                
                dsCache.put(PMS_DATASOURCE, pmsds);
                    
        	} catch (Exception e) {
        	    Log.logError(e.getMessage(), module);
        	}
        	finally {
        		ctx.close();
        	}
        }
    }
    
	public static synchronized DataSource getDataSource(String name) {
        DataSource ds = (DataSource)dsCache.get(name);
        if(ds == null) ds = (DataSource)ctx.getBean(name + "DataSource");
		return ds;
	}
    
    public void shutdownDataSource() {

        GenericDataSource pms = (GenericDataSource)dsCache.get(PMS_DATASOURCE);
        
        if(pms != null) {
            try {
            	pms.close();
                dsCache.remove(PMS_DATASOURCE);
            } catch (SQLException e) {
                Log.logError(e.getMessage(), module);
            }
        }
    }
    
}
