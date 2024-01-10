package com.csmc.mcs.webapp.util;

/**
 * 静态常量类 ConstantsMcs.java
 * @version  1.0  2009-7-1
 * @author dinghh
 */
public interface ConstantsMcs {
	
	// Integer(short-numeric) Type Flag: enabled,inControl
	public static final Integer INTEGER_1 = new Integer(1);
	public static final Integer INTEGER_0 = new Integer(0);
	
	// String(indicator) Type Flag: needScrapStore, needVendorRecycle, useBySuit, activeFlag 
	public static final String Y = "Y";
	public static final String N = "N";
	
	//	保存历史的状态
	public static final String EVT_INSERT = "INT";
	public static final String EVT_UPDATE = "UPT";
	public static final String EVT_DELETE = "DEL";
	
	// Seq Names of Table's SequenceValueItem
	public static final String MATERIAL_INDEX = "materialIndex";
	public static final String MATERIAL_HIST_INDEX = "materialHistIndex";
	public static final String MTR_OBJECT_INDEX = "mtrObjectIndex";
	public static final String MTR_OBJECT_HIST_INDEX = "mtrObjectHistIndex";
	public static final String MATERIAL_SUIT_INDEX = "materialSuitIndex";
	public static final String MATERIAL_SUIT_ITEM_INDEX = "materialSuitItemIndex";
	public static final String MATERIAL_STO_REQ_INDEX = "materialStoReqIndex";
	public static final String MATERIAL_STATUS_INDEX = "materialStatusIndex";
	public static final String MATERIAL_STATUS_HIST_INDEX = "materialStatusHistIndex";
	public static final String TRANSACTION_ID = "transactionId";
	
	// 物料组
	public static final String CHEMICAL = "100013";		//化学用品
	public static final String GAS = "100014";			//气体
	public static final String TARGET = "100015";		//靶材
	public static final String DEVELOPER = "100017";	//显影液
	public static final String QUARTZ = "100018";		//石英
	public static final String PHOTORESIST = "100020";  //光刻胶
	public static final String SPAREPART_2P = "20002P";  //备件
	public static final String SPAREPART_2S = "20002S";  //易耗备件
	public static final String SPAREPART_2T = "20002T";  //无料号备件
	
	// 物料状态码
	//public static final String STO_REQ = "STO-REQ";
	//public static final String STO_FROZEN = "STO-FROZEN";
	public static final String CABINET_NEW = "CABINET-NEW";						// 暂存区: 新进暂存
	public static final String CABINET_RECYCLE = "CABINET-RECYCLE";				// 暂存区: 放回暂存
	public static final String NEW_BACK_STOCK = "NEW-BACK-STOCK";				// 新领暂存退库
	public static final String USING = "USING";									// 使用中
	public static final String FAB_REPAIR = "FAB-REPAIR";						// 内部维修
	public static final String FINISH = "FINISH";								// 用完
	public static final String VENDOR_REPAIR_OPT = "VENDOR-REPAIR-OPT";			// 出厂维修,送外待维修
	public static final String VENDOR_REPAIR_LEADER = "VENDOR-REPAIR-LEADER";	// 出厂维修,送外维修中
	public static final String VENDOR_REPAIR_SHIP = "VENDOR-REPAIR-SHIP";
	public static final String ONLINE_SCRAP_OPT = "ONLINE-SCRAP-OPT";			// 在线报废
	public static final String ONLINE_SCRAP_LEADER = "ONLINE-SCRAP-LEADER";
	public static final String GENERAL_SCRAP_OPT = "GENERAL-SCRAP-OPT";			// 入库报废
	public static final String GENERAL_SCRAP_LEADER = "GENERAL-SCRAP-LEADER";
	public static final String GENERAL_SCRAP_SHIP = "GENERAL-SCRAP-SHIP";
	
	public static final String FAB_WASH = "FAB-WASH";							// 自主清洗
	public static final String VENDOR_WASH_OPT = "VENDOR-WASH-OPT";				// 送外待清洗
	public static final String VENDOR_WASH_LEADER = "VENDOR-WASH-LEADER";		// 送外清洗中
	public static final String CABINET_RECYCLE_WASH = "CABINET-RECYCLE-WASH";	// 已清洗
	public static final String CABINET_RECYCLE_REPAIR = "CABINET-RECYCLE-REPAIR";		// 已维修
	
	public static final String CABINET = "CABINET";								// 暂存区
	public static final String USE_AND_FINISH = "USE-AND-FINISH";				// 暂存区化学品直接使用并用完
	public static final String OFF_AND_USE = "OFF-AND-USE";						// barcode换下和使用
	public static final String SCRAP = "SCRAP";									// 入库报废 or 在线报废
	
	public static final String MSG_UN_SELECT = "未选择操作物料，请在物料列表中勾选！";
	public static final String SOG_PREFIX = "134000607";
	
	public static final String SEPARATOR = "~!~";
	
	public static final String PHOTORESIST_55CP = "20100063000";        //光刻胶 MCPRI7010N-55CP
	public static final String PHOTORESIST_7510 = "20100061502";        //光刻胶 Durimide 7510
	public static final String PHOTORESIST_RZJ50CP = "20100061410";     //正性光刻胶 RZJ-390PG-50CP
	
	//生产制造部按使用部门查询设备，光刻胶查询光刻部设备
	public static final String DEPT_INDEX_PP = "10001";
	public static final String DEPT_PD = "生产制造部";
	public static final String DEPT_PF = "设施部";
}
