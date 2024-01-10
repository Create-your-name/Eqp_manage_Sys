<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.List"%>
<% 
	List fileList=(List)request.getAttribute("fileList");
%>
<!-- yui page script-->
<script language="javascript">
	function fileSubmit(){
		fileUploadForm.submit();
	}

</script>
<form action="<%=request.getContextPath()%>/control/fileUploadDefineEntry" method="post" id="fileUploadForm" ENCTYPE="multipart/form-data">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>文件上传</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">文件:</td>
	       <td width="28%"><input type="file" class="input"  name="uploadFile"/></td>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">文件:</td>
	       <td width="28%"><input type="file" class="input"  name="uploadFile"/></td>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">文件:</td>
	       <td width="28%"><input type="file" class="input"  name="uploadFile"/></td>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">name:</td>
	       <td width="28%"><input type="text" class="input"  name="textName" id="textName"/></td>
	    </tr>
	    <tr>
	    <td width="20%" class="en11pxb" align="left">
	    	<table border="0" cellspacing="0" cellpadding="0">
			  <tr height="30">
			   	<td width="20">&nbsp;</td>
			    <td><ul class="button">
				<li><a class="button-text" href="#" onclick="javascript:fileSubmit();"><span>&nbsp;确定&nbsp;</span></a></li> 
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
          <td width="30%" class="en11pxb">文件名</td>
        </tr>
        		<% if (fileList!=null){
        			for (int i=0;i<fileList.size();i++){
        			String name=(String)fileList.get(i);
        		 %>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11px"><%=name%></td>
		        </tr>
			      <% 
			      	}
			      }
			      %>
      </table>
      </fieldset></td>
  </tr>
</table>