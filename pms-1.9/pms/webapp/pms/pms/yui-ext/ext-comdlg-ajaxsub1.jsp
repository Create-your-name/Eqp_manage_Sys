<script language="javascript">
var eytDlg1 = function(){
    var dialog, addLink, name, description;
    var tabs, postBtn;
    var error, errorMsg;
    var posting = false;
    var dlgWidth=500;
    var dlgHeight=300;
    var closeMethod='';
    var closeFlag=true;
    return {
        init : function(){
             // cache some elements for quick access
             error = Ext.get('post-error-y1');
             errorMsg = Ext.get('post-error-msg-y1');
             
             this.createDialog('1');
        },
        
        dlgInit:function(width,height,closeFunction,closeButtonFlag){
        	if(width!=''){
        		dlgWidth=width;
        	}
        	if(height!=''){
        		dlgHeight=height;
        	}
        	if(closeFunction!=undefined){
        		closeMethod=closeFunction;
        	}else{
        		closeMethod='';
        	}
        	if(closeFunction!=undefined){
        		closeFlag=closeButtonFlag;
        	}else{
        		closeFlag=true;
        	}
        },       
        
        // submit the comment to the server
        submitComment : function(){
        	postBtn.disable();
        	var errMsg=checkForm_y1();
        	if (errMsg!=""){
        		postBtn.enable();
                error.radioClass('active-msg');
                errorMsg.update(errMsg);
            	return;
        	}
			error.removeClass('active-msg');
			errorMsg.update('');
			var formId=Ext.get('y1-form').dom.childNodes(0).id;
			var submitUrl=Ext.get(formId).dom.action;
            Ext.lib.Ajax.formRequest(formId,submitUrl,{success: submitSuccess_y1, failure: submitFailure_y1});
        	tabs.activate('post-tab-y1');
          	//Ext.get(formId).dom.submit();
        },
        
        getDialog:function(){
        	return dialog;
        },
        
        getTabs:function(){
        	return tabs;
        },
        
        showAddDialog:function(obj){
        	tabs.activate('post-tab');
            dialog.show(obj);
        },
        
        showEditDialog:function(obj,actionURL){
                postBtn.enable();
            var commentFailure_y1 = function(o){
                error.radioClass('active-msg');
                errorMsg.update('Unable to connect.');
            };
            dialog.show(obj);
            var formId=Ext.get('y1-form').dom.childNodes(0).id;
            Ext.lib.Ajax.formRequest(formId,actionURL,{success: commentSuccess_y1, failure: commentFailure_y});
        	tabs.activate('post-tab-y1');
        },
        
        createDialog : function(){
            dialog = new Ext.BasicDialog("y1-dlg", {
            		closable:closeFlag,
                    autoTabs:true,
                    modal:true,
                    width:dlgWidth,
                    height:dlgHeight,
                    minWidth:300,
                    minHeight:300
            });
            dialog.addKeyListener(27, dialog.hide, dialog);
            postBtn = dialog.addButton('提交', this.submitComment, this);
            if(closeMethod!=''){
            	dialog.addButton('关闭', closeMethod, dialog);
            }else{
            	dialog.addButton('关闭', dialog.hide, dialog);
            }
            // clear any messages and indicators when the dialog is closed
            dialog.on('hide', function(){
                error.removeClass('active-msg');
            });
            
            // stoe a refeence to the tabs
            tabs = dialog.getTabs();
        }
    };
}();
Ext.EventManager.onDocumentReady(eytDlg1.init, eytDlg1, true);
</script>