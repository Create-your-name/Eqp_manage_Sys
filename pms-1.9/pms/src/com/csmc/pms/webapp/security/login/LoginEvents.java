/**
 * 1.0  2004-8-10
 * 版权归上揚软件（上海）有限公司所有
 * 本程序属上揚软件的私有机要资料
 * 未经本公司授权，不得非法传播和盗用
 * 可在本公司授权范围内，使用本程序
 * 保留所有权利
 */
package com.csmc.pms.webapp.security.login;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import com.csmc.pms.webapp.db.SQLProcess;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FlexibleStringExpander;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.content.stats.VisitHandler;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.WebShoppingCart;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import java.util.*;

import com.csmc.pms.webapp.util.*;

/**
 * 类 LoginEvents.java
 * 来自ofbiz默认的LoginEvents，根据需要做了必要的修改
 *
 * @author TONY
 * @version 1.0  2004-8-10
 */
public class LoginEvents {

    public static final String pLog = "com.fa.util.pLog";
    public static final String module = LoginEvents.class.getName();

    public static final String EXTERNAL_LOGIN_KEY_ATTR = "externalLoginKey";

    /**
     * This Map is keyed by the randomly generated externalLoginKey and the value is a UserLogin GenericValue object
     */
    public static Map externalLoginKeys = new HashMap();

    /**
     * This Map is keyed by userLoginId and the value is another Map keyed by the webappName and the value is the sessionId.
     * When a user logs in an entry in this Map will be populated for the given user, webapp and session.
     * When checking security this Map will be checked if the user is logged in to see if we should log them out automatically; this implements the universal logout.
     * When a user logs out this Map will be cleared so the user will be logged out automatically on subsequent requests.
     */
    public static Map loggedInSessions = new HashMap();


    /**
     * Save USERNAME and PASSWORD for use by auth pages even if we start in non-auth pages.
     *
     * @param request  The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String saveEntryParams(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        HttpSession session = request.getSession();

        // save entry login parameters if we don't have a valid login object
        if (userLogin == null) {

            String username = request.getParameter("USERNAME");
            String password = request.getParameter("PASSWORD");

            if ((username != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
                username = username.toLowerCase();
            }
            if ((password != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.lowercase")))) {
                password = password.toLowerCase();
            }

            // save parameters into the session - so they can be used later, if needed
            if (username != null) session.setAttribute("USERNAME", username);
            if (password != null) session.setAttribute("PASSWORD", password);

        } else {
            // if the login object is valid, remove attributes
            session.removeAttribute("USERNAME");
            session.removeAttribute("PASSWORD");
        }

        return "success";
    }

    /**
     * An HTTP WebEvent handler that checks to see is a userLogin is logged in.
     * If not, the user is forwarded to the /login.jsp page.
     *
     * @param request  The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return
     */
    public static String checkLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        HttpSession session = request.getSession();

        // anonymous shoppers are not logged in
        if (userLogin != null && "anonymous".equals(userLogin.getString("userLoginId"))) {
            userLogin = null;
        }

        // user is logged in; check to see if there is an entry in the loggedInSessions Map, if not log out this user
        // also check if they have permission for this login attempt; if not log them out as well.
        if (userLogin != null) {
            String username = null;
            String password = null;

            username = (String) userLogin.get("userLoginId");
            password = (String) userLogin.get("currentPassword");

            if ((username == null || "".equalsIgnoreCase(username)) || (password == null || "".equalsIgnoreCase(password))) {
                return "error";
            }
        	/*
        	boolean loggedInSession = isLoggedInSession(userLogin, request);
            boolean hasBasePermission = hasBasePermission(userLogin, request);
            if (!loggedInSession || !hasBasePermission) {
                doBasicLogout(userLogin, request);
                userLogin = null;
                // have to reget this because the old session object will be invalid
                session = request.getSession();
            }*/
        }

        String username = null;
        String password = null;

        if (userLogin == null) {
            // check parameters
            if (username == null) username = request.getParameter("USERNAME");
            if (password == null) password = request.getParameter("PASSWORD");
            // check session attributes
            if (username == null) username = (String) session.getAttribute("USERNAME");
            if (password == null) password = (String) session.getAttribute("PASSWORD");

            if ((username != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
                username = username.toLowerCase();
            }
            if ((password != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.lowercase")))) {
                password = password.toLowerCase();
            }

            // in this condition log them in if not already; if not logged in or can't log in, save parameters and return error
            if ((username == null) || (password == null) || ("error".equals(login(request, response)))) {
                Map reqParams = UtilHttp.getParameterMap(request);
                String queryString = UtilHttp.urlEncodeArgs(reqParams);
                Debug.logInfo("reqParams Map: " + reqParams, module);
                Debug.logInfo("queryString: " + queryString, module);

                session.setAttribute("_PREVIOUS_REQUEST_", request.getPathInfo());
                if (queryString != null && queryString.length() > 0) {
                    session.setAttribute("_PREVIOUS_PARAMS_", queryString);
                }

                if (Debug.infoOn()) Debug.logInfo("checkLogin: queryString=" + queryString, module);
                if (Debug.infoOn()) Debug.logInfo("checkLogin: PathInfo=" + request.getPathInfo(), module);
                if ("/checkLogin".equalsIgnoreCase(request.getPathInfo())) {
                    return "error";
                } else {
                    Debug.logInfo("Session is invalid. checkLogin logout.", module);
                    logout(request, response);
                    return "checkerror";
                }
            }
        }

        return "success";
    }

    /**
     * An HTTP WebEvent handler that logs in a userLogin. This should run before the security check.
     *
     * @param request  The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return Return a boolean which specifies whether or not the calling Servlet or
     * JSP should generate its own content. This allows an event to override the default content.
     */
    public static String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Debug.logTiming("============start login", pLog);
        HttpSession session = request.getSession();
        session.removeAttribute(com.csmc.pms.webapp.util.SessionNames.CURRENT_EQP_ID_KEY);
        session.removeAttribute(com.csmc.pms.webapp.util.SessionNames.CURRENT_EQP_INFO_KEY);
        if ((request.getParameter("USERNAME") == null) || (request.getParameter("USERNAME") == null)) {
            request.setAttribute("_ERROR_MESSAGE_", MiscUtils.getResourceValue("login.user_password_missing"));
            return "error";
        }

        String username = request.getParameter("USERNAME").trim();
        String password = request.getParameter("PASSWORD").trim();

        if (username == null) username = (String) session.getAttribute("USERNAME");
        if (password == null) password = (String) session.getAttribute("PASSWORD");

        // 域账号登陆
        String checkFlag = "SELECT FLAG_VALUE FROM CHECK_FLAG WHERE FLAG_NAME = 'LOGIN_LDAP'";
        List checkFlagList = SQLProcess.excuteSQLQuery(checkFlag, delegator);
        if(checkFlagList.size() > 0){
            HashMap flagValueMap = (HashMap) checkFlagList.get(0);
            String flagValue = (String)flagValueMap.get("FLAG_VALUE");
            if("Y".equals(flagValue)){
                String ctx1 = loginLdap(username, password);
                if (ctx1 != "success") {
                    throw new Exception(ctx1);
                }
                String sql2 = "SELECT U.HRID FROM BPMU_USER@OA U WHERE U.ACCOUNT = '" + username + "'";
                List num2 = SQLProcess.excuteSQLQuery(sql2, delegator);
                if (num2.size() == 0) {
                    throw new Exception("该域账号无对应的工号,请确认!");
                } else {
                    HashMap stoReqMap = (HashMap) num2.get(0);
                    String userNameStr = (String)stoReqMap.get("HRID");
                    session.setAttribute("USERNAME", userNameStr);
                    username = userNameStr;
                }
            }
        }else {
            String ctx1 = loginLdap(username, password);
            if (ctx1 != "success") {
                throw new Exception(ctx1);
            }
            String sql2 = "SELECT U.HRID FROM BPMU_USER@OA U WHERE U.ACCOUNT = '" + username + "'";
            List num2 = SQLProcess.excuteSQLQuery(sql2, delegator);
            if (num2.size() == 0) {
                throw new Exception("该域账号无对应的工号,请确认!");
            } else {
                HashMap stoReqMap = (HashMap) num2.get(0);
                String userNameStr = (String)stoReqMap.get("HRID");
                session.setAttribute("USERNAME", userNameStr);
                username = userNameStr;
            }
        }

        // get the visit id to pass to the userLogin for history
        String visitId = VisitHandler.getVisitId(session);

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map result = null;

        try {
            result = dispatcher.runSync("csmcUserLogin", UtilMisc.toMap("username", username, "password", password, "visitId", visitId));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error call csmcUserLogin service", module);
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "<b>The following error occurred during login:</b><br>" + e.getMessage());
            return "error";
        }

        if (ModelService.RESPOND_SUCCESS.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
            //userLogin中保存着用户的基本信息
            GenericValue userLogin = (GenericValue) result.get("userLogin");
            String menuString = (String) result.get("menuString");
            request.getSession().setAttribute("menuString", menuString);
            request.getSession().setAttribute("userLogin", userLogin);
            request.getSession().setAttribute(SessionNames.GUI_PRIV_LIST_KEY, result.get("guipriv"));
            request.getSession().setAttribute(SessionNames.ACCOUNT_GROUP_SET_KEY, result.get("accountgroupset"));
            request.getSession().setAttribute(SessionNames.ACCOUNT_CATEGORY_SET_KEY, result.get("accountcategoryset"));
        } else {
            String errMsg = (String) result.get(ModelService.ERROR_MESSAGE);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        request.setAttribute("_LOGIN_PASSED_", "TRUE");
        request.getSession().setAttribute("csmc.user.locale", Locale.CHINESE);
        // make sure the autoUserLogin is set to the same and that the client cookie has the correct userLoginId
        Debug.logTiming("============end login", pLog);
        String msg = autoLoginSet(request, response);
        if ("success".equalsIgnoreCase(msg)) {
            session.setAttribute("loginFlag", "T");
        }
        Constants.onlineSessions.put(session.getId(), createNewOnlineUserMap(request, username));
        return msg;
    }

    /**
     * 只要不抛出异常就是验证通过
     * 返回的上下文可以查询Ad域的用户信息
     *
     * @param username 用户名称，cn,ou,dc 分别：用户，组，域; 例如：CN=lufei,ou=深圳分公司,ou=开发部,dc=ceshi,dc=com 或 lufei@ceshi.com
     * @param password 用户密码
     */
    public static String loginLdap(String username, String password) {
        DirContext ctx = null;
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple"); // LDAP访问安全级别(none,simple,strong);
        env.put(Context.SECURITY_PRINCIPAL, username + "@crmicro.com"); // AD的用户名
        env.put(Context.SECURITY_CREDENTIALS, password); // AD的密码
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); // LDAP工厂类
        env.put("com.sun.jndi.ldap.connect.timeout", "3000");// 连接超时设置为3秒
        env.put(Context.PROVIDER_URL, "LDAP://10.163.80.5/");// 默认端口389
        try {
            ctx = new InitialDirContext(env);// 初始化上下文
            return "success";
        } catch (AuthenticationException e) {
            return "身份验证失败!";
        } catch (javax.naming.CommunicationException e) {
            return "AD域连接失败!";
        } catch (Exception e) {
            return "身份验证未知异常!";
        } finally {
            if (null != ctx) {
                try {
                    ctx.close();
                    ctx = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map createNewOnlineUserMap(HttpServletRequest request, String userLoginId) {
        Map map = new Hashtable();
        map.put("userLoginId", userLoginId);
        try {
            map.put("createTime", new Long(request.getSession().getCreationTime()));
            map.put("loginTime", new Date(System.currentTimeMillis()));
            map.put("clientAddr", request.getRemoteAddr());
            map.put("clientHost", request.getRemoteHost());
            if (request.getRemoteUser() != null) {
                map.put("clientUser", request.getRemoteUser());
            }
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;

    }

    public static void doBasicLogin(GenericValue userLogin, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userLogin", userLogin);
        // let the visit know who the user is
        VisitHandler.setUserLogin(session, userLogin, false);

        loginToSession(userLogin, request);
    }

    /**
     * An HTTP WebEvent handler that logs out a userLogin by clearing the session.
     *
     * @param request  The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return Return a boolean which specifies whether or not the calling Servlet or
     * JSP should generate its own content. This allows an event to override the default content.
     */
    public static String logout(HttpServletRequest request, HttpServletResponse response) {
        // invalidate the security group list cache
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        // log out from all other sessions too; do this here so that it is only done when a user explicitly logs out
        logoutFromAllSessions(userLogin);

        doBasicLogout(userLogin, request);

        if (request.getAttribute("_AUTO_LOGIN_LOGOUT_") == null) {
            return autoLoginCheck(request, response);
        }
        return "success";
    }

    public static void doBasicLogout(GenericValue userLogin, HttpServletRequest request) {
        HttpSession session = request.getSession();

        Security security = (Security) request.getAttribute("security");

        if (security != null && userLogin != null) {
            Security.userLoginSecurityGroupByUserLoginId.remove(userLogin.getString("userLoginId"));
        }

        // this is a setting we don't want to lose, although it would be good to have a more general solution here...
        String currCatalog = (String) session.getAttribute("CURRENT_CATALOG_ID");
        // also make sure the delegatorName is preserved, especially so that a new Visit can be created
        String delegatorName = (String) session.getAttribute("delegatorName");
        // also save the shopping cart if we have one
        // DON'T save the cart, causes too many problems: security issues with things done in cart to easy to miss, especially bad on public systems; was put in here because of the "not me" link for auto-login stuff, but that is a small problem compared to what it causes
        //ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("shoppingCart");
        Constants.onlineSessions.remove(session.getId());
        session.invalidate();

        session = request.getSession(true);
        if (currCatalog != null) session.setAttribute("CURRENT_CATALOG_ID", currCatalog);
        if (delegatorName != null) session.setAttribute("delegatorName", delegatorName);
        // DON'T save the cart, causes too many problems: if (shoppingCart != null) session.setAttribute("shoppingCart", new WebShoppingCart(shoppingCart, session));
    }

    /**
     * The user forgot his/her password.  This will either call showPasswordHint or emailPassword.
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String forgotPassword(HttpServletRequest request, HttpServletResponse response) {
        if ((UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT"))) || (UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT.x")))) {
            return showPasswordHint(request, response);
        } else {
            return emailPassword(request, response);
        }
    }

    /**
     * Show the password hint for the userLoginId specified in the request object.
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String showPasswordHint(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        String userLoginId = request.getParameter("USERNAME");

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            request.setAttribute("_ERROR_MESSAGE_", "<li>The Username was empty, please re-enter.");
            return "error";
        }

        GenericValue supposedUserLogin = null;

        try {
            supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException gee) {
            Debug.logWarning(gee, "", module);
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            request.setAttribute("_ERROR_MESSAGE_", "<li>The Username was not found, please re-enter.");
            return "error";
        }

        String passwordHint = supposedUserLogin.getString("passwordHint");

        if (!UtilValidate.isNotEmpty(passwordHint)) {
            // the Username was not found
            request.setAttribute("_ERROR_MESSAGE_", "<li>No password hint was specified, try having the password emailed instead.");
            return "error";
        }

        request.setAttribute("_EVENT_MESSAGE_", "The Password Hint is: " + passwordHint);
        return "success";
    }

    /**
     * Email the password for the userLoginId specified in the request object.
     *
     * @param request  The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String emailPassword(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);

        Map subjectData = new HashMap();
        subjectData.put("productStoreId", productStoreId);

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = request.getParameter("USERNAME");
        subjectData.put("userLoginId", userLoginId);

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            request.setAttribute("_ERROR_MESSAGE_", "<li>The Username was empty, please re-enter.");
            return "error";
        }

        GenericValue supposedUserLogin = null;
        String passwordToSend = null;

        try {
            supposedUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
            if (supposedUserLogin == null) {
                // the Username was not found
                request.setAttribute("_ERROR_MESSAGE_", "<li>The Username was not found, please re-enter.");
                return "error";
            }
            if (useEncryption) {
                // password encrypted, can't send, generate new password and email to user
                double randNum = Math.random();

                // multiply by 100,000 to usually make a 5 digit number
                passwordToSend = "auto" + ((long) (randNum * 100000));
                //supposedUserLogin.set("currentPassword", HashEncrypt.getHash(passwordToSend));
                supposedUserLogin.set("passwordHint", "Auto-Generated Password");
            } else {
                passwordToSend = supposedUserLogin.getString("currentPassword");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Error accessing password: " + e.toString());
            return "error";
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            request.setAttribute("_ERROR_MESSAGE_", "<li>A user with the username \"" + userLoginId + "\" was not found, please re-enter.");
            return "error";
        }

        StringBuffer emails = new StringBuffer();
        GenericValue party = null;

        try {
            party = supposedUserLogin.getRelatedOne("Party");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            party = null;
        }
        if (party != null) {
            Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = (GenericValue) emailIter.next();
                emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
            }
        }

        if (!UtilValidate.isNotEmpty(emails.toString())) {
            // the Username was not found
            request.setAttribute("_ERROR_MESSAGE_", "<li>No Primary Email Address has been set, please contact customer service.");
            return "error";
        }

        // get the ProductStore email settings
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", "PRDS_PWD_RETRIEVE"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }

        if (productStoreEmail == null) {
            request.setAttribute("_ERROR_MESSAGE_", "<li>Problems with configuration; please contact customer service.");
            return "error";
        }

        // need OFBIZ_HOME for processing
        String ofbizHome = System.getProperty("ofbiz.home");

        // set the needed variables in new context
        Map templateData = new HashMap();
        templateData.put("useEncryption", new Boolean(useEncryption));
        templateData.put("password", UtilFormatOut.checkNull(passwordToSend));

        // prepare the parsed subject
        String subjectString = productStoreEmail.getString("subject");
        subjectString = FlexibleStringExpander.expandString(subjectString, subjectData);

        Map serviceContext = new HashMap();
        serviceContext.put("templateName", ofbizHome + productStoreEmail.get("templatePath"));
        serviceContext.put("templateData", templateData);
        serviceContext.put("subject", subjectString);
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("sendBcc", productStoreEmail.get("ccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", emails.toString());

        try {
            Map result = dispatcher.runSync("sendGenericNotificationEmail", serviceContext);

            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                request.setAttribute("_ERROR_MESSAGE_", "Error occurred: unable to email password.  Please try again later or contact customer service. (error was: " + result.get(ModelService.ERROR_MESSAGE) + ")");
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e, "", module);
            request.setAttribute("_ERROR_MESSAGE_", "Error occurred: unable to email password.  Please try again later or contact customer service.");
            return "error";
        }

        // don't save password until after it has been sent
        if (useEncryption) {
            try {
                supposedUserLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "", module);
                request.setAttribute("_ERROR_MESSAGE_", "<li>Error saving new password, the email that you receive will not have the correct password in it, your old password is still being used: " + e.toString());
                return "error";
            }
        }

        if (useEncryption) {
            request.setAttribute("_EVENT_MESSAGE_", "A new password has been created and sent to you.  Please check your Email.");
        } else {
            request.setAttribute("_EVENT_MESSAGE_", "Your password has been sent to you.  Please check your Email.");
        }
        return "success";
    }

    protected static String getAutoLoginCookieName(HttpServletRequest request) {
        return UtilHttp.getApplicationName(request) + ".autoUserLoginId";
    }

    public static String getAutoUserLoginId(HttpServletRequest request) {
        String autoUserLoginId = null;
        Cookie[] cookies = request.getCookies();
        if (Debug.verboseOn()) Debug.logVerbose("Cookies:" + cookies, module);
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(getAutoLoginCookieName(request))) {
                    autoUserLoginId = cookies[i].getValue();
                    break;
                }
            }
        }
        return autoUserLoginId;
    }

    public static String autoLoginCheck(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();

        return autoLoginCheck(delegator, session, getAutoUserLoginId(request));
    }

    private static String autoLoginCheck(GenericDelegator delegator, HttpSession session, String autoUserLoginId) {
        if (autoUserLoginId != null) {
            Debug.logInfo("Running autoLogin check.", module);
            try {
                GenericValue autoUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", autoUserLoginId));
                GenericValue person = null;
                GenericValue group = null;
                if (autoUserLogin != null) {
                    person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", autoUserLogin.getString("partyId")));
                    group = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", autoUserLogin.getString("partyId")));
                    session.setAttribute("autoUserLogin", autoUserLogin);
                }
                if (person != null) {
                    session.setAttribute("autoName", person.getString("firstName") + " " + person.getString("lastName"));
                } else if (group != null) {
                    session.setAttribute("autoName", group.getString("groupName"));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get autoUserLogin information: " + e.getMessage(), module);
            }
        }

        return "success";
    }

    public static String autoLoginSet(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Cookie autoLoginCookie = new Cookie(getAutoLoginCookieName(request), userLogin.getString("userLoginId"));
        autoLoginCookie.setMaxAge(60 * 60 * 24 * 365);
        autoLoginCookie.setPath("/");
        response.addCookie(autoLoginCookie);
        return autoLoginCheck(delegator, session, userLogin.getString("userLoginId"));
    }

    public static String autoLoginRemove(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("autoUserLogin");

        // remove the cookie
        if (userLogin != null) {
            Cookie autoLoginCookie = new Cookie(getAutoLoginCookieName(request), userLogin.getString("userLoginId"));
            autoLoginCookie.setMaxAge(0);
            autoLoginCookie.setPath("/");
            response.addCookie(autoLoginCookie);
        }
        // remove the session attributes
        session.removeAttribute("autoUserLogin");
        session.removeAttribute("autoName");
        // logout the user if logged in.
        if (session.getAttribute("userLogin") != null) {
            request.setAttribute("_AUTO_LOGIN_LOGOUT_", new Boolean(true));
            return logout(request, response);
        }
        return "success";
    }

    /**
     * Gets (and creates if necessary) a key to be used for an external login parameter
     */
    public static String getExternalLoginKey(HttpServletRequest request) {
        Debug.logInfo("Running getExternalLoginKey, externalLoginKeys.size=" + externalLoginKeys.size(), module);
        GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");

        String externalKey = (String) request.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
        if (externalKey != null) return externalKey;

        HttpSession session = request.getSession();
        synchronized (session) {
            // if the session has a previous key in place, remove it from the master list
            String sesExtKey = (String) session.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
            if (sesExtKey != null) {
                externalLoginKeys.remove(sesExtKey);
            }

            //check the userLogin here, after the old session setting is set so that it will always be cleared
            if (userLogin == null) return "";

            //no key made yet for this request, create one
            while (externalKey == null || externalLoginKeys.containsKey(externalKey)) {
                externalKey = "EL" + Long.toString(Math.round(Math.random() * 1000000)) + Long.toString(Math.round(Math.random() * 1000000));
            }

            request.setAttribute(EXTERNAL_LOGIN_KEY_ATTR, externalKey);
            session.setAttribute(EXTERNAL_LOGIN_KEY_ATTR, externalKey);
            externalLoginKeys.put(externalKey, userLogin);
            return externalKey;
        }
    }

    public static String checkExternalLoginKey(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        String externalKey = request.getParameter(EXTERNAL_LOGIN_KEY_ATTR);
        if (externalKey == null) return "success";

        GenericValue userLogin = (GenericValue) externalLoginKeys.get(externalKey);
        if (userLogin != null) {
            // found userLogin, do the external login...

            // if the user is already logged in and the login is different, logout the other user
            GenericValue currentUserLogin = (GenericValue) session.getAttribute("userLogin");
            if (currentUserLogin != null) {
                if (currentUserLogin.getString("userLoginId").equals(userLogin.getString("userLoginId"))) {
                    // is the same user, just carry on...
                    return "success";
                }

                // logout the current user and login the new user...
                String logoutRetVal = logout(request, response);
                // ignore the return value; even if the operation failed we want to set the new UserLogin
            }

            if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "login.lock.active"))) {
                String username = userLogin.getString("userLoginId");
                boolean userIdLoggedIn = isLoggedInSession(username, request, false);
                boolean thisUserLoggedIn = isLoggedInSession(username, request, true);
                if (userIdLoggedIn && !thisUserLoggedIn) {
                    request.setAttribute("_ERROR_MESSAGE_", "<b>This user is already logged in.</b><br>");
                    return "error";
                }
            }

            doBasicLogin(userLogin, request);
        } else {
            Debug.logWarning("Could not find userLogin for external login key: " + externalKey, module);
        }

        return "success";
    }

    public static void cleanupExternalLoginKey(HttpSession session) {
        String sesExtKey = (String) session.getAttribute(EXTERNAL_LOGIN_KEY_ATTR);
        if (sesExtKey != null) {
            externalLoginKeys.remove(sesExtKey);
        }
    }

    public static boolean isLoggedInSession(GenericValue userLogin, HttpServletRequest request) {
        return isLoggedInSession(userLogin.getString("userLoginId"), request, true);
    }

    public static boolean isLoggedInSession(String userLoginId, HttpServletRequest request, boolean checkSessionId) {
        if (userLoginId != null) {
            Map webappMap = (Map) loggedInSessions.get(userLoginId);
            if (webappMap == null) {
                return false;
            } else {
                String sessionId = (String) webappMap.get(UtilHttp.getApplicationName(request));
                if (!checkSessionId) {
                    if (sessionId == null) {
                        return false;
                    }
                } else {
                    if (sessionId == null || !sessionId.equals(request.getSession().getId())) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static void loginToSession(GenericValue userLogin, HttpServletRequest request) {
        if (userLogin != null) {
            Map webappMap = (Map) loggedInSessions.get(userLogin.get("userLoginId"));
            if (webappMap == null) {
                webappMap = new HashMap();
                loggedInSessions.put(userLogin.get("userLoginId"), webappMap);
            }

            String webappName = UtilHttp.getApplicationName(request);
            webappMap.put(webappName, request.getSession().getId());
        }
    }

    public static void logoutFromAllSessions(GenericValue userLogin) {
        if (userLogin != null) {
            loggedInSessions.remove(userLogin.get("userLoginId"));
        }
    }

    protected static boolean hasBasePermission(GenericValue userLogin, HttpServletRequest request) {
        ServletContext context = (ServletContext) request.getAttribute("servletContext");
        Security security = (Security) request.getAttribute("security");
        HttpSession session = request.getSession();

        String serverId = (String) context.getAttribute("_serverId");
        String contextPath = request.getContextPath();

        ComponentConfig.WebappInfo info = ComponentConfig.getWebAppInfo(serverId, contextPath);
        if (security != null) {
            if (info != null) {
                String permission = info.getBasePermission();
                if (!"NONE".equals(permission) && !security.hasEntityPermission(permission, "_VIEW", userLogin)) {
                    return false;
                }
            } else {
                Debug.logInfo("No webapp configuration found for : " + serverId + " / " + contextPath, module);
            }
        } else {
            Debug.logWarning("Received a null Security object from HttpServletRequest", module);
        }

        return true;

    }
}
