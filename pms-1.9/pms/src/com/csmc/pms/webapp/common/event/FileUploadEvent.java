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
 * 文件上传下载
 * @author shaoaj
 * @2007-9-4
 */
public class FileUploadEvent  extends GeneralEvents{
	public static final String module = FileUploadEvent.class.getName();
	public static final String UPLOAD_INDEX_SESSION="upload_idnex_session";
	
	/**
	 * 进入文件上传页面
	 * (一)新增页面不存在eventIndex，以uploadIndex进行查询
	 * (二)修改页面存在eventIndex,以eventIndex进行查询
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
		//文档所属模块
		String eventType="";
		eventType=request.getParameter("eventType");
		if(StringUtils.isEmpty(eventType)){
			eventType=(String)request.getAttribute("eventType");
		}
		//页面需要返回给父窗口赋值的栏位
		String uploadItem="";
		uploadItem=request.getParameter("uploadItem");
		if(StringUtils.isEmpty(uploadItem)){
			uploadItem=(String)request.getAttribute("uploadItem");
		}
		//文件Id用";"连接的一串文件ID,
		//功能：维护过文件后，关闭文件管理页面，再打开时，需要通过upload查询出刚刚操作过的文件
		String uploadIndex=request.getParameter("uploadIndex");
		//文件上传后将request转至该页面,文件删除时，需要通过getAttribute方法获取没有删除的uploadIndex
		if(StringUtils.isEmpty(uploadIndex)){
			uploadIndex=(String)request.getAttribute("uploadIndex");
		}
		uploadIndex=UtilFormatOut.checkNull(uploadIndex);
		Map map=new HashMap();
		map.put("eventIndex", eventIndex);
		map.put("eventType", eventType);
		try {
			List fileList=new ArrayList();
			//从event信息新增页面进行文件管理页面时，eventIndex,uploadIndex都为空
			if(StringUtils.isNotEmpty(eventIndex)){
				fileList=FileUploadHelper.getFileList(delegator, map);
			}else if(StringUtils.isNotEmpty(uploadIndex)){
				//通过uploadIndex开始查询文件
				fileList=FileUploadHelper.getFileList(delegator, uploadIndex);
			}
			// 编辑文件时，第一次打开，没有uploadIndex,所以需要依次取出
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
			//将uploadIndex存放于session中,删除时使用,存放所有upload值
			request.getSession().setAttribute(UPLOAD_INDEX_SESSION, uploadIndex);
			request.setAttribute("eventIndex", eventIndex);
		} catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
	}
	
	/**
	 * 文件上传
	 * @param request
	 * @param response
	 * @return
	 */
	public static String fileUP(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String accountNo=CommonUtil.getUserNo(request);
		try {
			// 处理多文件上传
			JfileItemInfo jfileItemInfo=JfileUpload.commonFileUpload(request);
			//获取文件上传状态(文件是否上传成功)
			List stateList=jfileItemInfo.getFileUpLoadState();
			//获取文件上传的名称
			List fileNameList=jfileItemInfo.getFileNameList();
			//获得文件类型
			List contentType=jfileItemInfo.getContentTypeList();
			// 获取表单非文件域的值
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
					//组合文件ID,加入新增的uploadIndex
					uploadIndex=uploadIndex+String.valueOf(id)+";";
				}
			}
			//设备返回参数
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
     * 文件下载
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
    		 //实现文件下载
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
     * 文件删除
     * @param request
     * @param response
     * @return
     */
    public static String delFile(HttpServletRequest request, HttpServletResponse response) {
		 try {
			 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
			 //需要删除的文件
			 String index=request.getParameter("delUploadIndex");
			 //文件总数(如果在此页面已进行过文件的操作，会得到所有的index值)
			 String uploadIndex=(String)request.getSession().getAttribute(UPLOAD_INDEX_SESSION);
			 GenericValue file=FileUploadHelper.getFile(delegator, index);
			 //删除目录中的文件
			 String result=JfileUpload.delFile(file.getString("fileUrl"), request);
			 //更新index值
			 String resultUploadIndex=uploadIndex.replaceAll(index+";","");
			 //设置返回参数
			 request.setAttribute("uploadIndex", resultUploadIndex);
			 request.setAttribute("eventType", request.getParameter("eventType"));
			 request.setAttribute("uploadItem", request.getParameter("uploadItem"));
			 request.setAttribute("eventIndex", request.getParameter("eventIndex"));
			 if("error".equals(result)){
				 request.setAttribute("_ERROR_MESSAGE_", "目录中不存在此文件！");
		         return "error";
			 }
			 //删除文件表信息
			 FileUploadHelper.delFile(delegator, index);
			 request.setAttribute("_EVENT_MESSAGE_", "文件删除成功");
	     } catch (Exception e) {
	         request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	         Debug.logError(e, module);
	         return "error";
	     }
	     return "success";
    }
}
