<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>

   <table border="0" cellpadding="0" bgcolor="<%=colorMap.get("background")%>"  bordercolor="<%=colorMap.get("borderColor")%>">

    <tr><th colspan=7><%=title%></th></tr>
    <tr  bgcolor="<%=colorMap.get("titlebgColor")%>">
    	<td align="center" width="<%=colorMap.get("cellwidth")%>" ><font color="<%=colorMap.get("titleColor")%>"><%=daystitle[1]%></font></td>
    	<td align="center" width="<%=colorMap.get("cellwidth")%>" ><font color="<%=colorMap.get("titleColor")%>"><%=daystitle[2]%></font></td>
    	<td align="center" width="<%=colorMap.get("cellwidth")%>" ><font color="<%=colorMap.get("titleColor")%>"><%=daystitle[3]%></font></td>
    	<td align="center" width="<%=colorMap.get("cellwidth")%>" ><font color="<%=colorMap.get("titleColor")%>"><%=daystitle[4]%></font></td>
    	<td align="center" width="<%=colorMap.get("cellwidth")%>" ><font color="<%=colorMap.get("titleColor")%>"><%=daystitle[5]%></font></td>
    	<td align="center" width="<%=colorMap.get("cellwidth")%>" ><font color="<%=colorMap.get("titleColor")%>"><%=daystitle[6]%></font></td>
    	<td align="center" width="<%=colorMap.get("cellwidth")%>" ><font color="<%=colorMap.get("titleColor")%>"><%=daystitle[7]%></font></td>
    </tr>
    <tr>
    <%
    column = 0;
    row = 0;
    //out.println("startDay---->"+startDay);    
    for(j=1; j<startDay; j++) 
    {
    %>
    	<td><br></td>
    <%
         column++;
    }
    for(j=1; j<=ndays; j++) 
    {
    	String tdcolor = (String)timeMap.get(String.valueOf(firstDay.get(Calendar.YEAR))+String.valueOf(month)+String.valueOf(j));
		String creator = (String)creatorMap.get(String.valueOf(firstDay.get(Calendar.YEAR))+String.valueOf(month)+String.valueOf(j));    	
    	String periodStr = (String)periodMap.get(String.valueOf(firstDay.get(Calendar.YEAR))+String.valueOf(month)+String.valueOf(j));    	
    	boolean isFinished = ((Boolean)isFinishedMap.get(String.valueOf(firstDay.get(Calendar.YEAR))+String.valueOf(month)+String.valueOf(j))).booleanValue();      	
	%>
        <td align="center" bgcolor="<%=tdcolor%>" class="k">
       		<font color="<%=colorMap.get("fontColor")%>" onmouseover="color='#0000FF'" onmouseout=<%="color='"+ colorMap.get("fontColor")+"'"%> 
       				onclick="daytitle_onclick(this,'<%=firstDay.get(Calendar.YEAR)%>','<%=(month+1)%>','<%=j%>','<%=creator%>','<%=description%>','<%=periodStr%>','<%=isFinished%>')">
		        <B><%=j%></B>
        	</font>
		</td>

	<%
   if(j>=ndays) 
   {   		
        while(column<6) 
        {
    %>        
        <td><br></td>
    <%
            column++;
        }
    //if结束
    }          
        column++;
   		if (column == 7) 
 		//out.println("column->"+column);
   		{
   %>
        </tr>
   <%
        column = 0;
        row++;
        //内层if结束
        }
  
    //for结束
    }
    if (row!=6) {
    %>
    <tr>
    <%
    	while(column<7) {
    %>
    	<td><br></td>
    <%
            column++;
    	}
    %>
    </tr>	
    <%    
    }
    %>
</table> 