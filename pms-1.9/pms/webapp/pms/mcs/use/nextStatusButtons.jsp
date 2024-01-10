<!-- 物料换下，状态选择按钮 -->
<%if (mtrGrp==null || mtrGrp.equals("")) {%>

    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30">
        <td>
            <font color="#FF0000" face="黑体" size="-1">
                选择物料组，查询显示换下操作类型
            </font>
        </td>
      </tr>
    </table>

<%} else if (ConstantsMcs.CHEMICAL.equals(mtrGrp) || ConstantsMcs.GAS.equals(mtrGrp) || ConstantsMcs.DEVELOPER.equals(mtrGrp) || ConstantsMcs.PHOTORESIST.equals(mtrGrp)) {%>

    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30">
        <td class="en11pxb">
            换下备注
            <input type="text" class="input" size="20" name="offNote" value="">
        </td>

        <td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.FINISH%>', '<i18n:message key="mcs.status_finish" />');">
    		    <span class="finish">&nbsp;<i18n:message key="mcs.status_finish" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
    		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />&nbsp;</span>
    		</a></li>
    	</ul></td>
      </tr>
    </table>

<%} else if (ConstantsMcs.TARGET.equals(mtrGrp)) {%>

    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30">
        <td class="en11pxb">
            换下备注
            <input type="text" class="input" size="20" name="offNote" value="">
        </td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
    		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.ONLINE_SCRAP_OPT%>', '<i18n:message key="mcs.status_general_scrap" />');">
    		    <span class="onlineScrap">&nbsp;<i18n:message key="mcs.status_online_scrap" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_OPT%>', '<i18n:message key="mcs.status_vendor_repair" />');">
    		    <span class="vendor">&nbsp;回收&nbsp;</span>
    		</a></li>
    	</ul></td>
      </tr>
    </table>

<%} else if (ConstantsMcs.QUARTZ.equals(mtrGrp) || ConstantsMcs.SPAREPART_2P.equals(mtrGrp) || ConstantsMcs.SPAREPART_2S.equals(mtrGrp)) {%>

    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30">
        <td class="en11pxb">
            换下备注
            <input type="text" class="input" size="20" name="offNote" value="">
        </td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
    		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.SCRAP%>', '<i18n:message key="mcs.status_scrap" />');">
    		    <span class="scrap">&nbsp;<i18n:message key="mcs.status_scrap" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.FAB_REPAIR%>', '<i18n:message key="mcs.status_fab_repair" />');">
    		    <span class="fabrepair">&nbsp;<i18n:message key="mcs.status_fab_repair" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_OPT%>', '<i18n:message key="mcs.status_vendor_repair" />');">
    		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_repair" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.FAB_WASH%>', '<i18n:message key="mcs.status_fab_wash" />');">
    		    <span class="fabrepair">&nbsp;<i18n:message key="mcs.status_fab_wash" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_WASH_OPT%>', '<i18n:message key="mcs.status_vendor_wash" />');">
    		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_wash" />&nbsp;</span>
    		</a></li>
    	</ul></td>
      </tr>
    </table>

<%}%>