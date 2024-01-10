<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>


<%
	GenericValue materialInfo = (GenericValue) request.getAttribute("materialInfo");
	boolean addFlag = (materialInfo==null);

    String mtrGrp = addFlag ? "" : UtilFormatOut.checkNull(materialInfo.getString("mtrGrp"));
    boolean isSparePart = (ConstantsMcs.SPAREPART_2P.equals(mtrGrp) || ConstantsMcs.SPAREPART_2S.equals(mtrGrp));

	String enabled = "1";
	String flag = "0";

%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
	function doSubmit(url)
	{
		if(Trim(Ext.getDom('mtrNum').value)=='') {
			Ext.MessageBox.alert('����', '�ϺŲ���Ϊ��!');
			return;
		}

		if(Trim(Ext.getDom('mtrNum').value)!=Trim(Ext.getDom('histMtrNum').value)) {
			Ext.MessageBox.alert('����','�������Ϻŵ�ֵ������ѡ���Ϻ�');
	    	document.MaterialForm.mtrDesc.value="";
			document.MaterialForm.mtrGrp.value="";
			document.MaterialForm.plant.value="";
		    return;
		}

		if(Trim(Ext.getDom('mtrDesc').value)=='') {
			Ext.MessageBox.alert('����', '������������Ϊ��!');
			return;
		}

		if(Trim(Ext.getDom('mtrGrp').value)=='') {
			Ext.MessageBox.alert('����', '�����鲻��Ϊ��');
			return;
		}

		if (Ext.getDom('frozenTimeLimit') != null && Trim(Ext.getDom('frozenTimeLimit').value)==''){
			Ext.MessageBox.alert('����', '�ⶳʱ�䲻��Ϊ��');
			return;
		}

		if (Ext.getDom('needScrapStore') != null && Trim(Ext.getDom('needScrapStore').value)==''){
			Ext.MessageBox.alert('����','ȫ���������Ҫ������ⲻ��Ϊ��');
			return;
		}

		if(Trim(Ext.getDom('needVendorRecycle').value)==''){
			Ext.MessageBox.alert('����','���������賧�̻��ղ���Ϊ��');
			return;
		}

		if(Trim(Ext.getDom('inControl').value)=='0'){
			alertMsg = "���ø��Ϻţ������ϵͳ������������ü�ʹ�ü�¼��ȷ���޸���";
		} else {
		    alertMsg = "ȷ���޸���";
		}

		Ext.MessageBox.confirm('����ȷ��', alertMsg, function result(value){
    		if(value=="yes"){
        		loading();
        		document.MaterialForm.action = url;
        		document.MaterialForm.submit();
    		}else{
    			return;
    		}
	    });
	}

	function cancel(url) {
		loading();
		document.MaterialForm.action = url;
		document.MaterialForm.submit();
	}

	function doOpen(url) {
		window.open(url,"serachwindow",
		"top=130,left=240,width=685,height=180,title=,channelmode=0," +
		"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
		"scrollbars=1,status=0,titlebar=0,toolbar=no");
	}

	function doTurnto(url) {
		window.navigate(url);
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm"  method="POST">
<input name="materialIndex" type="hidden" class="input" value='<%=addFlag?"":(String )request.getAttribute("materialIndex")%>' readonly>
 <input id="Pflag" type="hidden" name="Pflag" value="" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
	<td valign="top"> <fieldset> <legend><%=addFlag?"����":"�޸�"%>������Ϣ��ϸ</legend>
        <table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
        <tr bgcolor="#DFE1EC">
            <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.material_number" /><i18n:message key="mcs.data_required_red_star" /></td>
    	    <td width="36%">
    	        <input name="mtrNum" type="text" <% if (addFlag==false) {%>  readonly <% } %>  class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("mtrNum"))%>' />
		  		<input name="histMtrNum" type="hidden" value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("mtrNum"))%>'>
		        <%if (addFlag == true ) {%>
		  	    <A href="#" onClick="doOpen('<ofbiz:url>/getSapMtrNum</ofbiz:url>'+'?mtrNum='+mtrNum.value);">
                   <img height="16" src="<%=request.getContextPath()%>/images/icon_search.gif" width="15" border="0">
                </A>
		        <%}%>
		    </td>

            <td width="17%" bgcolor="#ACD5C9"><i18n:message key="mcs.material_description" /></td>
            <td width="29%">
                <input name="mtrDesc" type="text" style="width:90%" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("mtrDesc"))%>' readonly >
            </td>
		</tr>

		<tr bgcolor="#DFE1EC">
            <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.material_group" /></td>
    	    <td width="36%">
		        <input name="mtrGrp" type="text" class="input" value='<%=mtrGrp%>' readonly >
		    </td>
		    <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.plant" /></td>
		    <td width="36%"><input name="plant" style="text" class="input" value='<%=Constants.PLANT%>' readonly ></td>
	    </tr>

        <%if (isSparePart) {%>
	    <tr bgcolor="#DFE1EC">
		    <td width="18%"  bgcolor="#ACD5C9"><i18n:message key="mcs.need_scrap_store" /><i18n:message key="mcs.data_required_red_star" /></td>
		    <td width="36%" >
		        <select id="needScrapStore" name="needScrapStore" class="select-short">
					<option value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("needScrapStore"))%>'><%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("needScrapStore"))%></option>
					<option value='Y'>Y</option>
					<ofbiz:if name="flag" value="OK">
					<option value='N'>N</option>
				    </ofbiz:if>
				</select>
		    </td>


		    <td width="18%" bgcolor="#ACD5C9">��ǰ��������</td>
		    <td width="36%">
		        <input name="preStoNumber" style="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("preStoNumber"))%>' >
		    </td>
		</tr>
		<%}%>

        <%if (isSparePart) {%>
		<tr bgcolor="#DFE1EC">
		    <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.usable_time_limit" /></td>
		    <td width="36%">
		        <input name="usableTimeLimit" style="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("usableTimeLimit"))%>' >
		    </td>

		    <td width="18%"  bgcolor="#ACD5C9">����������ǰ��������(��)</td>
		    <td width="36%" >
		         <input name="preAlarmDays" style="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("preAlarmDays"))%>' >
		    </td>
		</tr>
		<%}%>

        <%if (ConstantsMcs.CHEMICAL.equals(mtrGrp) || ConstantsMcs.PHOTORESIST.equals(mtrGrp)) {%>
		<tr bgcolor="#DFE1EC">
		    <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.frozen_time_limit" />
		   		<i18n:message key="mcs.data_required_red_star" />
		    </td>
		    <td width="36%">
			    <input name="frozenTimeLimit" style="text" class="input" value='<%=addFlag?"0":UtilFormatOut.checkNull(materialInfo.getString("frozenTimeLimit"))%>' >
			</td>

            <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.max_frozen_time_limit" /></td>
		    <td width="36%">
		        <input name="maxFrozenTimeLimit" style="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("maxFrozenTimeLimit"))%>' >
		    </td>
		</tr>
		<%}%>

		<tr bgcolor="#DFE1EC">
		    <td width="18%"  bgcolor="#ACD5C9">�Ƿ�����MCS�ܿ�<i18n:message key="mcs.data_required_red_star" /></td>
		    <td width="36%" >
		        <select id="inControl" name="inControl" class="select-short">
					<option value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("inControl"))%>'><%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("inControl"))%></option>
					<option value='1'>1����(Ĭ��ֵ)</option>
					<option value='0'>0����(ɾ�����Ϻ�ϵͳ������������ʹ�ü�¼)</option>
				</select>
		    </td>

		    <td width="18%"></td>
		    <td width="36%"></td>
		</tr>

		<input name="deptIndex" type="hidden" style="text"  class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("deptIndex"))%>' readonly >
		<input type="hidden" name="flag" id="flag" value="<%=flag%>">
		<input type="hidden" id="needVendorRecycle" name="needVendorRecycle" value="N">

	  </table>
    </fieldset>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td>
		<ul class="button">
				<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/saveMaterial</ofbiz:url>')"><span>&nbsp;<%=addFlag?"����":"�޸�"%>&nbsp;</span></a></li>
		</ul>
	</td>
    <td>
		<ul class="button">
				<li><a class="button-text" href="javascript:cancel('<ofbiz:url>/defineMaterialEntry</ofbiz:url>')"><span>&nbsp;����&nbsp;</span></a></li>
		</ul>
	</td>
<% if ((addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("enabled"))).equals(enabled)) { %>
    <td>
		<ul class="button">
				<li><a class="button-text" href="#" onclick="doTurnto('<ofbiz:url>/getMtrObject</ofbiz:url>'+'?materialIndex='+materialIndex.value+'&mtrNum='+mtrNum.value+'&mtrDesc='+mtrDesc.value)"><span>&nbsp;�����豸ά��&nbsp;</span></a></li>
		</ul>
	</td>
<% } %>
  </tr>
</table>

<% if ((addFlag?"":UtilFormatOut.checkNull(materialInfo.getString("enabled"))).equals(enabled)) { %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>��ά���豸�б�</legend>
      <table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
			<td width="20%" class="en11pxb">�����豸</td>
			<td class="en11pxb">�����豸ʹ������(��)</td>
      		<td class="en11pxb">�豸ͬʱʹ���������</td>
      		<td class="en11pxb">�豸��׼����</td>
    	</tr>

	    <ofbiz:iterator name="cust" property="mtrObjectList">
	         <tr class="tablelist" id="objTr1" style="cursor:hand">
	        	<td class="en11px">
					<ofbiz:entityfield attribute="cust" field="usingObjectId" />
				</td>
				<td class="en11px">
					<ofbiz:entityfield attribute="cust" field="usableTimeLimit" />
				</td>
	        	<td class="en11px">
					<ofbiz:entityfield attribute="cust" field="objMaxUseAmount" />
				</td>
				<td class="en11px">
				<ofbiz:entityfield attribute="cust" field="stdUseAmount" />
				</td>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>
<% } %>
<br>

</form>