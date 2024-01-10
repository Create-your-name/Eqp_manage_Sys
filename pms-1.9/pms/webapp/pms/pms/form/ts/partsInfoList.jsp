<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<%  
	String formreturn=request.getParameter("formreturn");
    String partNo=UtilFormatOut.checkNull(request.getParameter("partNo"));
    String accountSection=(String)request.getAttribute("accountSection");
    String eqpId=UtilFormatOut.checkNull(request.getParameter("eqpId"));
    String periodIndex=UtilFormatOut.checkNull(request.getParameter("periodIndex"));
    String formType=request.getParameter("formType");
    String eventIndex=request.getParameter("eventIndex");
    int size=0;
    List list=(List)request.getAttribute("partsNoList");
    if(list!=null){
        size=list.size();
    }
%>
<base target="_self">
<!-- yui page script-->
<script language="javascript">
    /* var pregion;
    var partType; */
    //��ѯ
    function partsQuery(){
      <ofbiz:if name="formType" value="PM">
        var eqpId=Ext.get('eqpId').dom.value;
        var periodIndex=Ext.get('periodIndex').dom.value;
        if(eqpId==''){
            Ext.MessageBox.alert('����', "�豸IDΪ��,����ϵ����Ա!");
            return;
        }else if(periodIndex==''){
            Ext.MessageBox.alert('����', "PM����Ϊ��,����ϵ����Ա!");
            return;
        }
      </ofbiz:if>
        var mtrGrpValue=Ext.get('mtrGrp').dom.value;
        if(mtrGrpValue==''){
            Ext.MessageBox.alert('����', "��ѡ��������!");
            return;
        }
        var url=document.getElementById("partsDataQueryForm").action;
        document.getElementById("partsDataQueryForm").action=url+"?formreturn=<%=formreturn%>";
        loading();
        partsDataQueryForm.submit();
    }
    
    //�ж�
    function isChecked(){
     for(i=0;i<<%=size%>;i++){
       var checkObj=document.getElementById("parts_"+i);
           if (checkObj.checked==true){
            return true;
           }
      }
      return false;
    }
    
    //���ϱ���
    function partSave(){
        var checked=isChecked();
        var reg = /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/;//Js��֤�Ǹ���(0,����������С��) 
        if(checked){
            for(i=0;i<<%=size%>;i++){
               var checkObj=document.getElementById("parts_"+i);
               if (checkObj.checked==true){
                    var inputObj=document.getElementById("useNum_"+i);
                    var qty=document.getElementById("qty_"+i).value;
                    if(inputObj.value==""){
                        alert("��ѡ������ϣ�������������!");
                        return;
                    }else if(isNaN(inputObj.value)){
                    	alert("������������Ϊ����!");
                        return;
                    }else if(reg.test(inputObj.value)==false){
                        alert("������������Ϊ��ֵ!");
                        return;
                    }
                    if(parseInt(inputObj.value)>parseInt(qty)){
                    	alert("ʹ���������ܴ��ڿ������!");
                        return;
                    }
                    var inputRemarkValue=document.getElementById("remark_"+i).value;
                    if(inputRemarkValue.length>50){
                        alert("��ע���Ȳ��ܳ���50���ַ�!!");
                        return;
                    }
               }
             }
            Ext.get('type').dom.value=Ext.get('mtrGrp').dom.value;
            var url=document.getElementById("partsDataSaveForm").action;
        	document.getElementById("partsDataSaveForm").action=url+"?formreturn=<%=formreturn%>";
            loading();
            partsDataSaveForm.submit();
        }else{
            alert("û��ѡ�����ϣ���ȷ��!");
        }
        
    }
    
    //ѡ������
    function partsSelect(obj){
        var id=obj.id;
        var valueArray=id.split("_");
        if(obj.checked==true){
            document.getElementById("useNum_"+valueArray[1]).readOnly=false;
            document.getElementById("useNum_"+valueArray[1]).style.background="#FFFFFF";

            document.getElementById("remark_"+valueArray[1]).readOnly=false;
            document.getElementById("remark_"+valueArray[1]).style.background="#FFFFFF";    
        }else{
            document.getElementById("useNum_"+valueArray[1]).readOnly=true;
            document.getElementById("useNum_"+valueArray[1]).style.background="#E9E9E9";

            document.getElementById("remark_"+valueArray[1]).readOnly=true;
            document.getElementById("remark_"+valueArray[1]).style.background="#E9E9E9";    
        }
    }
    
    //�������ѡ��
    function partTypeChange(){
        var partsTypeValue=partType.getValue();
        if(partsTypeValue=='OTHER'||partsTypeValue=='RECYCLE'){
            //��ʾѡ�񼶱�����
            Ext.get('level').dom.style.display='';
        }else{
            //����ѡ�񼶱�����
            Ext.get('level').dom.style.display='none';
        }
    }
    
    Ext.onReady(function(){
        if("Y" == "<%=request.getAttribute("flag")%>") {
        	if("new"=="<%=formreturn%>"){      		
            	window.opener.document.location.href="<%=request.getContextPath()%>/control/intoAbnormalFormManageEntry?functionType=1&activate=parts&abnormalIndex=<%=eventIndex%>&eqpId=<%=eqpId%>";
        	}else if("edit"=="<%=formreturn%>"){
           		window.opener.document.location.href="<%=request.getContextPath()%>/control/queryTsformByLeader?functionType=1&activate=parts&abnormalIndex=<%=eventIndex%>&eqpId=<%=eqpId%>";        	
        	}
            window.close();
        }        
     });
</script>
<form action="<%=request.getContextPath()%>/control/queryTSPartsNoPMList" method="post" id="partsDataQueryForm">
<input id="eqpId" type="hidden" name="eqpId" value="<%=eqpId%>" />
<input id="formType" type="hidden" name="formType" value="<%=formType%>" />
<input id="periodIndex" type="hidden" name="periodIndex" value="" />
<input id="index" type="hidden" name="eventIndex" value="" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>������Ϣ��ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
         <tr bgcolor="#DFE1EC"> 
           <td width="12%" class="en11pxb">ѡ������:</td>
           <td width="28%">
			   <select id="mtrGrp" name="mtrGrp">
					<option value=""></option>
					<option value="20002P">20002P(����)</option>
					<option value="20002S">20002S(�׺ı���)</option>
					<option value="20002T">20002T(���Ϻű���)</option>
                </select>
            </td>
         </tr>
         <tr bgcolor="#DFE1EC"> 
           <td width="12%" class="en11pxb">���Ϻ�:</td>
           <td width="28%"><input class="input" type="text" name="partNo" id="partNo" value="<%=partNo%>" size="22" tabindex="1" />
           </td>
          </tr>
          <tr bgcolor="#DFE1EC"> 
            <td width="20%" class="en11pxb" align="left" colspan="2">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr height="30">
                    <td width="20">&nbsp;</td>
                    <td><ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:partsQuery();"><span>&nbsp;��ѯ&nbsp;</span></a></li> 
                    </ul>
                    <ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:partSave();"><span>&nbsp;����&nbsp;</span></a></li> 
                    </ul>
                    <ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:window.close();"><span>&nbsp;�ر�&nbsp;</span></a></li> 
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
<form action="<%=request.getContextPath()%>/control/abnormalFormPartsSaveEntry" method="post" id="partsDataSaveForm">
<input id="equipmentId" type="hidden" name="eqpId" value="<%=eqpId%>" />
<input id="form" type="hidden" name="formType" value="<%=formType%>" />
<input id="type" type="hidden" name="type" value="" />
<input id="prodIndex" type="hidden" name="periodIndex" value="" />
<input id="formIndex" type="hidden" name="eventIndex" value="" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>������Ϣ�б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="10%" class="en11pxb">ѡ��</td>
          <td width="10%" class="en11pxb">PLANT</td>
          <td width="10%" class="en11pxb">���Ϻ�</td>
          <td width="15%" class="en11pxb">������</td>
          <td width="10%"  class="en11pxb">����</td>
          <td width="15%" class="en11pxb">�������</td>
          <td width="10%" class="en11pxb">ʹ������</td>
          <td width="10%" class="en11pxb">���</td>
          <td width="10%" class="en11pxb">����</td>
          <td width="15%" class="en11pxb">��ע</td>
        </tr>
      <% 
        List partsUseList=(List)request.getAttribute("partsNoList");
        if(partsUseList != null && partsUseList.size() > 0) {  
            int k = 0;
            for(Iterator it = partsUseList.iterator();it.hasNext();) { 
                Map map = (Map)it.next();
        %>
           <tr bgcolor="#DFE1EC"> 
             <td class="en11px">
             <input type="checkBox" id="parts_<%=k%>" name="parts_<%=k%>" value='<%=map.get("MTR_NUM")%>' onclick="partsSelect(this);"/>
             <input id="mcsid_<%=k%>" type="hidden" name="mcsid_<%=k%>" value='<%=map.get("REQ_INDEX")%>' />
             <input id="partsMtrGrp_<%=k%>" type="hidden" name="partsMtrGrp_<%=k%>" value='<%=map.get("MTR_GRP")%>' />
             </td>
             <td class="en11px"><%=map.get("PLANT")%></td>
             <td class="en11px"><%=map.get("MTR_NUM")%></td>
             <td class="en11px">
                <input id="partsName_<%=k%>" type="hidden" name="partsName_<%=k%>" value='<%=map.get("MTR_DESC")%>' />
                <%=map.get("MTR_DESC")%>
             </td>
             <td class="en11px">
                <input id="batchnum_<%=k%>" type="hidden" name="batchnum_<%=k%>" value='<%=map.get("BATCH_NUM")%>' />
                <%=map.get("BATCH_NUM")%>
             </td>
             <td class="en11px">
				<select name="partType_<%=k%>" id="partType_<%=k%>"/>
					<ofbiz:iterator name="cust" property="partTypeList" type="java.util.Map">
						<option value='<ofbiz:inputvalue entityAttr="cust" field="ID"/>'><ofbiz:inputvalue entityAttr="cust" field="PART_TYPE"/></option>
					</ofbiz:iterator>
				</select>
             </td>
             <td class="en11px"><input id="useNum_<%=k%>" name="useNum_<%=k%>" value="1" class="input" size=2 type="text" maxlength=2 readonly="readonly" style="background:E9E9E9"/></td>
             <td class="en11px"><input id="qty_<%=k%>" type="hidden" name="qty_<%=k%>" value='<%=map.get("QTY")%>' /><%=map.get("QTY")%></td>
             <td class="en11px"><%=map.get("VERPR")%></td>
             <td class="en11px"><input id="remark_<%=k%>" name="remark_<%=k%>" class="input" size=20 maxlength="40" type="text" readonly="readonly" style="background:E9E9E9"/></td>
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
</form>
<script language="javascript">
    //��ѯ���ҳ��ĳ�ʼ��
    Ext.get('formIndex').dom.value='<%=eventIndex%>'
    Ext.get('index').dom.value='<%=eventIndex%>'
    Ext.get('eqpId').dom.value='<%=eqpId%>'
    Ext.get('equipmentId').dom.value='<%=eqpId%>'
    Ext.get('periodIndex').dom.value='<%=periodIndex%>'
    Ext.get('prodIndex').dom.value='<%=periodIndex%>'
    Ext.get('partNo').dom.value='<%=UtilFormatOut.checkNull(request.getParameter("partNo"))%>'
    Ext.get('mtrGrp').dom.value='<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>'
</script>