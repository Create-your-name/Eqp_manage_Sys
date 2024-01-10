/** 	1.0  2004-8-23
  * 版权归上揚软件（上海）有限公司所有
  * 本程序属上揚软件的私有机要资料
  * 未经本公司授权，不得非法传播和盗用
  * 可在本公司授权范围内，使用本程序
  * 保留所有权利
  */
package com.csmc.pms.webapp.util;

/**
    *类 SessionNames.java 
    *@version  1.0  2004-8-23  
    *@author   Sky  
    */
public interface SessionNames {    
	public static final String CURRENT_SELECTED_LOTLIST_KEY="CURRENT_SELECTED_LOTLIST";
	public static final String CURRENT_AVAILABLEWAITING_LOTLIST_KEY="CURRENT_AVAILABLEWAITING_LOTLIST";
	public static final String CURRENT_MANUALDISPATCH_LOTLIST_KEY="CURRENT_MANUALDISPATCH_LOTLIST";	
	public static final String CURRENT_HOLDING_LOTLIST_KEY="CURRENT_HOLDING_LOTLIST";
	public static final String RUNNING_LOTLIST_KEY="RUNNING_LOTLIST";
	public static final String GUI_MSG_KEY="GUI_MSG";	
	public static final String ALARM_MSG_KEY="ALM_MSG";		
	public static final String OPT_MSG_KEY="OPT_MSG";	
	public static final String CURRENT_EQP_INFO_KEY="CURRENT_EQP_INFO_ID";		
	public static final String CURRENT_EQP_ID_KEY="CURRENT_EQP_ID";
	public static final String CURRENT_EQP_STATUS_KEY="CURRENT_EQP_STATUS";	
	public static final String SPLIT_LOT_ID_KEY="SPLIT_LOT_ID_KEY";
	public static final String IMMEDIATE_MERGE_HOLD_RESULT_ID_KEY="IMMEDIATE_MERGE_HOLD_RESULT_ID_KEY";		
	public static final String FUTUREACTION_RESULT_MAP_KEY="FUTUREACTION_RESULT_MAP";		
	public static final String DISPATCH_TYPE_KEY="DISPATCH_TYPE";		
	
	public static final String GUI_PRIV_LIST_KEY="GUI_PRIV_LIST";
	public static final String ACCOUNT_GROUP_SET_KEY="ACCOUNT_GROUP_SET";
	public static final String ACCOUNT_CATEGORY_SET_KEY="ACCOUNT_CATEGORY_SET";
	
	public static final String CURRENT_HOLDING_LOTINFOLIST_KEY="CURRENT_HOLDING_LOTINFOLIST_KEY";

	public static final String MERGELOT_ADD_CHILDLOT="MERGELOT_ADD_CHILDLOT";
	

	public static final String CURRENT_SELECTED_REAL_LOTLIST_KEY="CURRENT_SELECTED_REAL_LOTLIST";
	
	//Monitor Wafer Liaob
	public static final String CURRENT_SELECTED_LOTLIST_MONITOR_KEY="CURRENT_SELECTED_LOTLIST_MONITOR_KEY";
	
	public static final String CURRENT_BOTTOM_TYPE_KEY="CURRENT_BOTTOM_TYPE";
	
	public static final String EAP_MSG_KEY="EAP_MSG";
	public static final String EAP_ALARM_MSG_KEY="EAP_ALM_MSG";
	public static final String EAP_OPT_MSG_KEY="EAP_OPT_MSG";
	public static final String EAP_CURRENT_EQP_STATUS_KEY="EAP_CURRENT_EQP_STATUS";
	public static final String EAP_CURRENT_EQP_TYPE_KEY="EAP_CURRENT_EQP_TYPE";
	public static final String EAP_CURRENT_EQP_MONITORWAFEREQP_KEY="EAP_CURRENT_EQP_MONITORWAFEREQP";
	public static final String EAP_RECIPE_KEY = "EAP_RECIPE";
	public static final String SHIFT_EQP_INFO_LIST = "shiftEqpInfoList";
	public static final String SHIFT_EQPID_LIST = "shiftEqpIdList";	
	public static final String SHIFT_WAFER_INFO_LIST = "shiftWaferInfoList";
  	public static final String QC_DATA_NEW_VALUE = "QcDataNewValue";	
	public static final String ADD_MONITOR_FLAG="ADD_MONITOR_FLAG";		
}
