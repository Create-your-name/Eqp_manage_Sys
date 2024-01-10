<%@page contentType='text/html; charset=gb2312'%>
<html>
<head>
<title>menu</title>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<link href="<%=request.getContextPath()%>/images/default.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#ffffff" leftmargin="5" topmargin="5">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr> 
    <td height="1%" width="1%"><img src="<%=request.getContextPath()%>/images/border_top0.gif" width="5" height="18"></td>
    <td height="1%"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td height="1%" width="1%"><img src="<%=request.getContextPath()%>/images/border_top1.gif" width="122" height="18"></td>
          <td background="<%=request.getContextPath()%>/images/border_top_bg.gif" width="99%">&nbsp;</td>
        </tr>
      </table></td>
    <td height="1%" width="1%"><img src="<%=request.getContextPath()%>/images/border_top2.gif" width="5" height="18"></td>
  </tr>
  <tr> 
    <td width="1%" background="<%=request.getContextPath()%>/images/border_r2_c1.gif" class="border-left" height="99%"><img name="" src="" width="5" height="1" alt=""></td>
    <td width="98%" valign="top" class="menu-text">
		<iframe width="100%" height="100%" src="<%=request.getContextPath()%>/includes/tree.jsp" frameborder="0"></iframe>
	</td>
    <td width="1%" class="border-right"><img name="" src="" width="5" height="1" alt=""></td>
  </tr>
  <tr> 
    <td height="1%" valign="top"><img src="<%=request.getContextPath()%>/images/border_r3_c1.gif" width="5" height="5"></td>
    <td valign="top" background="<%=request.getContextPath()%>/images/border_r3_c2.gif"><img src="<%=request.getContextPath()%>/images/border_r3_c2.gif" width="198" height="5"></td>
    <td valign="top" height="2" align="right"><img src="<%=request.getContextPath()%>/images/border_r3_c3.gif" width="5" height="5"></td>
  </tr>
</table>
</body>
</html>
