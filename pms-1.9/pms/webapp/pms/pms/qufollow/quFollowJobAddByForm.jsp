<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.apache.commons.lang.StringUtils"%>

<%
	String dept=(String)request.getAttribute("DEPT");
	String section=(String)request.getAttribute("SECTION");
	String deptIndex=(String)request.getAttribute("DEPT_INDEX");
	String sectionIndex=(String)request.getAttribute("SECTION_INDEX");
	String creator=(String)request.getAttribute("creator");

	String eqpId=(String)request.getParameter("eqpId");
	if(StringUtils.isEmpty(eqpId)){
		eqpId=(String)request.getParameter("object");
	}

	String eventType=(String)request.getParameter("eventType");
	String eventIndex=(String)request.getParameter("eventIndex");
%>

<!-- yui page script-->
<script language="javascript">
	//保存
	function save(){
		var followName = Ext.get('followName').dom.value;
        var purpose = Ext.get('purpose').dom.value;
        if(followName==''){
        	Ext.MessageBox.alert('警告', '请输入问题名称!');
        	return;
        }

        if(followName.length >19){
        	Ext.MessageBox.alert('警告', '名称输入过长!');
        	return;
        }

        if(document.getElementById("object").value==""){
        	Ext.MessageBox.alert('警告', '请选择设备大类或设备!');
        	return;
        }
        if(purpose==''){
        	Ext.MessageBox.alert('警告', '请输入目的!');
        	return;
        }
		follwoJobForm.submit();
	}
</script>

<form action="<%=request.getContextPath()%>/control/followJobDefine" method="post" id="follwoJobForm" onsubmit="return false;">
<input id="deptIndex" type="hidden" name="deptIndex" value="<%=deptIndex%>" />
<input id="sectionIndex" type="hidden" name="sectionIndex" value="<%=sectionIndex%>" />
<input id="status" type="hidden" name="status" value="0" />
<input id="objectType" type="hidden" name="objectType" value='EQUIPMENT'/>
<input id="object" type="hidden" name="object" value='<%=eqpId%>'/>
<input id="type" type="hidden" name="type" value='FORM'/>
<input id="eventType" type="hidden" name="eventType" value='<%=eventType%>'/>
<input id="eventIndex" type="hidden" name="eventIndex" value='<%=eventIndex%>'/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>问题追踪查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
	        <td width="15%" bgcolor="#ACD5C9" class="en11pxb" height="20">问题追踪名称</td>
	    	<td width="35%"  class="en11px" colspan="3"><input type="text" name="followName" class="input" size="50"></td>
        </tr>

        <tr bgcolor="#DFE1EC">
	    	<td bgcolor="#ACD5C9" class="en11pxb" height="20">目的</td>
	    	<td class="en11pxb" colspan="3"><input type="text" name="purpose" class="input" size="50"></td>
        </tr>

        <tr bgcolor="#DFE1EC">
	        <td width="15%" bgcolor="#ACD5C9" class="en11pxb" height="20">部门</td>
	    	<td width="35%"  class="en11px"><%=dept%></td>
	    	<td width="15%" bgcolor="#ACD5C9" class="en11pxb">课别</td>
	    	<td width="35%" class="en11px"><%=section%></td>
        </tr>

        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb" >设备</td>
          <td class="en11pxb"><%=eqpId%></td>
    		<td bgcolor="#ACD5C9" class="en11pxb">创建者</td>
	    	<td class="en11px"><%=creator%></td>
        </tr>

        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					   	<td width="20">&nbsp;</td>
					    <td><ul class="button">
								<li><a class="button-text" href="#" onclick="javascript:save();"><span>&nbsp;创建&nbsp;</span></a></li>
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