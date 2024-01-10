package com.csmc.pms.webapp.eqp.helper;

import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.util.AsuraServiceHandle;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.TPServiceException;
import com.csmc.pms.webapp.util.TPServiceHandle;

public class FabAdapter {

	public static Map runCallService(GenericDelegator delegator, GenericValue userLogin, Map map, String serviceName) throws TPServiceException {
		if (Constants.CALL_TP_FLAG) {//fab1调用
			return TPServiceHandle.runTPService(delegator, userLogin, map, serviceName);
		} else if (Constants.CALL_ASURA_FLAG) {//fab5调用
			return AsuraServiceHandle.runAsuraService(delegator, userLogin, map, serviceName);
		}
		return null;
	}
}
