/**
 * 
 */
package com.csmc.pms.webapp.common.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.common.helper.FileUploadHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.jakartaFileUpload.JfileItemInfo;
import com.csmc.pms.webapp.util.jakartaFileUpload.JfileUpload;

/**
 * �ļ��ϴ�����
 * @author shaoaj
 * @2007-9-4
 */
public class FileUploadEvent  extends GeneralEvents{
	public static final String module = FileUploadEvent.class.getName();
	public static final String UPLOAD_INDEX_SESSION="upload_idnex_session";
	
	/**
	 * �����ļ��ϴ�ҳ��
	 * (һ)����ҳ�治����eventIndex����uploadIndex���в�ѯ
	 * (��)�޸�ҳ�����eventIndex,��eventIndex���в�ѯ
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoFileUP(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String eventIndex=request.getParameter("eventIndex");
		if(StringUtils.isEmpty(eventIndex)){
			eventIndex=(String)request.getAttribute("eventIndex");
		}
		//�ĵ�����ģ��
		String eventType="";
		eventType=request.getParameter("eventType");
		if(StringUtils.isEmpty(eventType)){
			eventType=(String)request.getAttribute("eventType");
		}
		//ҳ����Ҫ���ظ������ڸ�ֵ����λ
		String uploadItem="";
		uploadItem=request.getParameter("uploadItem");
		if(StringUtils.isEmpty(uploadItem)){
			uploadItem=(String)request.getAttribute("uploadItem");
		}
		//�ļ�Id��";"���ӵ�һ���ļ�ID,
		//���ܣ�ά�����ļ��󣬹ر��ļ�����ҳ�棬�ٴ�ʱ����Ҫͨ��upload��ѯ���ող��������ļ�
		String uploadIndex=request.getParameter("uploadIndex");
		//�ļ��ϴ���requestת����ҳ��,�ļ�ɾ��ʱ����Ҫͨ��getAttribute������ȡû��ɾ����uploadIndex
		if(StringUtils.isEmpty(uploadIndex)){
			uploadIndex=(String)request.getAttribute("uploadIndex");
		}
		uploadIndex=UtilFormatOut.checkNull(uploadIndex);
		Map map=new HashMap();
		map.put("eventIndex", eventIndex);
		map.put("eventType", eventType);
		try {
			List fileList=new ArrayList();
			//��event��Ϣ����ҳ������ļ�����ҳ��ʱ��eventIndex,uploadIndex��Ϊ��
			if(StringUtils.isNotEmpty(eventIndex)){
				fileList=FileUploadHelper.getFileList(delegator, map);
			}else if(StringUtils.isNotEmpty(uploadIndex)){
				//ͨ��uploadIndex��ʼ��ѯ�ļ�
				fileList=FileUploadHelper.getFileList(delegator, uploadIndex);
			}
			// �༭�ļ�ʱ����һ�δ򿪣�û��uploadIndex,������Ҫ����ȡ��
			if(StringUtils.isEmpty(uploadIndex)){
				for (int i = 0; i < fileList.size(); i++) {
					GenericValue gv = (GenericValue) fileList.get(i);
					uploadIndex = uploadIndex + gv.getString("uploadIndex") + ";";
				}
			}
			request.setAttribute("FILE_LIST", fileList);
			request.setAttribute("eventType", eventType);
			request.setAttribute("uploadItem", uploadItem);
			request.setAttribute("uploadIndex", uploadIndex);
			//��uploadIndex�����session��,ɾ��ʱʹ��,�������uploadֵ
			request.getSession().setAttribute(UPLOAD_INDEX_SESSION, uploadIndex);
			request.setAttribute("eventIndex", eventIndex);
		} catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
	}
	
	/**
	 * �ļ��ϴ�
	 * @param request
	 * @param response
	 * @return
	 */
	public static String fileUP(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String accountNo=CommonUtil.getUserNo(request);
		try {
			// ������ļ��ϴ�
			JfileItemInfo jfileItemInfo=JfileUpload.commonFileUpload(request);
			//��ȡ�ļ��ϴ�״̬(�ļ��Ƿ��ϴ��ɹ�)
			List stateList=jfileItemInfo.getFileUpLoadState();
			//��ȡ�ļ��ϴ�������
			List fileNameList=jfileItemInfo.getFileNameList();
			//����ļ�����
			List contentType=jfileItemInfo.getContentTypeList();
			// ��ȡ�����ļ����ֵ
			Map fileValue=jfileItemInfo.getFileValue();
			String message="";
			Map map=new HashMap();
			map.put("transBy", accountNo);
			String uploadIndex=(String)fileValue.get("uploadIndex");
			String eventType=(String)fileValue.get("eventType");
			String eventIndex=(String)fileValue.get("eventIndex");
			for(int i=0;i<stateList.size();i++){
				message=message+(String)stateList.get(i)+"<br>";
				String fileName=(String)fileNameList.get(i);
				if(!"error".equals(fileName)){
					String type=(String)contentType.get(i);
					String desc=(String)fileValue.get("desc"+(i+1));
					map.put("fileUrl", fileName);
					map.put("contentType", type);
					map.put("fileDescription", desc);
					map.put("eventType", eventType);
					map.put("eventIndex", eventIndex);
					Long id=FileUploadHelper.createFile(delegator, map);
					//����ļ�ID,����������uploadIndex
					uploadIndex=uploadIndex+String.valueOf(id)+";";
				}
			}
			//�豸���ز���
			request.setAttribute("_EVENT_MESSAGE_", message);
			request.setAttribute("uploadIndex", uploadIndex);
			request.setAttribute("uploadItem", (String)fileValue.get("uploadItem"));
			request.setAttribute("eventType", eventType);
			request.setAttribute("eventIndex", eventIndex);
		} catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
	}
	
	 /**
     * �ļ�����
     * @param request
     * @param response
     * @return
     */
    public static String fileLoad(HttpServletRequest request, HttpServletResponse response) {
    	 try {
    		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    		 String index=request.getParameter("uploadIndex");
    		 GenericValue gv=delegator.findByPrimaryKey("DocumentUpload", UtilMisc.toMap("uploadIndex",index));
    		 String fileName=gv.getString("fileUrl");
    		 String contentType=gv.getString("contentType");
    		 //ʵ���ļ�����
    		 String result=JfileUpload.fileLoad(fileName, contentType, request, response);
    		 request.setAttribute("eventType", request.getParameter("eventType"));
			 request.setAttribute("uploadItem", request.getParameter("uploadItem"));
			 request.setAttribute("eventIndex", request.getParameter("eventIndex"));
    		 if(!"success".equals(result)){
    			 request.setAttribute("_ERROR_MESSAGE_", result);
                 return "error";
    		 }
         } catch (Exception e) {
             request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
             Debug.logError(e, module);
             return "error";
         }
         return "success";
     }
    
    /**
     * �ļ�ɾ��
     * @param request
     * @param response
     * @return
     */
    public static String delFile(HttpServletRequest request, HttpServletResponse response) {
		 try {
			 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
			 //��Ҫɾ�����ļ�
			 String index=request.getParameter("delUploadIndex");
			 //�ļ�����(����ڴ�ҳ���ѽ��й��ļ��Ĳ�������õ����е�indexֵ)
			 String uploadIndex=(String)request.getSession().getAttribute(UPLOAD_INDEX_SESSION);
			 GenericValue file=FileUploadHelper.getFile(delegator, index);
			 //ɾ��Ŀ¼�е��ļ�
			 String result=JfileUpload.delFile(file.getString("fileUrl"), request);
			 //����indexֵ
			 String resultUploadIndex=uploadIndex.replaceAll(index+";","");
			 //���÷��ز���
			 request.setAttribute("uploadIndex", resultUploadIndex);
			 request.setAttribute("eventType", request.getParameter("eventType"));
			 request.setAttribute("uploadItem", request.getParameter("uploadItem"));
			 request.setAttribute("eventIndex", request.getParameter("eventIndex"));
			 if("error".equals(result)){
				 request.setAttribute("_ERROR_MESSAGE_", "Ŀ¼�в����ڴ��ļ���");
		         return "error";
			 }
			 //ɾ���ļ�����Ϣ
			 FileUploadHelper.delFile(delegator, index);
			 request.setAttribute("_EVENT_MESSAGE_", "�ļ�ɾ���ɹ�");
	     } catch (Exception e) {
	         request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	         Debug.logError(e, module);
	         return "error";
	     }
	     return "success";
    }
}
