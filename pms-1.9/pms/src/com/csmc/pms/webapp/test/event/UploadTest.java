package com.csmc.pms.webapp.test.event;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;

import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.util.jakartaFileUpload.JfileItemInfo;
import com.csmc.pms.webapp.util.jakartaFileUpload.JfileUpload;

public class UploadTest extends GeneralEvents {
	public static final String module = UploadTest.class.getName();

	public static String fileUpload(HttpServletRequest request,
			HttpServletResponse response) {
		try{
			//处理多文件上传
			JfileItemInfo jfileItemInfo=JfileUpload.commonFileUpload(request);
			//获取文件上传状态(文件是否上传成功)
			List stateList=jfileItemInfo.getFileUpLoadState();
			// 获取表单非文件域的值
			Map fileValue=jfileItemInfo.getFileValue();
			System.out.println(fileValue.get("textName"));
			request.setAttribute("fileList", stateList);
		}catch(Exception e){
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
}
