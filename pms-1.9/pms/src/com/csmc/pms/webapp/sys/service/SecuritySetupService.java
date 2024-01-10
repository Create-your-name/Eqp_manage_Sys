/** 
  * ��Ȩ���ϓP������Ϻ������޹�˾����
  * ���������ϓP�����˽�л�Ҫ����
  * δ������˾��Ȩ�����÷Ƿ������͵���
  * ���ڱ���˾��Ȩ��Χ�ڣ�ʹ�ñ�����
  * ��������Ȩ��
  */
package com.csmc.pms.webapp.sys.service;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.sys.helper.SecuritySetupHelper;
import com.csmc.pms.webapp.util.CommonUtil;

/**
   *�� SecuritySetupService.java
   *@version  1.0  2004-8-9
   *@author   Sky
   */
public class SecuritySetupService {
	public static Map updateAccountPriv(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();  
		Map result = new HashMap();
        
		try { 
//			GenericValue userLogin = (GenericValue) context.get("userLogin");
			GenericValue userLogin = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", "PMS")); // ���ڻ�ȡȨ���б�,����Ҫ��֤

			String accountId = (String)context.get("accountid");
			Map priv = (Map)context.get("privs");
			Map privMap =  new HashMap();
				
			GenericValue accountGv = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", accountId));			
			if (CommonUtil.isLocalUser(accountGv)) {
				//���б���У��
				Set accountGuiPriv = new HashSet();
				accountGuiPriv.addAll(SecuritySetupHelper.getLocalPriv(accountId, delegator));
				privMap.put("accountPriv", accountGuiPriv);
				privMap.put("groupGuiPriv", new HashSet());
			} else {
				privMap =  SecuritySetupHelper.getAllGuiPriv(accountId,delegator,userLogin);
			}
			
			Collection oldAccountPriv = (Collection)privMap.get("accountPriv");
			Collection oldGroupGuiPriv = (Collection)privMap.get("groupGuiPriv");	
			oldAccountPriv.removeAll(oldGroupGuiPriv);
			Map oldGuiPrivMap = SecuritySetupHelper.splitGuiPriv(oldAccountPriv);
			//Set oldPromisPrivs = (Set)oldGuiPrivMap.get("promisPriv");
			Set oldLocalPrivs = (Set)oldGuiPrivMap.get("localPriv");				

			Collection newAccountPrivs = priv.keySet();
			newAccountPrivs.removeAll(oldGroupGuiPriv);		
			Map guiPrivMap = SecuritySetupHelper.splitGuiPriv(newAccountPrivs);
			//Set promisPrivs = (Set)guiPrivMap.get("promisPriv");
			Set localPrivs = (Set)guiPrivMap.get("localPriv");					

			SecuritySetupHelper.updateLocalPriv(accountId,localPrivs,oldLocalPrivs,delegator);
			
			//SecuritySetupHelper.updateAccountPriv(accountId,null,promisPrivs,oldPromisPrivs,delegator,userLogin);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			return result;

	   } catch (Exception e) {
	   		//e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return result;
	   }          	        	
	}

}
