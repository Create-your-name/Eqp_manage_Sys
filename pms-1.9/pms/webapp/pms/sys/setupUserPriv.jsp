<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri="input" prefix="input" %>

<%
try {
	java.util.Collection userPriv = (java.util.Collection)request.getAttribute("userPriv");
	java.util.Collection groupPriv = (java.util.Collection)request.getAttribute("groupPriv");
	javax.swing.tree.DefaultMutableTreeNode dmt = (javax.swing.tree.DefaultMutableTreeNode)	request.getAttribute("dmt");
%>

<form method=POST action='<%=request.getContextPath()%>/control/updateUserPriv' name="setupUserPrivForm">
<fieldset> <legend>User Privilege Infomation </legend>
<input:input type="hidden" name="accountid" />
	<%
	if(dmt.getChildCount() >0){
	%>
        <table width="100%" border="0" cellspacing="1" cellpadding="0">
        <%
        javax.swing.tree.DefaultMutableTreeNode rootNode = (javax.swing.tree.DefaultMutableTreeNode)dmt.getFirstChild();
        while(rootNode!=null)
        {
        com.csmc.pms.webapp.security.model.MenuModel mm = (com.csmc.pms.webapp.security.model.MenuModel)rootNode.getUserObject();
        %>
        <tr bgcolor="#DFE1EC">
          <td width="10%" nowrap height="25" class="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<input name="chk_<%=mm.getMenuDesc()%>" type="hidden" value="on">
          		 	<input name="chk_x" checked disabled type="checkbox" id="chk0" value="true" >
          		<%}else{%>
              		<input name="chk_<%=mm.getMenuDesc()%>" onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');" <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  type="checkbox" id="chk0" value="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
          </td>
          <td width="90%" class="en11pxb">
            <%
            int twoLevelChildCount = rootNode.getChildCount();
        	if(twoLevelChildCount>0)
        	{
        	String[] bgColor=new String[]{"#DFE1EC","#DFE1EC"};
        	int i = 1;
        	%>
        		 <table width="100%"  bgcolor="silver"  border="0" cellspacing="1" cellpadding="0">
        	<%
        	    javax.swing.tree.DefaultMutableTreeNode twoLevelNode = (javax.swing.tree.DefaultMutableTreeNode)rootNode.getFirstChild();

        		while(twoLevelNode!=null){
         		mm = (com.csmc.pms.webapp.security.model.MenuModel)twoLevelNode.getUserObject();
         		i = 1-i;
            %>
				 <tr bgcolor="<%=bgColor[1-i]%>">
				          <td  width="10%" nowrap  height="25" class="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<input name="chk_<%=mm.getMenuDesc()%>" type="hidden" value="on">
          		 	<input name="chk_x" checked disabled type="checkbox" id="chk0" value="true" >
          		<%}else{%>
              		<input name="chk_<%=mm.getMenuDesc()%>"  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"  <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  type="checkbox" id="chk0" value="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
				   </td>
				  <td width="99%" class="en11pxb">
            <%
            int threeLevelChildCount = twoLevelNode.getChildCount();
        	if(threeLevelChildCount>0)
        	{
        	%>
        		 <table width="100%" bgcolor="white"  border="0" cellspacing="0" cellpadding="0">
        	<%
        		javax.swing.tree.DefaultMutableTreeNode threeLevelNode = (javax.swing.tree.DefaultMutableTreeNode)twoLevelNode.getFirstChild();
        		while(threeLevelNode!=null){

        		mm = (com.csmc.pms.webapp.security.model.MenuModel)threeLevelNode.getUserObject();
            %>
				 <tr bgcolor="<%=bgColor[1-i]%>">
				          <td  width="10%" nowrap  height="25" class="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<input name="chk_<%=mm.getMenuDesc()%>" type="hidden" value="on">
          		 	<input name="chk_x" checked disabled type="checkbox" id="chk0" value="true" >
          		<%}else{%>
              		<input name="chk_<%=mm.getMenuDesc()%>"  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"  <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  type="checkbox" id="chk0" value="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
				   </td>
			 <td width="36%" class="en11pxb">
			            <%
			            int fourLevelChildCount = threeLevelNode.getChildCount();
			        	if(fourLevelChildCount>0)
			        	{
			        	%>
			        		 <table width="100%" border="0" bgcolor="silver"  cellspacing="1" cellpadding="0">
			        	<%
			        		javax.swing.tree.DefaultMutableTreeNode fourLevelNode = (javax.swing.tree.DefaultMutableTreeNode)threeLevelNode.getFirstChild();
			        		while(fourLevelNode!=null){

			        		mm = (com.csmc.pms.webapp.security.model.MenuModel)fourLevelNode.getUserObject();
			            %>
							 <tr bgcolor="<%=bgColor[1-i]%>">
							          <td width="26%" height="25" class="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<input name="chk_<%=mm.getMenuDesc()%>" type="hidden" value="on">
          		 	<input name="chk_x" checked disabled type="checkbox" id="chk0" value="true" >
          		<%}else{%>
              		<input name="chk_<%=mm.getMenuDesc()%>"  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"  <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  type="checkbox" id="chk0" value="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
							   </td>
			 <td width="36%" class="en11pxb">
			            <%
			            int fiveLevelChildCount = fourLevelNode.getChildCount();
			        	if(fiveLevelChildCount>0)
			        	{
			        	%>
			        		 <table width="100%" border="0" bgcolor="silver"  cellspacing="1" cellpadding="0">
			        	<%
			        		javax.swing.tree.DefaultMutableTreeNode fiveLevelNode = (javax.swing.tree.DefaultMutableTreeNode)fourLevelNode.getFirstChild();
			        		while(fiveLevelNode!=null){

			        		mm = (com.csmc.pms.webapp.security.model.MenuModel)fiveLevelNode.getUserObject();
			            %>
							 <tr bgcolor="<%=bgColor[1-i]%>">
							          <td width="26%" height="25" class="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<input name="chk_<%=mm.getMenuDesc()%>" type="hidden" value="on">
          		 	<input name="chk_x" checked disabled type="checkbox" id="chk0" value="true" >
          		<%}else{%>
              		<input name="chk_<%=mm.getMenuDesc()%>"  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"  <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  type="checkbox" id="chk0" value="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
							   </td>
							   </tr>

						 <%
						 fiveLevelNode = fiveLevelNode.getNextSibling();
						 	}
						 %>
						 </table>
						<%

						}
						%>
			 		</td>

							   </tr>

						 <%
						 fourLevelNode = fourLevelNode.getNextSibling();
						 	}
						 %>
						 </table>
						<%

						}
						%>
			 		</td>
				   </tr>

			 <%
			 threeLevelNode = threeLevelNode.getNextSibling();
			 	}
			 %>
			 </table>
			<%

			}
			%>
			 		</td>
			 </tr>
			 <%
			 twoLevelNode = twoLevelNode.getNextSibling();
			 	}
			 %>
			 </table>
			<%

			}
			%>
			</td>
         </tr>
		<%
		rootNode = rootNode.getNextSibling();
		}
		%>
      </table>

<%
    }
} catch (Exception e) {
	out.println(e);
}
%>
<table width="100%" height="35"  border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="1%"><img name="" src="" width="20" height="1" alt=""></td>
    <td width="99%"><table height="35" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:submitform('<%=request.getContextPath()%>/control/updateUserPriv');" class="button-text">保存</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
		   <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:submitform('<%=request.getContextPath()%>/control/setupUserPriv');" class="button-text">重置</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
		  <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:submitform('<%=request.getContextPath()%>/control/viewUserInfo');" class="button-text">取消</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
        </tr>
      </table>
<script language="javascript">
function submitform(action){
if(action!=null)
	document.setupUserPrivForm.action=action;
document.setupUserPrivForm.submit();
}

function checkTheSame(obj,name){
	var i = 0;
	while(i<document.setupUserPrivForm.elements[name].length){
		document.setupUserPrivForm.elements[name][i].checked = obj.checked;
		i = i+1;
	};
}
</script>