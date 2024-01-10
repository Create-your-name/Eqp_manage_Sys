package com.csmc.pms.job;

import java.sql.Timestamp;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.csmc.pms.service.PmsFacade;
import com.csmc.util.Constants;
import com.csmc.util.Log;
import com.csmc.util.mail.MailManager;

public class RequisitionOverdueJob {
	public static final String module = RequisitionOverdueJob.class.getName();

    public void start() {
        FileSystemXmlApplicationContext ctx = null;
        
        Log.logDebug("pms-Overdue start[" + new Timestamp(System.currentTimeMillis()) + "]", module);
        
        try {
            ctx = new FileSystemXmlApplicationContext(new String[] {
			        Constants.PMS_CONFIG_DIR,
                    Constants.MAIL_CONFIG_DIR
            });
            
            PmsFacade pmsService = (PmsFacade) ctx.getBean("pms");
            MailManager mailManager = (MailManager) ctx.getBean("mailManager");
            pmsService.setMailManager(mailManager);

            pmsService.invokeRequisitionOverdueService();
            
        } catch(Exception e) {
            e.printStackTrace();
            Log.logError("pms-Overdue error[" + e.getMessage() + "]", module);
        } finally {
            ctx.close();
            Log.logDebug("pms-Overdue end[" + new Timestamp(System.currentTimeMillis()) + "]", module);
        }
    }
    
    public static void main(String[] args) {
    	RequisitionOverdueJob job = new RequisitionOverdueJob();
        job.start();
    }
}
