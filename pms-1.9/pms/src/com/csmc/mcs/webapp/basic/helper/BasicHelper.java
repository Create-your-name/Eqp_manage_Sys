package com.csmc.mcs.webapp.basic.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;

/**
 * 类 BasicHelper.java 
 * @version  1.0  2009-7-22
 * @author   jiangjing
 */
public class BasicHelper {

    public static final String module = BasicHelper.class.getName();
    
	/**
     * 按条件查询物料基本信息
     * @param delegator
     * @param paramMap
	 * @throws SQLProcessException 
     */
	public static List getMaterialInfoList(GenericDelegator delegator,Map paramMap) throws SQLProcessException{
		String mtrGrp = (String) paramMap.get("mtrGrp");	
        String deptIndex = (String) paramMap.get("deptIndex");
        String enabled = (String) paramMap.get("enabled");
        String inControl = (String) paramMap.get("inControl");   
		String mtrNum = (String) paramMap.get("mtrNum");
		
		StringBuffer sql = new StringBuffer();
		sql.append("select t.* from Mcs_Material_Info t where 1=1");
//		sql.append(" and t.dept_index='").append(deptIndex).append("'");
		sql.append(" and t.mtr_grp ='").append(mtrGrp).append("'");
		sql.append(" and t.enabled ='").append(enabled).append("'");
		sql.append(" and t.in_control ='").append(inControl).append("'");
		
		if ( StringUtils.isNotEmpty(mtrNum) ) {
			sql.append("and t.mtr_num like '").append(mtrNum).append("%'");
		}
		sql.append(" order by t.mtr_num");
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/**
     * 查询sap物料号信息
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
	 * @throws SQLProcessException 
     */
	public static List getSapMtrNumInfo(GenericDelegator delegator,Map paramMap) throws SQLProcessException{
		String deptIndex = (String) paramMap.get("deptIndex");
		String mtrNum = (String) paramMap.get("mtrNum");	
		
		StringBuffer sql = new StringBuffer();
		sql.append("select t.mtr_num,t.mtr_desc,t.mtr_grp,t.plant from mcs_sap_mtr_table t where 1=1 ");
		sql.append("and t.mtr_num not in (select t2.mtr_num from mcs_material_info t2 where t2.dept_index='")
				.append(deptIndex).append("')");
		sql.append("and t.mtr_num like '").append(mtrNum).append("%'");
		
		List mtrNumList = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return mtrNumList;
	}
	
	
	/**
     * 修改或增加物料基本资料信息
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static String saveMaterialInfo(GenericDelegator delegator, Map paramMap) throws GenericEntityException{
    	List toStore = new LinkedList();
    	
    	GenericValue materialInfo = delegator.makeValidValue("McsMaterialInfo", paramMap);
    	toStore.add(materialInfo);
    	
    	Long addMaterialHistIndex = delegator.getNextSeqId(ConstantsMcs.MATERIAL_HIST_INDEX);
        paramMap.put(ConstantsMcs.MATERIAL_HIST_INDEX, addMaterialHistIndex); 
        GenericValue materialInfoHist = delegator.makeValidValue("McsMaterialInfoHist", paramMap);
        toStore.add(materialInfoHist);        
    	
    	delegator.storeAll(toStore);
    	return (String) materialInfo.get("mtrNum");
    }	
    
	/**
     * 得到distinct的设备大类
     * @param delegator
     * @param param
     * 需要保存的信息
     * @throws SQLProcessException             
     */    
     public static List getEqpTypeList(GenericDelegator delegator,String deptIndex) throws SQLProcessException{
    	 StringBuffer sql = new StringBuffer();
    	 sql.append("select distinct equipment_type from equipment t where dept_index=" + deptIndex
    			 + " or t.use_dept in (select t1.equipment_dept from equipment_dept t1 where t1.dept_index=" + deptIndex + ")"
    			 + " order by equipment_type");
    	 List eqpTypeList = SQLProcess.excuteSQLQuery(sql.toString(), delegator);    	 
    	 return eqpTypeList;    	 
     }
     
 	/**
      * 得到的设备
      * @param delegator
      * @param param
      * 需要保存的信息
      * @throws SQLProcessException             
      */    
      public static List getEqpList(GenericDelegator delegator,String materialIndex,String deptIndex) throws SQLProcessException{
     	 StringBuffer sql = new StringBuffer();
     	 sql.append("select equipment_id from equipment t");
     	 sql.append(" where (dept_index=" + deptIndex + " or t.use_dept in (select t1.equipment_dept from equipment_dept t1 where t1.dept_index=" + deptIndex + "))"
     			 + " and equipment_id not in ( select using_object_id from mcs_mtr_object where material_Index='").append(materialIndex).append("')").append(" order by equipment_id");
     	 List eqpList = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
     	 return eqpList;
      }	
      
   	/**
       * 保存物料设备信息
       * @param delegator
       * @param param
       *            需要保存的信息
       * @throws SQLProcessException             
       */  
      public static void saveMtrObjectInfo (GenericDelegator delegator, Map paramMap ) throws SQLProcessException,GenericEntityException {
    	  
    		List toStore = new LinkedList();
    		String equipmentType = ( String ) paramMap.get("equipmentType");
    		String evt = (String ) paramMap.get("evt");
    		String materialIndex= (String )paramMap.get("materialIndex");
    		
    		//根据物料Index查询物料相关信息
			GenericValue materialInfo = delegator.findByPrimaryKey("McsMaterialInfo", UtilMisc.toMap("materialIndex", materialIndex));
			paramMap.put("mtrNum", materialInfo.getString("mtrNum"));
			paramMap.put("deptIndex", materialInfo.getString("deptIndex"));
			
    		//if:对于选择了设备没有选择设备大类的情况
    		//else:对于选择了设备大类没有选择设备的情况
    		if ( StringUtils.isEmpty(equipmentType)) {
    			
    			if (evt == "UPT") {   				
        			//更新物料设备实时表
    				GenericValue mtrObjectInfo = delegator.makeValidValue("McsMtrObject", paramMap); 			       			
        			toStore.add(mtrObjectInfo); 
        			
        			//更新物料设备历史表
        			Long mtrObjectHistIndex = delegator.getNextSeqId(ConstantsMcs.MTR_OBJECT_HIST_INDEX);
        			paramMap.put("mtrObjectHistIndex", mtrObjectHistIndex);
        			GenericValue mtrObjectInfoHist = delegator.makeValidValue("McsMtrObjectHist", paramMap); 			       			
        			toStore.add(mtrObjectInfoHist);        			
    			} else {    				        			
        			//插入到物料设备信息实时表
        			GenericValue mtrObjectInfo = delegator.makeValidValue("McsMtrObject", paramMap); 			       			
        			toStore.add(mtrObjectInfo); 
        			//插入到物料设备信息历史表
        			Long mtrObjectHistIndex = delegator.getNextSeqId(ConstantsMcs.MTR_OBJECT_HIST_INDEX);
        			paramMap.put("mtrObjectHistIndex", mtrObjectHistIndex);
        			GenericValue mtrObjectInfoHist = delegator.makeValidValue("McsMtrObjectHist", paramMap);
        			toStore.add(mtrObjectInfoHist);
    			}
    			
    		} else {
    			//得到所选择的设备大类对应的设备
    		    //新增设备标准用量
    	     	 StringBuffer sql = new StringBuffer();
    	     	 sql.append("select  equipment_id from equipment where  equipment_type='").append(equipmentType).append("'");
    	     	 sql.append("and equipment_id not in (select using_object_id from mcs_mtr_object where material_index='").append(materialIndex).append("')");
    	     	 sql.append(" order by equipment_id ");
    	     	 List eqpList = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
    			
    	     	 for ( Iterator it = eqpList.iterator(); it.hasNext(); ) {
    	     		HashMap stoReqMap = (HashMap) it.next();
    	     		String equipmentId = (String)stoReqMap.get("EQUIPMENT_ID");
    	     		paramMap.put("usingObjectId", equipmentId);
    	    		Long addMtrObjectIndex = delegator.getNextSeqId(ConstantsMcs.MTR_OBJECT_INDEX);
    	    		paramMap.put("mtrObjectIndex", addMtrObjectIndex);    	     		
          			
    	    		//插入物料设备信息实时表
    	    		GenericValue mtrObjectInfo = delegator.makeValidValue("McsMtrObject", paramMap); 			       			
        			toStore.add(mtrObjectInfo);    	     		 
    	     		 
         			//插入到物料设备信息历史表
        			Long mtrObjectHistIndex = delegator.getNextSeqId(ConstantsMcs.MTR_OBJECT_HIST_INDEX);
        			paramMap.put("mtrObjectHistIndex", mtrObjectHistIndex);
        			GenericValue mtrObjectInfoHist = delegator.makeValidValue("McsMtrObjectHist", paramMap);
        			toStore.add(mtrObjectInfoHist);
    	     	 } 
    			
    	     	 //更新已设置标准用量的设备
    	     	StringBuffer sqlUpdate = new StringBuffer();
    	     	sqlUpdate.append("select  equipment_id from equipment where equipment_type='").append(equipmentType).append("'");
    	     	sqlUpdate.append("and equipment_id in (select using_object_id from mcs_mtr_object where material_index='").append(materialIndex).append("')");
    	     	sqlUpdate.append(" order by equipment_id ");
                List eqpListUpdate = SQLProcess.excuteSQLQuery(sqlUpdate.toString(), delegator);
               
                for ( Iterator it = eqpListUpdate.iterator(); it.hasNext(); ) {
                   HashMap stoReqMap = (HashMap) it.next();
                   String equipmentId = (String)stoReqMap.get("EQUIPMENT_ID");
                   HashMap queryMap = new HashMap();
                   queryMap.put("usingObjectId", equipmentId);
                   queryMap.put("materialIndex", materialIndex);
                   List list = delegator.findByAnd("McsMtrObject", queryMap);
                   String mtrObjectIndex = ((Map)list.get(0)).get("mtrObjectIndex").toString();
                   queryMap.put("mtrObjectIndex", mtrObjectIndex);      
                   queryMap.put("stdUseAmount", paramMap.get("stdUseAmount"));
                   
                   //插入物料设备信息实时表
                   GenericValue mtrObjectInfo = delegator.makeValidValue("McsMtrObject", queryMap);         
                   toStore.add(mtrObjectInfo);                  
                    
                   //插入到物料设备信息历史表
                   Long mtrObjectHistIndex = delegator.getNextSeqId(ConstantsMcs.MTR_OBJECT_HIST_INDEX);
                   paramMap.put("usingObjectId", equipmentId);
                   paramMap.put("mtrObjectHistIndex", mtrObjectHistIndex);
                   GenericValue mtrObjectInfoHist = delegator.makeValidValue("McsMtrObjectHist", paramMap);
                   toStore.add(mtrObjectInfoHist);
                }		
    		}
    		
    		delegator.storeAll(toStore);
	  }
   
     	/**
       * 保存物料设备信息
       * 
       * @param delegator
       * @param param
       *            需要保存的信息
       * @throws SQLProcessException             
       */  
      
      public static void delMtrObjectInfo (GenericDelegator delegator, Map paramMap ) throws SQLProcessException,GenericEntityException {
    	  String mtrObjectIndex = ( String )paramMap.get("mtrObjectIndex");
          String materialIndex = ( String )paramMap.get("materialIndex");
    	  
    	  //删除正式表数据
    	  GenericValue gv = delegator.findByPrimaryKey("McsMtrObject", UtilMisc.toMap("mtrObjectIndex", mtrObjectIndex));
          delegator.removeValue(gv);    	  
  		   
          //根据物料Index查询物料相关信息
			GenericValue materialInfo = delegator.findByPrimaryKey("McsMaterialInfo", UtilMisc.toMap("materialIndex", materialIndex));
			paramMap.put("mtrNum", materialInfo.getString("mtrNum"));
			paramMap.put("deptIndex", materialInfo.getString("deptIndex")); 
			
			//插入到物料设备信息历史表
			Long mtrObjectHistIndex = delegator.getNextSeqId(ConstantsMcs.MTR_OBJECT_HIST_INDEX);
			paramMap.put(ConstantsMcs.MTR_OBJECT_HIST_INDEX, mtrObjectHistIndex);
			GenericValue mtrObjectInfoHist = delegator.makeValidValue("McsMtrObjectHist", paramMap);
			delegator.create(mtrObjectInfoHist);
      }
      
	/**
     * 增加套件名称与描述信息
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void addSuit(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("McsMaterialSuit", param);
        Long id = delegator.getNextSeqId(ConstantsMcs.MATERIAL_SUIT_INDEX);
        gv.put(ConstantsMcs.MATERIAL_SUIT_INDEX, id);
        delegator.create(gv);
    }

    /**
     * 删除套件名称与描述信息
     * 
     * @param delegator
     * @param param
     *            需要删除的信息
     */
    public static void delSuitByIndex(GenericDelegator delegator, String value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("McsMaterialSuit", UtilMisc.toMap("materialSuitIndex", value));
        delegator.removeValue(gv);
    } 

    /**
     * 显示套件的物料号信息
     * 
     * @param delegator
     * @param param
     */    
	public static List querySuitMaterialList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
    	String materialSuitIndex = (String) paramMap.get("materialSuitIndex");
		
		StringBuffer sql = new StringBuffer();
		sql.append("select t.suit_name,t.suit_desc,t.material_suit_index,t1.material_index,t1.mtr_qty,t2.mtr_num,t2.mtr_desc,t1.material_suit_item_index");
		sql.append(" from mcs_material_suit t, mcs_material_suit_item t1, mcs_material_info t2");
		sql.append(" where t.material_suit_index = t1.material_suit_index and t2.material_index = t1.material_index");
		
		if (StringUtils.isNotEmpty(materialSuitIndex)) {
			sql.append(" and t1.material_suit_index='").append(materialSuitIndex).append("'");
		}

		sql.append(" order by t1.material_index");
    	List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
    	return list;    	

	}    
    
    /**
     * 增加套件料号
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void manageSuitMaterial(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("McsMaterialSuitItem", param);
        if (StringUtils.isEmpty((String) param.get(ConstantsMcs.MATERIAL_SUIT_ITEM_INDEX))) {
        	Long id = delegator.getNextSeqId(ConstantsMcs.MATERIAL_SUIT_ITEM_INDEX);
            gv.put(ConstantsMcs.MATERIAL_SUIT_ITEM_INDEX, id);
        }
        delegator.createOrStore(gv);
    } 
    
    /**
     * 删除套件料号
     * 
     * @param delegator
     * @param param
     *            需要删除的信息
     */
    public static void delSuitMaterialByIndex(GenericDelegator delegator, String value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("McsMaterialSuitItem", UtilMisc.toMap("materialSuitItemIndex", value));
        delegator.removeValue(gv);
    }
    
    /**
     * 显示物料号的厂商信息
     * 
     * @param delegator
     * @param param
     */    
	public static List queryVendorMaterialList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
    	String mtrGrp = (String) paramMap.get("mtrGrp");
    	String userDeptIndex = (String) paramMap.get("deptIndex");
		
    	StringBuffer sql = new StringBuffer();
		sql.append("select t.mtr_num,t1.mtr_desc,t.vendor_mtr_num,t1.material_index");
		sql.append(" from mcs_vendor_material t,mcs_material_info t1");
		sql.append(" where t1.mtr_num = t.mtr_num and t1.MTR_GRP = '" + mtrGrp + "' and t1.ENABLED = '1' and t1.IN_CONTROL = '1' and t1.DEPT_INDEX = '" + userDeptIndex + "'");
		sql.append(" order by t1.mtr_num");
    	List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
    	return list;    	

	}
	
	/**
     * 增加料件厂商批号
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void manageVendorMaterial(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("McsVendorMaterial", param);
        delegator.createOrStore(gv);
    }
    
    /**
     * 删除料件厂商批号
     * 
     * @param delegator
     * @param param
     *            需要删除的信息
     */
    public static void delVendorMaterial(GenericDelegator delegator, String value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("McsVendorMaterial", UtilMisc.toMap("mtrNum", value));
        delegator.removeValue(gv);
    }

}