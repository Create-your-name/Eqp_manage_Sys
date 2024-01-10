/**
 * 
 */
package com.csmc.pms.webapp.common.helper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;


/**
 * @author shaoaj
 * @2007-9-4
 */
public class FileUploadHelper {
	public static final String module = FileUploadHelper.class.getName();
	
	/**
	 * ����������ѯ�ĵ���Ϣ
	 * 
	 * @param delegator
	 * @return ��½����Ϣ�б�
	 */
	public static List getFileList(GenericDelegator delegator,Map map) throws Exception {
		List list = delegator.findByAnd("DocumentUpload", map);
		return list;
	}
	
	
	/**
	 * ����������ѯ�ļ���Ϣ
	 * 
	 * @param delegator
	 * @return ��½����Ϣ�б�
	 */
	public static List getFileList(GenericDelegator delegator,String uploadIndex) throws Exception {
		String[] upload=uploadIndex.split(";");
		String sql="";
		for(int i=0;i<upload.length;i++){
			String and="";
			if(i!=0){
				and=" or ";
			}
			if(StringUtils.isNotEmpty(upload[i])){
				sql=sql+and+" UPLOAD_INDEX='"+upload[i]+"'";
			}
		}
		EntityWhereString con = new EntityWhereString(sql);
        List list = delegator.findByCondition("DocumentUpload", con, null, null);
		return list;
	}
	
	/**
	 * �����ĵ���Ϣ
	 * @param delegator
	 * @param uploadMap
	 * @throws Exception
	 */
	public static Long createFile(GenericDelegator delegator,Map uploadMap)throws Exception {
		Long id = null;
		// ����document��Ϣ
		GenericValue upload = delegator.makeValidValue("DocumentUpload",uploadMap);
		id = delegator.getNextSeqId("uploadIndex");
		upload.put("uploadIndex", id);
		upload.put("updateTime", new Timestamp(System.currentTimeMillis()));
		delegator.create(upload);
		return id;
	}
	
	/**
	 * ͨ��uploadIndex�����ĵ���Ϣ
	 * @param delegator
	 * @param uploadIndex
	 * @param eventIndex
	 * @param eventType
	 * @throws Exception
	 */
	public static void updateFile(GenericDelegator delegator,String uploadIndex,String eventIndex,String eventType)throws Exception {
		List list=getFileList(delegator,uploadIndex);
		for(int i=0;i<list.size();i++){
			GenericValue gv=(GenericValue)list.get(i);
			gv.put("eventIndex", eventIndex);
			gv.put("eventType", eventType);
		}
		
		delegator.storeAll(list);
	}
	
	/**
	 * ��������ɾ���ĵ���Ϣ
	 * @param delegator
	 * @param uploadIndex
	 * @throws Exception
	 */
	public static void delFile(GenericDelegator delegator,String uploadIndex)throws Exception {
		 GenericValue fupload=getFile(delegator, uploadIndex);
	     delegator.removeValue(fupload);
	}
	
	/**
	 * ͨ��������ȡ�ļ���Ϣ
	 * @param delegator
	 * @param uploadIndex ����
	 * @return
	 * @throws Exception
	 */
	public static GenericValue getFile(GenericDelegator delegator,String uploadIndex)throws Exception {
		GenericValue file = delegator.findByPrimaryKey("DocumentUpload", UtilMisc.toMap("uploadIndex", uploadIndex));
		return file;
	}
	
	/**
	 * ����eventIndex,eventTypeɾ���ļ���Ϣ
	 * @param delegator
	 * @param eventIndex
	 * @param eventType
	 * @throws Exception
	 */
	public static void delUploadFileByEventIndex(GenericDelegator delegator,String eventIndex,String eventType) throws Exception{
		delegator.removeByAnd("DocumentUpload", UtilMisc.toMap("eventIndex", eventIndex,"eventType", eventType));
	}
}
