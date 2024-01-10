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
      <fieldset><legend>查询机台基本资料</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">

			<tr bgcolor='#DFE1EC'>
				<td>&nbsp;EQP ID</td>
				<td><input name="eqpId" id="eqpId" type="text"></td>

				<td>&nbsp;拥有部门</td>
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
				<td>&nbsp;维护部门</td>
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
				<td>&nbsp;所在区域</td>
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
				<td>&nbsp;关键设备</td>
				<td>
					<select name='keyEqp' style="width:170">
						<option value='' ></option>
						<option value='N'>N</option>
						<option value='Y'>Y</option>
					</select>
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td>&nbsp;计量设备</td>
				<td>
					<select name='adjustEqp' style="width:170">
						<option value='' ></option>
						<option value='N'>N</option>
						<option value='Y'>Y</option>
					</select>
				</td>
				<td>&nbsp;校准设备</td>
				<td>
					<select name='measureEqp' style="width:170">
						<option value='' ></option>
						<option value='N'>N</option>
						<option value='Y'>Y</option>
					</select>
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td>&nbsp;设备大类</td>
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
					<input type='checkbox' name='chk_ownDept' checked>拥有部门
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_maintDept' checked>维护部门
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_location' checked>所在区域
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_section' >SECTION
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_equipmentType' >设备大类
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_equipmentId' checked>EQP ID
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_style' checked>机台型号
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_useDept' checked>使用部门
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_equipmentEngineer' checked>设备工程师
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_backupEngineer' >设备BACKUP工程师
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_equipmentLeader' >设备LEADER
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_processEngineer' >工艺工程师
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_assetNo' >财产编号
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_vendorEngineer' >厂商工程师
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_vendorPhone' >厂商电话
				</td>
			</tr>


			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_vendorEqpNo' >厂商机台编号
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
					<input type='checkbox' name='chk_exhaust' >排风
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_meter'>相位
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_voltage' >电压
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_power' >功率
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_parentEqp' >母设备
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_keyEqp' >关键设备
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_adjustEqp' >计量设备
				</td>
				<td style='width:33%;'>
					<input type='checkbox' name='chk_measureEqp' >校准设备
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
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/queryEqpBasicInfo')"><span>&nbsp;显示&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.eqpForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
  </tr>
</table>

</form>


