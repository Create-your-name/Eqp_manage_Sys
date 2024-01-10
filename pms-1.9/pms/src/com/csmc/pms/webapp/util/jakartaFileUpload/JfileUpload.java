/**
 * 
 */
package com.csmc.pms.webapp.util.jakartaFileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.csmc.pms.webapp.test.event.UploadTest;

/**
 * �ļ�����
 * @author shaoaj
 * 
 */
public class JfileUpload {
	public static final String module = UploadTest.class.getName();
	public static final String FILE_DIR="/pms/fileUpload";

	/**
	 * �ļ��ϴ�����
	 * 
	 * @param request
	 */
	public static JfileItemInfo commonFileUpload(HttpServletRequest request)
			throws Exception {
		JfileItemInfo jfileItemInfo = new JfileItemInfo();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		List stateList = new ArrayList();
		List fileNameList=new ArrayList();
		List contentTypeList=new ArrayList();
//		ServletFileUpload sevletFileUpload = new ServletFileUpload(factory);
		// �ļ������·��
		String filePath = request.getSession().getServletContext().getRealPath(FILE_DIR);
		// ����ƥ�䣬����·��ȡ�ļ���
		String regExp = ".+\\\\(.+)$";
		Pattern pattern = Pattern.compile(regExp);
//		sevletFileUpload.setHeaderEncoding("UTF-8");
		Map valueMap = new HashMap();
		request.setCharacterEncoding("UTF-8");
//		List fileItems = sevletFileUpload.parseRequest(request);
		
		DiskFileUpload fu = new DiskFileUpload();
		fu.setSizeThreshold(4096);
		fu.setSizeMax(20000000);
		fu.setRepositoryPath(filePath);
		List fileItems = fu.parseRequest(request);
		   
		if(fileItems.size()!=0){
			// ���δ���ÿ���ϴ����ļ�
			Iterator iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				// �ļ�������б���Ϣ
				if (!item.isFormField()) {
					String name = item.getName();
					String contentType=item.getContentType();
					long size = item.getSize();
					if ((name == null || name.equals("")) && size == 0)
						continue;
					Matcher m = pattern.matcher(name);
					boolean result = m.find();
					if (result) {
						File file = new File(filePath + "/" + m.group(1));
						if (file.exists()) {
							// �ļ�����ʱ
							stateList.add(m.group(1) + "�Ѵ���,�ϴ�ʧ�ܣ�");
							fileNameList.add("error");
						} else {
							// �ļ�������
							item.write(file);
							stateList.add(m.group(1) + ",�ϴ��ɹ���");
							fileNameList.add(m.group(1));
							contentTypeList.add(contentType);
						}
					} else {
						stateList.add(m.group(1) + ",�ļ��ϴ�ʧ�ܣ�");
						fileNameList.add("error");
					}
				} else {
					// �����ļ�������б���Ϣ
					String fileNam = item.getFieldName();
					String fileValue = new String(item.getString().getBytes(
							"ISO8859_1"), "UTF-8");
					valueMap.put(fileNam, fileValue);
				}
			}
		}else{
			
		}
		jfileItemInfo.setFileUpLoadState(stateList);
		jfileItemInfo.setFileValue(valueMap);
		jfileItemInfo.setFileNameList(fileNameList);
		jfileItemInfo.setContentTypeList(contentTypeList);
		return jfileItemInfo;
	}
	
	/**
	 * �ļ�����
	 * @param fileName �ļ���
	 * @param contentType �ļ�����
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String fileLoad(String fileName,String contentType,HttpServletRequest request, HttpServletResponse response) throws Exception{
		 String fileDir=FILE_DIR+"/"+fileName;
 		// �ļ�·��
 		String filePath = request.getSession().getServletContext().getRealPath(fileDir);
 		 File f = new File(filePath);
 		 if(f.exists()){
    		 FileInputStream in = new FileInputStream(f); 
    		 response.setContentType(contentType+"; charset=GBK");
    		 response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(fileName, "UTF-8"));
    		 response.setContentLength((int)f.length());
    		 //fetch the file
    		 int length = (int)f.length();	 	
    		 if(length != 0)  {
    			 byte[] buf = new byte[4096];	
    			 ServletOutputStream op = response.getOutputStream();			
    			 while ((in != null) && ((length = in.read(buf)) != -1))  {				
    				 op.write(buf,0, length);
    			}
    			 in.close();
    			 op.flush();	
    			 op.close(); 	
    			 }
 		 }else{
 			 return "�ļ�������,��ȷ�ϴ��ļ��Ƿ��ѱ�ɾ��!";
 		 }
 		 return "success";
	}
	
	/**
	 * ɾ���ļ�
	 * @param fileName �ļ���
	 * @param request 
	 * @return ɾ���Ƿ�ɹ�
	 */
	public static String delFile(String fileName,HttpServletRequest request){
		String fileDir=FILE_DIR+"/"+fileName;
		// �ļ�·��
 		String filePath = request.getSession().getServletContext().getRealPath(fileDir);
 		File f = new File(filePath);
 		String message="";
 		if(f.exists()){//���File.txt�Ƿ���� 
 			f.delete();//ɾ��File.txt�ļ� 
 			message="success";
 		}else{
 			message="error";
 		}
		return message;
	}
}
