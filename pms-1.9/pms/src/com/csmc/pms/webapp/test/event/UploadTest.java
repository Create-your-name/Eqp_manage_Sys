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
			//������ļ��ϴ�
			JfileItemInfo jfileItemInfo=JfileUpload.commonFileUpload(request);
			//��ȡ�ļ��ϴ�״̬(�ļ��Ƿ��ϴ��ɹ�)
			List stateList=jfileItemInfo.getFileUpLoadState();
			// ��ȡ�����ļ����ֵ
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
