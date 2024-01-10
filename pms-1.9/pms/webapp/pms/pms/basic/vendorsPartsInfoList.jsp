<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<% 
	List partsList=(List)request.getAttribute("partsList");
%>
<base target="_self">
<!-- yui page script-->
<script language="javascript">    

	function partsQuery(){
		var partsId=document.getElementById("partNo").value;
		if(partsId==null||partsId==""){
			alert("partsId不能为空");
			return;
		}
		partsDataQueryForm.submit();
	}
	
	function partsCheck(partsId){		
		window.opener.document.location.href="<%=request.getContextPath()%>/control/partsVendorsList?partsId="+partsId;
		window.close();
	}
	
</script>
<form action="<%=request.getContextPath()%>/control/queryVendorsPartsList" method="post" id="partsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>物料信息查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
         <tr bgcolor="#DFE1EC"> 
           <td width="5%" class="en11pxb">物料号:</td>
           <td width="92%" class="en11pxb"><input class="input" type="text" name="partNo" id="partNo" value="" />
           </td>
         </tr>
          <tr bgcolor="#DFE1EC"> 
            <td width="20%" class="en11pxb" align="left" colspan="2">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr height="30">
                    <td width="20">&nbsp;</td>
                    <td><ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:partsQuery();"><span>&nbsp;查询&nbsp;</span></a></li> 
                    </ul>
                    <ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:window.close();"><span>&nbsp;关闭&nbsp;</span></a></li> 
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

<ofbiz:if name="flag" value="OK">
<div id="vendorsPartsList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="vendorsPartsList" style="visibility:'hidden';">
</ofbiz:unless>

<form action="" method="post" id="">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>物料信息列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb"></td>        
          <td width="8%" class="en11pxb">物料号</td>
          <td width="87%" class="en11pxb">物料名</td>
        </tr>
      <% 
        if(partsList != null && partsList.size() > 0) {  
            int k = 0;
            for(Iterator it = partsList.iterator();it.hasNext();) { 
                Map map = (Map)it.next();
               
        %>
           <tr bgcolor="#DFE1EC"> 
             <td class="en11px"><a href="#" onclick="partsCheck('<%=map.get("PART_NO")%>')">选择</a></td>          
             <td class="en11px"><%=map.get("PART_NO")%></td>
             <td class="en11px"><%=map.get("PART_NAME")%></td>
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
</div>
<script language="javascript">
    //查询后的页面的初始化
    Ext.get('partNo').dom.value='<%=UtilFormatOut.checkNull(request.getParameter("partNo"))%>'
</script>