<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>
<%	String eqpModel="";
	List keyPartsInfoList=(List)request.getAttribute("keyPartsinfo");
	if(keyPartsInfoList != null && keyPartsInfoList.size() > 0) {
		Map map=(Map)keyPartsInfoList.get(0);
        eqpModel=(String)map.get("eqpType");
    }    
%>
<script language="javascript">
//����״��
	function addActionStatus(){
		//��ñ��е�����
		var index = Ext.DomQuery.select('tr',Ext.getDom('statusArea')).length;
		//������к�
		var orderNum = index;	
		//������
		Ext.get('statusArea').createChild({
			tag:'tr', style:'background-color:#DFE1EC', children: [
				{tag: 'td', html:'<img name="statusName_'+orderNum+'" src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="deleteActionStatus(this)" />',align:'center',cls:'en11pxb'},
				{tag: 'td', html:'<input name="delayItem_'+orderNum+'" type="text", style="width:30%", cls="input" />',cls:'en11pxb',align:'center'},
				{tag: 'td', html:'<input name="delaySpec_'+orderNum+'" type="text", style="width:90%", cls="input" />',cls:'en11pxb'},
				{tag: 'td', html:'<input name="delayRule_'+orderNum+'" type="text", style="width:90%", cls="input" />',cls:'en11pxb'}
		]});		
	}
	
	//ɾ��״��
	function deleteActionStatus(e,keyPartsDelayId) {
		var obj,index;
		keyPartsId=<%=request.getParameter("keyPartsId")%>;
		if(e.nodeType == 1) {
			obj = e;
		} else {
			obj = e.getTarget('img');
		}
		var trList = Ext.DomQuery.select('tr',Ext.getDom('statusArea'));

		//���������
		index = obj.name.substr(obj.name.indexOf('_')+1);
		//�õ��ж���
		var row = Ext.DomQuery.select('tr',Ext.getDom('statusArea'))[index];		

		//���statusIndex��Ϊ�գ���ʾ�Ƿ�ɾ��
		Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ����״̬��',function result(value){
			if(value=='no') return;
			if(value=='yes') {
				//alert(Ext.getDom('keyPartsId').value);				
				//Ext.Element.get(row).remove();												
				var delActionUrl='<ofbiz:url>/deleteKeyPartsDelayItem</ofbiz:url>?keyPartsDelayId='+keyPartsDelayId+'&keyPartsId='+keyPartsId;
				//alert(delActionUrl);
				document.location=delActionUrl;
				
			}
		});	
		
	}
	//��������   
    function keyPartsDelaySave(){
    	var size=Ext.DomQuery.select('tr',Ext.getDom('statusArea')).length-1;   	
            for(i=1;i<size+1;i++){
               var delayItemObj=document.getElementById("delayItem_"+i);
               var delaySpecObj=document.getElementById("delaySpec_"+i);
               var delayRuleObj=document.getElementById("delayRule_"+i);
               var delayResultObj=document.getElementById("delayResult_"+i);
               	   for(k=i+1;k<size+1;k++){
               	   	   var delayItemObj1=document.getElementById("delayItem_"+k);
  	             	   if(delayItemObj.value==delayItemObj1.value){
  	             	   		alert("ITEM"+delayItemObj.value+"�Ѵ���");
  	             	   		return;
  	             	   }
               	   }
	               if(delayItemObj.value==""){
 	              	   alert("ITEM����Ϊ��");
    	                   return;
    	           }else if(isNaN(delayItemObj.value)){
  	    	      		   alert("ITEM������������");
    	                   return;
    	           }             	   
	               if(delaySpecObj.value==""){
	                    	alert("������Ŀ����Ϊ��");
	                    	return;
	               }  
	               if(delayRuleObj.value==""){
	                        alert("�淶����Ϊ��");
	                        return;
	               }           	               
            }

        loading();
        var url=document.partsDelayDataSaveForm.action;
        document.partsDelayDataSaveForm.action=url+"?size="+size;
        var url1=document.partsDelayDataSaveForm.action;
	  	  partsDelayDataSaveForm.submit();      
    }
    function windowClose(){
    	window.opener.document.location.href="<%=request.getContextPath()%>/control/keyEqpPartsList?eqp_Model=<%=eqpModel%>";
        window.close();
    }
	
</script>	


<form action="<%=request.getContextPath()%>/control/saveKeyPartsDelay" method="post" name="partsDelayDataSaveForm" id="partsDelayDataSaveForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>������Ϣ</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
         <tr bgcolor="#ACD5C9">
           <td width="5%" class="en11pxb">�ؼ���</td>
           <td width="5%" class="en11pxb">�Ϻ�</td>
           <td width="5%" class="en11pxb">eqpModel</td>     
           <td width="20%" class="en11pxb">��������</td>
         </tr>
    <%	
    	if(keyPartsInfoList != null && keyPartsInfoList.size() > 0) {  
            int k = 0;
            for(Iterator it = keyPartsInfoList.iterator();it.hasNext();) { 
                Map keyPartsInfoMap = (Map)it.next();              
    %>
         <tr bgcolor="#DFE1EC">
           <td width="5%" class="en11pxb"><input  type="hidden" name="keydesc_<%=k%>" value="<%=keyPartsInfoMap.get("keydesc")%>" /><%=keyPartsInfoMap.get("keydesc")%></td>
           <td width="5%" class="en11pxb"><input  type="hidden" name="partsId_<%=k%>" value="<%=keyPartsInfoMap.get("partsId")%>" /><%=keyPartsInfoMap.get("partsId")%></td>
           <td width="5%" class="en11pxb"><%=keyPartsInfoMap.get("eqpType")%></td>     
           <td width="20%" class="en11pxb"><%=keyPartsInfoMap.get("partsName")%></td>
         </tr> 
     <%
          k++;
            }
          } 
	 %> 
      </table>
      </fieldset></td>
  </tr>
</table>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ؼ��������������趨</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" id="statusArea">
      	<input style="width:90%" type="hidden" name="keyPartsId" value="<%=request.getParameter("keyPartsId")%>" />
        <tr bgcolor="#ACD5C9">
          <td width="3%" align="center" class="en11pxb"><img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addActionStatus()"/></td> 
          <td width="10%" align="center" class="en11pxb">����ITEM</td>
          <td width="30%" class="en11pxb">������Ŀ</td>
          <td width="30%" class="en11pxb">�淶</td>         
        </tr>
     <% 
        List keyPartsDelayList=(List)request.getAttribute("keyPartsDelayList");
        if(keyPartsDelayList != null && keyPartsDelayList.size() > 0) {  
            int k = 0;
            for(Iterator it = keyPartsDelayList.iterator();it.hasNext();) { 
                Map map = (Map)it.next();              
	 %>
	 	<tr bgcolor="#DFE1EC">
	 	  <input style="width:90%" type="hidden" name="keyPartsDelayId_<%=k+1%>" value="<%=map.get("KEY_PARTS_DELAY_ID")%>" />
          <td width="3%" align="center" class="en11pxb"><img name="statusName_<%=k+1%>" src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="deleteActionStatus(this,'<%=map.get("KEY_PARTS_DELAY_ID")%>','<%=map.get("KEY_PARTS_ID")%>')"/></td>
          <td width="10%" align="center" class="en11pxb"><input class="en11pxb" style="width:30%" type="text" name="delayItem_<%=k+1%>" value="<%=map.get("DELAY_ITEM")%>" /></td>
          <td width="30%" class="en11pxb"><input style="width:90%" type="text" name="delaySpec_<%=k+1%>" value="<%=map.get("DELAY_SPEC")%>" /></td>
          <td width="30%" class="en11pxb"><input style="width:90%" type="text" name="delayRule_<%=k+1%>" value="<%=map.get("DELAY_RULE")%>" /></td>       
        </tr>
	 <%
          k++;
            }
          } 
	 %> 
	 
      </table>
      </fieldset></td>
  </tr>
	 <tr bgcolor="#DFE1EC">	 		
            <td width="20%" class="en11pxb" align="right" colspan="2">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr height="30">
                    <td width="20">&nbsp;</td>
                    <td>
                    <ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:keyPartsDelaySave();"><span>&nbsp;����&nbsp;</span></a></li> 
                    </ul>
                    <ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:windowClose();"><span>&nbsp;�ر�&nbsp;</span></a></li> 
                    </ul>
                    </td>
                  </tr>
                </table>
            </td>
        </tr>	 
</table>
</form>
