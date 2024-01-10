<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>

<%
	String dept=(String)request.getAttribute("DEPT");
	String followIndex=(String)request.getAttribute("followIndex");
	String section=(String)request.getAttribute("SECTION");
	String existWfSubmitedFollow = (String) request.getAttribute("existWfSubmitedFollow");

	GenericValue gv=(GenericValue)request.getAttribute("GU_FOLLOW");
	pageContext.setAttribute("follow",gv);
	List followItemList = (List)request.getAttribute("ITEM_LIST");
	String eventType=String.valueOf(Constants.DOC_FOLLOW);
%>

<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
		var content = Ext.get('itemContent').dom.value;
		if(content==""){
			return "����׷�����ݲ���Ϊ��";
		}
		return "";
	}

	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300',dialogClose,false);

	//�������ܵ���ҳ
	function addFollowItem(obj){
		Ext.get('itemContent').dom.value="";
        Ext.get('itemIndex').dom.value="";
        Ext.get('uploadIndex').dom.value="";
		extDlg.showAddDialog(obj);
	}

	//�޸Ĺ��ܵ���ҳ
	function editFollowItem(obj,itemIndex){
		Ext.get('itemIndex').dom.value=itemIndex;
		Ext.get('uploadIndex').dom.value="";
		var url='<ofbiz:url>/intoEditfollowItem</ofbiz:url>?itemIndex='+itemIndex;
		extDlg.showEditDialog(obj,url);
	}

	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('itemContent').dom.value=result.itemContent;
		}
	}

	//ɾ��
	function delFollowItemByIndex(itemIndex,uploadIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delEditfollowItem</ofbiz:url>?itemIndex='+itemIndex+'&followIndex=<%=followIndex%>';
				document.location=url;
			}else{
				return;
			}
        });
	}

	//�ļ�����ҳ��
	function manageFile(){
		var uploadIndex=Ext.get('uploadIndex').dom.value;
		var itemIndex=Ext.get('itemIndex').dom.value;
		var url='<ofbiz:url>/fileUploadDefineEntry</ofbiz:url>?eventIndex='+itemIndex+'&eventType=<%=eventType%>&uploadItem=uploadIndex&uploadIndex='+uploadIndex;
		windowOpen(url,'�ļ��ϴ�����',685,400);
	}

	//�᰸
	function overCase(followIndex){
	 	Ext.MessageBox.confirm('�᰸ȷ��', '��ȷ��Ҫ�᰸��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/followJobOverCase</ofbiz:url>?followIndex='+followIndex+'&flag=1'
				document.location.href=url;
			}else{
				return;
			}
        });
	}

	//�ر��ťʱ����
	function dialogClose(){
		if(Ext.get('uploadIndex').dom.value==''){
			extDlg.getDialog().hide();
		}else{
			Ext.MessageBox.alert('����', '���ϴ��ļ����ڲ���ֱ�ӹرգ���ѡ��[�ύ]');
		}
	}

    //���ͽ᰸���뵽���տγ�
	function sendSubmitProcess(followIndex, followName) {
	    var ownerProcess = Ext.getDom('ownerProcess').value;
		if(ownerProcess.length == 0) {
			Ext.MessageBox.alert('����', '��ѡ���տγ�����!');
			return;
		}

		var submitObject = '<%=Constants.SUBMIT_FOLLOW%>';
	 	var submitObjectIndex = followIndex;
	 	var submitType = '<%=Constants.SUBMIT_INSERT%>';
	 	var submitObjectName = followName;
	 	var parameter = Ext.urlEncode({submitObject: submitObject, submitObjectIndex: submitObjectIndex, submitType: submitType, submitObjectName: submitObjectName, ownerProcess: ownerProcess});
	 	Ext.MessageBox.confirm('��ǩȷ��', '��ȷ��Ҫ��ǩ����Ϣ��',function result(value){
	 		if(value=="yes"){
	 			var url='<ofbiz:url>/sendSubmitProcess</ofbiz:url>?'+parameter;
				document.location.href=url;
			}else{
				return;
			}
	     });
	}
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����׷�ٲ�ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
          <td width="15%" bgcolor="#ACD5C9" class="en11pxb">����׷������</td>
	      <td width="35%" class="en11px"><ofbiz:inputvalue entityAttr="follow" field="followName"/></td>
          <td width="15%"  bgcolor="#ACD5C9" class="en11pxb">Ŀ��</td>
          <td width="35%" class="en11px"><ofbiz:inputvalue entityAttr="follow" field="purpose"/></td>
        </tr>
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">����</td>
	    	<td class="en11px"><%=dept%></td>
	    	<td bgcolor="#ACD5C9" class="en11pxb">�α�</td>
	    	<td class="en11px"><%=section%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb"><ofbiz:inputvalue entityAttr="follow" field="objectType"/></td>
	    	<td class="en11px"><ofbiz:inputvalue entityAttr="follow" field="object"/></td>
	    	<td bgcolor="#ACD5C9" class="en11pxb">������</td>
	    	<td class="en11px"><ofbiz:inputvalue entityAttr="follow" field="creator"/></td>
        </tr>

        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
	        	    <%if (existWfSubmitedFollow.equals("N")) {%>
              		    <tr bgcolor="#DFE1EC">
              		        <td class="en11px" nowrap>
              		            <b>*��������ѡ��</b>
              		            <select name="ownerProcess" id="ownerProcess">
              		                <option value=""></option>
                  		            <ofbiz:if name="ownerProcessList">
                            			<ofbiz:iterator name="cust" property="ownerProcessList" type="java.util.Map">
                            			    <option value="<ofbiz:entityfield attribute="cust" field="SECTION_LEADER"/>">
                            			        <ofbiz:entityfield attribute="cust" field="SECTION"/>
                            			        :
                            			        <ofbiz:entityfield attribute="cust" field="ACCOUNT_NAME"/>
                            			    </option>
                            		    </ofbiz:iterator>
                            	    </ofbiz:if>
                			    </select>
              		            <input type="button" name="btn" value="���ͽ᰸����" style="cursor:hand" onclick="sendSubmitProcess('<ofbiz:inputvalue entityAttr="follow" field="followIndex"/>', '<ofbiz:inputvalue entityAttr="follow" field="followName"/>')">
                            </td>
              		    </tr>
              		<%} else {%>
						<td class="en11px" nowrap>����ǩ����֪ͨ��������ǩ�ˣ�<td/>
					<%}%>

			  		<!--<tr height="30">
					   	<td width="20">&nbsp;</td>
					    <td><ul class="button">
								<li><a class="button-text" href="#" onclick="javascript:overCase('<ofbiz:inputvalue entityAttr="follow" field="followIndex"/>');"><span>&nbsp;�᰸&nbsp;</span></a></li>
						</ul></td>
			  		</tr>-->
				</table>
        	</td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
</table>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����׷�ٹ����б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="5%" class="en11pxb" align="center">
            <%if (existWfSubmitedFollow.equals("N")) {%>
                <img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addFollowItem(this);"/>
            <%}%>
          </td>
          <td width="8%" class="en11pxb">�����</td>
          <td width="27%" class="en11pxb">׷�ٲ�������</td>
          <td width="24%" class="en11pxb">���ӵ���</td>
          <td width="18%" class="en11pxb">�������</td>
          <td width="18%" class="en11pxb">�޸�ʱ��</td>
        </tr>
          <% if(followItemList != null && followItemList.size() > 0) {
          		int size=followItemList.size();
				for(int i=0;i<size;i++){
					Map map=(Map)followItemList.get(i);
					%>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center">
		            <%if (i==(size-1) && existWfSubmitedFollow.equals("N")){%>
		                <img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delFollowItemByIndex('<%=map.get("ITEM_INDEX")%>')"/>
		            <%}%>
		          </td>
		          <td class="en11px">
		            <%if (existWfSubmitedFollow.equals("N")) {%>
    		            <a href="#" onclick="editFollowItem(this,'<%=map.get("ITEM_INDEX")%>')">
    		                <%=map.get("ITEM_ORDER")%>
    		            </a>
		            <%} else {%>
		                <%=map.get("ITEM_ORDER")%>
		            <%}%>
		          </td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ITEM_CONTENT"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("FILE_DESCRIPTION"))%></td>
		          <td class="en11px"><%=map.get("CREATE_TIME")%></td>
		          <td class="en11px"><%=map.get("UPDATE_TIME")%></td>
		        </tr>
		    <%
		  	}
		  } %>
      </table>
      </fieldset></td>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:history.back(-1)"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
	</tr>
</table>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">����׷�ٹ���</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="����׷�ٹ���">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/followItemManage" method="post" id="followItemForm" onsubmit="return false;">
                	<input id="followIndex" type="hidden" name="followIndex" value="<%=followIndex%>" />
                	<input id="itemIndex" type="hidden" name="itemIndex"/>
                	<input id="uploadIndex" type="hidden" name="uploadIndex"/>
                <p>
                <label for="name"><small>����</small></label>
                <input class="textinput" type="text" name="itemContent" id="itemContent" value="" tabindex="1" />
                </p>
                <p><label for="description"><small>���ӵ���</small>&nbsp;<img src="../images/icon_search.gif" width="15" height="16" border="0" onclick="manageFile();" style="cursor:hand"></label>
                </p>
                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>