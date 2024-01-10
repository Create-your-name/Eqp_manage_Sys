/*
Main Function List:

Cstr(string)			:change the string into int;return string;
Rep1(string)			:change the string 's "'",""" into "`" and change "<>" into "()" ;return string;
Trim(string)			:Trim the string's " " before and after the string;return string;
*/


//*************************************** string function *************************



//to solve the hyper link that does not inherit form bean
function doSubmit(actionform,action){  
//alert( actionform);
		if(null != action){
	    	var origAction = actionform.action;
			actionform.action = action;
			actionform.submit();
	    	actionform.action = origAction;
    	}else{
    		actionform.submit();
    	}
}

//to solve the hyper link that does not inherit form bean
function submitFormByConfirm(actionform,confirmMessage){

    var message = "Are you sure?";
    //alert(confirmMessage);
    if(null != confirmMessage){
        message = confirmMessage;
    }
    //alert(message);
    if(!window.confirm(message))
        return;
	actionform.submit();
}

//to solve the hyper link that does not inherit form bean
function doSubmitByConfirm(actionform,action,overlay,isConfirm,confirmMessage){
    if(true==isConfirm){
        var message = "Are you sure?";
        //alert(confirmMessage);
        if(null != confirmMessage){
            message = confirmMessage;
        }
        //alert(message);
        if(!window.confirm(message))
            return;
    }
    var origAction = actionform.action;
	if(overlay == null){
		
		if(null != action){
			destAction = origAction + "?" + action;
		}else{
			destAction = origAction;
		}
	} else {
		destAction = action;

	}    
    //alert(destAction);
	actionform.action = destAction;
	actionform.submit();
    actionform.action = origAction;
}
//to solve the hyper link that does not inherit form bean
function doDelete(actionform,destAction,confirmMsg){
    var message = "Are you sure?";
    if(confirmMsg!=null){
    	message = confireMsg;	
    }
    if(!window.confirm(message))
            return;    
    var origAction = actionform.action;
	actionform.action = destAction;
	actionform.submit();
    actionform.action = origAction;
}
//to solve the hyper link that does not inherit form bean
function doSaveInOverride(actionform,action,overlay){
    var origAction = actionform.action;
	if(overlay == null){
		
		if(null != action){
			destAction = origAction + "?" + action;
		}else{
			destAction = origAction;
		}
	} else {
		destAction = action;

	}    
    //alert(destAction);
	actionform.action = destAction;
	actionform.submit();
   // window.close();
}

function validate(form){
        return true;
}

function Cstr(inp)
{
          return(""+inp+"");
}
function cstr(inp)
{
          return(""+inp+"");
}
function Trim(inString)
{
        var l,i,g,t,r;
    inString=Cstr(inString);
    l=inString.length;
    t=inString;
           for(i=0;i<l;i++)
           {
            g=inString.substring(i,i+1);
               if(g==" ")
               {
                t=inString.substring(i+1,l);
        }
               else
               {
                break;;
               }
    }
           r=t;
           l=t.length;
           //Delete the spaces back
           for(i=l;i>0;i--)
           {
            g=t.substring(i,i-1);
              if(g==" ")
              {
                r=t.substring(i-1,0);
              }
              else
             {
                break;
             }
           }
           return(r);
}

function trim(inString)
{
        var l,i,g,t,r;
    inString=Cstr(inString);
    l=inString.length;
    t=inString;
           for(i=0;i<l;i++)
           {
            g=inString.substring(i,i+1);
               if(g==" ")
               {
                t=inString.substring(i+1,l);
        }
               else
               {
                break;;
               }
    }
           r=t;
           l=t.length;
           //Delete the spaces back
           for(i=l;i>0;i--)
           {
            g=t.substring(i,i-1);
              if(g==" ")
              {
                r=t.substring(i-1,0);
              }
              else
             {
                break;
             }
           }
           return(r);
}

function IsNumeric(str)
{
        var chrC;
           var intC;
           var intLen;
           var intI;
           var token=0;//tag the number of "."
           var token1=0;//tag the number of "e" or "E"
           str=Cstr(str);
           str=Trim(str);
           intLen=str.length;
           intI=0;
           chrC=str.substring(intI,intI+1);//get the first char
           if ((chrC.indexOf("+")!=-1)||(chrC.indexOf("-")!=-1)) intI++;
           chrC=str.substring(intI,intI+1);//remorve first + or -
           if (isNaN(parseInt(chrC)) && (chrC.indexOf(".")==-1) && (chrC.indexOf("E")==-1) && (chrC.indexOf("e")==-1)) return false;
          //first char is E or e
           if ((chrC.indexOf("e")!=-1) || (chrC.indexOf("E")!=-1))
           {
                return false;
        intI++;
        if (intI<intLen)
        {
                chrC=str.substring(intI,intI+1);
                  if (isNaN(parseInt(chrC)) && (chrC.indexOf("+")==-1) && (chrC.indexOf("-")==-1)) return false;
                  if ((chrC.indexOf("+")!=-1) || (chrC.indexOf("-")==-1))
                  {
                           intI++;
                  }
                  for(;intI<intLen;intI++)
                  {
                          chrC=str.substring(intI,intI+1);
                        if(isNaN(parseInt(chrC)))
                        {
                                  return false;
                    }
                  }
               }
        return true;
        }
           //first char is not a E or e
           intI++;
           for(;intI<intLen;intI++)
           {
            chrC=str.substring(intI,intI+1);
                if((isNaN(parseInt(chrC))) && (chrC.indexOf(".")==-1) && (chrC.indexOf("e")==-1) && (chrC.indexOf("E")==-1))
                 {
                           return false;
             }
             //exist "."
             if((isNaN(parseInt(chrC))) && (chrC.indexOf(".")!=-1))
             {
                       //intI++;
                       token++;
                       if (token!=1) return false;
             }
             if((chrC.indexOf("e")!=-1)|| (chrC.indexOf("E")!=-1))
             {
                       intI++;token1++;
                       if (token1!=1) return false;
            }
        }
        return true;
}

//-------------------------------------------------------------   check numeric
//this function is used to tell whether str is numeric.
function IsLong(str)
{
        var chrC;
           var intC;
           var intLen;
           var intI;
           var token=0;//tag the number of "."
           var token1=0;//tag the number of "e" or "E"
           str=Cstr(str);
           str=Trim(str);
           intLen=str.length;
           intI=0;
           if (str.indexOf(".")!=-1) return false;
           
           
           chrC=str.substring(intI,intI+1);//get the first char
           if ((chrC.indexOf("+")!=-1)||(chrC.indexOf("-")!=-1)) intI++;
           chrC=str.substring(intI,intI+1);//remorve first + or -
           if (isNaN(parseInt(chrC)) && (chrC.indexOf(".")==-1) && (chrC.indexOf("E")==-1) && (chrC.indexOf("e")==-1)) return false;
          //first char is E or e
           if ((chrC.indexOf("e")!=-1) || (chrC.indexOf("E")!=-1))
           {
                return false;
        intI++;
        if (intI<intLen)
        {
                chrC=str.substring(intI,intI+1);
                  if (isNaN(parseInt(chrC)) && (chrC.indexOf("+")==-1) && (chrC.indexOf("-")==-1)) return false;
                  if ((chrC.indexOf("+")!=-1) || (chrC.indexOf("-")==-1))
                  {
                           intI++;
                  }
                  for(;intI<intLen;intI++)
                  {
                          chrC=str.substring(intI,intI+1);
                        if(isNaN(parseInt(chrC)))
                        {
                                  return false;
                    }
                  }
               }
        return true;
        }
           //first char is not a E or e
           intI++;
           for(;intI<intLen;intI++)
           {
            chrC=str.substring(intI,intI+1);
                if((isNaN(parseInt(chrC))) && (chrC.indexOf(".")==-1) && (chrC.indexOf("e")==-1) && (chrC.indexOf("E")==-1))
                 {
                           return false;
             }
             //exist "."
             if((isNaN(parseInt(chrC))) && (chrC.indexOf(".")!=-1))
             {
                       intI++;token++;
                       if (token!=1) return false;
             }
             if((chrC.indexOf("e")!=-1)|| (chrC.indexOf("E")!=-1))
             {
                       intI++;token1++;
                       if (token1!=1) return false;
            }
        }
        return true;
}

//-------------------------------------------------------------   check length
//to get the string(unicode including GBK)'s real length,
//a chinese char means a three chars length
function getLength(str){
        var _str_code="";
        var i=0;
        var j=0;
        //avoid the null pointer exception;
        if(str==null) return;

        while(true){
        _str_code=str.charCodeAt(i);
        i++;
        if(_str_code>=0&&_str_code<=255){
                j++;
        }else if(_str_code>255){
                j=j+2;
        }else{
                break;
        }
        }
        return j;
}

//-------------------------------------------------------------   check date format

function IsDate(str){
        bValid = true;
        dateRegexp = new RegExp("^(\\d{4})[/-](\\d{2})[/-](\\d{2})$");

        var matched = dateRegexp.exec(str);

        if(matched != null) {
                if (!isValidDate(matched[1], matched[2], matched[3])) {
                        bValid =  false;
                }
        } else {
                bValid =  false;
        }
        return bValid;
}

function isValidDate(year, month, day) {
        if (month < 1 || month > 12)
          return false;

        if (day < 1 || day > 31)
          return false;

        if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31)
          return false;

        if (month == 2) {
          var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
          if (day>29 || (day == 29 && !leap))
             return false;
        }
        
        if ((!IsNumeric(year)) || (!IsNumeric(month)) || (!IsNumeric(day))) {
             return false;
        }

        return true;
}

//-------------------------------------------------------------   check time
function IsTime(str){
        if (str.search(/^[0-9]+:[0-9]+:[0-9]+$/) != -1)
        return true;
    else
        return false;
}

//-------------------------------------------------------------   check email
function IsEmail(str){
        if (str.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) != -1)
        return true;
    else
        return false;
}

//-------------------------------------------------------------   Check IP address
function isIP(str) {
	if (str.search(/^/) != -1) return ture;
	return false;
}

function goToURL() {
  var i, args=goToURL.arguments; document.returnValue = false;
  for (i=0; i<(args.length-1); i+=2) eval(args[i]+".location='"+args[i+1]+"'");
}


//---------------------------------------------------------------  replace for validate_adhoc
function replace(target,oldTerm,newTerm,caseSens,wordOnly){
        var wk ;
          var ind = 0;
          var next = 0;
          wk=Cstr(target);
          if (!caseSens)
        {
                  oldTerm = oldTerm.toLowerCase();
                  wk = target.toLowerCase();
        }
          while ((ind = wk.indexOf(oldTerm,next)) >= 0)
          {
                if (wordOnly)
                {
                    var before = ind - 1;
                        var after = ind + oldTerm.length;
                    if (!(space(wk.charAt(before)) && space(wk.charAt(after))))
                        {
                        next = ind + oldTerm.length;
                                   continue;
                        }
                }
                 target = target.substring(0,ind) + newTerm + target.substring(ind+oldTerm.length,target.length);
                 wk = wk.substring(0,ind) + newTerm + wk.substring(ind+oldTerm.length,wk.length);
                 next = ind + newTerm.length;
                if (next >= wk.length) { break; }
         }
          return target;
}

function replace(source, oldStr, newStr) {
        var i;
        var chr;
        var str = "~";

        for (i = 0; i < source.length; i++) {
                chr = source.charAt(i);
                if (chr == oldStr) {
                        source = source.replace(chr, str);
                }
        }

        for (i = 0; i < source.length; i++) {
                chr = source.charAt(i);
                if (chr == "~") {
                        source = source.replace(chr, newStr);
                }
        }
        return source;
}

function replaceAll (streng, soeg, erstat) {
 var st = streng;
 if (soeg.length == 0)
  return st;
 var idx = st.indexOf(soeg);
 while (idx >= 0)        
 {  
  st = st.substring(0,idx) + erstat + st.substr(idx+soeg.length);
  idx = st.indexOf(soeg);
 }
 return st;
}
function hide_screen(p_screen,p_display,p_alpha)
{
	var iHeight	= document.body.scrollHeight > screen.height ? document.body.scrollHeight : screen.height;
	var iWidth	= document.body.clientWidth;
	
	if (p_display == "none")
	{
		document.getElementById("dialogScreen").innerHTML	= "<div style='position:absolute;top:0;left:0;opacity:"+p_alpha+";filter:alpha(opacity="+(p_alpha*100)+");background:#fff;width:" + iWidth + "px;height:"+iHeight+"px;z-index:9000'></div>";
	}
	else
	{
		document.getElementById("dialogScreen").innerHTML	= "";
	}
	
	//return true;
}
function to_left(p_display)
{
	var i_style			= document.getElementById("loadBar").style;
	i_style.display		= p_display;
	i_style.left		= 10;
	i_style.top			= 20;
}
function loading()
{
	to_left("");
	hide_screen("","none",0.7);
	document.getElementById("loadBar").focus();
}
function unLoad()
{
	to_left("none");
	hide_screen("","","");
}
//js windowOpen
function windowOpen(url,windowName,width,height){
	window.open(url,windowName,
		"top=130,left=240,width="+width+",height="+height+",title=,channelmode=0," +
		"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
		"scrollbars=1,status=1,titlebar=0,toolbar=no");
}

//check栏位长度
function checkColumnLength(columnString) {
	var arr = columnString.split(";");
	for(var i = 0; i < arr.length; i++) {
		var columnInfo = arr[i].split("||");
		if(document.getElementById(columnInfo[0]) != null) {
			var columnLength = getLength(document.getElementById(columnInfo[0]).value);
			if(columnLength > parseInt(columnInfo[1])) {
				var msg = columnInfo[2] + "\u8d85\u8fc7\u957f\u5ea6" + columnInfo[1];
				return msg;
			}
		}
	}
	return "";
}

//更新或查询设备状态时
function setSuccessMsg(msg){
	var successDiv=document.getElementById("Eqp_Status_Msg_Success_Div");
	var errorDiv=document.getElementById("Eqp_Status_Msg_Error_Div");
	var successSpan=document.getElementById("Eqp_Status_Msg_Success_Span");
	errorDiv.style.display='none';
	successDiv.style.display='';
	successSpan.innerText=msg;
}

//更新或查询设备状态时
function setErrorMsg(msg){
	var successDiv=document.getElementById("Eqp_Status_Msg_Success_Div");
	var errorDiv=document.getElementById("Eqp_Status_Msg_Error_Div");
	var errorSpan=document.getElementById("Eqp_Status_Msg_Error_Span");
	successDiv.style.display='none';
	errorDiv.style.display='';
	errorSpan.innerText=msg;
}