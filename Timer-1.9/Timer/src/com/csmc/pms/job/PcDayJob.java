package com.csmc.pms.job;

import java.sql.Timestamp;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.csmc.pms.persistence.model.PeriodSchedule;
import com.csmc.pms.service.PmsFacade;
import com.csmc.util.Constants;
import com.csmc.util.Log;
import com.csmc.util.mail.MailManager;

public class PcDayJob {
    public static final String module = PcDayJob.class.getName();

    public void start() {
        FileSystemXmlApplicationContext ctx = null;
        
        Log.logDebug("pms-Pc start[" + new Timestamp(System.currentTimeMillis()) + "]", module);
        
        try {
            ctx = new FileSystemXmlApplicationContext(new String[] {
			        Constants.PMS_CONFIG_DIR,
                    Constants.MAIL_CONFIG_DIR
            });
            
            PmsFacade pmsService = (PmsFacade)ctx.getBean("pms");
            MailManager mailManager = (MailManager)ctx.getBean("mailManager");
            pmsService.setMailManager(mailManager);

            Log.logInfo("Start Service--------------", module);
            //PeriodSchedule periodSchedule =new PeriodSchedule();periodSchedule.getPcStyleName()
            pmsService.invokeDayUnfinishPcService("");//"安全巡检",PmsSupport需修改收件人
            Log.logInfo("End Service--------------", module);
            
        } catch(Exception e) {
            e.printStackTrace();
            Log.logError("pms-Pc error[" + e.getMessage() + "]", module);
        } finally {
            ctx.close();
            Log.logDebug("pms-Pc end[" + new Timestamp(System.currentTimeMillis()) + "]", module);
        }
    }
    
    public static void main(String[] args) {
    	PcDayJob job = new PcDayJob();
        job.start();
    }
}
