<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>

<%
    List partList = (List)request.getAttribute("PM_PARTS_LIST");
    String eqp = UtilFormatOut.checkNull((String)request.getAttribute("equipmentType"));
    String pm = UtilFormatOut.checkNull((String)request.getAttribute("period"));
    String flow = UtilFormatOut.checkNull((String)request.getAttribute("flow"));
    String isRecord="false";
    if(partList==null||partList.size()==0){
        isRecord="true";
    }
    int i=0;
 %>

<!-- yui page script-->
<script language="javascript">
    var oldEqpValue="";
    var oldEqpCopyValue="";
    var flag='false';
    var isCopy=false;
    <ofbiz:if name="flag" value="ok">
        flag='true';
    </ofbiz:if>

    //���ݺϷ���У��
    function checkForm(){
        var partsNo = Ext.get('partsNo').dom.value;
        var partName = Ext.get('partName').dom.value;
        var partCount = Ext.get('partCount').dom.value;
        var templateCount = Ext.get('templateCount').dom.value;
        if(partsNo==""){
            return "���ϺŲ���Ϊ��";
        }
        if(partName==""){
            return "����������Ϊ��";
        }
        if(templateCount==""){
            return "�ο���������Ϊ��";
        }
        if(partCount==""){
            return "Ĭ����������Ϊ��";
        }
       // var reg = /^[0-9]*[1-9][0-9]*$/;//��֤������       
		var reg=/^-?\d+$/;
        if(reg.test(templateCount)==false){
             return "�ο�����ֻ����������!";
        }
        if(reg.test(partCount)==false){
            return "Ĭ������ֻ����������!";
        }
        return "";
    }

    //�ı䵯�����Сwidth,height
    extDlg.dlgInit('450','450');

    //��ȡpartNo
    function getPartsNo(obj){
        var par=new Array();
        //�豸ID��formTypeֵΪPMʱ��Ҫ
        //PM����,formTypeֵΪPMʱ��Ҫ
        var result=window.open ("<%=request.getContextPath()%>/control/intoPartsQueryEntry?periodIndex=<%=pm%>&flowIndex=<%=flow%>&eqpType=<%=eqp%>",
                "parts","top=130,left=240,width=700,height=450,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=0");
    }

    //�޸�����
    function editParts(index,obj){
        Ext.get('partPmIndex').dom.value=index;
        Ext.get('periodIndex').dom.value='<%=pm%>';
        Ext.get('equipType').dom.value='<%=eqp%>';
        Ext.get('flowIndex').dom.value='<%=flow%>';
        var url='<ofbiz:url>/queryPmPartsInfo</ofbiz:url>?partPmIndex='+index;
        extDlg.showEditDialog(obj,url);
    }

    //�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
    function commentSuccess(o){
         var result = eval('(' + o.responseText + ')');
         if (result!=null & result!=""){
            Ext.get('partsNo').dom.value=result.partsNo;
            Ext.get('partName').dom.value=result.partName;
            Ext.get('partCount').dom.value=result.partCount;
            Ext.get('templateCount').dom.value=result.templateCount;
            Ext.get('mtrGrp').dom.value=result.mtrGrp;
            Ext.get('partType').dom.value=result.partType;
            Ext.get('remark').dom.value=result.remark;
            if(Ext.get('templateCount').dom.value=='undefined'){
                Ext.get('templateCount').dom.value='';
            }
            if(Ext.get('remark').dom.value=='undefined'){
                Ext.get('remark').dom.value='';
            }
        }
    }

    //ɾ������
    function delPartsByIndex(index){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
            if(value=="yes"){
                var url='<ofbiz:url>/delPmPartsByPmPartsIndex</ofbiz:url>?partPmIndex='+index+'&equipmentType=<%=eqp%>';
                document.location=url;
            }else{
                return;
            }
        });
    }

    //��ѯpmParts
    function pmPartsQuery(){
        var period=Ext.get('period').dom.value;
        if(period==""){
            Ext.MessageBox.alert('����', '��ѡ��������!');
            return;
        }
        var flow=Ext.get('flow').dom.value;
        if(flow==""){
            Ext.MessageBox.alert('����', '��ѡ��������!');
            return;
        }
        pmPartsQueryForm.submit();
    }

    //����������ʾ״̬�ı�
    function copyDisplayChange(){
        Ext.get('copy').dom.style.display ='none';
    }

    //���ư�ť
    function copyParts(){
        if('<%=isRecord%>'=='true'){
            Ext.MessageBox.alert('����', 'û�пɸ��Ƶļ�¼,��ִ�в�ѯ����!');
            return;
        }
        if(Ext.get('copy').dom.style.display=='none'){
            Ext.get('copy').dom.style.display='';
            Ext.get('copyButton').dom.innerText="ȡ������";
        }else if(Ext.get('copy').dom.style.display==''){
            Ext.get('copy').dom.style.display='none'
            Ext.get('copyButton').dom.innerText="����"
        }
    }

    //��ʼ����
    function pmPartsCopy(){
        var eqpCopy=Ext.get('equipmentTypeCopy').dom.value;
        var periodCopy=Ext.get('periodCopy').dom.value;
        var flowCopy=Ext.get('flowCopy').dom.value;
        if(eqpCopy==""||periodCopy==""||flowCopy==""){
            Ext.MessageBox.alert('����', '��ѡ����Ҫ���Ƶ�������');
            return;
        }
        pmPartsCopyForm.submit();
    }

    //���ƣ��豸��������ѡ�񴥷�ʱ
    function periodTypeCopyChange(){
        periodIndex=Ext.get('periodCopy').dom.value;
        var actionURL='<ofbiz:url>/queryJsonFlowList</ofbiz:url>?periodIndex='+periodIndex;
        Ext.lib.Ajax.formRequest('pmPartsQueryForm',actionURL,{success: selectPeriodCopySuccess, failure: commentFailure});
    }

    function periodTypeChange(){
        periodIndex=Ext.get('period').dom.value;
        var actionURL='<ofbiz:url>/queryJsonFlowList</ofbiz:url>?periodIndex='+periodIndex;
        Ext.lib.Ajax.formRequest('pmPartsQueryForm',actionURL,{success: selectPeriodSuccess, failure: commentFailure});
    }

    function selectPeriodSuccess(o){
        var result = eval('(' + o.responseText + ')');
        if (result!=null & result!=""){
            var nameSize=result.flowNameArray.length;
            var nameArray=result.flowNameArray;
            var valueArray=result.flowValueArray;
            var pmObj;
            flowObj=document.getElementById("flow");
            flowObj.length=1;
            for(var i=0;i<nameSize;i++){
                flowObj.options[flowObj.length]=new Option(nameArray[i],valueArray[i]);
            }
        }
        var flowObj=document.getElementById('flow');
        flowObj.value='<%=flow%>'
    }

    function selectPeriodCopySuccess(o){
        var result = eval('(' + o.responseText + ')');
        if (result!=null & result!=""){
            var nameSize=result.flowNameArray.length;
            var nameArray=result.flowNameArray;
            var valueArray=result.flowValueArray;
            var pmObj;
            flowObj=document.getElementById("flowCopy");
            flowObj.length=1;
            for(var i=0;i<nameSize;i++){
                flowObj.options[flowObj.length]=new Option(nameArray[i],valueArray[i]);
            }
        }
        var flowObj=document.getElementById('flowCopy');
        flowObj.value='<%=flow%>'
    }
    //�豸����ѡ�񴥷�ʱ
    function equipMentCopyChange(){
        equipMentChange("1");
    }

    //�豸����ѡ��
    //flag:����ҳ���е��豸����ѡ�񴥷�ʱ��ֵΪ1
    function equipMentChange(flag){
        var newEqpValue=""
        var isQuery=false;
        if(flag=='1'){
            newEqpValue=Ext.get('equipmentTypeCopy').dom.value;
            //�жϵ�ǰѡ�е�ֵ�Ƿ�Ϊ�ϴ�ѡ�е�ֵ
            if(oldEqpCopyValue!=newEqpValue){
                isQuery=true;
            }
            //�ж��Ƿ�Ϊcopy�����е�ѡ���
            isCopyEqp=true;
        }else{
            newEqpValue=Ext.get('equipmentType').dom.value;
            isCopyEqp=false;
            if(oldEqpValue!=newEqpValue){
                isQuery=true;
            }
        }
        var actionURL='<ofbiz:url>/queryEqpIdAndPmTypeList</ofbiz:url>?equipmentType='+newEqpValue;
        if(isQuery){
                Ext.lib.Ajax.formRequest('pmPartsQueryForm',actionURL,{success: selectSuccess, failure: commentFailure});
        }
        if(flag=='1'){
            oldEqpCopyValue=newEqpValue;
        }else{
            oldEqpValue=newEqpValue;
        }
    }

    //Զ�̵��óɹ�
    function selectSuccess(o){
         var result = eval('(' + o.responseText + ')');
         if (result!=null & result!=""){
            //�豸�����������ݳ�ʼ��
            var nameSize=result.priodNameArray.length;
            var nameArray=result.priodNameArray;
            var valueArray=result.priodValueArray;
            var pmObj;
            var equipId;
            if(isCopyEqp){
                pmObj=document.getElementById("periodCopy");
            }else{
                pmObj=document.getElementById("period");
            }
            pmObj.length=1;
            for(var i=0;i<nameSize;i++){
                pmObj.options[pmObj.length]=new Option(nameArray[i],valueArray[i]);
            }


            if(flag=='true'){
                initQuery();
                flag='false';
            }
        }
    }

   //��ʼ����ѯ��Ľ���
   function initQuery(){
        var pmObj=document.getElementById('period');
        pmObj.value='<%=pm%>'
        periodTypeChange();
   }

   //Զ�̵���ʧ��
   var commentFailure = function(o){
        Ext.MessageBox.alert('Status', 'Unable to connect.');
   };

    //��ʼ��ҳ��ؼ�
    Ext.onReady(function(){
        var eqpType = new Ext.form.ComboBox({
            typeAhead: true,
            triggerAction: 'all',
            transform:'equipmentType',
            width:170,
            forceSelection:true
        });
        eqpType.on('select',equipMentChange);

        var eqpTypeCopy = new Ext.form.ComboBox({
            typeAhead: true,
            triggerAction: 'all',
            transform:'equipmentTypeCopy',
            width:170,
            forceSelection:true
        });
        eqpTypeCopy.on('select',equipMentCopyChange);
        copyDisplayChange();
     });
</script>

<form action="<%=request.getContextPath()%>/control/queryPmPartsTemplateList" method="post" id="pmPartsQueryForm" name="pmPartsQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>��̨����Parts��ѯ</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
         <tr bgcolor="#DFE1EC">
           <td width="12%" class="en11pxb">�豸����:</td>
           <td width="28%"><select id="equipmentType" name="equipmentType">
              <option value=""></option>
              <ofbiz:if name="equipMentList">
                <ofbiz:iterator name="cust" property="equipMentList">
                <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentType"/></option>
              </ofbiz:iterator>
            </ofbiz:if>
            </select></td>
         <tr bgcolor="#DFE1EC">
            <td width="12%" class="en11pxb">��������:</td>
            <td width="28%" colspan="3">
              <select id="period" name="period" class="select-short" onchange="periodTypeChange()">
                <option value=""></option>
              </select>
            </td>
        </tr>
        <tr bgcolor="#DFE1EC">
            <td width="12%" class="en11pxb">��������:</td>
            <td width="28%" colspan="3"><select id="flow" name="flow" class="select-short">
            <option value=""></option>
            </select></td>
        </tr>
        <tr bgcolor="#DFE1EC">
            <td width="20%" class="en11pxb" align="left" colspan="4">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr height="30" >
                    <td width="0">&nbsp;</td>
                    <td><ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:pmPartsQuery();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
                    </ul><ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:copyParts();"><span id="copyButton">����</span></a></li>
                    </ul></td>
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

<form action="<%=request.getContextPath()%>/control/pmPartsCopy" method="post" id="pmPartsCopyForm" name="pmPartsCopyForm">
<div id="copy" >
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td><fieldset>
          <legend>��̨������Ϣ���Ʋ���ѡ��</legend>
          <table width="100%" border="0" cellspacing="1" cellpadding="2">
             <tr bgcolor="#DFE1EC">
               <td width="12%" class="en11pxb">�豸����:</td>
               <td width="28%"><select id="equipmentTypeCopy" name="equipmentTypeCopy">
                  <option value=""></option>
                  <ofbiz:if name="equipMentList">
                    <ofbiz:iterator name="cust" property="equipMentList">
                    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentType"/></option>
                  </ofbiz:iterator>
                </ofbiz:if>
                </select></td>
             <tr bgcolor="#DFE1EC">
                <td width="12%" class="en11pxb">�豸��������:</td>
                <td width="28%" colspan="3"><select id="periodCopy" name="periodCopy" class="select-short" onchange="periodTypeCopyChange()">
                <option value=""></option>
                </select></td>
            </tr>
            <tr bgcolor="#DFE1EC">
                <td width="12%" class="en11pxb">��������:</td>
                <td width="28%" colspan="3"><select id="flowCopy" name="flowCopy" class="select-short">
                <option value=""></option>
                </select></td>
            </tr>
            <tr bgcolor="#DFE1EC">
                <td width="20%" class="en11pxb" align="left" colspan="4">
                    <table border="0" cellspacing="0" cellpadding="0">
                      <tr height="30" >
                        <td width="0">&nbsp;</td>
                        <td><ul class="button">
                        <li><a class="button-text" href="#" onclick="javascript:pmPartsCopy();"><span>&nbsp;�ύ&nbsp;</span></a></li>
                        </ul></td>
                      </tr>
                    </table>
                </td>
            </tr>
          </table>
          </fieldset></td>
      </tr>
    </table>
    <br>
</div>
<!-- add dispalayList -->
<ofbiz:if name="flag" value="ok">
<div id="reaList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="reaList" style="visibility:'hidden';">
</ofbiz:unless>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td><fieldset>
          <legend>��̨����Parts�б�</legend>
          <table width="100%" border="0" cellspacing="1" cellpadding="2">
            <tr bgcolor="#ACD5C9">
              <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="getPartsNo(this);"/></td>
              <td width="8%" class="en11pxb">����ѡ��</td>
              <td width="5%" class="en11pxb">������</td>
              <td width="10%" class="en11pxb">���Ϻ�</td>
              <td width="32%" class="en11pxb">������</td>
              <td width="10%" class="en11pxb">�������</td>
              <td width="5%" class="en11pxb">�ο�����</td>
              <td width="5%" class="en11pxb">Ĭ������</td>
              <td width="30%" class="en11pxb">��ע</td>
            </tr>
           <% if(partList != null && partList.size() > 0) {
                for(Iterator it = partList.iterator();it.hasNext();) {
                        Map map = (Map)it.next();
                        i=i+1;
                        %>
                    <input type="hidden"  name="copyPartsNo_<%=i%>" id="copyPartsNo_<%=i%>"  value='<%=map.get("PART_NO")%>'>
                    <input type="hidden"  name="copyPartsName_<%=i%>" id="copyPartsName_<%=i%>"  value='<%=map.get("PART_NAME")%>'>
                    <input type="hidden"  name="copyPartsCount_<%=i%>" id="copyPartsCount_<%=i%>"  value='<%=map.get("PART_COUNT")%>'>
                    <input type="hidden"  name="copyTemplateCount_<%=i%>" id="copyTemplateCount_<%=i%>"  value='<%=map.get("TEMPLATE_COUNT")%>'>
                    <input type="hidden"  name="copyMtrGrp_<%=i%>" id="copyMtrGrp_<%=i%>"  value='<%=map.get("MTR_GRP")%>'>
                    <input type="hidden"  name="copyPartType_<%=i%>" id="copyPartType_<%=i%>"  value='<%=map.get("PART_TYPE")%>'>
                    <input type="hidden"  name="copyRemark_<%=i%>" id="copyRemark_<%=i%>"  value='<%=UtilFormatOut.checkNull((String) map.get("REMARK"))%>'>
                    <tr bgcolor="#DFE1EC">
                      <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delPartsByIndex('<%=map.get("PART_PM_INDEX")%>')"/></td>
                      <td class="en11px"><input type="checkbox" name="check_parts_<%=i%>" id="check_parts_<%=i%>"  checked ></td>
                      <td class="en11px"><%=map.get("MTR_GRP")%></td>
                      <td class="en11px"><a href="#" onclick="editParts('<%=map.get("PART_PM_INDEX")%>',this)"><%=map.get("PART_NO")%></a></td>
                      <td class="en11px"><%=map.get("PART_NAME")%></td>
                      <td class="en11px"><%=map.get("PART_TYPE")%></td>
                      <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("TEMPLATE_COUNT"))%></td>
                      <td class="en11px"><%=map.get("PART_COUNT")%></td>
                      <td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("REMARK"))%></td>
                    </tr>
          <%
            }
          } %>
          </table>
          </fieldset></td>
      </tr>
    </table>
</div>
<input type="hidden"  name="count" id="count"  value='<%=i%>'>
</form>
<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;" height=150>
    <div class="x-dlg-hd">��̨����Parts�趨</div>
    <div>
        <div id="post-tab" class="x-dlg-tab" title="��̨����Parts�趨">
            <div class="inner-tab" id="x-form" >
                <form action="<%=request.getContextPath()%>/control/pmPartsTemplateDefine" method="post" id="pmPartsForm" onsubmit="checkForm();">
                    <input id="partPmIndex" type="hidden" name="partPmIndex" value="" />
                    <input id="periodIndex" type="hidden" name="periodIndex" value="" />
                    <input id="flowIndex" type="hidden" name="flowIndex" value="" />
                    <input id="equipType" type="hidden" name="equipType" value="" />
                <p>
                <label for="name"><small>������</small></label>
                <input type="text" id="mtrGrp" name="mtrGrp" size="10" tabindex="1" readonly="readonly">
                </p>
                <p>
                <label for="partsNo"><small>���Ϻ�</small></label>
                <input type="text" id="partsNo" name="partsNo" size="20" tabindex="1" readonly="readonly">
                </p>
                 <p><label for="partName"><small>������</small></label>
                <input type="text" name="partName" id="partName" value="" size="50" tabindex="2" readonly="readonly"/>
                </p>
                <p><label for="templateCount"><small>�ο�����</small></label>
                <input type="text" name="templateCount" id="templateCount" value="" size="3" tabindex="3"/>
                </p>
                <p><label for="partCount"><small>Ĭ������</small></label>
                <input type="text" name="partCount" id="partCount" value="" size="3" tabindex="3"/>
                </p>
                <p><label for="partType"><small>�������</small></label>
                    <ofbiz:if name="partTypeList">
                        <select name="partType" id="partType"/>
                            <ofbiz:iterator name="cust" property="partTypeList" type="java.util.Map">
                                <option value='<ofbiz:inputvalue entityAttr="cust" field="ID"/>'><ofbiz:inputvalue entityAttr="cust" field="PART_TYPE"/></option>
                            </ofbiz:iterator>
                        </select>
                    </ofbiz:if>
                </p>
                <p><label for="remark"><small>��ע</small></label>
                <input type="text" name="remark" id="remark" value="" size="10" maxlength="10" tabindex="4"/>
                </p>
                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>
<script language="javascript">
        var obj=document.getElementById('equipmentType');
        obj.value='<%=eqp%>'
        equipMentChange();
</script>
