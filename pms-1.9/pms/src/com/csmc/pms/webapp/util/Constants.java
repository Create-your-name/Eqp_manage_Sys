package com.csmc.pms.webapp.util;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;

/**
 * 静态常量类
 * @author dinghh
 * @2007-8-20
 */
public interface Constants {
    public static final String PLANT = "1000";
    //线边仓表单，查询月份
	public static final String MONTH_START = "2014-11";

	//保养
	public static final String PM = "PM";
	//巡检
	public static final String PC = "PC";
	//异常
	public static final String TS = "TS";

	//MSA 相关的质量保证部
	public static final String MSA = "MSA";
	public static final String DEPT_QC_INDEX = "10006";
	public static final String DEPT_QC = "质量保证部";

	public static final String DEPT_PF = "设施部";
	public static final String[] DEPTS = new String[] {"工程一部", "工程二部", "生产制造部", "质量保证部", "动力保障部"};

	//PMS_REASON REASON_TYPE
	public static final String ABNORMAL="ABNORMAL";
	public static final String OVERTIME="OVERTIME";

	// EQUIPMENT_BASIC_DATA TYPE
	public static final String MODEL="MODEL";

	//EQUIPMENT_VENDOR设备供应商，维护商
	public static final String CLEAN="CLEAN";

	//保养、巡检、异常表单和文档建立的几个状态
	public static final int CREAT = -1;
	public static final int START = 0;
	public static final int HOLD = 2;
	public static final int OVER = 1;
	public static final int BIND = 3;

	//保存历史的状态
	public static final String INSERT = "INT";
	public static final String UPDATE = "UPT";
	public static final String DELETE = "DEL";

	//问题追踪中的状态
	public static final int FOLLOWJOB_CREAT = 0;
	public static final int FOLLOWJOB_OVER = 1;
	public static final int FOLLOWJOB_NOT_OVER = 2;

	//contentType
	public static final String CONTENT_TYPE = "application/x-java-serilized-object;charset=GB2312";

	//上传文档类型
	public static final int DOC_FOLLOW = 1;
	public static final int DOC_ABNORMAL = 2;
	public static final int DOC_IMPROVER = 3;

	//表单，文档Char
	public static final String PM_CHAR = "PM"; //保养表单
	public static final String PC_CHAR = "PC"; //巡检表单
	public static final String TS_CHAR = "ER"; //异常表单

	public static final String ABNORMAL_DOC_CHAR = "TR"; //异常报告书
	public static final String IMPROVE_DOC_CHAR = "IMP"; //改善报告书

	//使用
	public static final int ENABLE = 1;
	public static final int DISABLE = 0;

	//冻结
	public static final int FROZEN = 1;
	public static final int UNFROZEN = 0;

	//表单状态
	public static final String FORM_TYPE_NORMAL="NORMAL";
	public static final String FORM_TYPE_PATCH="PATCH";

	//动作项目类型
	public static final int TEXT_ITEM = 1;
	public static final int NUMBER_ITEM = 2;
	public static final int OPTION_ITEM = 3;

	//动作类型
	public static final String ACTION_DCOP_TYPE = "DCOP";
	public static final String ACTION_NORMAL_TYPE = "NORMAL";

	//流程状态
	public static final int JOB_START = 0;
	public static final int JOB_PROCESS = 2;
	public static final int JOB_END = 1;

    public static final String Y = "Y";
    public static final String N = "N";

    //签核对象
    public static final String SUBMIT_FLOW = "FLW";
    public static final String SUBMIT_FLOW_ACTION_ITEM = "FAI";
    public static final String SUBMIT_FOLLOW = "FUW";
    public static final String SUBMIT_PM_DELAY = "PMDELAY";
    public static final String SUBMIT_PMFORM = "PMFORM";
    public static final String SUBMIT_TSFORM = "TSFORM";
    public static final String SUBMIT_PM_PARTS_USE = "PM_PARTS_USE";
    public static final String SUBMIT_TS_PARTS_USE = "TS_PARTS_USE";

    //签核状况
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String SUBMITED = "SUBMITED";
    public static final int SAVED_CODE = 0;
    public static final int APPROVED_CODE = 1;
    public static final int SUBMITED_CODE = 2;
	public static final int REJECTED_CODE = 3;

    //送签类型
    public static final String SUBMIT_INSERT = "INSERT";
    public static final String SUBMIT_MODIFY = "MODIFY";
    public static final String SUBMIT_DELETE = "DELETE";
    public static final String SUBMIT_PATCH = "PATCH";

    //PM TS送签修改状态
    public static final String FORM_STATUS_LOCKED = "LOCKED";
    public static final String FORM_STATUS_SUBMITED = "SUBMITED";

    //超级用户
    public static final String SUPER_USER = "IT_SUPER";
    public static final String PASSWORD = "CSMCSUPER";

    //abnormal status
    //public static final String TS_STATUS = "03_DOWN";
    public static final String TS_STATUS = "03-DOWN";
    //abnormal start status
    //public static final String TS_START_STATUS = "03_REPAIR";
    public static final String TS_START_STATUS = "03-REP";
    //abnormal end status
    //public static final String TS_END_STATUS = "03_MON_DOWN";
    public static final String TS_END_STATUS = "03-POST";
    //pm end status
    public static final String PM_END_STATUS = "04_MON_PM";
//    public static final String PM_END_STATUS = "04MONPM";
    // 05-PR start status
    public static final String PR_START_STATUS = "";
    // 05-PR end status
    public static final String PR_END_STATUS = "";

    //pm start status check
    //public static final String PM_START_STATUS = "02_IDLE";
    //pm status
    //public static final String PM_STATUS = "04_PM";

    //oracleException
    public static final String[] ORA_EXCEPTION = new String[]{"ORA-00001"};
    public static final String[] ORA_EXCEPTION_MESSAGE = new String[]{"记录已经存在，无法新增！"};

    // security
    public static Map GUI_PROMIS_PRIV = new HashMap(UtilMisc.toMap(
			"guiprivmap", new HashMap(), "promisprivlist", null,
			"promisLocalPrivs", null, "guiprivflagmap", new HashMap()));
	public static Map onlineSessions = new HashMap();

    //promis command
	public static final boolean CALL_TP_FLAG = false;//fab1:true, fab5:false
	public static final boolean CALL_ASURA_FLAG = true;//fab1:false, fab5:true
    public static final String EQP_INFO_QUERY = "eqpInfoQuery";
    public static final String EQP_STATUS_CHANGE = "eqpStatusChange";
    public static final String LOGIN_CHECK_PRIV = "loginCheckPrivilege";
    public static final String ACCOUNT_INFO_QUERY = "accountInfoQuery";
    public static final String ACCOUNT_DELETE = "accountDelete";
    public static final String CHANGE_PASSWORD = "userChangePassword";
    public static final String ACCOUNT_UPDATE = "accountUpdate";
    public static final String ACCOUNT_LIST_QUERY = "accountListQuery";
	public static final String TP_ERROR_MESSAGE = "TP_ERROR";
	public static final String DCOP_FORMAT_QUERY = "dcopFormatQuery";
	public static final String ENTER_LOC_DCOP_COMP = "enterGenDcopComp";
	public static final String ENTER_LOC_DCOP_ITEM = "enterGenDcopItem";
	public static final String ENTER_LOC_DCOP_SITE = "enterGenDcopSite";
	public static final String LOGIN_CHECK = "login";

	//DCOP
	public static final String CHTQUERY_READCHART = "chartInfoQuery";
    public static final String[] ALARM_STATUS = new String[] {"OPEN", "PENDING", "CLOSED"};

	// Part 查询类型
    public static final String EQUIPMENT_TYPE = "EQUIPMENT_TYPE";
    public static final String DEPT = "DEPT";

    // 最大日保养日期
	public static final String MAX_DAYPM_DEFAULT_DAYS = "2";

    // mail config
    public static final String IT_SYSTEM_MAIL = "crmic_nc_PMS_zy@rxgz.crmicro.com";
    public static final String ADMIN_MAIL = "liuhai82@rxgz.crmicro.com";
    public static final String QC_MAIL = "malixue@rxgz.crmicro.com";
    public static final String THINFILM_PROCESS_MAIL = "lisk@csmc.crmicro.com";

    // fab5 PM MCS interface
    public static final String CHANGE_TARGET_PERIOD_PREFIX = "不定期PM换靶";
}
