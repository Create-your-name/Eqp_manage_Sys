package com.csmc.pms.webapp.workflow.exception;

public class FlowException extends Exception {
	
    public FlowException() {
        super();
    }

    public FlowException(String str) {
        super(str);
    }
    
    public FlowException(Throwable nested) {
        super(nested);
    }

    public FlowException(String str, Throwable nested) {
        super(str, nested);
    }
}
