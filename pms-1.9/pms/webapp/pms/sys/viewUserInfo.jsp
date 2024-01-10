<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ taglib uri="input" prefix="input" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.*" %>
<%@ page import ="com.csmc.pms.webapp.util.*"%>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<%
List guiPriv = (List)request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY);
boolean isNewFlag = false;
if("true".equals(request.getAttribute("isNewUser"))){
isNewFlag=true;
}

%>
<form name="UserSetupInfo" action="<%=request.getContextPath()%>/control/updateUserInfo" method=post >
<input name="selectedGroup" type="hidden" >
<input name="availableGroup" type="hidden" >
<input name="isNewUser" value="<%=isNewFlag%>" type="hidden" >
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td>&nbsp;</td>
    <td valign="top"> <fieldset> <legend><%if(isNewFlag){%>新用户<%}else{%>用户信息<%}%></legend> 
        <table width="100%" border="0" cellspacing="1" cellpadding="0">
        <tr bgcolor="#DFE1EC"> 
          <td width="16%" class="en11pxb">用户名</td>
          <td width="21%"> 
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td height="26" class="en11pxb" width="20%"> 
            <input:text name="accountid"  maxlength="20" readonly="true" styleclass="readonly" />
          </td>
		                 
              </tr>
            </table>
          </td>
          <td width="16%" class="en11pxb">&nbsp;</td>
          <td width="47%">&nbsp; </td>
        </tr>
        <tr bgcolor="#DFE1EC"> 
          <td class="en11pxb" width="16%">密码</td>
          <td height="26" class="en11pxb" width="21%"> 
            <input:text name="password" password="true"  maxlength="20" />
          </td>
          <td class="en11pxb" width="16%">确认密码</td>
          <td width="47%"> 
            <input:text name="confirmedPassword" password="true"  maxlength="20" />
          </td>
        </tr>
        <tr bgcolor="#DFE1EC"> 
          <td class="en11pxb" height="26" width="16%">描述</td>
          <td height="26" class="en11pxb" width="21%" colspan="3"> 
            <input:text name="description" size="79"  maxlength="80" />
          </td>
          
        </tr>
      </table>
      </fieldset>
      <br>
             <%
            	if(!isNewFlag){
            %>      
	<fieldset>
	<legend>用户组信息</legend> 
      <table width="100%" border="0" cellspacing="1" cellpadding="0">
        <tr bgcolor="#DFE1EC"> 
          <td class="en11pxb" height="28" width="307">
		  <table width="300" border="0" cellspacing="1" cellpadding="0">
            <tr bgColor=#DFE1EC align="center">
              <td width="95%" align="center" class="title-en">可选组列表</td>
            </tr>
            <tr bgColor=#bdd9ec>
              <td width="95%" align="center">
				<%			
				List availableGroups = (List)request.getAttribute("availableGroups");
				if(availableGroups!=null){
					Collections.sort(availableGroups);
				}	
					
				%>               
                <input:select name="availableGroupList" styleclass="select" multiple="true" ondblclick="javascript:groupAdd();" options="<%=availableGroups%>" />           
				</td> 
            </tr>  
          </table></td>
          <td width="111" height="28" align="center">
		  <table width="94" height="63" border="0" cellpadding="0" cellspacing="1">
		  <tr align="center"><td width="92">
		  <table border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
              <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:groupAdd()" class="button-text">&gt;&gt;</a></td>
              <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
            </tr>
          </table> 
		  </td></tr>
		  <tr align="center"><td height="23">
		  <table border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
              <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:groupRemove()" class="button-text">&lt;&lt;</a></td>
              <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
            </tr>
          </table> 
		  </td></tr>
            </table>
		  </td>
          <td class="en11pxb" height="28" width="531" colspan="2"><table width="300" border="0" cellspacing="1" cellpadding="0">
            <tr bgColor=#DFE1EC align="center">
              <td width="95%" align="center" class="title-en">已选组列表</td>
            </tr>
            <tr bgColor=#bdd9ec>
              <td width="95%" align="center">
                <select name="selectedGroupList" class="select" multiple size=5 ondblclick="javascript:groupRemove()">
				<%
				List groupList = (List)request.getAttribute("groupList");
				if(groupList!=null){
					Collections.sort(groupList);
					for(Iterator it = groupList.iterator();it.hasNext();){
					String name = it.next().toString();
				%>
				<option value="<%=name%>"> <%=name%></option>
				<%
				}
				}
				%>                
                </select>
</td>
            </tr>
          </table></td>
          
        </tr>
      </table>
      </fieldset> 
          <%
          }
          %>        
	  </td>
  </tr>
</table>
<table width="100%" height="35"  border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="1%"><img name="" src="" width="20" height="1" alt=""></td>
    <td width="99%"><table height="35" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:submit();" class="button-text">保存 </a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
             <%
            	if(!isNewFlag){
            %>
		  <td> 
		 <%
		  if(guiPriv.contains("ADD_PRIVILEGE")) {
		 %>	
		  <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:submit('<%=request.getContextPath()%>/control/setupUserPriv');" class="button-text">设置用户权限 </a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table>
			<%
			} else {
			%>
		  <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="#" class="button-text-disable">设置用户权限 </a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table>			
			<%
			}
			%>
			</td>
          <td>&nbsp;</td>

		  <td> 
		 <%
		  if(guiPriv.contains("COPY_USER")) {
		 %>	
		  <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="<%=request.getContextPath()%>/control/queryUser?newaccountid=<%=request.getParameter("accountid")%>&isNewUser=<%=isNewFlag%>" class="button-text">复制用户</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table>
			<%
			} else {
			%>
		  	<table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="#" class="button-text-disable">复制用户</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table>
			<%
			}
			%>
			</td>
          <td>&nbsp;</td>
          <%
          }
          %>          
		  <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="<%=request.getContextPath()%>/control/setupUser" class="button-text">取消 </a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
            <%
            	if(!isNewFlag){
            %>
		  <td> 
		 <%
		  if(guiPriv.contains("DELETE_USER")) {
		 %>	
		  <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:doDelete(document.UserSetupInfo,'<%=request.getContextPath()%>/control/deleteUser');" class="button-text">删除用户 </a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table>
			<%
			} else {
			%>
		  	<table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="#" class="button-text-disable">删除用户 </a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table>
			<%
			}
			%>			</td> 
			<%
			}
			%>                       
          <td>&nbsp;</td>
        </tr>
      </table></td>
  </tr>
</table>
</form>

<script language="javascript1.2">
function groupAdd(){
		with(document.UserSetupInfo){
				var leng = availableGroupList.length;
				for(var loop=0;loop<leng;loop++){
						if(availableGroupList.options[loop].selected){
								var sOption = document.createElement("OPTION");
								var text = availableGroupList.options[loop].text;
								var value = availableGroupList.options[loop].value;
								sOption.text=text;
								sOption.value=value;
								selectedGroupList.add(sOption);
								availableGroupList.remove(loop);
								break;
						}
				}
		}
}

function groupRemove(){
	with(document.UserSetupInfo){
			var leng = selectedGroupList.length;
			for(var loop=0;loop<leng;loop++){
					if(selectedGroupList.options[loop].selected){
							var sOption = document.createElement("OPTION");
							var text = selectedGroupList.options[loop].text;
							var value = selectedGroupList.options[loop].value;
							sOption.text=text;
							sOption.value=value;
							availableGroupList.add(sOption);
							selectedGroupList.remove(loop);
							break;
					}
			}
	}
}
<%
String token = "\u0019";
  
%>
function submit(action){
	var selectedGroupStr = "";
	var availableGroupStr = "";	
	try{
		with(document.UserSetupInfo){
			var leng = selectedGroupList.length;
			for(var loop=0;loop<leng;loop++){
				if(selectedGroupStr.length > 0){
					selectedGroupStr = selectedGroupStr + "<%=token%>"+ selectedGroupList.options[loop].value;
				}else{
					selectedGroupStr = selectedGroupList.options[loop].value;
				}
			}
			selectedGroup.value=selectedGroupStr;
		}
	}catch(e){
	
	}
	if(action!=null)
		document.UserSetupInfo.action=action;
	document.UserSetupInfo.submit();
}
</script>