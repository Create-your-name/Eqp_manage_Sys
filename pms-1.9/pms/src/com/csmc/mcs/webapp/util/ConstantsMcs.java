package com.csmc.mcs.webapp.util;

/**
 * ��̬������ ConstantsMcs.java
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
	
	//	������ʷ��״̬
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
	
	// ������
	public static final String CHEMICAL = "100013";		//��ѧ��Ʒ
	public static final String GAS = "100014";			//����
	public static final String TARGET = "100015";		//�в�
	public static final String DEVELOPER = "100017";	//��ӰҺ
	public static final String QUARTZ = "100018";		//ʯӢ
	public static final String PHOTORESIST = "100020";  //��̽�
	public static final String SPAREPART_2P = "20002P";  //����
	public static final String SPAREPART_2S = "20002S";  //�׺ı���
	public static final String SPAREPART_2T = "20002T";  //���Ϻű���
	
	// ����״̬��
	//public static final String STO_REQ = "STO-REQ";
	//public static final String STO_FROZEN = "STO-FROZEN";
	public static final String CABINET_NEW = "CABINET-NEW";						// �ݴ���: �½��ݴ�
	public static final String CABINET_RECYCLE = "CABINET-RECYCLE";				// �ݴ���: �Ż��ݴ�
	public static final String NEW_BACK_STOCK = "NEW-BACK-STOCK";				// �����ݴ��˿�
	public static final String USING = "USING";									// ʹ����
	public static final String FAB_REPAIR = "FAB-REPAIR";						// �ڲ�ά��
	public static final String FINISH = "FINISH";								// ����
	public static final String VENDOR_REPAIR_OPT = "VENDOR-REPAIR-OPT";			// ����ά��,�����ά��
	public static final String VENDOR_REPAIR_LEADER = "VENDOR-REPAIR-LEADER";	// ����ά��,����ά����
	public static final String VENDOR_REPAIR_SHIP = "VENDOR-REPAIR-SHIP";
	public static final String ONLINE_SCRAP_OPT = "ONLINE-SCRAP-OPT";			// ���߱���
	public static final String ONLINE_SCRAP_LEADER = "ONLINE-SCRAP-LEADER";
	public static final String GENERAL_SCRAP_OPT = "GENERAL-SCRAP-OPT";			// ��ⱨ��
	public static final String GENERAL_SCRAP_LEADER = "GENERAL-SCRAP-LEADER";
	public static final String GENERAL_SCRAP_SHIP = "GENERAL-SCRAP-SHIP";
	
	public static final String FAB_WASH = "FAB-WASH";							// ������ϴ
	public static final String VENDOR_WASH_OPT = "VENDOR-WASH-OPT";				// �������ϴ
	public static final String VENDOR_WASH_LEADER = "VENDOR-WASH-LEADER";		// ������ϴ��
	public static final String CABINET_RECYCLE_WASH = "CABINET-RECYCLE-WASH";	// ����ϴ
	public static final String CABINET_RECYCLE_REPAIR = "CABINET-RECYCLE-REPAIR";		// ��ά��
	
	public static final String CABINET = "CABINET";								// �ݴ���
	public static final String USE_AND_FINISH = "USE-AND-FINISH";				// �ݴ�����ѧƷֱ��ʹ�ò�����
	public static final String OFF_AND_USE = "OFF-AND-USE";						// barcode���º�ʹ��
	public static final String SCRAP = "SCRAP";									// ��ⱨ�� or ���߱���
	
	public static final String MSG_UN_SELECT = "δѡ��������ϣ����������б��й�ѡ��";
	public static final String SOG_PREFIX = "134000607";
	
	public static final String SEPARATOR = "~!~";
	
	public static final String PHOTORESIST_55CP = "20100063000";        //��̽� MCPRI7010N-55CP
	public static final String PHOTORESIST_7510 = "20100061502";        //��̽� Durimide 7510
	public static final String PHOTORESIST_RZJ50CP = "20100061410";     //���Թ�̽� RZJ-390PG-50CP
	
	//�������첿��ʹ�ò��Ų�ѯ�豸����̽���ѯ��̲��豸
	public static final String DEPT_INDEX_PP = "10001";
	public static final String DEPT_PD = "�������첿";
	public static final String DEPT_PF = "��ʩ��";
}
