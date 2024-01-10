/*
 * 创建日期 2006-8-22
 *
 * @author laol
 */
package com.csmc.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class Log {

	public static final int INFO = 0;
	
	public static final int ERROR = 1;
	
	public static final int DEBUG = 2;
	
	public static final Level[] levels = {Level.INFO, Level.FATAL, Level.DEBUG};
	
	public static void logInfo(String msg, String module) {
		log(INFO, null, msg, module);
	}
	
	public static void logDebug(String msg, String module) {
		log(DEBUG, null, msg, module);
	}
	
	public static void logError(String msg, String module) {
		log(ERROR, null, msg, module);
	}
	
	public static void log(int level, Throwable t, String msg, String module) {
        log(level, t, msg, module, "com.csmc.util.Log");
    }
	
	public static void log(int level, Throwable t, String msg, String module, String callingClass) {
		Logger logger =getLogger(module);
		logger.log(callingClass, levels[level], msg, t);  
	}
	
	public static Logger getLogger(String name) {
		return Logger.getLogger(name);
	}
	
	static {
		 String logFile = System.getProperty("log_config");
		 
		 if(logFile == null) logFile = "config/log-config.xml";

		 if(logFile != null) DOMConfigurator.configure(logFile);
	}
	
}
