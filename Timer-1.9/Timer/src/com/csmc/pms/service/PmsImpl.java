package com.csmc.pms.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.csmc.pms.persistence.iface.AccountDao;
import com.csmc.pms.persistence.iface.EquipmentScheduleDao;
import com.csmc.pms.persistence.iface.PeriodScheduleDao;
import com.csmc.pms.service.support.PmsSupport;
import com.csmc.util.Log;
import com.csmc.util.mail.MailManager;
import com.csmc.util.mail.MimeMailManager;

public class PmsImpl extends PmsSupport implements PmsFacade {
	public final static String module = PmsImpl.class.getName();
	
	private PeriodScheduleDao periodScheduleDao;
    private EquipmentScheduleDao equipmentScheduleDao;
    private AccountDao accountDao;
    private MailManager mailManager;
    private MimeMailManager mimeMailManager;
    
    public void setPeriodScheduleDao(PeriodScheduleDao periodScheduleDao) {
        this.periodScheduleDao = periodScheduleDao;
    }
    
    public void setEquipmentScheduleDao(EquipmentScheduleDao equipmentScheduleDao) {
        this.equipmentScheduleDao = equipmentScheduleDao;
    }
    
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    
    public void setMailManager(MailManager mailManager) {
    	this.mailManager = mailManager;
    }

    public void setMimeMailManager(MimeMailManager mimeMailManager) {
        this.mimeMailManager = mimeMailManager;
    }
 
    /**
     * Pc Day Service
     */
    public void invokeDayUnfinishPcService(String pcStyleName) {
        Log.logInfo("invokeDayUnfinishPcService--------------" + pcStyleName, module);			
        
        try {
			List unFinishedPc = periodScheduleDao.getDayUnFinishedPc(pcStyleName);
			checkUnFinishedPc(mailManager,pcStyleName,unFinishedPc);       
        } catch (DataAccessException e) {
			e.printStackTrace();
			Log.logError("invokeDayUnfinishPcService["	+ e.toString() + "]", module);	
		}
	}
    
    /**
     * Pc Week Service
     */
    public void invokeWeekUnfinishPcService(String pcStyleName) {
        Log.logInfo("invokeWeekUnfinishPcService--------------" + pcStyleName, module);			
        
        try {
			List unFinishedPc = periodScheduleDao.getWeekUnFinishedPc(pcStyleName);
			checkUnFinishedPc(mailManager, pcStyleName, unFinishedPc);       
        } catch (DataAccessException e) {
			e.printStackTrace();
			Log.logError("invokeWeekUnfinishPcService["	+ e.toString() + "]", module);	
		}
	}
    
    /**
     * Pm Service
     */
    public void invokePmDayService() {
        Log.logInfo("invokePmDayService--------------", module);			
        
        try {
			List unFinishedPm = equipmentScheduleDao.getDayUnFinishedPm();
			checkUnFinishedPm(mailManager, unFinishedPm, accountDao);       
        } catch (DataAccessException e) {
			e.printStackTrace();
			Log.logError("invokePmDayService["	+ e.toString() + "]", module);	
		}
	}

	public void invokeUnscheduleParamOosService() {
		try {
//			List paramlist = this.queryOutOfMaxUnscheduleParam();
//			if(paramlist.size() > 0) {
//				//修改设备状态
//			}
			this.checkOutOfMaxUnscheduleParam(this.mailManager,this.accountDao);
		} catch(Exception e) {
			
		}
		
	}
	
	/**
     * Delay Pm Service
     * 每天9点报警,修改设备状态
     */
    public void invokeDelayPmDayService() {
        Log.logInfo("invokeDelayPmDayService--------------", module);			
       	checkDelayPM(mailManager, accountDao);        
	}
    
    /**
     * 当日未作保养提醒
     */
    public void invokeForecastPmDayService() {
    	Log.logInfo("invokeForecastPmDayService--------------", module);
    	checkUndoTodayPm(mailManager, accountDao);
    }
    
    /**
     * 领用超过一个月提醒
     */
    public void invokeRequisitionOverdueService() {
    	Log.logInfo("invokeRequisitionOverdueService--------------", module);
    	checkRequisitionOverdue(mailManager);
    }

    /**
    * 关键备件使用期限预警
    */
    public void KeyPartsUseAlarm()
    {
        Log.logInfo("KeyPartsUseAlarmService--------------", module);
            checkKeyPartsUse(mimeMailManager, accountDao);
    }
}
