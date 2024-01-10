<%@page contentType='text/html; charset=gb2312'%>

<FORM ID="dataSynForm" NAME="dataSynForm" METHOD=POST ACTION="<%=request.getContextPath()%>/control/dataSyn">
	<DIV ID="DIV1" style="VISIBILITY: hidden"></DIV>
	<FIELDSET><LEGEND>WIP 表数据与MES同步</LEGEND>
	  	<TABLE WIDTH="100%" HEIGHT="35" BORDER="0" CELLPADDING="0" CELLSPACING="0">
	  		<TR>
	    		<TD WIDTH="1%"><IMG NAME="" SRC="" WIDTH="20" HEIGHT="1" ALT=""></TD>
	    		<TD WIDTH="99%">
	    			<TABLE HEIGHT="35" BORDER="0" CELLPADDING="0" CELLSPACING="0">
	        			<TR> 
	          				<TD> 
	          					<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0">
	              					<TR> 
	                					<TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_left.gif"></TD>
	                					<TD NOWRAP BACKGROUND="<%=request.getContextPath()%>/images/button_bg.gif" >
	                						<A ID="A1" HREF="javascript:SynWIP()" CLASS="button-text">确定</A>
	                					</TD>
	                					<TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_right.gif"></TD>
	              					</TR>
	            				</TABLE>
	            			</TD>
	          				<TD>&nbsp;</TD>
	        			</TR>
	      			</TABLE>
	      		</TD>
	  		</TR>
		</TABLE>
	</FIELDSET> 
</FORM>

<SCRIPT LANGUAGE="JavaScript" type="text/JavaScript">
	function SynWIP(){
		DIV1.style.visibility = "visible" ;
		DIV1.innerHTML="<FONT COLOR=red SIZE=4><B>程序正在运行中,请稍后.............</></FONT><BR><BR>"
		A1.disabled = true ;
		document.forms['dataSynForm'].action="<%=request.getContextPath()%>/control/synWip"  ;
		document.forms['dataSynForm'].submit() ;
	}
</SCRIPT>




