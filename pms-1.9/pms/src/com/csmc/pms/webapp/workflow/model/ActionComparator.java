package com.csmc.pms.webapp.workflow.model;

import java.util.Comparator;

public class ActionComparator implements Comparator {

	public int compare(Object arg0, Object arg1) {
		Action action1 = (Action) arg0;
		Action action2 = (Action) arg1;
		if(action1.getActionId() == action2.getActionId()) {
			return 0;
		} else if(action1.getActionId() > action2.getActionId()) {
			return 1;
		} else if(action1.getActionId() < action2.getActionId()) {
			return -1;
		}
		return 0;
	}
}
