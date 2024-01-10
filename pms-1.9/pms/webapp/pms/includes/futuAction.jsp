<!--------------------------------action info---------------------------------------->
<table width="100%" height="35"  border="0" cellpadding="0" cellspacing="0" id="actionInfoTb" style="display:none">
	<tr><td><fieldset>
      <legend>Action信息</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="0">
        <tr bgcolor="#DFE1EC" id="action1"> 
 	<td class="en11pxb" width="20%" height="28" nowrap>Action</td> 
		  <td class="en11pxb"  width="80%"  height="28">
		  <input type="text" name="actionInfo1" class="input-diable" id="actionInfo1" size="73" readonly>
		  </td>
        </tr>
		    <tr bgcolor="#DFE1EC" id="action2"> 
 	<td class="en11pxb" width="20%" height="28" nowrap>UserId</td> 
		  <td class="en11pxb"  width="80%"  height="28">
		  <input name="actionInfo2" type="text" class="input-diable" id="actionInfo2" size="73" readonly>
		  </td>
        </tr>					
	    <tr bgcolor="#DFE1EC" id="action3"> 
 	<td class="en11pxb" width="20%" height="28" nowrap>Hold码</td> 
		  <td class="en11pxb"  width="80%"  height="28">
		  <input name="actionInfo3" type="text" class="input-diable" id="actionInfo3" size="73" readonly>
		  </td>
        </tr>	
	    <tr bgcolor="#DFE1EC" id="action4"> 
 	<td class="en11pxb" width="20%" height="28" nowrap>Hold原因</td> 
		  <td class="en11pxb"  width="80%"  height="28">
		  <input name="actionInfo4" type="text" class="input-diable" id="actionInfo4" size="73" readonly>
		  </td>
        </tr>	
		    <tr bgcolor="#DFE1EC" id="action5"> 
 	<td class="en11pxb" width="20%" height="28" nowrap>NoteText</td> 
		  <td class="en11pxb"  width="80%"  height="28">
		  <input name="actionInfo5" type="text" class="input-diable" id="actionInfo5" size="73" readonly>
		  </td>
        </tr>	
		    <tr bgcolor="#DFE1EC" id="action6"> 
 	<td class="en11pxb" width="20%" height="28" nowrap>NoteTime</td> 
		  <td class="en11pxb"  width="80%"  height="28">
		  <input name="actionInfo6" type="text" class="input-diable" id="actionInfo6" size="73" readonly>
		  </td>
        </tr>	
		    <tr bgcolor="#DFE1EC" id="action7"> 
 	<td class="en11pxb" width="20%" height="28" nowrap>PartId</td> 
		  <td class="en11pxb"  width="80%"  height="28">
		  <input name="actionInfo7" type="text" class="input-diable" id="actionInfo7" size="73" readonly>
		  </td>
        </tr>	
		    <tr bgcolor="#DFE1EC" id="action8"> 
 	<td class="en11pxb" width="20%" height="28" nowrap>具体位置</td> 
		  <td class="en11pxb"  width="80%"  height="28">
		  <input name="actionInfo8" type="text" class="input-diable" id="actionInfo8" size="73" readonly>
		  </td>
        </tr>	
      </table>
      </fieldset>
	</td></tr>
</table>
<script language="javascript">
var token = "\u0019";
function selectAction(fileTime,action,prcdId,instNum,path,desc) {
	doDisplay(new Array(1,2,0,0,0,0,0,0));
	document.getElementById("actionInfo1").value = action; 
}
function selectNoteAction(fileTime,action,prcdId,instNum,path,desc){
	doDisplay(new Array(1,2,0,0,5,6,0,8));
	document.getElementById("actionInfo1").value = action;
	if(!(desc=="")){
		var descValue = desc.split(token);
		var userId = descValue[0];
		var noteText = descValue[1];
		var noteTime = descValue[2];  
		var stepPlacement = descValue[3];
		noteText = replaceAll(noteText, "&semicolo", ";");
	    noteText = replaceAll(noteText, "&comma", ",");
		document.getElementById("actionInfo5").value = noteText;
		document.getElementById("actionInfo6").value = noteTime;
		document.getElementById("actionInfo2").value = userId;
		document.getElementById("actionInfo8").value = stepPlacement;
	}
}
function selectHoldAction(fileTime,action,prcdId,instNum,path,desc){ 
	 doDisplay(new Array(1,2,3,4,0,0,0,8));
	 document.getElementById("actionInfo1").value = action;  
	if(!(desc=="")){
		var descValue = desc.split(token);
		var holdCode = descValue[0];
		var holdReason = descValue[1];
		var userId = descValue[2];
		var stepPlacement = descValue[3];
		document.getElementById("actionInfo3").value = holdCode;
		document.getElementById("actionInfo4").value = holdReason;
		document.getElementById("actionInfo2").value = userId;
		document.getElementById("actionInfo8").value = stepPlacement;
	 }
}

function selectNewPartAction(fileTime,action,prcdId,instNum,path,desc){
	 doDisplay(new Array(1,2,0,0,0,0,7,8));
	document.getElementById("actionInfo1").value = action;  
	if(!(desc=="")){
		var descValue = desc.split(token);
		var partId = descValue[0];
		var userId = descValue[1];
		var stepPlacement = descValue[2];
		document.getElementById("actionInfo7").value = partId;
		document.getElementById("actionInfo8").value = stepPlacement;
		document.getElementById("actionInfo2").value = userId;
	 }
}

function doDisplay(array) {
	 processTb.style.display="none";
	 actionInfoTb.style.display="";
	 for(var loop = 1;loop <= 8;loop++) {
	 	if(array[loop - 1] == loop) {
			//alert("action" + loop + ".style.display=''");
			eval("document.getElementById('actionInfo" + loop + "').value=''");
			eval("action" + loop + ".style.display=''");
		} else {
			eval("action" + loop + ".style.display='none'");
			eval("document.getElementById('actionInfo" + loop + "').value=''");
		}
	 }
}	
</script>