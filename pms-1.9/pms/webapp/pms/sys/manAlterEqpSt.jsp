<%@page contentType='text/html; charset=gb2312'%>

<%
String stateValue = (String)request.getAttribute("stateValue");
%>

<form name="eqpSt" id="eqpSt" method="post" action="<%=request.getContextPath()%>/control/manAlterEqpSt">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td colspan="2"></td>
  </tr>
  <tr>
     <td valign="top"><fieldset><legend>�޸�</legend>
        <table width="100%" border="0" cellspacing="1" cellpadding="0">
      	  <tr bgcolor="#DFE1EC">
        	  <td width="18%" class="en11pxb">����״̬</td>
        	  <td width="82%" class="en11pxb">
        	    <select name="stateValue">
        			<option value="Y">���ܸ��豸״̬</option>
        			<option value="N" <%if("N".equalsIgnoreCase(stateValue)) {%>selected<%}%>>���Ը��豸״̬</option>
        		</select>
			  </td>
      	  </tr>
      </table>
		</fieldset>
	</td>
  </tr>
</table>

<table width="100%" height="35"  border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="1%"></td>
    <td width="99%"><table height="35" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:eqpSt.submit();" class="button-text">ȷ��</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
          <td>��</td>
		  <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="<%=request.getContextPath()%>/control/manAlterEqpStEntry" class="button-text">ȡ��</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
          <td>��</td>
		</tr>
      </table></td>
  </tr>
</table>
</form>
