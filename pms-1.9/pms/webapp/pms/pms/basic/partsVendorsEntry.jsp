<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>
<% 	
	List partsList=(List)request.getAttribute("partsList");
	List partsVendorsList=(List)request.getAttribute("partsVendorsList");
	String partsId=(String)request.getAttribute("partsId"); 
	int size=0;
	if(partsVendorsList!=null){
		size=partsVendorsList.size();
	}
%>
<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	function partsVendorsListQuery(){
		var partsId=document.getElementById("partsId").value;
		if(partsId==null||partsId==""){
			alert("��ѡ�����Ϻ�");
			return;
		}
		partsVendorsForm.submit();
	}
	
	function addPartsVendors(obj){
		var url="";
        extDlg.showEditDialog(obj,url);
	}
	
	function commentSuccess(o){
	}
	
	var commentFailure = function(o){
        Ext.MessageBox.alert('Status', 'Unable to connect.');
    };
    
    function partsIdCheck(){
    	window.open ("<%=request.getContextPath()%>/control/queryVendorsPartsList","parts","top=130,left=240,width=650,height=350,title=,channelmode=0," +
				"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
				"scrollbars=1,status=1,titlebar=0,toolbar=0");
    }
    
    function checkForm(){
    	var index = Ext.DomQuery.select('tr',Ext.getDom('addVendors')).length;
	    var partsId=Ext.get('aPartsId').dom.value;
    	for(i=0;i<index;i++){
	    var mark="aVendors_"+i;
	    	var aVendors=Ext.get(mark).dom.value;
	    	if(aVendors==""){
	    		return "��Ӧ�̲���Ϊ��";
	    	}
    	}
    	var actionUrl=document.getElementById("addPartsVendorsForm").action;
		document.getElementById("addPartsVendorsForm").action=actionUrl+"?partsId="+partsId+"&size="+index;
     	return "";
    }
    
	function editPartsVendors(){
		for(i=0;i<<%=size%>;i++){
			var vendors=document.getElementById("vendors_"+i).value;
			if(vendors==""){
				alert("��Ӧ�̲���Ϊ��");
	    		return;
	    	}
		}
		var actionUrl=document.getElementById("editPartsVendorsForm").action;
		document.getElementById("editPartsVendorsForm").action=actionUrl+"?size=<%=size%>&partsId=<%=partsId%>";
		editPartsVendorsForm.submit();
	}
	
    function delPartsVendors(obj,partsVendorsId,partsId,vendors){
    	Ext.MessageBox.confirm('ɾ��ȷ��', '�Ƿ�ȷ��ɾ���˹�Ӧ��!',function result(value){
            if(value=="yes"){ 
		    	var actionUrl="<%=request.getContextPath()%>/control/delPartsVendors?partsId="+partsId+"&partsVendorsId="+partsVendorsId;
                loading();
                document.location.href=actionUrl;
            }else{
                return;
            }
        });      
    }
    
    function EditClick(){
    	for(i=0;i<<%=size%>;i++){
    		document.getElementById("vendors_"+i).style.background="#FFFFFF";
    		document.getElementById("vendors_"+i).readOnly=false;
    		document.getElementById("minus_"+i).style.display="none";
    	}
    	document.getElementById("edit").style.display="none";
		document.getElementById("save").style.display="";
    } 
    
    function addActionStatus(){
	//��ñ��е�����
		var index = Ext.DomQuery.select('tr',Ext.getDom('addVendors')).length;
		//������к�
		var orderNum = index;
		//������
		Ext.get('addVendors').createChild({
			tag:'tr', style:'background-color:#DFE1EC', children: [
				{tag: 'td', html:'��Ӧ��',cls:'en11pxb',width:'10%'},
				{tag: 'td', html:'<input id=="aVendors_'+orderNum+'" name="aVendors_'+orderNum+'" type="text", style="width:30%", cls="input" />',cls:'en11pxb',width:'90%'}
		]});		
	}
	
	Ext.onReady(function(){
	    var partsType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'partsId',
	        width:170,
	        forceSelection:true
	    });
	});	
</script>
<form name="partsVendorsForm" method="POST" action="<%=request.getContextPath()%>/control/partsVendorsList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td><fieldset><legend>��ѯ�豸����</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30"> 
          <td class="en11pxb" width="10%">���Ϻ�</td>
          <td class="en11pxb" width="90%">
    		<select id="partsId" name="partsId">
          			<option value=''></option>
          	<%
          		if(partsList != null && partsList.size() > 0) {  
            		int k = 0;
            		for(Iterator it = partsList.iterator();it.hasNext();) { 
                		Map map = (Map)it.next();
          	%>
          			<option value='<%=map.get("PARTS_ID")%>'><%=map.get("PARTS_ID")%></option>
          	<%
          			}
          		}
          	%>
    			</select>
			<img src="<%=request.getContextPath()%>/images/search.gif" style="cursor:hand" onclick="partsIdCheck()"/>
          </td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	 	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:partsVendorsListQuery();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>
</form>

<ofbiz:if name="flag" value="OK">
<div id="keyEqpPartsList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="keyEqpPartsList" style="visibility:'hidden';">
</ofbiz:unless>

<form id="editPartsVendorsForm" name="editPartsVendorsForm" method="POST" action="<%=request.getContextPath()%>/control/editPartsVendors">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
	  <td><fieldset>
	      <legend>���Ϲ�Ӧ���б�</legend>
	      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#ACD5C9"> 
	          <td width="10%" class="en11pxb">���Ϻ�</td>      
	          <td width="90%" class="en11pxb">��Ӧ��</td>
	        </tr>     
	       	<tr bgcolor="#DFE1EC">
	       	 <td width="10%" class="en11px"><input type="hidden" id="partsNo" name="partsNo" value="<%=partsId%>" /><%=partsId%></td>
			 <td width="90%" class="en11px">
			 	<table id="partsVendorsListForm" name="partsVendorsListForm">
			 	<% 
			 		if(partsVendorsList != null && partsVendorsList.size() > 0) {  
	        			int k = 0;
	            		for(Iterator it = partsVendorsList.iterator();it.hasNext();) { 
	                    	Map map = (Map)it.next();
			 	%>
					<tr bgcolor="#DFE1EC">
						<input type="hidden" id="parts_vendors_id_<%=k%>" name="parts_vendors_id_<%=k%>" value="<%=map.get("PARTS_VENDORS_ID")%>" />
						<td width="5%" class="en11px" align="center"><div id="minus_<%=k%>" ><img name="" src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delPartsVendors(this,'<%=map.get("PARTS_VENDORS_ID")%>','<%=map.get("PARTS_ID")%>','<%=map.get("VENDORS")%>')"/></div></td>
						<td width="75%" class="en11px" ><input id="vendors_<%=k%>" name="vendors_<%=k%>" value="<%=map.get("VENDORS")%>" readOnly style="background:E9E9E9" type="text" class="input" /></td>
					</tr>
				<%
	          		k++;
		        		}
		      		} 
		     	%>		
				</table>
	          <td>
			</tr>
	      </table>
	      </fieldset></td>
	  </tr>
	</table>
	<table border="0" cellspacing="0" cellpadding="0">
	  <tr height="30">
	    <td><div id="edit"><ul class="button">
	            <li ><a class="button-text" id="tempSave" href="#" onclick="EditClick();" ><span>&nbsp;�޸�&nbsp;</span></a></li> 
	    </ul></div></td>
	    <td><div id="save" style="display:'none';"><ul class="button">
	            <li><a class="button-text" id="overForm" href="#" onclick="editPartsVendors();"><span>&nbsp;����&nbsp;</span></a></li> 
	    </ul><div></td>
	    <td><div id="add"><ul class="button">
	            <li><a class="button-text" id="addForm" href="#" onclick="addPartsVendors();"><span>&nbsp; ����&nbsp;</span></a></li> 
	    </ul><div></td>	    
	  </tr>
	</table>
</form>
</div>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">��Ӧ������</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="��Ӧ������">
            <div class="inner-tab" id="x-form">
                <form name="addPartsVendorsForm" action="<%=request.getContextPath()%>/control/addPartsVendors" method="post" id="addPartsVendorsForm" onsubmit="return false;">
                	<table id="addPartsId" width="100%" border="0" cellspacing="1" cellpadding="2">
                		<tr bgcolor="#ACD5C9"><td class="en11pxb" width="10%">���Ϻ�</td><td width="90%" class="en11px"><%=partsId%><input type="hidden" id="aPartsId" name="aPartsId" value="<%=partsId%>" />
                		<img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addActionStatus()"/></td> 
                		</tr>
                	</table>
                	<table id="addVendors" width="100%" border="0" cellspacing="1" cellpadding="2">
                	</table>
                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
    	</div>
    </div>
</div>

<script language="javascript"> 	
		var deptobj=document.getElementById('partsId');		
		deptobj.value='<%=partsId%>'
</script>
