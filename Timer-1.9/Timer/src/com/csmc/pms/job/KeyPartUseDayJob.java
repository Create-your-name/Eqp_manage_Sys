package com.csmc.pms.job;

import java.sql.Timestamp;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.csmc.pms.service.PmsFacade;
import com.csmc.util.Constants;
import com.csmc.util.Log;
import com.csmc.util.mail.MimeMailManager;


public class KeyPartUseDayJob {
    public static final String module = KeyPartUseDayJob.class.getName();

    public void start() {
        FileSystemXmlApplicationContext ctx = null;
        
        Log.logDebug("KeyPartUse start[" + new Timestamp(System.currentTimeMillis()) + "]", module);
        
        try {
            ctx = new FileSystemXmlApplicationContext(new String[] {
			        Constants.PMS_CONFIG_DIR,
                    Constants.MAIL_CONFIG_DIR
            });
            
            System.setProperty("logconfig", "config/log-config.xml");
            
            PmsFacade pmsService = (PmsFacade) ctx.getBean("pms");
            MimeMailManager mimeMailManager = (MimeMailManager) ctx.getBean("mimeMailManager");
            pmsService.setMimeMailManager(mimeMailManager);

            Log.logInfo("Start Service--------------", module);
            pmsService.KeyPartsUseAlarm();
            Log.logInfo("End Service--------------", module);
            
        } catch(Exception e) {
            e.printStackTrace();
            Log.logError("KeyPartUse error[" + e.getMessage() + "]", module);
        } finally {
            ctx.close();
            Log.logDebug("KeyPartUse end[" + new Timestamp(System.currentTimeMillis()) + "]", module);
        }
    }
    
    public static void main(String[] args) {
    	KeyPartUseDayJob job = new KeyPartUseDayJob();
        job.start();
    }
}
