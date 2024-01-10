<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<%
    String mtrNum=request.getParameter("mtrNum");
    String mtrDesc=request.getParameter("mtrDesc");
%>

<!-- ##################################### script ################################ -->
<script language="javascript"  >
	function doSubmit(url) {
		loading();
		document.getMtrObjectForm.action=url;
		document.getMtrObjectForm.submit();
	}

	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');

	//�������ܵ���ҳ
	function addMtrObject(obj,materialIndex){
	    //alert(materialIndex);
		Ext.get('usingObjectId_select').dom.disabled=false;
		Ext.get('equipmentType').dom.disabled=false;
		Ext.get('mtrObjectIndex').dom.value="";
		Ext.get('usingObjectId_select').dom.value="";
		Ext.get('usingObjectId').dom.value="";
		Ext.get('usableTimeLimit').dom.value="";
		Ext.get('objMaxUseAmount').dom.value="";
		Ext.get('stdUseAmount').dom.value="";
		Ext.get('materialIndex').dom.value=materialIndex;
		extDlg.showAddDialog(obj);
	}

    //�޸�
	function editMtrObject(obj,mtrObjectIndex,materialIndex,usingObjectId) {
	    //alert(materialIndex);
		//alert(usingObjectId);
		Ext.get('mtrObjectIndex').dom.value = mtrObjectIndex;
		Ext.get('materialIndex').dom.value = materialIndex;
		Ext.get('usingObjectId_select').dom.disabled=true;
		Ext.get('equipmentType').dom.disabled=true;
		var url='<ofbiz:url>/getJsonMtrObjectByIndex</ofbiz:url>?mtrObjectIndex='+mtrObjectIndex;
		extDlg.showEditDialog(obj,url);
	}

	//ɾ��
	function delMtrObject(mtrObjectIndex,materialIndex){
	    //alert(materialIndex);
		//var mtrNum=replace("<%=mtrNum%>","'","");
		//var mtrDesc=replace("<%=mtrDesc%>","'","");
		var mtrNum="<%=mtrNum%>";
		var mtrDesc="<%=mtrDesc%>";
		Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
			if(value=="yes"){
				var url='<ofbiz:url>/delMtrObjectByIndex</ofbiz:url>?mtrObjectIndex='+mtrObjectIndex+'&materialIndex='+materialIndex+'&mtrNum='+mtrNum+'&mtrDesc='+mtrDesc;
				document.location=url;
			}else{
				return;
			}
		});
	}

	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
		    //alert(result.usingObjectId);
			Ext.get('usingObjectId').dom.value=result.usingObjectId;
			Ext.get('usingObjectId_select').dom.value=result.usingObjectId;
			Ext.get('usingObjectIdHidden').dom.value=result.usingObjectId;
			Ext.get('usableTimeLimit').dom.value=result.usableTimeLimit;
			Ext.get('objMaxUseAmount').dom.value=result.objMaxUseAmount;
			Ext.get('stdUseAmount').dom.value=result.stdUseAmount;
		}
	}

	//���ݺϷ���У��
	function checkForm(){
		var equipmentType = trim(Ext.get('equipmentType').dom.value);
		var usingObjectId = trim(Ext.get('usingObjectId').dom.value);
		var usableTimeLimit = trim(Ext.get('usableTimeLimit').dom.value);
		var objMaxUseAmount = trim(Ext.get('objMaxUseAmount').dom.value);
		var stdUseAmount = trim(Ext.get('stdUseAmount').dom.value);

		if(equipmentType =="" && usingObjectId==""){
			return "�豸������߿����豸��ѡ������һ��";
		}
		if(equipmentType !="" && usingObjectId!=""){
			return "�豸������߿����豸��ѡ������һ��";
		}

		if(usableTimeLimit!=""){
			if (IsNumeric(usableTimeLimit) ) {
				if ( usableTimeLimit <1 ) {
					return "ʹ�������������0";
				}
			} else {
			    return "ʹ������ֻ��Ϊ����";
			}
		}

		if(objMaxUseAmount!=""){
			if (IsNumeric(objMaxUseAmount) ) {
				if ( objMaxUseAmount <1 ) {
					return "�豸ͬʱʹ����������������0";
				}
			} else {
			    return "�豸ͬʱʹ���������ֻ��Ϊ����";
			}
		}

		if(stdUseAmount!=""){
		    if (IsNumeric(stdUseAmount) ) {
		        if ( stdUseAmount <1 ) {
		            return "�豸��׼�����������0";
		        }
		    } else {
		        return "�豸��׼����ֻ��Ϊ����";
		    }
		}

		return "";
	}

	function clearEqp(){
		Ext.getDom('usingObjectId_select').value = "";
		Ext.getDom('usingObjectId').value = "";
	}

	function clearEqpType(){
		Ext.getDom('equipmentType').value = "";
		Ext.getDom('usingObjectId').value = Ext.getDom('usingObjectId_select').value;
	}

	function cancel(url) {
		window.navigate(url);
	}

	function IsNumeric(str)
	{
			var chrC;
			   var intC;
			   var intLen;
			   var intI;
			   var token=0;//tag the number of "."
			   var token1=0;//tag the number of "e" or "E"
			   str=Cstr(str);
			   str=Trim(str);
			   intLen=str.length;
			   intI=0;
			   chrC=str.substring(intI,intI+1);//get the first char
			   if ((chrC.indexOf("+")!=-1)||(chrC.indexOf("-")!=-1)) intI++;
			   chrC=str.substring(intI,intI+1);//remorve first + or -
			   if (isNaN(parseInt(chrC)) && (chrC.indexOf(".")==-1) && (chrC.indexOf("E")==-1) && (chrC.indexOf("e")==-1)) return false;
			  //first char is E or e
			   if ((chrC.indexOf("e")!=-1) || (chrC.indexOf("E")!=-1))
			   {
					return false;
			intI++;
			if (intI<intLen)
			{
					chrC=str.substring(intI,intI+1);
					  if (isNaN(parseInt(chrC)) && (chrC.indexOf("+")==-1) && (chrC.indexOf("-")==-1)) return false;
					  if ((chrC.indexOf("+")!=-1) || (chrC.indexOf("-")==-1))
					  {
							   intI++;
					  }
					  for(;intI<intLen;intI++)
					  {
							  chrC=str.substring(intI,intI+1);
							if(isNaN(parseInt(chrC)))
							{
									  return false;
						}
					  }
				   }
			return true;
			}
			   //first char is not a E or e
			   intI++;
			   for(;intI<intLen;intI++)
			   {
				chrC=str.substring(intI,intI+1);
					if((isNaN(parseInt(chrC))) && (chrC.indexOf(".")==-1) && (chrC.indexOf("e")==-1) && (chrC.indexOf("E")==-1))
					 {
							   return false;
				 }
				 //exist "."
				 if((isNaN(parseInt(chrC))) && (chrC.indexOf(".")!=-1))
				 {
						   //intI++;
						   token++;
						   if (token!=1) return false;
				 }
				 if((chrC.indexOf("e")!=-1)|| (chrC.indexOf("E")!=-1))
				 {
						   intI++;token1++;
						   if (token1!=1) return false;
				}
			}
			return true;
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>
		<% out.print("���Ϻ�:"+(String )request.getParameter("mtrNum")+"&nbsp;");
		   out.print("��������:"+(String )request.getParameter("mtrDesc"));
		%>&nbsp;�����豸��Ϣ�б�
	 </legend>
      <table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
            <td width="5%" class="en11pxb" align="center"><img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addMtrObject(this,<%=(String )request.getAttribute("materialIndex")%>);"/></td>
			<td width="30%" class="en11pxb">�����豸</td>
			<td class="en11pxb">ʹ������(��)</td>
      		<td class="en11pxb">�豸ͬʱʹ���������</td>
      		<td class="en11pxb">�豸��׼����</td>
    	</tr>

	    <ofbiz:iterator name="cust" property="mtrObjectList">
	         <tr class="tablelist" id="objTr1" style="cursor:hand">
		        <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand"  onclick="delMtrObject('<ofbiz:inputvalue entityAttr="cust" field="mtrObjectIndex"/>','<ofbiz:inputvalue entityAttr="cust" field="materialIndex"/>');"/></td>
	        	<td class="en11px">
				    <a href="#"  onclick="editMtrObject(this,'<ofbiz:inputvalue entityAttr="cust" field="mtrObjectIndex"/>','<ofbiz:inputvalue entityAttr="cust" field="materialIndex"/>','<ofbiz:entityfield attribute="cust" field="usingObjectId" />')">
				        <ofbiz:entityfield attribute="cust" field="usingObjectId" />
				    </a>
				</td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="usableTimeLimit" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="objMaxUseAmount" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="stdUseAmount" /></td>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td>
		<ul class="button">
				<li><a class="button-text" href="#" onclick="javascript:cancel('<ofbiz:url>/defineMaterial</ofbiz:url>'+'?materialIndex='+<%=(String )request.getAttribute("materialIndex")%>)"><span>&nbsp;����&nbsp;</span></a></li>
		</ul>
	</td>
  </tr>
</table>
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">�����豸�趨</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�����豸�趨">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/saveMtrObject?mtrNum=<%=mtrNum%>&mtrDesc=<%=mtrDesc%>" method="post" id="saveMtrObjectForm" name="saveMtrObjectForm" onsubmit="return false;">
                	<input id="mtrObjectIndex" type="hidden" name="mtrObjectIndex" value="" />
					<input id="materialIndex" type="hidden" name="materialIndex" value="">
                <p>
                <label for="name"><small>�豸����</small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
					<select id="equipmentType"  name="equipmentType" onchange="clearEqp()"   class="select-short">
						<option value=''></option>
						<ofbiz:if name="eqpTypeList" >
							<ofbiz:iterator name="equipmentType" property="eqpTypeList" type="java.util.Map">
								<option value='<ofbiz:inputvalue entityAttr="equipmentType" field="EQUIPMENT_TYPE"/>'><ofbiz:inputvalue entityAttr="equipmentType" field="EQUIPMENT_TYPE"/></option>
							</ofbiz:iterator>
						</ofbiz:if>
					</select>
                <label for="name"><small>�����豸</small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <input class="textinput" type="hidden" readonly="" name="usingObjectIdHidden" id="usingObjectIdHidden" value="" size="22" tabindex="1" />
					<select id="usingObjectId_select" name="usingObjectId_select" onchange="clearEqpType()"  class="select-short">
						<option value=''></option>
						<ofbiz:if name="eqpList" >
							<ofbiz:iterator name="equipment" property="eqpList" type="java.util.Map">
								<option value='<ofbiz:inputvalue entityAttr="equipment" field="EQUIPMENT_ID"/>'><ofbiz:inputvalue entityAttr="equipment" field="EQUIPMENT_ID"/></option>
							</ofbiz:iterator>
						</ofbiz:if>
					</select>
				<input class="textinput" type="hidden" name="usingObjectId" id="usingObjectId" value="" size="22" tabindex="1" readonly />

				<label for="name"><small>ʹ������(��)</small></label>
                <input class="textinput" type="text" name="usableTimeLimit" id="usableTimeLimit" value="" size="22" tabindex="1" />
                <label for="name"><small>�豸ͬʱʹ���������</small></label>
                <input class="textinput" type="text" name="objMaxUseAmount" id="objMaxUseAmount" value="" size="22" tabindex="1" />
                <label for="name"><small>�豸��׼����</small></label>
                <input class="textinput" type="text" name="stdUseAmount" id="stdUseAmount" value="" size="22" tabindex="1" />
                </p>
                </form>
            </div>
			<div class="inner-tab" >
				<p>
					<font color="#FF0000" face="����" size="-1">
						Tips :
						<br>
						&nbsp;1. ѡ���豸�����൱��ѡ���˸��豸�����Ӧ�������豸
						<br>
						&nbsp;2. "�豸ͬʱʹ���������",���Բ���,�����������0
                        <br>
                        &nbsp;3. "�豸��׼����"���ڼ����쳣����ͳ��,��ʹ���������ڱ�׼�����ᴥ���ʼ�����,���Բ���,�����������0
					</font>
				</p>
			</div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>


