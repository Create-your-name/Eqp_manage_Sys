<!-- PopUp Calendar BEGIN -->
<script language="JavaScript" src="<%=request.getContextPath()%>/images/pupdate.js"></script>
<script language="JavaScript">
if (document.all) {
 document.writeln("<div id=\"PopUpCalendar\" style=\"position:absolute; left:0px; top:0px; z-index:7; width:200px; height:77px; overflow: visible; visibility: hidden; background-color: #FFFFFF; border: 1px none #000000\" onMouseOver=\"if(ppcTI){clearTimeout(ppcTI);ppcTI=false;}\" onMouseOut=\"ppcTI=setTimeout(\'hideCalendar()\',500)\">");
 document.writeln("<div id=\"monthSelector\" style=\"position:absolute; left:0px; top:0px; z-index:9; width:181px; height:27px; overflow: visible; visibility:inherit\">");}
else if (document.layers) {
 document.writeln("<layer id=\"PopUpCalendar\" pagex=\"0\" pagey=\"0\" width=\"200\" height=\"200\" z-index=\"100\" visibility=\"hide\" bgcolor=\"#FFFFFF\" onMouseOver=\"if(ppcTI){clearTimeout(ppcTI);ppcTI=false;}\" onMouseOut=\"ppcTI=setTimeout('hideCalendar()',500)\">");
 document.writeln("<layer id=\"monthSelector\" left=\"0\" top=\"0\" width=\"181\" height=\"27\" z-index=\"9\" visibility=\"inherit\">");}
else {
 document.writeln("<p><font color=\"#FF0000\"><b>Error ! The current browser is either too old or too modern (usind DOM document structure).</b></font></p>");}
</script>
<noscript><p><font color="#FF0000"><b>JavaScript is not activated !</b></font></p></noscript>
<table border="1" cellspacing="1" cellpadding="2" width="200" bordercolorlight="#000000" bordercolordark="#000000" vspace="0" hspace="0"><form name="ppcMonthList"><tr><td align="center" bgcolor="#CCCCCC"><a href="javascript:moveMonth('Back')" onMouseOver="window.status=' ';return true;"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><b>&lt;&nbsp;</b></font></a><font face="MS Sans Serif, sans-serif" size="1"> 
<select name="sItem" onMouseOut="if(ppcIE){window.event.cancelBubble = true;}" onChange="switchMonth(this.options[this.selectedIndex].value)" style="font-family: 'MS Sans Serif', sans-serif; font-size: 9pt">
<option value="0">2001 &#149; January</option>
<option value="1">2001 &#149; February</option>
<option value="2">2001 &#149; March</option>
<option value="3">2001 &#149; April</option>
<option value="4">2001 &#149; May</option>
<option value="5">2001 &#149; June</option>
<option value="6">2001 &#149; July</option>
<option value="7">2001 &#149; August</option>
<option value="8">2001 &#149; September</option>
<option value="9">2001 &#149; October</option>
<option value="10">2001 &#149; November</option>
<option value="11">2001 &#149; December</option>
<option value="12">2002 &#149; January</option>
<option value="13">2002 &#149; February</option>
<option value="14">2002 &#149; March</option>
<option value="15">2002 &#149; April</option>
<option value="16">2002 &#149; May</option>
<option value="17">2002 &#149; June</option>
<option value="18">2002 &#149; July</option>
<option value="19">2002 &#149; August</option>
<option value="20">2002 &#149; September</option>
<option value="21">2002 &#149; October</option>
<option value="22">2002 &#149; November</option>
<option value="23">2002 &#149; December</option>
<option value="24">2003 &#149; January</option>
<option value="25">2003 &#149; January</option>
<option value="26">2003 &#149; February</option>  
<option value="27">2003 &#149; March</option>
<option value="28">2003 &#149; April</option>
<option value="29">2003 &#149; May</option>
<option value="30">2003 &#149; June</option>
<option value="31">2003 &#149; July</option>
<option value="32">2003 &#149; August</option>
<option value="33">2003 &#149; September</option>
<option value="34">2003 &#149; October</option>
<option value="35">2003 &#149; November</option>
<option value="36">2003 &#149; December</option>
<option value="37">2004 &#149; January</option>
<option value="38">2004 &#149; February</option>
<option value="39">2004 &#149; March</option>
<option value="40">2004 &#149; April</option>
<option value="41">2004 &#149; May</option>
<option value="42">2004 &#149; June</option>
<option value="43">2004 &#149; July</option>
<option value="44">2004 &#149; August</option>
<option value="45">2004 &#149; September</option>
<option value="46">2004 &#149; October</option>
<option value="47">2004 &#149; November</option>
<option value="48">2004 &#149; December</option>
<option value="49">2005 &#149; January</option>
<option value="50">2005 &#149; February</option>
<option value="51">2005 &#149; March</option>
<option value="52">2005 &#149; April</option>
<option value="53">2005 &#149; May</option>
<option value="54">2005 &#149; June</option>
<option value="55">2005 &#149; July</option>
<option value="56">2005 &#149; August</option>
<option value="57">2005 &#149; September</option>
<option value="58">2005 &#149; October</option>
<option value="59">2005 &#149; November</option>
<option value="60">2005 &#149; December</option>
   
</select></font><a href="javascript:moveMonth('Forward')" onMouseOver="window.status=' ';return true;"><font face="Arial, Helvetica, sans-serif" size="2" color="#000000"><b>&nbsp;&gt;</b></font></a></td></tr></form></table>
<table border="1" cellspacing="1" cellpadding="2" bordercolorlight="#000000" bordercolordark="#000000" width="200" vspace="0" hspace="0"><tr align="center" bgcolor="#CCCCCC"><td width="20" bgcolor="#FFFFCC"><b><font face="MS Sans Serif, sans-serif" size="1">Su</font></b></td><td width="20"><b><font face="MS Sans Serif, sans-serif" size="1">Mo</font></b></td><td width="20"><b><font face="MS Sans Serif, sans-serif" size="1">Tu</font></b></td><td width="20"><b><font face="MS Sans Serif, sans-serif" size="1">We</font></b></td><td width="20"><b><font face="MS Sans Serif, sans-serif" size="1">Th</font></b></td><td width="20"><b><font face="MS Sans Serif, sans-serif" size="1">Fr</font></b></td><td width="20" bgcolor="#FFFFCC"><b><font face="MS Sans Serif, sans-serif" size="1">Sa</font></b></td></tr></table>
<script language="JavaScript">
if (document.all) {
 document.writeln("</div>");
 document.writeln("<div id=\"monthDays\" style=\"position:absolute; left:0px; top:52px; z-index:8; width:200px; height:17px; overflow: visible; visibility:inherit; background-color: #FFFFFF; border: 1px none #000000\">&nbsp;</div></div>");}
else if (document.layers) {
 document.writeln("</layer>");
 document.writeln("<layer id=\"monthDays\" left=\"0\" top=\"52\" width=\"200\" height=\"17\" z-index=\"8\" bgcolor=\"#FFFFFF\" visibility=\"inherit\">&nbsp;</layer></layer>");}
else {/*NOP*/}
</script>