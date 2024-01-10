<%@ page import="java.text.SimpleDateFormat" %>
<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%
		String accountName = "";
		String accountDept = "";
		String accountSection = "";
		String equipmentID = "";
		String equipmentDes = "";
		String NowTime = "";
		String abnormalEvent = "";
		String member = "";
		String abnormalEffect = "";
		String issueTech = "";
		String preventMethod = "";
		String abnormalDocIndex = "";
		String createTime = "";
		String endTime = "";
		String DocName = "";
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Date now=new Date();
		NowTime = dFormat.format(now);
		if(pageContext.findAttribute("AccountShowList")!=null){
			GenericValue user = (GenericValue)request.getAttribute("AccountShowList");
			if(user != null) {
				accountName = user.getString("accountName");
				member = user.getString("accountName");
				accountDept = user.getString("accountDept");
				accountSection =  user.getString("accountSection");
			}
		}
		
		if(pageContext.findAttribute("EquipmentShowList")!=null){
			GenericValue user = (GenericValue)request.getAttribute("EquipmentShowList");
			if(user != null) {
				equipmentID = user.getString("equipmentId");
				equipmentDes = user.getString("equipmentDesc");
			}
		}
		
		if(pageContext.findAttribute("DocumentShowList")!=null){
			GenericValue user = (GenericValue)request.getAttribute("DocumentShowList");
			if(user != null) {
				abnormalEvent = user.getString("abnormalEvent");
				member = user.getString("member");
				abnormalEffect = user.getString("abnormalEffect");
				issueTech = user.getString("issueTech");
				preventMethod = user.getString("preventMethod");
				abnormalDocIndex = user.getString("abnormalDocIndex");
				DocName = user.getString("abnormalDocName");
				//
				//if (user.get("createTime") != null ){
				//	createTime = dFormat1.format(user.get("createTime"));
				//}
				//if (user.get("endTime") != null){
				//	endTime = dFormat1.format(user.get("endTime"));
				//}
				if (user.getString("createTime") != null ){
                	createTime = user.getString("createTime");
                }
                if (user.getString("endTime") != null ){
                	createTime = user.getString("endTime");
                }

			}
		}

		if(pageContext.findAttribute("member")!=null){
			member = request.getAttribute("member").toString();
		}
	String eventType=String.valueOf(Constants.DOC_ABNORMAL);
%>

<form  action="<%=request.getContextPath()%>/control/abnormalDocDefine" name="abnormalDocDefine" method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="0"><input type="hidden"  name="abnormalDocIndex"  id="abnormalDocIndex" class="input" value = <%=UtilFormatOut.checkNull(abnormalDocIndex)%>>
    <input type="hidden"  name="status"  id="status" class="input">
     <input type="hidden"  name="equipmentId"  id="equipmentId" class="input" value = <%=UtilFormatOut.checkNull(equipmentID)%>>
    </td>
    <td width="100%"><fieldset><legend>�쳣������</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
        <!-----���ŵ��豸ID---->
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">����</td>
          <td width="13%" class="en11px"><%=UtilFormatOut.checkNull(accountDept)%></td>
          <td width="14%" bgcolor="#ACD5C9" class="en11pxb">�α�</td>
          <td width="17%" class="en11px"><%=UtilFormatOut.checkNull(accountSection)%></td>
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">׫дʱ��</td>
          <td width="32%" class="en11px"><%=UtilFormatOut.checkNull(NowTime)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">������</td>
          <td class="en11px" ><%=UtilFormatOut.checkNull(accountName)%></td>
          <td class="en11pxb" bgcolor="#ACD5C9">�ļ����</td>
          <td colspan="3" class="en11px"><%=UtilFormatOut.checkNull(DocName) %></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">�쳣�¼�</td>
          <td colspan="5" class="en11px"><label>
            <input type="text" size="80" name="abnormalEvent" id="abnormalEvent" class="input" value = <%=UtilFormatOut.checkNull(abnormalEvent)%>>
          </label></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">��Ա</td>
          <td colspan="5" class="en11px"><input type="text" size="80" name="member" id="member" class="input" value = <%=UtilFormatOut.checkNull(member)%>></td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<br/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="98%"><fieldset><legend>�쳣����</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
        <!-----���ŵ��豸ID---->
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">����ʱ��</td>
          <td width="13%" class="en11px"><input type="text" ID="create_Time" NAME="create_Time" readonly value = <%=UtilFormatOut.checkNull(createTime)%>></td>
          <td width="14%" bgcolor="#ACD5C9" class="en11pxb">����ʱ��</td>
          <td width="17%" class="en11px"><input type="text" ID="end_Time" NAME="end_Time" readonly value = <%=UtilFormatOut.checkNull(endTime)%>></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb">�豸</td>
          <td class="en11px"><%=UtilFormatOut.checkNull(equipmentID)%></td>
          <td bgcolor="#ACD5C9" class="en11pxb">�豸����</td>
          <td class="en11px"><%=UtilFormatOut.checkNull(equipmentDes)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb">�쳣����</td>
          <td colspan="3" class="en11px">
            <textarea name="abnormalEffect" id="abnormalEffect" cols="60" rows="3" ><%=UtilFormatOut.checkNull(abnormalEffect)%></textarea>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb">�쳣����</td>
          <td colspan="3" class="en11px"><textarea name="issueTech" id="issueTech" cols="60" rows="3"><%=UtilFormatOut.checkNull(issueTech)%></textarea></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb">Ԥ������</td>
          <td colspan="3" class="en11px"><textarea name="preventMethod" id="preventMethod" cols="60" rows="3"><%=UtilFormatOut.checkNull(preventMethod)%></textarea></td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/abnormalDocDefineSave','2')"><span>&nbsp;�ݴ�&nbsp;</span></a></li> 
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/abnormalDocDefineSave','1')"><span>&nbsp;���&nbsp;</span></a></li> 
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:manageFile()"><span>&nbsp;����&nbsp;</span></a></li> 
	</ul></td>
    <td>
    <input id="itemIndex" type="hidden" name="itemIndex"/>
    <input id="uploadIndex" type="hidden" name="uploadIndex"/>
    </td>
  </tr>
</table>
</form>

<script language="javascript">
	Ext.onReady(function(){
	    var create_Time = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });
	    var end_Time = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });
	    //���ؼ���ҳ���INPUT������
	    create_Time.applyTo('create_Time');   
	    end_Time.applyTo('end_Time');  
	    
	    var checkColumnLengthMsg = checkColumnLength('abnormalEvent||100||�쳣�¼�;abnormalEffect||500||�쳣����;issueTech||500||�쳣����;preventMethod||500||Ԥ������');
    if(checkColumnLengthMsg.length > 0 ) {
    	return checkColumnLengthMsg;
	}
	});
		
	function doSubmit(url,status) {
		if (abnormalDocDefine.create_Time.value !="" & abnormalDocDefine.end_Time.value != ""){
			if (abnormalDocDefine.end_Time.value < abnormalDocDefine.create_Time.value){
				Ext.MessageBox.alert("����","����ʱ�䲻�ܴ��ڽ���ʱ�䣡");
				return;
			}
		}
		if (status == "1"){
			if (abnormalDocDefine.create_Time.value ==""){
				Ext.MessageBox.alert("����","����ʱ�䲻��Ϊ�գ�");
				return;
			}
			if (abnormalDocDefine.end_Time.value ==""){
				Ext.MessageBox.alert("����","����ʱ�䲻��Ϊ�գ�");
				return;
			}
			if (abnormalDocDefine.abnormalEvent.value ==""){
				Ext.MessageBox.alert("����","�쳣�¼�����Ϊ�գ�");
				return;
			}
		}
		document.abnormalDocDefine.status.value = status;
		document.abnormalDocDefine.action = url;
		document.abnormalDocDefine.submit();	
	}
	
	
	
	//�ļ�����ҳ��
	function manageFile(){
		var uploadIndex=Ext.get('uploadIndex').dom.value;
		var itemIndex=Ext.get('abnormalDocIndex').dom.value;
		var url='<ofbiz:url>/fileUploadDefineEntry</ofbiz:url>?eventIndex='+itemIndex+'&eventType=<%=eventType%>&uploadItem=uploadIndex&uploadIndex='+uploadIndex;
		windowOpen(url,'�ļ��ϴ�����',685,400);
	}
</script>