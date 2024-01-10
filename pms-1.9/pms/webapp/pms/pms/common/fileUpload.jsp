<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<% 
	String upload=UtilFormatOut.checkNull((String)request.getAttribute("uploadIndex"));
	String eventIndex=UtilFormatOut.checkNull((String)request.getAttribute("eventIndex"));
	String uplodType=(String)request.getAttribute("uploadItem");
	String eventType=(String)request.getAttribute("eventType");
%>
<base target="_self">
<!-- yui page script-->
<script language="javascript">
	//文件上传
	function fileSubmit(){
		var fileInput=document.getElementsByName("uploadFile");
		var isHasFile=false;
		var isHasDesc=false;
		for(var i=0;i<fileInput.length;i++){
			var descObj=document.getElementById("desc"+(i+1));
			//有文件
			if(fileInput[i].value!=""){
				isHasFile=true;
				if(descObj.value!=""){
					isHasDesc=true;
				}else{
					isHasDesc=false;
					break;
				}
			}else if(i==0){
				isHasFile=false;
				break;
			}
		}
		if(!isHasFile){
			Ext.MessageBox.alert('警告', '请选择需要上传的文件!');
			return;
		}
		
		if(!isHasDesc){
			Ext.MessageBox.alert('警告', '文件描述必填!');
			return;
		}
		fileUploadForm.submit();
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

	Ext.onReady(function(){
	    window.opener.document.getElementById('<%=uplodType%>').value='<%=upload%>';
	});
</script>
<form action="<%=request.getContextPath()%>/control/fileUpDefine" method="post" id="fileUploadForm" ENCTYPE="multipart/form-data">
<input id="eventType" type="hidden" name="eventType" value='<%=eventType%>'/>
<input id="uploadItem" type="hidden" name="uploadItem" value='<%=uplodType%>'/>
<input id="eventIndex" type="hidden" name="eventIndex" value='<%=eventIndex%>'/>
<input id="uploadIndex" type="hidden" name="uploadIndex" value='<%=upload%>'/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>文件上传</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">文件:</td>
	       <td width="28%"><input type="file" class="input"  name="uploadFile"/></td>
	       <td width="12%" class="en11pxb">描述:</td>
	       <td width="28%"><input type="text" class="input"  name="desc1" id="desc1"/></td>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">文件:</td>
	       <td width="28%"><input type="file" class="input"  name="uploadFile"/></td>
	       <td width="12%" class="en11pxb">描述:</td>
	       <td width="28%"><input type="text" class="input"  name="desc2" id="desc2"/></td>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">文件:</td>
	       <td width="28%"><input type="file" class="input"  name="uploadFile"/></td>
	       <td width="12%" class="en11pxb">描述:</td>
	       <td width="28%"><input type="text" class="input"  name="desc3" id="desc3"/></td>
	    </tr>
	    <tr bgcolor="#DFE1EC">
	    <td width="20%" class="en11pxb" align="left" colspan="4">
	    	<table border="0" cellspacing="0" cellpadding="0">
			  <tr height="30">
			   	<td width="20">&nbsp;</td>
			    <td><ul class="button">
				<li><a class="button-text" href="#" onclick="javascript:fileSubmit();"><span>&nbsp;上传&nbsp;</span></a></li> 
				</ul></td>
			  </tr>
			</table>
	    </td>
	    </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>文件名列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
			<td width="10%" class="en11pxb">操作</td>
			<td width="90%" class="en11pxb">文件名</td>
        </tr>
        <ofbiz:if name="FILE_LIST">
	        <ofbiz:iterator name="file" property="FILE_LIST">
		        <tr bgcolor="#DFE1EC">
		        <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delFile('<ofbiz:inputvalue entityAttr="file" field="uploadIndex"/>')"></td>
		        <td class="en11px"><a href="#" onclick="fileLoad('<ofbiz:inputvalue entityAttr="file" field="uploadIndex"/>')"><ofbiz:entityfield attribute="file" field="fileUrl"/></a></td>
		        </tr>
	 	</ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>