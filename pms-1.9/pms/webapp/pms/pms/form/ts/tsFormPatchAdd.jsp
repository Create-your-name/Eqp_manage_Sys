<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<!-- yui page script-->
<script language="javascript">
	var eqp;
	//保存
	function save(){
	 	var eqpId=Ext.get('eqpId').dom.value;
	 	if(eqpId==""||eqpId==undefined){
	 		Ext.MessageBox.alert('警告', '设备编号不能为空!');
	 	}
		loading();
		tsForm.submit();
	}
	
	//选择设备编号时
	function eqpChange(){
		var eqpId=Ext.get('eqpId').dom.value;
		var actionURL='<ofbiz:url>/getDeapProEntry</ofbiz:url>?eqpId='+eqpId;
		Ext.lib.Ajax.formRequest('tsForm',actionURL,{success: commentSuccess, failure: commentFailure});
	}
	
	//远程调用成功
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			//设备保养种类数据初始化
			var nameSize=result.jobNameArray.length;
			var nameArray=result.jobNameArray;
			var valueArray=result.jobIndexArray;
			var secObj=document.getElementById("jobIndex");
			secObj.length=1;
			for(var i=0;i<nameSize;i++){
				secObj.options[secObj.length]=new Option(nameArray[i],valueArray[i]);
			}
		}
	}
	
	//远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('警告', 'Unable to connect.');
   };
   
	
	//初始化页面控件
	Ext.onReady(function(){
	     eqp = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqpId',
	        width:170,
	        forceSelection:true
	    });
	    eqp.on('select',eqpChange);
	 });
</script>
<form action="<%=request.getContextPath()%>/control/savePatchTsFormEntry" method="post" id="tsForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>异常表单补填作业</legend>
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
          <td width="30%" bgcolor="#ACD5C9" class="en11pxb">选择处理程序</td>
          <td width="70%"><select id="jobIndex" name="jobIndex">
		          <option value=""></option>
		          <ofbiz:if name="DEALPRO_LIST">
			        <ofbiz:iterator name="cust" property="DEALPRO_LIST">
				    <option value='<ofbiz:inputvalue entityAttr="cust" field="jobIndex"/>'><ofbiz:inputvalue entityAttr="cust" field="jobName"/></option>
			      </ofbiz:iterator>
		      	</ofbiz:if>
    		</select>
    		</td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
  <tr> 
       	<td class="en11pxb" colspan="4" align="left">
        	<table border="0" cellspacing="0" cellpadding="0">
		  		<tr height="30">
				    <td><ul class="button">
						<li><a class="button-text" href="#" onclick="javascript:save();"><span>&nbsp;确定&nbsp;</span></a></li> 
					</ul>
					</td>
		  		</tr>
			</table>
       	</td>
    </tr>
</table>
<script language="javascript">
Ext.get('eqpId').dom.value='<%=request.getParameter("eqpId")%>'
</script>
