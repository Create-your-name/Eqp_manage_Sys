<!-- ���ϻ���ʹ�ð�ť -->
<ofbiz:if name="mtrGrp">
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td class="en11pxb">
        ���ϱ�ע
        <input type="text" class="input" size="20" name="useNote" value="">
    </td>

    <td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.USING%>', '<i18n:message key="mcs.status_using" />');">
		    <span>&nbsp;<i18n:message key="mcs.status_using" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>
</ofbiz:if>

<br>
<p>
	<font color="#FF0000" face="����" size="-1">
		Tips :

		<br>
		&nbsp;1. ʹ���ݴ�������ǰ��������ά�������豸��
		<a href="<ofbiz:url>/queryMaterialEntry?usingObjectId=<%=eqpId%></ofbiz:url>">
		    ��ѯ������Ϣ�趨
		</a>

		<br>
		&nbsp;2. �ݴ��������б����г���Ч���ڵ����ϡ�
		<a href="<ofbiz:url>/queryOverExpirationEntry</ofbiz:url>">
		    ��ѯ����Ч�ڲ���
		</a>
		����ϵIQC�޸���Ч�ڡ�
	</font>
</p>