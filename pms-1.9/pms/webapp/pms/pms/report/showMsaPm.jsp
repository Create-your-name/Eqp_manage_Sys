<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%
	List dataList = (List) request.getAttribute("dataList");
%>

<TABLE WIDTH="100%" BORDER="0" CELLPADDING="0" CELLSPACING="0">
  <TR>

    <TD BGCOLOR="#CCCCD4" CLASS="title-en" nowrap>&nbsp;
		部门: <%=request.getParameter("equipmentDept")%>; 设备大类: <%=request.getParameter("equipmentType")%>; PM周期: <%=request.getAttribute("periodName")%>
	</TD>
  </TR>
</TABLE>

</br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>机台保养纪录参数分析</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">

      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb" align='middle'>EqpId</td>
      		<td class="en11pxb" align='middle'>设备大类</td>
      		<td class="en11pxb" align='middle'>PM项目</td>
      		<td class="en11pxb" align='middle'>Calibration Frequency</td>
            <td class="en11pxb" align='middle'>Process Character</td>
            <td class="en11pxb" align='middle'>Value</td>
            <td class="en11pxb" align='middle'>备注</td>
      		<td class="en11pxb" align='middle'>Spec</td>
      		<td class="en11pxb" align='middle'>保养时间</td>
    	</tr>
        <%
    	if(dataList != null && dataList.size() > 0) {

    		for(Iterator it = dataList.iterator();it.hasNext();) {
    		    String spec = "";
				Map map = (Map) it.next();
				String itemType = (String) map.get("ITEM_TYPE");
				String itemValue = (String) map.get("ITEM_VALUE");
				String itemNote = UtilFormatOut.checkNull((String) map.get("ITEM_NOTE"));
				//数字型项目
				if(itemType.equals("2")){
				    String default_value = (String) map.get("DEFAULT_VALUE");
				    String upper = (String) map.get("ITEM_UPPER_SPEC");
				    String lower = (String) map.get("ITEM_LOWER_SPEC");
				    String unit = (String) map.get("ITEM_UNIT");
				    if (upper == null) {
				        // for MSA NDC
				        if(lower != null && lower.equals(default_value)) {
				            spec = "≥"+lower;
				        } else {
				            spec = ">"+lower;
				        }
				    } 
				    if (lower == null ) {
				        spec = "<"+upper;
				    } 
				    if (upper != null && lower != null) {
				        double upper_d = Double.parseDouble(upper);
				        double lower_d = Double.parseDouble(lower);
				        double difference = upper_d - lower_d;
				        spec = "<"+difference;
				    }
				    if (unit != null) {
				        spec += unit;
				        itemValue += unit;
				    }
				}
        %>
        <!--todo-->	
        <tr bgcolor="#DFE1EC">
            <td class="en11px" align='middle'><%=UtilFormatOut.checkNull((String) map.get("EQUIPMENT_ID"))%></td>
            <td class="en11px" align='middle'><%=request.getParameter("equipmentType")%></td>
            <td class="en11px" align='middle'><%=UtilFormatOut.checkNull((String) map.get("ITEM_NAME"))%></td>
            <td class="en11px" align='middle'><%=request.getAttribute("defaultDays")%> days</td>
            <td class="en11px" align='middle'><%=UtilFormatOut.checkNull((String) map.get("ACTION_DESCRIPTION"))%></td>
            <td class="en11px" align='middle'><%=itemValue%></td>
            <td class="en11px" align='middle'><%=itemNote%></td>
            <td class="en11px" align='middle'><%=spec%></td>
            <td class="en11px" align='middle'><%=UtilFormatOut.checkNull((String) map.get("UPDATE_TIME"))%></td>
        </tr>
         <%
                    }
             }
		 %>
		 
      </table>
      </fieldset></td>
  </tr>
</table>


