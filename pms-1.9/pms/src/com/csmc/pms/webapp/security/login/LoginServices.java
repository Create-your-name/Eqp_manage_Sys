/** 	1.0  2004-8-10
  * 版权归上揚软件（上海）有限公司所有
  * 本程序属上揚软件的私有机要资料
  * 未经本公司授权，不得非法传播和盗用
  * 可在本公司授权范围内，使用本程序
  * 保留所有权利
  */
package com.csmc.pms.webapp.security.login;
 
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.transaction.InvalidTransactionException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.swing.tree.DefaultMutableTreeNode;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionFactory;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.*;

import com.csmc.pms.webapp.util.*;
import com.csmc.pms.webapp.security.*;

/**
    *类 LoginServices.java 
    *来自ofbiz默认的LoginServices，根据需要做了必要的修改
    *@version  1.0  2004-8-10
    *@author   TONY
    */
public class LoginServices {
	public static final String pLog = "com.fa.util.pLog";
    public static final String module = LoginServices.class.getName();
 
	
    /** Login service to authenticate username and password
     * @return Map of results including (userLogin) GenericValue object
     */
    public static Map userLogin(DispatchContext ctx, Map context) {
		Debug.logTiming("============start run csmcUserLogin", pLog);
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
    	Map privResult = new HashMap();
    	LocalDispatcher dispatcher = (LocalDispatcher) ctx.getDispatcher();

        String username = (String) context.get("username");
        String password = (String) context.get("password");

        // get the visitId for the history entity
        String visitId = (String) context.get("visitId");
        
        String errMsg = "";
        if (username == null || username.length() <= 0) {
            errMsg = "Username missing.";
        } else if (password == null || password.length() <= 0) {
            errMsg = "Password missing";
        } else {
            //String realPassword = useEncryption ? HashEncrypt.getHash(password) : password;
        	String realPassword = password;
            //boolean repeat = true;
            // starts at zero but it incremented at the beggining so in the first pass passNumber will be 1
            //int passNumber = 0;

           // while (repeat) {
                //repeat = false;
                // pass number is incremented here because there are continues in this loop so it may never get to the end
                //passNumber++;

                GenericValue userLogin = null;

                // only get userLogin from cache for service calls; for web and other manual logins there is less time sensitivity
            	//目前的用户信息不是从数据库里得到，因此需要自己生成UserLogin对象
            	userLogin = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", username, "currentPassword", password, "enabled", "Y"));
            	
            	try {
    				privResult = dispatcher.runSync("checkGUIPriv", UtilMisc.toMap("userlogin", userLogin));
    			} catch (GenericServiceException e) {
    	            Debug.logError(e, "Error calling checkGUIPriv service", module);
    	            e.printStackTrace();
    	            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
    	            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
    	            return result;
    	        }
    			
				DefaultMutableTreeNode privTree = null;
				String menuString = "";
				try {
	    		    if (ModelService.RESPOND_SUCCESS.equals(privResult.get(ModelService.RESPONSE_MESSAGE))) {
	    		    	result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
	    		    } else {
	    	            errMsg = (String) privResult.get(ModelService.ERROR_MESSAGE);
	    	            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
	    	            result.put(ModelService.ERROR_MESSAGE, errMsg);
	    	            return result;
	    	        }
						Debug.logTiming("============start getUserPrivTree", pLog);
	            		privTree = SecurityDAO.getUserPrivTree(dispatcher.getDelegator(), (List)privResult.get("guipriv"));
						Debug.logTiming("============end getUserPrivTree", pLog);
						Debug.logTiming("============start generateUserMenu", pLog);
	            		menuString = SecurityDAO.generateUserMenu(privTree);
						Debug.logTiming("============end generateUserMenu", pLog);
    			} catch (Exception e) {
    	            Debug.logError(e, "Error calling SecurityDAO", module);
    	            e.printStackTrace();
    	            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
    	            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
    	        }
    			//System.out.println("menuString === " + menuString);
    	        result.put("userLogin", userLogin);
    	        result.put("menuString", menuString);
				result.put("guipriv", privResult.get("guipriv"));
				result.put("accountgroupset", privResult.get("accountgroupset"));
				result.put("accountcategoryset", privResult.get("accountcategoryset"));
    	        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

            }
        //}

        if (errMsg.length() > 0) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, errMsg);
        }
		Debug.logTiming("============end run csmcUserLogin", pLog);
        return result;
    }

    /** Creates a UserLogin
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    /*
    public static Map createUserLogin(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");
        List errorMessageList = new LinkedList();

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = (String) context.get("userLoginId");
        String partyId = (String) context.get("partyId");
        String currentPassword = (String) context.get("currentPassword");
        String currentPasswordVerify = (String) context.get("currentPasswordVerify");
        String passwordHint = (String) context.get("passwordHint");

        // security: don't create a user login if the specified partyId (if not empty) already exists
        // unless the logged in user has permission to do so (same partyId or PARTYMGR_CREATE)
        if (partyId != null && partyId.length() > 0) {
            GenericValue party = null;

            try {
                party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "", module);
            }

            if (party != null) {
                if (loggedInUserLogin != null) {
                    // <b>security check</b>: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
                    if (!partyId.equals(loggedInUserLogin.getString("partyId"))) {
                        if (!security.hasEntityPermission("PARTYMGR", "_CREATE", loggedInUserLogin)) {
                            errorMessageList.add("Party with specified party ID exists and you do not have permission to create a user login with this party ID");
                        }
                    }
                } else {
                    errorMessageList.add("You must be logged in and have permission to create a user login with a party ID for a party that already exists");
                }
            }
        }

        checkNewPassword(null, null, currentPassword, currentPasswordVerify, passwordHint, errorMessageList, true);

        GenericValue userLoginToCreate = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        userLoginToCreate.set("passwordHint", passwordHint);
        userLoginToCreate.set("partyId", partyId);
        userLoginToCreate.set("currentPassword", useEncryption ? HashEncrypt.getHash(currentPassword) : currentPassword);

        try {
            if (delegator.findByPrimaryKey(userLoginToCreate.getPrimaryKey()) != null) {
                errorMessageList.add("Could not create login user: user with ID \"" + userLoginId + "\" already exists");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            errorMessageList.add("Could not create login user (read failure): " + e.getMessage());
        }

        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        try {
            userLoginToCreate.create();
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            return ServiceUtil.returnError("Could create login user (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    */
    /** Updates UserLogin Password info
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    /*
    public static Map updatePassword(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));
        boolean adminUser = false;

        String userLoginId = (String) context.get("userLoginId");

        if (userLoginId == null || userLoginId.length() == 0) {
            userLoginId = loggedInUserLogin.getString("userLoginId");
        }

        // <b>security check</b>: userLogin userLoginId must equal userLoginId, or must have PARTYMGR_UPDATE permission
        // NOTE: must check permission first so that admin users can set own password without specifying old password
        if (!security.hasEntityPermission("PARTYMGR", "_UPDATE", loggedInUserLogin)) {
            if (!userLoginId.equals(loggedInUserLogin.getString("userLoginId"))) {
                return ServiceUtil.returnError("You do not have permission to update the password for this user login");
            }
        } else {
            adminUser = true;
        }

        GenericValue userLoginToUpdate = null;

        try {
            userLoginToUpdate = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (read failure): " + e.getMessage());
        }

        if (userLoginToUpdate == null) {
            return ServiceUtil.returnError("Could not change password, UserLogin with ID \"" + userLoginId + "\" does not exist");
        }

        String currentPassword = (String) context.get("currentPassword");
        String newPassword = (String) context.get("newPassword");
        String newPasswordVerify = (String) context.get("newPasswordVerify");
        String passwordHint = (String) context.get("passwordHint");

        if ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.lowercase"))) {
            currentPassword = currentPassword.toLowerCase();
            newPassword = newPassword.toLowerCase();
            newPasswordVerify = newPasswordVerify.toLowerCase();
        }

        List errorMessageList = new LinkedList();

        if (newPassword != null && newPassword.length() > 0) {
            checkNewPassword(userLoginToUpdate, currentPassword, newPassword, newPasswordVerify,
                passwordHint, errorMessageList, adminUser);
        }

        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        userLoginToUpdate.set("currentPassword", useEncryption ? HashEncrypt.getHash(newPassword) : newPassword, false);
        userLoginToUpdate.set("passwordHint", passwordHint, false);

        try {
            userLoginToUpdate.store();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put("updatedUserLogin", userLoginToUpdate);
        return result;
    }

    /** Updates the UserLoginId for a party, replicating password, etc from
     *    current login and expiring the old login.
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateUserLoginId(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");
        List errorMessageList = new LinkedList();

        //boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = (String) context.get("userLoginId");

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        String partyId = loggedInUserLogin.getString("partyId");
        String password = loggedInUserLogin.getString("currentPassword");
        String passwordHint = loggedInUserLogin.getString("passwordHint");

        // security: don't create a user login if the specified partyId (if not empty) already exists
        // unless the logged in user has permission to do so (same partyId or PARTYMGR_CREATE)
        if (partyId != null || partyId.length() > 0) {
            //GenericValue party = null;
            //try {
            //    party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
            //} catch (GenericEntityException e) {
            //    Debug.logWarning(e, "", module);
            //}

            if (loggedInUserLogin != null) {
                // security check: userLogin partyId must equal partyId, or must have PARTYMGR_CREATE permission
                if (!partyId.equals(loggedInUserLogin.getString("partyId"))) {
                    errorMessageList.add("Party with specified party ID exists and you do not have permission to create a user login with this party ID");
                }
            } else {
                errorMessageList.add("You must be logged in and have permission to create a user login with a party ID for a party that already exists");
            }
        }

        GenericValue newUserLogin = null;
        boolean doCreate = true;

        // check to see if there's a matching login and use it if it's for the same party
        try {
            newUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            errorMessageList.add("Could not create login user (read failure): " + e.getMessage());
        }

        if (newUserLogin != null) {
            if (!newUserLogin.get("partyId").equals(partyId)) {
                errorMessageList.add("Could not create login user: user with ID \"" + userLoginId + "\" already exists");
            } else {
                doCreate = false;
            }
        } else {
            newUserLogin = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        }

        newUserLogin.set("passwordHint", passwordHint);
        newUserLogin.set("partyId", partyId);
        newUserLogin.set("currentPassword", password);
        newUserLogin.set("enabled", "Y");
        newUserLogin.set("disabledDateTime", null);

        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        try {
            if (doCreate) {
                newUserLogin.create();
            } else {
                newUserLogin.store();
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            return ServiceUtil.returnError("Couldn't create login user (write failure): " + e.getMessage());
        }

        loggedInUserLogin.set("enabled", "N");
        loggedInUserLogin.set("disabledDateTime", UtilDateTime.nowTimestamp());

        try {
            loggedInUserLogin.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            return ServiceUtil.returnError("Couldn't disable old login user (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put("newUserLogin", newUserLogin);
        return result;
    }

    /** Updates UserLogin Security info
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map updateUserLoginSecurity(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");

        String userLoginId = (String) context.get("userLoginId");

        if (userLoginId == null || userLoginId.length() == 0) {
            userLoginId = loggedInUserLogin.getString("userLoginId");
        }

        // <b>security check</b>: must have PARTYMGR_UPDATE permission
        if (!security.hasEntityPermission("PARTYMGR", "_UPDATE", loggedInUserLogin) && !security.hasEntityPermission("SECURITY", "_UPDATE", loggedInUserLogin)) {
            return ServiceUtil.returnError("You do not have permission to update the security info for this user login");
        }

        GenericValue userLoginToUpdate = null;

        try {
            userLoginToUpdate = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (read failure): " + e.getMessage());
        }

        if (userLoginToUpdate == null) {
            return ServiceUtil.returnError("Could not change password, UserLogin with ID \"" + userLoginId + "\" does not exist");
        }

        boolean wasEnabled = !"N".equals(userLoginToUpdate.get("enabled"));

        if (context.containsKey("enabled")) {
            userLoginToUpdate.set("enabled", context.get("enabled"), true);
        }
        if (context.containsKey("disabledDateTime")) {
            userLoginToUpdate.set("disabledDateTime", context.get("disabledDateTime"), true);
        }
        if (context.containsKey("successiveFailedLogins")) {
            userLoginToUpdate.set("successiveFailedLogins", context.get("successiveFailedLogins"), true);
        }

        // if was disabled and we are enabling it, clear disabledDateTime
        if (!wasEnabled && "Y".equals(context.get("enabled"))) {
            userLoginToUpdate.set("disabledDateTime", null);
        }

        // if was enabled and we are disabling it, and no disabledDateTime was passed, set it to now
        if (wasEnabled && "N".equals(context.get("enabled")) && context.get("disabledDateTime") == null) {
            userLoginToUpdate.set("disabledDateTime", UtilDateTime.nowTimestamp());
        }

        try {
            userLoginToUpdate.store();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Could not change password (write failure): " + e.getMessage());
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
/*
    public static void checkNewPassword(GenericValue userLogin, String currentPassword, String newPassword, String newPasswordVerify, String passwordHint, List errorMessageList, boolean ignoreCurrentPassword) {
        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        if (!ignoreCurrentPassword) {
            String realPassword = currentPassword;

            if (useEncryption && currentPassword != null) {
                realPassword = HashEncrypt.getHash(currentPassword);
            }
            // if the password.accept.encrypted.and.plain property in security is set to true allow plain or encrypted passwords
            boolean passwordMatches = currentPassword != null && (realPassword.equals(userLogin.getString("currentPassword")) ||
                    ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.accept.encrypted.and.plain")) && currentPassword.equals(userLogin.getString("currentPassword"))));

            if ((currentPassword == null) || (userLogin != null && currentPassword != null && !passwordMatches)) {
                errorMessageList.add("Old Password was not correct, please re-enter.");
            }
        }

        if (!UtilValidate.isNotEmpty(newPassword) || !UtilValidate.isNotEmpty(newPasswordVerify)) {
            errorMessageList.add("Password or verify password missing.");
        } else if (!newPassword.equals(newPasswordVerify)) {
            errorMessageList.add("Password did not match verify password");
        }

        int minPasswordLength = 0;

        try {
            minPasswordLength = Integer.parseInt(UtilProperties.getPropertyValue("security.properties", "password.length.min", "0"));
        } catch (NumberFormatException nfe) {
            minPasswordLength = 0;
        }

        if (newPassword != null) {
            if (!(newPassword.length() >= minPasswordLength)) {
                errorMessageList.add("Password must be at least " + minPasswordLength + " characters long");
            }
            if (userLogin != null && newPassword.equalsIgnoreCase(userLogin.getString("userLoginId"))) {
                errorMessageList.add("Password may not equal the Username");
            }
            if (UtilValidate.isNotEmpty(passwordHint) && (passwordHint.toUpperCase().indexOf(newPassword.toUpperCase()) >= 0)) {
                errorMessageList.add("Password hint may not contain the password");
            }
        }
    }*/
}
