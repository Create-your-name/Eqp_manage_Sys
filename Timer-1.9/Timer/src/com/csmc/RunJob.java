/*
 * 创建日期 2006-9-6
 *
 * @author laol
 */
package com.csmc;

import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RunJob {
    
	public static void main(String[] args) {
		FileSystemXmlApplicationContext ctx = null;
		
        try {
        	ctx = new FileSystemXmlApplicationContext("config/quartz-config.xml");
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
}
