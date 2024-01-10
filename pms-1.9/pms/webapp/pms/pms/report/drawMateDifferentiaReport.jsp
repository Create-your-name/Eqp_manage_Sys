<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%
	List partList = (List)request.getAttribute("PART_LIST");
	String date=UtilFormatOut.checkNull(request.getParameter("date"));
	String deptMent=UtilFormatOut.checkNull(request.getParameter("deptMent"));
	String partNo=UtilFormatOut.checkNull(request.getParameter("partNo"));
 %>

<!-- yui page script-->
<script language="javascript">
	function openPartInfo(partNo){
		var url="<%=request.getContextPath()%>/control/pmPartsInfoList?date=<%=date%>&deptMent=<%=deptMent%>&partNo="+partNo
		window.open(url,"serachwindow",
		"top=130,left=240,width=685,height=500,title=,channelmode=0," +
		"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
		"scrollbars=1,status=1,titlebar=0,toolbar=no");
	}
	//��ѯ
   function partsUserQuery(){
		var date=Ext.get('date').dom.value;
		var deptMent=Ext.get('deptMent').dom.value;
		var partNo=Ext.get('partNo').dom.value;
		if(date==""&&deptMent==""&&partNo==""){
				Ext.MessageBox.alert('����', '������ѡ��һ����ѯ����!');
				return;
		}
   		partsDataQueryForm.submit();
   }
	//��ʼ��ҳ��ؼ�
	Ext.onReady(function(){
	    var date = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'date',
	        width:170,
	        forceSelection:true
	    });
	    
	      var deptMent = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'deptMent',
	        width:170,
	        forceSelection:true
	    });
	 });
</script>
<form action="<%=request.getContextPath()%>/control/pmPartsUseDiff" method="post" id="partsDataQueryForm" name="partsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�������Ϻ�ʹ�ò����ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">ʱ������:</td>
	       <td width="28%"><select id="date" name="date">
	        <option value=""></option>
	       <option value="30">һ��</option>
	       <option value="90">һ����</option>
	       <option value="365">һ��</option>
	       </select></td>
	    <td width="12%" class="en11pxb">����:</td>
	    <td width="28%"><select id="deptMent" name="deptMent">
	          <option value=""></option>
	         <ofbiz:if name="DEP_LIST">
		        <ofbiz:iterator name="dept" property="DEP_LIST">
			    <option value='<ofbiz:inputvalue entityAttr="dept" field="deptIndex"/>'><ofbiz:inputvalue entityAttr="dept" field="equipmentDept"/></option>
		      </ofbiz:iterator>
	      	</ofbiz:if></td>
	    </tr>
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">���Ϻ�:</td>
	       <td width="28%" colspan="3"><input class="input" type="text" name="partNo" id="partNo" /></td>
	    </tr>
	    <tr bgcolor="#DFE1EC">
	   		<td width="20%" class="en11pxb" align="left" colspan="4">
		    	<table border="0" cellspacing="0" cellpadding="0">
				  <tr height="30" >
				   	<td width="0">&nbsp;</td>
				    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:partsUserQuery();"><span>&nbsp;��ѯ&nbsp;</span></a></li> 
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
      <legend>�������Ϻ�ʹ�ò����б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="30%" class="en11pxb">���Ϻ�</td>
          <td width="20%" class="en11pxb">��ȡ����</td>
          <td width="20%" class="en11pxb">ʹ������</td>
          <td width="30%" class="en11pxb">������������ֵ</td>
        </tr>
       <% if(partList != null && partList.size() > 0) {  
       		for(Iterator it = partList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();%>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11px"><%=map.get("PART_NO")%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("REQUIRE_SUM"))%></td>
		          <td class="en11px"><a href="#" onclick="openPartInfo('<%=map.get("PART_NO")%>');"><%=UtilFormatOut.checkNull((String)map.get("USE_SUM"))%></a></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("PART_SUB"))%></td>
		        </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
<br>
<script language="javascript">
		var obj=document.getElementById('deptMent');
		obj.value='<%=deptMent%>'
		
		var obj=document.getElementById('date');
		obj.value='<%=date%>'
		
		var obj=document.getElementById('partNo');
		obj.value='<%=partNo%>'
</script>