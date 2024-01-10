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
 * 文件操作
 * @author shaoaj
 * 
 */
public class JfileUpload {
	public static final String module = UploadTest.class.getName();
	public static final String FILE_DIR="/pms/fileUpload";

	/**
	 * 文件上传方法
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
		// 文件保存的路径
		String filePath = request.getSession().getServletContext().getRealPath(FILE_DIR);
		// 正则匹配，过滤路径取文件名
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
			// 依次处理每个上传的文件
			Iterator iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				// 文件域的所有表单信息
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
							// 文件存在时
							stateList.add(m.group(1) + "已存在,上传失败！");
							fileNameList.add("error");
						} else {
							// 文件不存在
							item.write(file);
							stateList.add(m.group(1) + ",上传成功！");
							fileNameList.add(m.group(1));
							contentTypeList.add(contentType);
						}
					} else {
						stateList.add(m.group(1) + ",文件上传失败！");
						fileNameList.add("error");
					}
				} else {
					// 不是文件域的所有表单信息
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
	 * 文件下载
	 * @param fileName 文件名
	 * @param contentType 文件类型
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String fileLoad(String fileName,String contentType,HttpServletRequest request, HttpServletResponse response) throws Exception{
		 String fileDir=FILE_DIR+"/"+fileName;
 		// 文件路径
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
 			 return "文件不存在,请确认此文件是否已被删除!";
 		 }
 		 return "success";
	}
	
	/**
	 * 删除文件
	 * @param fileName 文件名
	 * @param request 
	 * @return 删除是否成功
	 */
	public static String delFile(String fileName,HttpServletRequest request){
		String fileDir=FILE_DIR+"/"+fileName;
		// 文件路径
 		String filePath = request.getSession().getServletContext().getRealPath(fileDir);
 		File f = new File(filePath);
 		String message="";
 		if(f.exists()){//检查File.txt是否存在 
 			f.delete();//删除File.txt文件 
 			message="success";
 		}else{
 			message="error";
 		}
		return message;
	}
}
