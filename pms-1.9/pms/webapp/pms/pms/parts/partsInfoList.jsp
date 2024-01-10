<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>


<%
	String mtrGrp=UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
	String category=UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
	String partNo=UtilFormatOut.checkNull(request.getParameter("partNo"));
	String periodIndex=UtilFormatOut.checkNull(request.getParameter("periodIndex"));
	String flowIndex=UtilFormatOut.checkNull(request.getParameter("flowIndex"));
	String eqpType=UtilFormatOut.checkNull(request.getParameter("eqpType"));
	if(eqpType.equals("")){
	    eqpType = (String) request.getAttribute("eqpType");
	}
	int size=0;
	List list=(List)request.getAttribute("partsNoList");
	if(list!=null){
		size=list.size();
	}
%>

<base target="_self">
<!-- yui page script-->
<script language="javascript">
	//��ѯ
	function partsQuery(){
	  	var mtrGrpValue=Ext.get('mtrGrp').dom.value;
	  	if(mtrGrpValue==''){
			Ext.MessageBox.alert('����', "��ѡ��������!");
   			return;
   		}
	  	var partNoValue=Ext.get('partNo').dom.value;
	  	if(partNoValue.length < 4){
			Ext.MessageBox.alert('����', "��������4λ�Ϻų��Ȳ�ѯ!");
   			return;
   		}
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
		if(checked){
			for(i=0;i<<%=size%>;i++){
		   	   var checkObj=document.getElementById("parts_"+i);
			   if (checkObj.checked==true){
			   		var inputObj=document.getElementById("partsNum_"+i);
			   		if(inputObj.value==""){
			   			alert("��ѡ������ϣ���������Ĭ������!");
			   			return;
			   		}else if(isNaN(inputObj.value)){
			   			alert("Ĭ����������Ϊ����!");
			   			return;
			   		}
					var inputObj1=document.getElementById("templateCount_"+i);
			   		if(inputObj1.value==""){
			   			alert("��ѡ������ϣ���������ο�����!");
			   			return;
			   		} else if(isNaN(inputObj.value)){
			   			alert("�ο���������Ϊ����!");
			   			return;
			   		} else {
			   		    //var reg = new RegExp("^([0-9]*)+(.[0-9]{1,2})?$");//��֤���ֱ�����λС��
                        var reg = new RegExp("^([1-9]{1})+([0-9]*)?$");//��֤������
                        if(!reg.test(inputObj.value)){
                            alert("������������Ϊ������!");
			   			    return;
                        }
			   		}

			   		var inputRemarkValue=document.getElementById("remark_"+i).value;
			   		if(inputRemarkValue.length>10){
			   			alert("��ע���Ȳ��ܳ���10���ַ�!!");
			   			return;
			   		}
			   }
		 	 }
			loading();
			partsDataSaveForm.submit();
		}else{
			alert("û��ѡ�����ϣ���ȷ��!");
		}
	}

	//�رղ�ѯҳ�棬ˢ��ĸҳ��
	function closeWindow(){
	    window.close();
	    window.opener.document.location.href="<%=request.getContextPath()%>/control/queryPmPartsTemplateList?flow=<%=flowIndex%>&period=<%=periodIndex%>&equipmentType=<%=eqpType%>";
	}

	//ѡ������
	function partsSelect(obj){
		var id=obj.id;
		var valueArray=id.split("_");
		if(obj.checked==true){
			document.getElementById("partsNum_"+valueArray[1]).readOnly=false;
			document.getElementById("partsNum_"+valueArray[1]).style.background="#FFFFFF";

		    document.getElementById("remark_"+valueArray[1]).readOnly=false;
			document.getElementById("remark_"+valueArray[1]).style.background="#FFFFFF";

			document.getElementById("templateCount_"+valueArray[1]).readOnly=false;
			document.getElementById("templateCount_"+valueArray[1]).style.background="#FFFFFF";
		}else{
			document.getElementById("partsNum_"+valueArray[1]).readOnly=true;
			document.getElementById("partsNum_"+valueArray[1]).style.background="#E9E9E9";

		    document.getElementById("remark_"+valueArray[1]).readOnly=true;
			document.getElementById("remark_"+valueArray[1]).style.background="#E9E9E9";

			document.getElementById("templateCount_"+valueArray[1]).readOnly=true;
			document.getElementById("templateCount_"+valueArray[1]).style.background="#E9E9E9";	
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

    /*Ext.onReady(function(){�˷��������ҳ��������ִ���ж�̫����
		if("Y" == "<%=request.getAttribute("flag")%>") {
            window.close();
			window.opener.document.location.href="<%=request.getContextPath()%>/control/queryPmPartsTemplateList?flow=<%=flowIndex%>&period=<%=periodIndex%>&equipmentType=<%=eqpType%>";
		}
    });*/

    <%if ("Y".equals(request.getAttribute("flag"))) {%>
        closeWindow();//�����ر�ҳ�沢ˢ��
    <%}%>
</script>

<form action="<%=request.getContextPath()%>/control/queryPartsNoTemplateList?flowIndex=<%=flowIndex%>&periodIndex=<%=periodIndex%>&eqpType=<%=eqpType%>" method="post" id="partsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" id="eqpType" name="eqpType" value="" />
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
					<li><a class="button-text" href="#" onclick="javascript:closeWindow();"><span>&nbsp;�ر�&nbsp;</span></a></li>
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
<form action="<%=request.getContextPath()%>/control/partsSaveEntry?flowIndex=<%=flowIndex%>&periodIndex=<%=periodIndex%>&equipType=<%=eqpType%>&mtrGrp=<%=category%>" method="post" id="partsDataSaveForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>������Ϣ�б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">ѡ��</td>
          <td width="20%" class="en11pxb">���Ϻ�</td>
          <td width="20%" class="en11pxb">������</td>
          <td width="10%" class="en11pxb">�������</td>
          <td width="10%" class="en11pxb">�ο�����</td>
          <td width="10%" class="en11pxb">Ĭ������</td>
          <td width="20%" class="en11pxb">��ע</td>
        </tr>
        <%
        List partsUseList=(List)request.getAttribute("partsNoList");
        if(partsUseList != null && partsUseList.size() > 0) {
            int k = 0;
            for(Iterator it = partsUseList.iterator();it.hasNext();) {
                Map map = (Map)it.next();
        %>
		<input id="partsName_<%=k%>" type="hidden" name="partsName_<%=k%>" value='<%=map.get("PART_NAME")%>' />
			<td class="en11px"><input type="checkBox" id="parts_<%=k%>" name="parts_<%=k%>" value='<%=map.get("PART_NO")%>' onclick="partsSelect(this);"></td>
			<td class="en11px"><%=map.get("PART_NO")%></td>
			<td class="en11px"><%=map.get("PART_NAME")%></td>
			<td class="en11px">
			<ofbiz:if name="partTypeList">
				<select name="partType_<%=k%>" id="partType_<%=k%>"/>
					<ofbiz:iterator name="cust" property="partTypeList" type="java.util.Map">
						<option value='<ofbiz:inputvalue entityAttr="cust" field="ID"/>'><ofbiz:inputvalue entityAttr="cust" field="PART_TYPE"/></option>
					</ofbiz:iterator>
				</select>
			</ofbiz:if>
			</td>
			<td class="en11px"><input id="templateCount_<%=k%>" name="templateCount_<%=k%>" class="input" type="text" readonly="readonly" style="background:E9E9E9"/></td>    
			<td class="en11px"><input id="partsNum_<%=k%>" name="partsNum_<%=k%>" class="input" type="text" readonly="readonly" style="background:E9E9E9"/></td>
			<td class="en11px"><input id="remark_<%=k%>" name="remark_<%=k%>" class="input" size="40" type="text" readonly="readonly" style="background:E9E9E9"/></td>
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
	Ext.get('mtrGrp').dom.value='<%=mtrGrp%>'
	Ext.get('partNo').dom.value='<%=partNo%>'
	Ext.get('eqpType').dom.value='<%=eqpType%>'
</script>