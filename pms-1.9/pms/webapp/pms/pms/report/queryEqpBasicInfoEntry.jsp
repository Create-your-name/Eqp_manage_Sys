<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<script lanaguage="javascript">
	function doSubmit(url) {
		loading();
		document.eqpForm.action = url;
		document.eqpForm.submit();
	}

	Ext.onReady(function(){

	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });
	});
</script>


<form name="eqpForm" method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>��ѯ��̨��������</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">

			<tr bgcolor='#DFE1EC'>
				<td>&nbsp;EQP ID</td>
				<td><input name="eqpId" id="eqpId" type="text"></td>

				<td>&nbsp;ӵ�в���</td>
				<td>
					<select name='ownDept' style="width:170">
					  <option value=""></option>
			          <ofbiz:if name="eqpDeptList">
				        <ofbiz:iterator name="cust" property="eqpDeptList">
					    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>'>
					    	<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
					    </option>
				      </ofbiz:iterator>
			      	</ofbiz:if>
		    		</select>
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td>&nbsp;ά������</td>
				<td>
					<select name='maintDept' style="width:170">
					  <option value=""></option>
			          <ofbiz:if name="eqpDeptList">
				        <ofbiz:iterator name="cust" property="eqpDeptList">
					    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>'>
					    	<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
					    </option>
				      </ofbiz:iterator>
			      	</ofbiz:if>
		    		</select>
				</td>
				<td>&nbsp;��������</td>
				<td>
					<select name='location' style="width:170">
					  <option value=""></option>
			          <ofbiz:if name="eqpLocationList">
				        <ofbiz:iterator name="cust" property="eqpLocationList">
					    <option value='<ofbiz:inputvalue entityAttr="cust" field="location"/>'>
					    	<ofbiz:inputvalue entityAttr="cust" field="location"/>
					    </option>
				      </ofbiz:iterator>
			      	</ofbiz:if>
		    		</select>
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td>&nbsp;SECTION</td>
				<td>
					<select name="section" style="width:170">
					  <option value=""></option>
			          <ofbiz:if name="eqpSectionList">
				        <ofbiz:iterator name="cust" property="eqpSectionList">
					    <option value='<ofbiz:inputvalue entityAttr="cust" field="section"/>'>
					    	<ofbiz:inputvalue entityAttr="cust" field="section"/>
					    </option>
				      </ofbiz:iterator>
			      	</ofbiz:if>
		    		</select>
				</td>
				<td>&nbsp;�ؼ��豸</td>
				<td>
					<select name='keyEqp' style="width:170">
						<option value='' ></option>
						<option value='N'>N</option>
						<option value='Y'>Y</option>
					</select>
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td>&nbsp;�����豸</td>
				<td>
					<select name='adjustEqp' style="width:170">
						<option value='' ></option>
						<option value='N'>N</option>
						<option value='Y'>Y</option>
					</select>
				</td>
				<td>&nbsp;У׼�豸</td>
				<td>
					<select name='measureEqp' style="width:170">
						<option value='' ></option>
						<option value='N'>N</option>
						<option value='Y'>Y</option>
					</select>
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td>&nbsp;�豸����</td>
				<td>
					<select name="equipmentType" id="equipmentType">
					  <option value=""></option>
			          <ofbiz:if name="eqpTypeList">
				        <ofbiz:iterator name="cust" property="eqpTypeList">
					    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'>
					    	<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>
					    </option>
				      </ofbiz:iterator>
			      	</ofbiz:if>
		    		</select>
				</td>
				<td></td>
				<td></td>
			</tr>
		</table>


		<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_ownDept' checked>ӵ�в���
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_maintDept' checked>ά������
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_location' checked>��������
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_section' >SECTION
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_equipmentType' >�豸����
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_equipmentId' checked>EQP ID
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_style' checked>��̨�ͺ�
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_useDept' checked>ʹ�ò���
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_equipmentEngineer' checked>�豸����ʦ
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_backupEngineer' >�豸BACKUP����ʦ
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_equipmentLeader' >�豸LEADER
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_processEngineer' >���չ���ʦ
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_assetNo' >�Ʋ����
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_vendorEngineer' >���̹���ʦ
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_vendorPhone' >���̵绰
				</td>
			</tr>


			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_vendorEqpNo' >���̻�̨���
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_productionMode' >PRODUCTION MODE
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_length' >LENGTH
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_width' >WIDTH
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_height' >HEIGHT
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_weight' >WEIGHT
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_exhaust' >�ŷ�
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_meter'>��λ
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_voltage' >��ѹ
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_power' >����
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_parentEqp' >ĸ�豸
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_keyEqp' >�ؼ��豸
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_adjustEqp' >�����豸
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_measureEqp' >У׼�豸
				</td>
				<td></td>
			</tr>
		</table>

	  </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/queryEqpBasicInfo')"><span>&nbsp;��ʾ&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.eqpForm.reset();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
  </tr>
</table>

</form>


