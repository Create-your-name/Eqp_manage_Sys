<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>


<%
try {
	java.util.List groupNames=(java.util.List) request.getAttribute("groupNames");
	Collections.sort(groupNames);
	groupNames.remove("errcode");
	java.util.Iterator groupNameItor =(java.util.Iterator) groupNames.iterator();
	java.util.Collection userPriv=(java.util.Collection) request.getAttribute("groupPriv");
	java.util.Collection groupPriv=(java.util.Collection) request.getAttribute("legencyPrivs");
	javax.swing.tree.DefaultMutableTreeNode dmt=(javax.swing.tree.DefaultMutableTreeNode) request.getAttribute("dmt");
%>
<FORM METHOD=POST ACTION='<%=request.getContextPath()%>/control/updateUserGroupInfo' NAME="setupGroupPrivForm">
	<FIELDSET><LEGEND>用户组列表</LEGEND>
		<TABLE WIDTH="100%" BORDER="0" CELLSPACING="0" CELLPADDING="0">
			<TR BGCOLOR="#DFE1EC">
	        	<TD WIDTH="20%" NOWRAP HEIGHT="25" CLASS="en11pxb">
	        		Group List
	        		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	        		<SELECT ID="accountid" NAME="accountid" CLASS="input" onchange="grouplist_onchange();">
						<%
						while (groupNameItor.hasNext()) {
							String groupNameString = (String) groupNameItor.next();

						%>
							<OPTION VALUE="<%=groupNameString%>" <%if(groupNameString.equalsIgnoreCase((String)request.getAttribute("groupName"))) out.print("selected");%>><%=groupNameString%></OPTION>
						<%
						}%>
	        		</select>
	        	</TD>
	        </TR>
		</TABLE>
	</FIELDSET>
	<BR>
	<FIELDSET><LEGEND>用户组权限详细信息</LEGEND>
	<%
	if (dmt.getChildCount()>0){
		javax.swing.tree.DefaultMutableTreeNode rootNode = (javax.swing.tree.DefaultMutableTreeNode)dmt.getFirstChild();
	%>
        <TABLE WIDTH="100%" BORDER="2" CELLSPACING="0" CELLPADDING="0" BORDERCOLOR=white>
        <%while(rootNode!=null)
        {
        	com.csmc.pms.webapp.security.model.MenuModel mm = (com.csmc.pms.webapp.security.model.MenuModel)rootNode.getUserObject();
        %>
        <TR BGCOLOR="#DFE1EC">
          <TD WIDTH="10%" NOWRAP HEIGHT="25" CLASS="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<INPUT NAME="chk_<%=mm.getMenuDesc()%>" TYPE="hidden" VALUE="on">
          		 	<INPUT NAME="chk_x" checked disabled TYPE="checkbox" ID="chk0" VALUE="true" >
          		<%}else{%>
              		<INPUT NAME="chk_<%=mm.getMenuDesc()%>" <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"   TYPE="checkbox" ID="chk0" VALUE="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
          </TD>
          <TD WIDTH="90%" CLASS="en11pxb">
            <%
            int twoLevelChildCount = rootNode.getChildCount();
        	if(twoLevelChildCount>0)
        	{
        	String[] bgColor=new String[]{"#DFE1EC","#DFE1EC"};
        	int i = 1;
        	%>
        		 <TABLE WIDTH="100%"  BGCOLOR="silver"  BORDER="2" CELLSPACING="0" CELLPADDING="0" BORDERCOLOR=white>
        	<%
        	    javax.swing.tree.DefaultMutableTreeNode twoLevelNode = (javax.swing.tree.DefaultMutableTreeNode)rootNode.getFirstChild();

        		while(twoLevelNode!=null){
         		mm = (com.csmc.pms.webapp.security.model.MenuModel)twoLevelNode.getUserObject();
         		i = 1-i;
            %>
				 <TR BGCOLOR="<%=bgColor[1-i]%>">
				          <TD  WIDTH="10%" NOWRAP  HEIGHT="25" CLASS="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<INPUT NAME="chk_<%=mm.getMenuDesc()%>" TYPE="hidden" VALUE="on">
          		 	<INPUT NAME="chk_x" checked disabled TYPE="checkbox" ID="chk0" VALUE="true" >
          		<%}else{%>
              		<INPUT NAME="chk_<%=mm.getMenuDesc()%>"  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"  <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  TYPE="checkbox" ID="chk0" VALUE="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
				   </TD>
				  <TD WIDTH="99%" CLASS="en11pxb">
            <%
            int threeLevelChildCount = twoLevelNode.getChildCount();
        	if(threeLevelChildCount>0)
        	{
        	%>
        		 <TABLE WIDTH="100%" BGCOLOR="white"  BORDER="2" CELLSPACING="0" CELLPADDING="0" BORDERCOLOR=white>
        	<%
        		javax.swing.tree.DefaultMutableTreeNode threeLevelNode = (javax.swing.tree.DefaultMutableTreeNode)twoLevelNode.getFirstChild();
        		while(threeLevelNode!=null){

        		mm = (com.csmc.pms.webapp.security.model.MenuModel)threeLevelNode.getUserObject();
            %>
				 <TR BGCOLOR="<%=bgColor[1-i]%>">
				          <TD  WIDTH="10%" NOWRAP  HEIGHT="25" CLASS="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<INPUT NAME="chk_<%=mm.getMenuDesc()%>" TYPE="hidden" VALUE="on">
          		 	<INPUT NAME="chk_x" checked disabled TYPE="checkbox" ID="chk0" VALUE="true" >
          		<%}else{%>
              		<INPUT NAME="chk_<%=mm.getMenuDesc()%>"  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"  <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  TYPE="checkbox" ID="chk0" VALUE="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
				   </TD>
			 <TD WIDTH="36%" CLASS="en11pxb">
			            <%
			            int fourLevelChildCount = threeLevelNode.getChildCount();
			        	if(fourLevelChildCount>0)
			        	{
			        	%>
			        		 <TABLE WIDTH="100%" BORDER="2" BGCOLOR="silver"  CELLSPACING="0" CELLPADDING="0" BORDERCOLOR=white>
			        	<%
			        		javax.swing.tree.DefaultMutableTreeNode fourLevelNode = (javax.swing.tree.DefaultMutableTreeNode)threeLevelNode.getFirstChild();
			        		while(fourLevelNode!=null){

			        		mm = (com.csmc.pms.webapp.security.model.MenuModel)fourLevelNode.getUserObject();
			            %>
							 <TR BGCOLOR="<%=bgColor[1-i]%>">
							          <TD WIDTH="26%" HEIGHT="25" CLASS="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<INPUT NAME="chk_<%=mm.getMenuDesc()%>" TYPE="hidden" VALUE="on">
          		 	<INPUT NAME="chk_x" checked disabled TYPE="checkbox" ID="chk0" VALUE="true" >
          		<%}else{%>
              		<INPUT NAME="chk_<%=mm.getMenuDesc()%>"  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"  <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  TYPE="checkbox" ID="chk0" VALUE="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
							   </TD>
			 <TD WIDTH="36%" CLASS="en11pxb">
			            <%
			            int fiveLevelChildCount = fourLevelNode.getChildCount();
			        	if(fiveLevelChildCount>0)
			        	{
			        	%>
			        		 <TABLE WIDTH="100%" BORDER="2" BGCOLOR="silver"  CELLSPACING="0" CELLPADDING="0" BORDERCOLOR=white>
			        	<%
			        		javax.swing.tree.DefaultMutableTreeNode fiveLevelNode = (javax.swing.tree.DefaultMutableTreeNode)fourLevelNode.getFirstChild();
			        		while(fiveLevelNode!=null){

			        		mm = (com.csmc.pms.webapp.security.model.MenuModel)fiveLevelNode.getUserObject();
			            %>
							 <TR BGCOLOR="<%=bgColor[1-i]%>">
							          <TD WIDTH="26%" HEIGHT="25" CLASS="en11pxb">
          		<%if(mm.getMenuType().equalsIgnoreCase("FUNCTION")){
          			if(userPriv.contains(mm.getMenuDesc())&&groupPriv.contains(mm.getMenuDesc())){
          		%>
          			<INPUT NAME="chk_<%=mm.getMenuDesc()%>" TYPE="hidden" VALUE="on">
          		 	<INPUT NAME="chk_x" checked disabled TYPE="checkbox" ID="chk0" VALUE="true" >
          		<%}else{%>
              		<INPUT NAME="chk_<%=mm.getMenuDesc()%>"  onclick="javascript:checkTheSame(this,'chk_<%=mm.getMenuDesc()%>');"  <%if(userPriv.contains(mm.getMenuDesc())) {out.print("checked");}%>  TYPE="checkbox" ID="chk0" VALUE="true" >
				<%
					}
				}
				%>
				<%=mm.getMenuDesc()%>
							   </TD>
							   </TR>

						 <%
						 fiveLevelNode = fiveLevelNode.getNextSibling();
						 	}
						 %>
						 </TABLE>
						<%

						}
						%>
			 		</TD>

							   </TR>

						 <%
						 fourLevelNode = fourLevelNode.getNextSibling();
						 	}
						 %>
						 </TABLE>
						<%

						}
						%>
			 		</TD>
				   </TR>

			 <%
			 threeLevelNode = threeLevelNode.getNextSibling();
			 	}
			 %>
			 </TABLE>
			<%

			}
			%>
			 		</TD>
			 </TR>
			 <%
			 twoLevelNode = twoLevelNode.getNextSibling();
			 	}
			 %>
			 </TABLE>
			<%

			}
			%>
			</TD>
         </TR>
		<%
		rootNode = rootNode.getNextSibling();
		}
		%>
      </TABLE>
      </FIELDSET>
		<TABLE WIDTH="100%" HEIGHT="35"  BORDER="0" CELLPADDING="0" CELLSPACING="0">
		  <TR>
		    <TD WIDTH="1%"><IMG NAME="" SRC="" WIDTH="20" HEIGHT="1" alt=""></TD>
		    <TD WIDTH="10%">
		    	<TABLE HEIGHT="35" BORDER="0" CELLPADDING="0" CELLSPACING="0">
			        <TR>
			          <TD> <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0">
			              <TR>
			                <TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_left.gif"></TD>
			                <TD NOWRAP BACKGROUND="<%=request.getContextPath()%>/images/button_bg.gif" ><A HREF="javascript:save();" CLASS="button-text">确认/保存</A></TD>
			                <TD  NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_right.gif"></TD>
			              </TR>
			            </TABLE></TD>
			          <TD>&nbsp;</TD>
			        </TR>
		     	 </TABLE>
		     </TD>
		     <TD WIDTH="10%">
		     	 <TABLE HEIGHT="35" BORDER="0" CELLPADDING="0" CELLSPACING="0">
			        <TR>
			          <TD> <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0">
			              <TR>
			                <TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_left.gif"></TD>
			                <TD NOWRAP BACKGROUND="<%=request.getContextPath()%>/images/button_bg.gif" ><A HREF="javascript:reset();" CLASS="button-text">重置</A></TD>
			                <TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_right.gif"></TD>
			              </TR>
			            </TABLE></TD>
			          <TD>&nbsp;</TD>
			        </TR>
		     	 </TABLE>
		     </TD>
		     <TD WIDTH="79%">&nbsp;</TD>
		  </TR>
		</TABLE>
<%
    }
} catch (Exception e) {
    out.println(e);
}
%>
</FORM>

<SCRIPT LANGUAGE="javascript">
    function checkTheSame(obj,name){
    	var i = 0;
    	while(i<document.setupGroupPrivForm.elements[name].length){
    		document.setupGroupPrivForm.elements[name][i].checked = obj.checked;
    		i = i+1;
    	};
    }
	function grouplist_onchange(){
		document.forms['setupGroupPrivForm'].action="<%=request.getContextPath()%>/control/setupUserGroup";
		document.forms['setupGroupPrivForm'].submit();
	}
	function save(){
		document.forms['setupGroupPrivForm'].action="<%=request.getContextPath()%>/control/updateUserGroupInfo";
		document.forms['setupGroupPrivForm'].submit();
	}
	function reset(){
		document.forms['setupGroupPrivForm'].reset();
	}
</SCRIPT>
