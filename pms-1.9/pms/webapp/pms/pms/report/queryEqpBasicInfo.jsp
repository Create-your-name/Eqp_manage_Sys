<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%

%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>��̨��������</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <ofbiz:if name="chk_ownDept" value="on"><td class="en11pxb">ӵ�в���</td></ofbiz:if>
          <ofbiz:if name="chk_maintDept" value="on"><td class="en11pxb">ά������</td></ofbiz:if>
          <ofbiz:if name="chk_location" value="on"><td class="en11pxb">��������</td></ofbiz:if>
          <ofbiz:if name="chk_section" value="on"><td class="en11pxb">SECTION</td></ofbiz:if>

          <ofbiz:if name="chk_equipmentType" value="on"><td class="en11pxb">�豸����</td></ofbiz:if>
          <ofbiz:if name="chk_equipmentId" value="on"><td class="en11pxb">EQP ID</td></ofbiz:if>
          <ofbiz:if name="chk_style" value="on"><td class="en11pxb">��̨�ͺ�</td></ofbiz:if>
          <ofbiz:if name="chk_useDept" value="on"><td class="en11pxb">ʹ�ò���</td></ofbiz:if>
          <ofbiz:if name="chk_equipmentEngineer" value="on"><td class="en11pxb">�豸����ʦ</td></ofbiz:if>
          <ofbiz:if name="chk_backupEngineer" value="on"><td class="en11pxb">�豸BACKUP����ʦ</td></ofbiz:if>
          <ofbiz:if name="chk_equipmentLeader" value="on"><td class="en11pxb">�豸LEADER</td></ofbiz:if>
          <ofbiz:if name="chk_processEngineer" value="on"><td class="en11pxb">���չ���ʦ</td></ofbiz:if>

          <ofbiz:if name="chk_assetNo" value="on"><td class="en11pxb">�Ʋ����</td></ofbiz:if>
          <ofbiz:if name="chk_vendorEngineer" value="on"><td class="en11pxb">���̹���ʦ</td></ofbiz:if>
          <ofbiz:if name="chk_vendorPhone" value="on"><td class="en11pxb">���̵绰</td></ofbiz:if>
          <ofbiz:if name="chk_vendorEqpNo" value="on"><td class="en11pxb">���̻�̨���</td></ofbiz:if>

          <ofbiz:if name="chk_productionMode" value="on"><td class="en11pxb">PRODUCTION MODE</td></ofbiz:if>
          <ofbiz:if name="chk_length" value="on"><td class="en11pxb">LENGTH</td></ofbiz:if>
          <ofbiz:if name="chk_width" value="on"><td class="en11pxb">WIDTH</td></ofbiz:if>
          <ofbiz:if name="chk_height" value="on"><td class="en11pxb">HEIGHT</td></ofbiz:if>
          <ofbiz:if name="chk_weight" value="on"><td class="en11pxb">WEIGHT</td></ofbiz:if>

          <ofbiz:if name="chk_exhaust" value="on"><td class="en11pxb">�ŷ�</td></ofbiz:if>
          <ofbiz:if name="chk_meter" value="on"><td class="en11pxb">��λ</td></ofbiz:if></ofbiz:if>
          <ofbiz:if name="chk_voltage" value="on"><td class="en11pxb">��ѹ</td></ofbiz:if>
          <ofbiz:if name="chk_power" value="on"><td class="en11pxb">����</td></ofbiz:if>

          <ofbiz:if name="chk_parentEqp" value="on"><td class="en11pxb">ĸ�豸</td></ofbiz:if>
          <ofbiz:if name="chk_keyEqp" value="on"><td class="en11pxb">�ؼ��豸</td></ofbiz:if>
          <ofbiz:if name="chk_adjustEqp" value="on"><td class="en11pxb">�����豸</td></ofbiz:if>
          <ofbiz:if name="chk_measureEqp" value="on"><td class="en11pxb">У׼�豸</td></ofbiz:if>
        </tr>

        <ofbiz:if name="equipmentList">
	        <ofbiz:iterator name="cust" property="equipmentList">
		        <tr bgcolor="#DFE1EC">
		        	<ofbiz:if name="chk_ownDept" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="ownDept"/></td></ofbiz:if>
					<ofbiz:if name="chk_maintDept" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="maintDept"/></td></ofbiz:if>
					<ofbiz:if name="chk_location" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="location"/></td></ofbiz:if>
					<ofbiz:if name="chk_section" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="section"/></td></ofbiz:if>

					<ofbiz:if name="chk_equipmentType" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="equipmentType"/></td></ofbiz:if>
		        	<ofbiz:if name="chk_equipmentId" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="equipmentId"/></td></ofbiz:if>
					<ofbiz:if name="chk_style" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="style"/></td></ofbiz:if>
					<ofbiz:if name="chk_useDept" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="useDept"/></td></ofbiz:if>
					<ofbiz:if name="chk_equipmentEngineer" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="equipmentEngineer"/></td></ofbiz:if>
					<ofbiz:if name="chk_backupEngineer" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="backupEngineer"/></td></ofbiz:if>
					<ofbiz:if name="chk_equipmentLeader" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="equipmentLeader"/></td></ofbiz:if>
					<ofbiz:if name="chk_processEngineer" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="processEngineer"/></td></ofbiz:if>

					<ofbiz:if name="chk_assetNo" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="assetNo"/></td></ofbiz:if>
					<ofbiz:if name="chk_vendorEngineer" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="vendorEngineer"/></td></ofbiz:if>
					<ofbiz:if name="chk_vendorPhone" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="vendorPhone"/></td></ofbiz:if>
					<ofbiz:if name="chk_vendorEqpNo" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="vendorEqpNo"/></td></ofbiz:if>

					<ofbiz:if name="chk_productionMode" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="projectCode"/></td></ofbiz:if>
					<ofbiz:if name="chk_length" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="length"/></td></ofbiz:if>
					<ofbiz:if name="chk_width" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="width"/></td></ofbiz:if>
					<ofbiz:if name="chk_height" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="height"/></td></ofbiz:if>
					<ofbiz:if name="chk_weight" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="weight"/></td></ofbiz:if>

					<ofbiz:if name="chk_exhaust" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="exhaust"/></td></ofbiz:if>
					<ofbiz:if name="chk_meter"><td class="en11px"><ofbiz:entityfield attribute="cust" field="meter"/></td></ofbiz:if>
					<ofbiz:if name="chk_voltage" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="voltage"/></td></ofbiz:if>
					<ofbiz:if name="chk_power" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="power"/></td></ofbiz:if>

					<ofbiz:if name="chk_parentEqp" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="parentEqpid"/></td></ofbiz:if>
					<ofbiz:if name="chk_keyEqp" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="keyEqp"/></td></ofbiz:if>
					<ofbiz:if name="chk_adjustEqp" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="adjustEqp"/></td></ofbiz:if>
					<ofbiz:if name="chk_measureEqp" value="on"><td class="en11px"><ofbiz:entityfield attribute="cust" field="measureEqp"/></td></ofbiz:if>
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>

