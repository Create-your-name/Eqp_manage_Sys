<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%
		String accountName = "";
		String accountDept = "";
		String accountSection = "";
		String NowTime = "";
		String content = "";
		String Type = "11";
		String TypeID = "";
		String member = "";
		String docName = "";
		String improveDocIndex = "";
		String createTime = "";
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Date now=new Date();
		NowTime = dFormat.format(now);
		createTime = dFormat.format(now);
		
		if(pageContext.findAttribute("AccountShowList")!=null){
			GenericValue user = (GenericValue)request.getAttribute("AccountShowList");
			if(user != null) {
				accountName = user.getString("accountName");
				accountDept = user.getString("accountDept");
				accountSection =  user.getString("accountSection");
				member = user.getString("accountName");
				//owner = user.getString("accountName");
			}
		}
		if(pageContext.findAttribute("type")!=null){
			TypeID = (String)request.getAttribute("type");
			if (TypeID.equals("EQP")){
				Type = "设备改善";
			}else if (TypeID.equals("PM")){
				Type = "保养改善";
			}else if (TypeID.equals("SE")){
				Type = "安全改善";
			}
		}
		if(pageContext.findAttribute("flag").equals("new")){

		}
		else{
			if(pageContext.findAttribute("DocumentShowList")!=null){
				GenericValue user = (GenericValue)request.getAttribute("DocumentShowList");
				if(user != null) {
					content = user.getString("content");
					docName = user.getString("docName");
				    improveDocIndex = user.getString("impDocIndex");
					//
				//	if (user.getDate("createTime") != null ){
				//		createTime = dFormat1.format(user.getDate("createTime"));
				//	}
					if (user.getString("createTime") != null ){
                    	createTime = user.getString("createTime");
                    }
					else{
						createTime = dFormat1.format(now);
					}
					//
					TypeID = user.getString("type");
					if (TypeID.equals("EQP")){
						Type = "设备改善";
					}else if (TypeID.equals("PM")){
						Type = "保养改善";
					}else if (TypeID.equals("SE")){
						Type = "安全改善";
					}
				}
			}
		}
		
	String eventIndex=UtilFormatOut.checkNull((String)request.getAttribute("eventIndex"));
	String uplodType=(String)request.getAttribute("uploadItem");
	String eventType = String.valueOf(Constants.DOC_IMPROVER);
%>

<form  action="<%=request.getContextPath()%>/control/improveDocDefine1" name="improveDocDefine" method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="98%"><fieldset><legend>改善类报告书</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
        <!-----部门的设备ID---->
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">部门</td>
          <td width="13%" class="en11px"><%=UtilFormatOut.checkNull(accountDept)%></td>
          <td width="14%" bgcolor="#ACD5C9" class="en11pxb">课别</td>
          <td width="17%" class="en11px"><%=UtilFormatOut.checkNull(accountSection)%></td>
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">撰写时间</td>
          <td width="32%" class="en11px"><%=UtilFormatOut.checkNull(NowTime)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">报告人</td>
          <td class="en11px" ><%=UtilFormatOut.checkNull(accountName)%></td>
          <td class="en11pxb" bgcolor="#ACD5C9">文件编号</td>
          <td colspan="3" class="en11px"><%=UtilFormatOut.checkNull(docName)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">改善内容</td>
          <td colspan="5" class="en11px">
           <%=UtilFormatOut.checkNull(content)%>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9"><input type="hidden"  name="impDocIndex"  id="impDocIndex" class="input" value = <%=UtilFormatOut.checkNull(improveDocIndex)%>>
    <input type="hidden"  name="create_Time"  id="create_Time" class="input" value = <%=UtilFormatOut.checkNull(createTime)%>>
     <input type="hidden"  name="type"  id="type" class="input" value = <%=UtilFormatOut.checkNull(TypeID)%>>
          改善类型</td>
 		<td colspan="5" class="en11px"><input type="hidden" size="80" name="type" id="typename" class="input" readonly value = <%=UtilFormatOut.checkNull(Type)%>><%=UtilFormatOut.checkNull(Type)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">
    		成员</td>
          <td colspan="5" class="en11px"><input type="hidden" size="80" border = "0" name="member" readonly id="member" class="input" value = <%=UtilFormatOut.checkNull(member)%>><%=UtilFormatOut.checkNull(member)%></td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>文件名列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
			
			<td width="90%" class="en11pxb">文件名</td>
        </tr>
        <ofbiz:if name="FILE_LIST">
	        <ofbiz:iterator name="file" property="FILE_LIST">
		        <tr bgcolor="#DFE1EC">
		        
		        <td class="en11px"><a href="#" onclick="fileLoad('<ofbiz:inputvalue entityAttr="file" field="uploadIndex"/>')"><ofbiz:entityfield attribute="file" field="fileUrl"/></a></td>
		        </tr>
	 	</ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>

<script language="javascript">
	
	function doSubmit(url) {
		document.improveDocDefine.action = url;
		document.improveDocDefine.submit();	
	}
	
	//文件下载
	function fileLoad(uploadIndex){
		var url='<ofbiz:url>/loadFile</ofbiz:url>?uploadIndex='+uploadIndex+'&eventType=<%=eventType%>&uploadItem=<%=uplodType%>&eventIndex=<%=eventIndex%>';
		document.location=url;
	}
	
	//文件删除
	function delFile(uploadIndex){
	 Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
        if(value=="yes"){
			var url='<ofbiz:url>/delFileDefine</ofbiz:url>?delUploadIndex='+uploadIndex+'&eventType=<%=eventType%>&uploadItem=<%=uplodType%>&eventIndex=<%=eventIndex%>';
			document.location=url;
		}else{
			return;
		}
       });
	}
</script>