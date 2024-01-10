package com.csmc.pms.job;

import java.sql.Timestamp;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.csmc.pms.service.PmsFacade;
import com.csmc.util.Constants;
import com.csmc.util.Log;
import com.csmc.util.mail.MailManager;


public class DelayPmDayJob {
    public static final String module = DelayPmDayJob.class.getName();

    public void start() {
        FileSystemXmlApplicationContext ctx = null;
        
        Log.logDebug("pms-Pm start[" + new Timestamp(System.currentTimeMillis()) + "]", module);
        
        try {
            ctx = new FileSystemXmlApplicationContext(new String[] {
			        Constants.PMS_CONFIG_DIR,
                    Constants.MAIL_CONFIG_DIR
            });
            
            PmsFacade pmsService = (PmsFacade) ctx.getBean("pms");
            MailManager mailManager = (MailManager) ctx.getBean("mailManager");
            //MailManager mailManager = (MailManager) ctx.getBean("simpleMailManager");
            pmsService.setMailManager(mailManager);

            Log.logInfo("Start Service--------------", module);
            pmsService.invokeDelayPmDayService();
            Log.logInfo("End Service--------------", module);
            
        } catch(Exception e) {
            e.printStackTrace();
            Log.logError("pms-Pm error[" + e.getMessage() + "]", module);
        } finally {
            ctx.close();
            Log.logDebug("pms-Pm end[" + new Timestamp(System.currentTimeMillis()) + "]", module);
        }
    }
    
    public static void main(String[] args) {
    	DelayPmDayJob job = new DelayPmDayJob();
        job.start();
    }
}
