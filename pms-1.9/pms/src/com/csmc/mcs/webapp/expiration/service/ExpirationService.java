package com.csmc.mcs.webapp.expiration.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.expiration.helper.ExpirationHelper;

public class ExpirationService {
	public static final String moduel = ExpirationService.class.getName();
	
	/**
	 * 保存有效期
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map saveExpirationDate(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		Map result = new HashMap();
		Map paramMap = (Map) context.get("paramMap");
		String listSize = (String)paramMap.get("listSize");
		String userId = (String)paramMap.get("userId");
		try {
			for(int i=0;i<Integer.parseInt(listSize);i++){
				String materialStatusIndex = (String)paramMap.get("materialStatusIndex"+i);
				String oldMrbId = (String)paramMap.get("oldMrbId"+i);
				String mrbId = (String)paramMap.get("mrbId"+i);
				String oldMrbDate = (String)paramMap.get("oldMrbDate"+i);
				String mrbDate = (String)paramMap.get("mrbDate"+i);
				if(!oldMrbId.equalsIgnoreCase(mrbId) || !oldMrbDate.equalsIgnoreCase(mrbDate)){
					GenericValue mcsMaterialStatus = delegator.findByPrimaryKey("McsMaterialStatus", UtilMisc.toMap("materialStatusIndex", materialStatusIndex));
					mcsMaterialStatus.put("mrbId", mrbId);	
					if(StringUtils.isNotEmpty(mrbDate)){
						mcsMaterialStatus.put("mrbDate", new java.sql.Date(ExpirationHelper.parseDate(mrbDate).getTime()));
					}else{
						mcsMaterialStatus.put("mrbDate", null);
					}
					mcsMaterialStatus.put("mrbTransBy", userId);
					mcsMaterialStatus.put("mrbTransTime",  new java.sql.Date(UtilDateTime.nowDate().getTime()));
					delegator.store(mcsMaterialStatus);
				}
			}
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), moduel);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
}
