<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%
		String accountName = "";
		String accountDept = "";
		String accountSection = "";
		String NowTime = "";
		String content = "";
		String Type = "11";
		String TypeID = "";
		String member = "";
		String docName = "";
		String improveDocIndex = "";
		String createTime = "";
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		Date now=new Date();
		NowTime = dFormat.format(now);
		createTime = dFormat.format(now);
		
		if(pageContext.findAttribute("AccountShowList")!=null){
			GenericValue user = (GenericValue)request.getAttribute("AccountShowList");
			if(user != null) {
				accountName = user.getString("accountName");
				accountDept = user.getString("accountDept");
				accountSection =  user.getString("accountSection");
				member = user.getString("accountName");
				//docName = user.getString("docName");
			}
		}
		if(pageContext.findAttribute("type")!=null){
			TypeID = (String)request.getAttribute("type");
			if (TypeID.equals("EQP")){
				Type = "�豸����";
			}else if (TypeID.equals("PM")){
				Type = "��������";
			}else if (TypeID.equals("SE")){
				Type = "��ȫ����";
			}
		}
		if(pageContext.findAttribute("flag").equals("new")){

		}
		else{
			if(pageContext.findAttribute("DocumentShowList")!=null){
				GenericValue user = (GenericValue)request.getAttribute("DocumentShowList");
				if(user != null) {
					content = user.getString("content");
					docName = user.getString("docName");
				    improveDocIndex = user.getString("impDocIndex");
					//
					if (user.getString("createTime") != null ){
					//	createTime = dFormat1.format(dFormat1.parse(user.getString("createTime")));
						createTime = user.getString("createTime");
					}
					else{
						createTime = dFormat1.format(now);
					}
					//
					TypeID = user.getString("type");
					if (TypeID.equals("EQP")){
						Type = "�豸����";
					}else if (TypeID.equals("PM")){
						Type = "��������";
					}else if (TypeID.equals("SE")){
						Type = "��ȫ����";
					}
				}
			}
		}
		
		String eventType=String.valueOf(Constants.DOC_IMPROVER);
%>

<form  action="<%=request.getContextPath()%>/control/improveDocDefine" name="improveDocDefine" method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="98%"><fieldset><legend>�����౨����</legend>
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
          <td colspan="3" class="en11px"><%=UtilFormatOut.checkNull(docName)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">��������</td>
          <td colspan="5" class="en11px"><label>
           <textarea name="content" id="content" cols="60" rows="3" ><%=UtilFormatOut.checkNull(content)%></textarea>
          </label></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">��������</td>
 		<td colspan="5" class="en11px"><%=UtilFormatOut.checkNull(Type)%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9"><input type="hidden"  name="impDocIndex"  id="impDocIndex" class="input" value = <%=UtilFormatOut.checkNull(improveDocIndex)%>>
    <input type="hidden"  name="create_Time"  id="create_Time" class="input" value = <%=UtilFormatOut.checkNull(createTime)%>>
     <input type="hidden"  name="type"  id="type" class="input" value = <%=UtilFormatOut.checkNull(TypeID)%>>
          ��Ա<input id="itemIndex" type="hidden" name="itemIndex"/>
    <input id="uploadIndex" type="hidden" name="uploadIndex"/></td>
          <td colspan="5" class="en11px"><input type="hidden" size="80" border = "0" name="member" readonly id="member" class="input" value = <%=UtilFormatOut.checkNull(member)%>><%=UtilFormatOut.checkNull(member)%></td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<br/>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/improveDocDefineSave')"><span>&nbsp;���&nbsp;</span></a></li> 
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:manageFile()"><span>&nbsp;����&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>
</form>

<script language="javascript">
	
	function doSubmit(url) {
	
		if (improveDocDefine.content.value == ""){
			Ext.MessageBox.alert("����","�������ݲ���Ϊ�գ�");
			return;
		}
		Ext.MessageBox.confirm('����', '��ȷ��Ҫ�����˼�¼��',function result(value){
			if(value=="yes"){
		document.improveDocDefine.action = url;
		document.improveDocDefine.submit();	
	}
	})}
	
	//�ļ�����ҳ��
	function manageFile(){
		var uploadIndex=Ext.get('uploadIndex').dom.value;
		var itemIndex=Ext.get('impDocIndex').dom.value;
		var url='<ofbiz:url>/fileUploadDefineEntry</ofbiz:url>?eventIndex='+itemIndex+'&eventType=<%=eventType%>&uploadItem=uploadIndex&uploadIndex='+uploadIndex;
		windowOpen(url,'�ļ��ϴ�����',685,400);
	}
</script>