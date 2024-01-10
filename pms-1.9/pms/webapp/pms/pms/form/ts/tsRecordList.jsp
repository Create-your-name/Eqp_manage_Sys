<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>

<%
	String eqpId=UtilFormatOut.checkNull(request.getParameter("eqpId"));
	String startDate=UtilFormatOut.checkNull(request.getParameter("startDate"));
	String endDate=UtilFormatOut.checkNull(request.getParameter("endDate"));
	List tsList = (List)request.getAttribute("TS_LIST");

	if("".equals(startDate)){
		startDate=UtilFormatOut.checkNull((String)request.getAttribute("startDate"));
	}
%>

<!-- yui page script-->
<script language="javascript">
	var eqpType;
	//查询
	function query(){
		var eqpId=Ext.get('eqpId').dom.value;
		var startDate=Ext.get('startDate').dom.value;
		var endDate=Ext.get('endDate').dom.valu;
		if((eqpId==""||eqpId==undefined) && (startDate==""||startDate==undefined) &&(endDate==""||endDate==undefined)){
			Ext.MessageBox.alert('警告', '查询条件不能都为空!');
			return;
		}
		loading();
		tsForm.submit();
	}

	//重置
	function reset(){
		 Ext.get('eqpId').dom.value='';
		 Ext.get('startDate').dom.value='';
		 Ext.get('endDate').dom.value='';
	}

	//进入补填作业页面
	function pathAdd(){
		var eqpId=Ext.get('eqpId').dom.value;
		if((eqpId==""||eqpId==undefined)){
			Ext.MessageBox.alert('警告', '请选择设备编号!');
			return;
		}
		var actionURL='<ofbiz:url>/intoPatchTsFormEntry</ofbiz:url>?eqpId='+eqpId;
		document.location.href=actionURL;
	}

	//进入表单新建页面
	function intoTsFormAdd(eqpId,seqIndex,abnormalTime,eqpStatus){
		loading();
	 	Ext.get('parentEqpId').dom.value=eqpId;
	 	Ext.get('seqIndex').dom.value=seqIndex;
	 	Ext.get('abnormalTime').dom.value=abnormalTime;
	 	Ext.get('eqpStatus').dom.value=eqpStatus;

	 	Ext.get('queryEqpId').dom.value=Ext.get('eqpId').dom.value;
	 	intoTsForm.submit();
	}

	//初始化页面控件
	Ext.onReady(function(){
	     var startDate = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });

	 	var endDate = new Ext.form.DateField({
	 		format: 'Y-m-d',
	        allowBlank:true
	    });

	    startDate.applyTo('startDate');
	    endDate.applyTo('endDate');

	    eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqpId',
	        width:170,
	        forceSelection:true
	    });
	    initEqpState();
	 });

	 function initEqpState(){
		 eqpType.setValue('<%=eqpId%>');
		 Ext.get('startDate').dom.value='<%=startDate%>';
		 Ext.get('endDate').dom.value='<%=endDate%>';
	 }
</script>
<form action="<%=request.getContextPath()%>/control/queryTsRecordEntry" method="post" id="tsForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>异常记录查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
          <td width="20%" bgcolor="#ACD5C9" class="en11pxb">设备</td>
          <td width="80%" colspan="3"><select id="eqpId" name="eqpId">
		          <option value=""></option>
		          <ofbiz:if name="EQPID_LIST">
			        <ofbiz:iterator name="cust" property="EQPID_LIST">
				    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentId"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentId"/></option>
			      </ofbiz:iterator>
		      	</ofbiz:if>
    		</select>
    		</td>
        </tr>
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">日期</td>
	        <td width="30%"><input type="text" ID="startDate" NAME="startDate" readonly size="26"></td>
	    	<td  width="5%" class="en11pxb">到:</td>
	    	<td width="45%"><input type="text" ID="endDate" NAME="endDate"  readonly size="26"></td>
        </tr>
        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					    <td><ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:query();"><span>&nbsp;查询&nbsp;</span></a></li>
						</ul>
						<ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:reset();"><span>&nbsp;重置&nbsp;</span></a></li>
						</ul>
						<ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:pathAdd();"><span>&nbsp;异常表单补填作业&nbsp;</span></a></li>
						</ul>
						</td>
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
      <legend>异常记录列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="20%" class="en11pxb">机台编号</td>
          <td width="20%" class="en11pxb">设备状态</td>
          <td width="25%" class="en11pxb">日期</td>
        </tr>
         <% if(tsList != null && tsList.size() > 0) {
       		for(Iterator it = tsList.iterator();it.hasNext();) {
					Map map = (Map)it.next();%>
	        <tr bgcolor="#DFE1EC">
    		    <td class="en11px">
    		        <a href="#" onclick="intoTsFormAdd('<%=map.get("EQUIPMENTID")%>','<%=map.get("SEQINDEX")%>','<%=map.get("STARTTIME")%>','<%=map.get("EQUIPMENT_STATUS")%>')">
    		            <%=map.get("EQUIPMENTID")%>
    		        </a>
    		    </td>

    		    <td class="en11px"><%=map.get("EQUIPMENT_STATUS")%></td>

    		    <td class="en11px"><%=map.get("STARTTIME")%></td>
		    </tr>
	     <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
<br />
<p class="en11px" style="color:red">
    注：<%=Constants.PR_START_STATUS%> 降低成本延迟恢复，仅当恢复时需处理，结束后设备状态修改为 <%=Constants.PR_END_STATUS%>
</p>

<form action="<%=request.getContextPath()%>/control/intoAddTsFormEntry" method="post" id="intoTsForm" onsubmit="return false;">
<input id="beginDate" type="hidden" name="beginDate" value='<%=startDate%>' />
<input id="eDate" type="hidden" name="eDate" value='<%=endDate%>' />
<input id="abnormalTime" type="hidden" name="abnormalTime" />
<input id="parentEqpId" type="hidden" name="parentEqpId"/>
<input id="seqIndex" type="hidden" name="seqIndex"/>
<input id="queryEqpId" type="hidden" name="queryEqpId"/>
<input id="eqpStatus" type="hidden" name="eqpStatus"/>
</form>
