/*
 * Created on 2004-7-2
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.csmc.pms.webapp.util;


import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.fa.gwy.TPServiceDelegator;
/**
 * @author Sky
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TPServiceHandle {
	public static final ThreadLocal threadLocal = new ThreadLocal();

	public static final String module = TPServiceHandle.class.getName();
	
	public static Map runTPService(GenericDelegator delegator,GenericValue userLogin,Map map,String serviceName) throws TPServiceException{
		map.put(GeneralEvents.USER_NAME,userLogin.get("userLoginId"));
		map.put(GeneralEvents.PASSWORD,userLogin.get("currentPassword"));	
		Map result = runTPService(delegator,userLogin,map,serviceName,false);
		return result;
	}
		
	public static Map runTPService(GenericDelegator delegator,GenericValue userLogin,Map map,String serviceName,boolean markTransFlag)throws TPServiceException{
		GenericValue transactionLog = null;
		if(markTransFlag){
		  transactionLog = transBegin(delegator,userLogin,serviceName,(map.get("transComment")!=null?map.get("transComment").toString():""));
		}
		Map result = null;	
		try{	
			TPServiceDelegator tpDelegator = TPServiceDelegator.getInstance();
			result = tpDelegator.runTPService(serviceName,map);	
			if (result.get(Constants.TP_ERROR_MESSAGE) != null) {
				throw new TPServiceException(result.get(Constants.TP_ERROR_MESSAGE).toString().substring(result.get(Constants.TP_ERROR_MESSAGE).toString().lastIndexOf(":")));
			}			
			if(markTransFlag){
				transEnd(delegator,transactionLog,false);
			}
		} catch (TPServiceException e) {
			throw e;
		}	catch (Exception e) {
			Debug.logError(e,module);
			throw new TPServiceException(e);		
		}		
		return result;	
	
	}
	
	private static GenericValue makeTransLog(GenericDelegator delegator,GenericValue userLogin,String transName,String transComment){
		GenericValue transactionLog = delegator.makeValue("TPTransLog", null);
		if(threadLocal.get()!=null){
			transactionLog.set("transSeq", threadLocal.get().toString());
		}else{
			transactionLog.set("transSeq", "1");
		}
		
//		if(map.get("transCat")!=null)
//		transactionLog.set("transCat", map.get("transCat"));
//		if(map.get("transObject")!=null)
//		transactionLog.set("transObject", map.get("transObject"));
		if(transComment!=null)
		transactionLog.set("transComment", transComment);
		//add client Ip here
//		if(map.get("clientIp")!=null)
//		transactionLog.set("clientIp", map.get("clientIp"));
//		if(map.get("transName")!=null)					
		transactionLog.set("transName", transName);
//		if(map.get("userLoginId")!=null)
		transactionLog.set("userLoginId", userLogin.get("userLoginId"));
//		if(map.get("statusId")!=null)
//		transactionLog.set("statusId", map.get("statusId"));
//		if(map.get("requestUrl")!=null)
//		transactionLog.set("requestUrl", map.get("requestUrl"));
//		if(map.get("referrerUrl")!=null)
//		transactionLog.set("referrerUrl", map.get("referrerUrl"));
//		if(map.get("serverIpAddress")!=null)
//		transactionLog.set("serverIpAddress", map.get("serverIpAddress"));
//		if(map.get("serverHostName")!=null)
//		transactionLog.set("serverHostName", map.get("serverHostName"));
		return transactionLog;
	}
	private static void transEnd(GenericDelegator delegator,GenericValue transactionLog,boolean flag){
		if(flag){
			transactionLog = null;
		}else{		
			if(transactionLog!=null && delegator!=null){
				Long nextId;		
				try {
					nextId = delegator.getNextSeqId("TPTransLog");
					transactionLog.set("endDateTime",new Timestamp( new Date().getTime()));
					transactionLog.set("runningTimeMillis",new Long(((Timestamp)transactionLog.get("endDateTime")).getTime()-((Timestamp)transactionLog.get("startDateTime")).getTime()));													
					if (nextId == null) {
						Debug.logError("Not persisting transactionLog, could not get next seq id", module);
					}else{
						transactionLog.set("transId", nextId.toString());
						transactionLog.create();
					}	
					transactionLog=null;				
				} catch (GenericEntityException e) {
					Debug.logError(e, "Could not create transactionLog ", module);	 
				} 
			}
		}	
	}
		private static  GenericValue transBegin(GenericDelegator delegator,GenericValue userLogin,String transName,String transComment){
			GenericValue transactionLog = makeTransLog(delegator,userLogin,transName,transComment);
			transactionLog.set("startDateTime",new Timestamp( new Date().getTime()));	
			return transactionLog;
		/*	if(this.transactionLog!=null){
				transactionLog.set("startDateTime",new Timestamp( new Date().getTime()));
				if(withTransCtl){				
					try {
						UserTransaction ut = TransactionFactory.getUserTransaction();
						if (ut.getStatus() != TransactionUtil.STATUS_NO_TRANSACTION) {
							ut.rollback();
							if (Debug.verboseOn())Debug.log("[TransUtil.begin] fore transaction did not commit , roll back it now", module);							
						}
						ut.begin();
						beganTransaction=true;
						if (Debug.verboseOn())Debug.log("[TransUtil.begin] transaction begun", module);					
					} catch (NotSupportedException e) {
						//This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
						Debug.logError("error with begin transaction:", module);
					} catch (SystemException e) {
						//This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
						Debug.logError("error with begin transaction:", module);
					} 
				}
			}else{
				Debug.logError("error with transactionLog:", module);
			}   */                           
		}   
		/**
		*transBegin()应在事务处理的起始点运行
		*/
//		public void transBegin(){
//			this.transBegin(false);                            
//		}   
}
