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
		String equipmentID = "";
		String equipmentDes = "";
		String NowTime = "";
		String abnormalEvent = "";
		String member = "";
		String abnormalEffect = "";
		String issueTech = "";
		String preventMethod = "";
		String abnormalDocIndex = "";
		String abnormalDocName = "";
		String createTime = "";
		String endTime = "";
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Date now=new Date();
		NowTime = dFormat.format(now);
		if(pageContext.findAttribute("AccountShowList")!=null){
			GenericValue user = (GenericValue)request.getAttribute("AccountShowList");
			if(user != null) {
				accountName = user.getString("accountName");
				accountDept = user.getString("accountDept");
				accountSection =  user.getString("accountSection");
			}
		}
		
		if(pageContext.findAttribute("EquipmentShowList")!=null){
			GenericValue user = (GenericValue)request.getAttribute("EquipmentShowList");
			if(user != null) {
				equipmentID = user.getString("equipmentId");
				equipmentDes = user.getString("equipmentDesc");
			}
		}
		
		if(pageContext.findAttribute("DocumentShowList")!=null){
			GenericValue user = (GenericValue)request.getAttribute("DocumentShowList");
			if(user != null) {
				abnormalEvent = user.getString("abnormalEvent");
				member = user.getString("member");
				abnormalEffect = user.getString("abnormalEffect");
				issueTech = user.getString("issueTech");
				preventMethod = user.getString("preventMethod");
				abnormalDocIndex = user.getString("abnormalDocIndex");
				abnormalDocName = user.getString("abnormalDocName");
				//
				//if (user.getDate("createTime") != null ){
				//	createTime = dFormat1.format(user.getDate("createTime"));
				//}
				//if (user.getDate("endTime") != null){
				//	endTime = dFormat1.format(user.getDate("endTime"));
				//}
				//if (user.getDate("updateTime") != null ){
				//	NowTime = dFormat1.format(user.getDate("updateTime"));
				//}

				if (user.getString("createTime") != null ){
                	createTime = user.getString("createTime");
                }
                if (user.getString("endTime") != null ){
                	createTime = user.getString("endTime");
                }
                if (user.getString("updateTime") != null ){
                	createTime = user.getString("updateTime");
                }
			}
		}
	String eventIndex=UtilFormatOut.checkNull((String)request.getAttribute("eventIndex"));
	String uplodType=(String)request.getAttribute("uploadItem");
	String eventType = String.valueOf(Constants.DOC_ABNORMAL);
%>


<form  action="<%=request.getContextPath()%>/control/abnormalDocDefine1" name="queryAbnormalDocDefine" method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="98%"><fieldset><legend>异常报告书</legend>
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
          <td colspan="3" class="en11px"><%=UtilFormatOut.checkNull(abnormalDocName)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">异常事件</td>
          <td colspan="5" class="en11px">
          		<%=UtilFormatOut.checkNull(abnormalEvent)%>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">成员</td>
          <td colspan="5" class="en11px"><%=UtilFormatOut.checkNull(member)%></td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<br/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="98%"><fieldset><legend>异常分析</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
        <!-----部门的设备ID---->
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">发生时间</td>
          <td width="13%" class="en11px"><%=UtilFormatOut.checkNull(createTime)%></td>
          <td width="14%" bgcolor="#ACD5C9" class="en11pxb">结束时间</td>
          <td width="17%" class="en11px"><%=UtilFormatOut.checkNull(endTime)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb">设备</td>
          <td class="en11px"><%=UtilFormatOut.checkNull(equipmentID)%></td>
          <td bgcolor="#ACD5C9" class="en11pxb">设备描述</td>
          <td class="en11px"><%=UtilFormatOut.checkNull(equipmentDes)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb">异常现象</td>
          <td colspan="3" class="en11px">
            <%=UtilFormatOut.checkNull(abnormalEffect)%>
		  </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb"><input type="hidden"  name="abnormalDocIndex"  id="abnormalDocIndex" class="input" value = <%=UtilFormatOut.checkNull(abnormalDocIndex)%>>
    		<input type="hidden"  name="status"  id="status" class="input">
     		<input type="hidden"  name="equipmentId"  id="equipmentId" class="input" value = <%=UtilFormatOut.checkNull(equipmentID)%>>
          异常分析</td>
          <td colspan="3" class="en11px"><%=UtilFormatOut.checkNull(issueTech)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb">预防方法</td>
          <td colspan="3" class="en11px"><%=UtilFormatOut.checkNull(preventMethod)%></td>
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
<div  style="visibility:'hidden';">
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
   	<td width="20">&nbsp;</td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/abnormalDocDefineSave','2')"><span>&nbsp;暂存&nbsp;</span></a></li> 
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/abnormalDocDefineSave','1')"><span>&nbsp;完成&nbsp;</span></a></li> 
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="abnormalDocDefine.htm"><span>&nbsp;附档&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>
</div>
</form>

<script language="javascript">
	Ext.onReady(function(){
	    var createTime = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });
	    var endTime = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });
	    //将控件与页面的INPUT框捆绑
	    //createTime.applyTo('create_Time');   
	    //endTime.applyTo('end_Time');  
	});
	
	function doSubmit(url,status) {
		document.abnormalDocDefine.status.value = status;
		document.abnormalDocDefine.action = url;
		document.abnormalDocDefine.submit();	
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