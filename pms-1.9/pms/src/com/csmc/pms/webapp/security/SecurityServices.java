/*
 * ��Ȩ���ϓP������Ϻ������޹�˾����
 * ���������ϓP�����˽�л�Ҫ����
 * δ������˾��Ȩ�����÷Ƿ������͵���
 * ���ڱ���˾��Ȩ��Χ�ڣ�ʹ�ñ�����
 * ��������Ȩ��
 */
package com.csmc.pms.webapp.security;


import java.util.*;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.eqp.helper.FabAdapter;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.TPServiceException;
import com.fa.exception.TPClientException;
import com.fa.object.ObjectCollection;
import com.fa.object.TPCommand;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * �� SecurityServices.java
 * ����û���Ӧ��Ȩ��
 *
 * @author TONY
 * @version 1.0  2004-8-10
 */
public class SecurityServices {
    public static final String pLog = "com.fa.util.pLog";
    public static final String module = SecurityServices.class.getName();

    public static Map checkGUIPriv(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        Map result = new HashMap();
        List guiPrivList = new ArrayList();
        try {

            Set privsSet = new HashSet();
            Set categorySet = new HashSet();
            Set groupSet = new HashSet();
            GenericValue userLogin = (GenericValue) context.get("userlogin");

            //check password
            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", userLogin.getString("userLoginId")));
            if (account == null) {
                throw new Exception("PMS�˺Ż�δ����������ϵ����ִ�п�PMS����Ա��");
            }

            //isCheckLogin���Ҫ��PMSϵͳ�н��б���У��
            if (Constants.CALL_TP_FLAG && !"Y".equals(account.getString("isCheckLogin"))) {
                // ��PROMIS�н��м���
                Map checkMap = loginCheck(userLogin.getString("userLoginId"), userLogin.getString("currentPassword"), delegator, userLogin);
                if ("N".equalsIgnoreCase((String) checkMap.get("checkResult"))) {
                    throw new Exception("���벻��: " + (String) checkMap.get("failedReason"));
                }

                // ����û���������
                Map accountMap = getAccountGroup((String) userLogin.get("userLoginId"), delegator, userLogin);
                Collection group = (Collection) accountMap.get("grouplist");
                Collection categoryColl = (Collection) accountMap.get("catglist");

                if (categoryColl != null) {
                    categorySet.addAll(categoryColl);
                }

                if (group != null) {
                    groupSet = getAccountAllGroup(group, groupSet, categorySet, delegator, userLogin);
                }

            } else {
//				if (Constants.CALL_ASURA_FLAG && !"Y".equals(account.getString("isCheckLogin"))) {
                if (Constants.CALL_ASURA_FLAG && "Y".equals(account.getString("isCheckLogin"))) {
/*					// ����Ϊ��,��֤MES
					if (StringUtils.isEmpty(account.getString("password"))){
						String sha256Str = CommonUtil.Sha256(userLogin.getString("currentPassword"));
						String sql1 = "select * from FWUSERPROFILE@SMC WHERE USERNAME = '" + userLogin.getString("userLoginId") + "'";
						List num1 = SQLProcess.excuteSQLQuery(sql1,delegator);
						if (num1.size()>0){
							String sql2 = "select * from FWUSERPROFILE@SMC WHERE USERNAME = '" + userLogin.getString("userLoginId") + "' and password =' "+ sha256Str +"'";
							List num2 = SQLProcess.excuteSQLQuery(sql2,delegator);
							if (num2.size()>0){
								throw new Exception("MES���벻��ȷ��");
							}
						}else {
							throw new Exception("MES�����ڸ��û���");
						}

					}
					else{
						Map checkMap = loginCheck(userLogin.getString("userLoginId"), userLogin.getString("currentPassword"), delegator, userLogin);
						if ("N".equalsIgnoreCase((String)checkMap.get("checkResult"))) {
							throw new Exception("���벻��: " + (String) checkMap.get("failedReason"));
						}
					}*/
//                    Map checkMap = loginCheck(userLogin.getString("userLoginId"), userLogin.getString("currentPassword"), delegator, userLogin);
//                    if ("N".equalsIgnoreCase((String) checkMap.get("checkResult"))) {
//                        throw new Exception("���벻��: " + (String) checkMap.get("failedReason"));
//                    }
                }
//                else if (!userLogin.getString("currentPassword").equalsIgnoreCase(account.getString("password"))) {
//                    // fab1 isCheckLogin = Y, У�鱾������
//                    // fab5 isCheckLogin = N, У�鱾������
//                    throw new Exception("�������벻�ԣ�");
//                }
                // �õ�Local��½���û���
                String roles = account.getString("roles");
                if (roles != null) {
                    String[] roleArr = roles.split(",");
                    for (int i = 0; i < roleArr.length; i++) {
                        String role = roleArr[i];
                        groupSet.add(role);
                        categorySet.add(role);
                    }
                }

            }

            //�õ��û���Ȩ��
            for (Iterator it = groupSet.iterator(); it.hasNext(); ) {
                String role = (String) it.next();
                List rolePrivs = delegator.findByAnd("AccountPrivs", UtilMisc.toMap("accountId", role.toUpperCase()));
                Iterator itorRolePrivs = rolePrivs.iterator();
                while (itorRolePrivs.hasNext()) {
                    GenericValue priv = (GenericValue) itorRolePrivs.next();
                    privsSet.add(priv.getString("localPriv"));
                }
            }

            //���ݵ�½�ߵõ�Ȩ��
            List accountPrivs = delegator.findByAnd("AccountPrivs", UtilMisc.toMap("accountId", ((String) userLogin.get("userLoginId")).toUpperCase()));
            Iterator itorAccountPrivs = accountPrivs.iterator();
            while (itorAccountPrivs.hasNext()) {
                GenericValue priv = (GenericValue) itorAccountPrivs.next();
                privsSet.add(priv.getString("localPriv"));
            }
            guiPrivList.addAll(privsSet);

            //init log
            //Log.tpInfo("init", module);
            //return
            result.put("guipriv", guiPrivList);
            result.put("accountgroupset", groupSet);
            result.put("accountcategoryset", categorySet);
        } catch (Exception e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return result;
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * ֻҪ���׳��쳣������֤ͨ��
     * ���ص������Ŀ��Բ�ѯAd����û���Ϣ
     *
     * @param username �û����ƣ�cn,ou,dc �ֱ��û����飬��; ���磺CN=lufei,ou=���ڷֹ�˾,ou=������,dc=ceshi,dc=com �� lufei@ceshi.com
     * @param password �û�����
     */
    public static DirContext login(String username, String password) {
        DirContext ctx = null;
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple"); // LDAP���ʰ�ȫ����(none,simple,strong);
        env.put(Context.SECURITY_PRINCIPAL, "jiangmeiyuan@crmicro.com"); // AD���û���
        env.put(Context.SECURITY_CREDENTIALS, "Rxgz@202304"); // AD������
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory"); // LDAP������
        env.put("com.sun.jndi.ldap.connect.timeout", "3000");// ���ӳ�ʱ����Ϊ3��
        env.put(Context.PROVIDER_URL, "LDAP://10.163.80.5/");// Ĭ�϶˿�389
        try {
            ctx = new InitialDirContext(env);// ��ʼ��������
            System.out.println("�����֤�ɹ�!");
            return ctx;
        } catch (AuthenticationException e) {
            System.out.println("�����֤ʧ��!");
            e.printStackTrace();
            return ctx;
        } catch (javax.naming.CommunicationException e) {
            System.out.println("AD������ʧ��!");
            e.printStackTrace();
            return ctx;
        } catch (Exception e) {
            System.out.println("�����֤δ֪�쳣!");
            e.printStackTrace();
            return ctx;
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

    // У���û��Ƿ�ΪPromis�û�
    public static Map loginCheck(String user, String pwd,
                                 GenericDelegator delegator, GenericValue userLogin) {
        Map result = new HashMap();

        try {
            Map userMap = new HashMap();
            userMap.put("user", user);
            userMap.put("pwd", pwd);
            // ����TP
            Map ret = FabAdapter.runCallService(delegator, userLogin,
                    userMap, Constants.LOGIN_CHECK);
        } catch (Exception tpe) {
            result.put("checkResult", "N");
            result.put("failedReason", tpe.getMessage());
            return result;
        }

        result.put("checkResult", "Y");
        return result;
    }

    /**
     * ����PROMISȨ�޵õ�GUI��Ȩ��
     *
     * @param ctx     The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map checkGUIPrivBackUp(DispatchContext ctx, Map context) {
        Debug.logTiming("============start checkGUIPriv", pLog);
        GenericDelegator delegator = ctx.getDelegator();
        Map result = new HashMap();
        List promisList = null;
        List resultList = new ArrayList();
        Map promisMap = new HashMap();

        try {
            promisMap = queryPromisPriv(ctx, context);
            if (ModelService.RESPOND_ERROR.equals(promisMap.get(ModelService.RESPONSE_MESSAGE))) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, promisMap.get(ModelService.ERROR_MESSAGE));
                return result;
            }
            //���û�ӵ�е�PROMISȨ�ޣ���ÿ��GUI��Ӧ��Ȩ�޽��бȽϣ��õ��û�ӵ�е�GUIȨ��
            promisList = (ArrayList) promisMap.get("promislist");
            Map guiPromisMap = Constants.GUI_PROMIS_PRIV;
            Map guiPrivmap = (HashMap) guiPromisMap.get("guiprivmap");
            List promisLocalPrivList = (List) guiPromisMap.get("promisLocalPrivs");

            Iterator iterator = guiPrivmap.keySet().iterator();
            String guiPriv = "";
            List promisPrivList = new ArrayList();
            while (iterator.hasNext()) {
                guiPriv = (String) iterator.next();
                promisPrivList = (List) guiPrivmap.get(guiPriv);
                if (promisList.containsAll(promisPrivList)) {
                    resultList.add(guiPriv);
                }
            }

            GenericValue userLogin = (GenericValue) context.get("userlogin");

            //����û���������
            Map accountMap = getAccountGroup((String) userLogin.get("userLoginId"), delegator, userLogin);
            Collection group = (Collection) accountMap.get("grouplist");
            Collection categoryColl = (Collection) accountMap.get("catglist");

            Set categorySet = new HashSet();
            if (categoryColl != null) {
                categorySet.addAll(categoryColl);
            }
            Set groupSet = new HashSet();


            if (group != null) {
                groupSet = getAccountAllGroup(group, groupSet, categorySet, delegator, userLogin);
            }


            //ȥ��Local��Ȩ��
            resultList.removeAll(promisLocalPrivList);

            Set resultSet = new HashSet();

            //�����ݿ���ȡ��gui���Զ����Ȩ�ޣ���Promis���壩
            GenericDelegator pmDelegator = delegator;
            List guiNotPromis = pmDelegator.findByAnd("AccountPrivs", UtilMisc.toMap("accountId", ((String) userLogin.get("userLoginId")).toUpperCase()));
            Iterator itorGuiNotPromis = guiNotPromis.iterator();

            //��Promis�ж����Ȩ�޺�gui�ж����Ȩ�޺ϲ�
            while (itorGuiNotPromis.hasNext()) {
                GenericValue guiNotPromisValue = (GenericValue) itorGuiNotPromis.next();
                resultSet.add((String) guiNotPromisValue.get("localPriv"));
            }

            //ȡ���ú��������LocalȨ��
            Iterator itorGroupSet = groupSet.iterator();
            while (itorGroupSet.hasNext()) {
                String groupString = (String) itorGroupSet.next();
                guiNotPromis = pmDelegator.findByAnd("AccountPrivs", UtilMisc.toMap("accountId", groupString));
                itorGuiNotPromis = guiNotPromis.iterator();
                while (itorGuiNotPromis.hasNext()) {
                    GenericValue guiNotPromisValue = (GenericValue) itorGuiNotPromis.next();
                    resultSet.add((String) guiNotPromisValue.get("localPriv"));
                }
            }

            resultList.addAll(resultSet);

            result.put("guipriv", resultList);
            result.put("accountgroupset", groupSet);
            result.put("accountcategoryset", categorySet);
        } catch (Exception e) {
            Debug.logError(e, "Error in checkGUIPriv service", module);
            e.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return result;
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        Debug.logTiming("============end checkGUIPriv", pLog);
        return result;
    }

    /**
     * ��PROMIS�м����û���������Ϳ��õ�Ȩ��
     *
     * @param ctx     The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    private static Map queryPromisPriv(DispatchContext ctx, Map context) {
        List promisList = new ArrayList();
        TPCommand tpComm = null;
        ObjectCollection oColl = new ObjectCollection();
        HashMap result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userlogin");
        Map map = new HashMap();
        Map guiPromisMap = Constants.GUI_PROMIS_PRIV;
        if (ModelService.RESPOND_ERROR.equals(guiPromisMap.get(ModelService.RESPONSE_MESSAGE))) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "");
            return result;
        }

        //ȡ��PROMISȨ���б�������ÿ��Ȩ�ޱ��ֵ�TPCommand�����С�
        Iterator iterator = ((List) guiPromisMap.get("promisprivlist")).iterator();
        while (iterator.hasNext()) {
            String promisPriv = (String) iterator.next();
            tpComm = new TPCommand();
            tpComm.setName(promisPriv);
            oColl.add(tpComm);
        }
        map.put("commandlist", oColl);

        try {
            //��PROMIS�н��м��飬�����õ�Ȩ�޴���promisList��
            Map callMap = FabAdapter.runCallService(delegator, userLogin, map, Constants.LOGIN_CHECK_PRIV);
            TPClientException tpClientExc = (TPClientException) callMap.get("TP_ERROR");
            if (tpClientExc != null) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, tpClientExc.getMessage());
                return result;
            }

            iterator = ((List) guiPromisMap.get("promisprivlist")).iterator();
            while (iterator.hasNext()) {
                String promisPriv = (String) iterator.next();
                String promisStatus = (String) callMap.get(promisPriv);
                if ("T".equalsIgnoreCase(promisStatus)) {
                    promisList.add(promisPriv);
                }
            }
            result.put("promislist", promisList);
        } catch (Exception e) {
            Debug.logError(e, "Error in queryPromisPriv service", module);
            e.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return result;
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * �����ݿ��в�ѯGUI��PROMISȨ�޵Ĺ�ϵ����Map����ʽ���ؽ����
     * ����а�������
     * guiprivmap��Ӧһ��Map�������Map�а���ÿ��GUIȨ�޶�Ӧ��PROMISȨ�ޣ�
     * promisprivlist��Ӧһ��List�������List�а��������е�PROMISȨ�ޡ�
     *
     * @param ctx     The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map queryAllPrivsMap(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();

        EntityListIterator entityListItor = null;
        GenericValue gValue = null;
        String guiPriv = "";
        String promisPriv = "";
        String mainPrivFlag = "";
        String promisLocalPriv = "";

        Map guiMap = new HashMap();
        Map guiFlagMap = new HashMap();
        List promisPrivList = new ArrayList();
        List promisLocalPrivList = new ArrayList();
        List promisPrivFalgList = new ArrayList();

        try {
            //��ѯPrivsMap��ȡ��guiPriv��promisPriv������������
            entityListItor = delegator.findListIteratorByCondition("PrivsMap", null, null, UtilMisc.toList("guiPriv", "promisPriv", "mainPrivFlag"), UtilMisc.toList("guiPriv"), new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
            while (entityListItor.hasNext()) {
                gValue = (GenericValue) entityListItor.next();
                //��guiPriv�Ĳ�Ϊ�ղ����뵱ǰ��gValue�е�guiPriv����ȣ��������guiMap��
                if ((!"".equalsIgnoreCase(guiPriv)) && (!guiPriv.equalsIgnoreCase(gValue.getString("guiPriv")))) {
                    guiMap.put(guiPriv, promisPrivList);
                    guiFlagMap.put(guiPriv, promisPrivFalgList);
                    promisPrivList = new ArrayList();
                    promisPrivFalgList = new ArrayList();
                }
                guiPriv = gValue.getString("guiPriv");
                promisPriv = gValue.getString("promisPriv");
                mainPrivFlag = gValue.getString("mainPrivFlag");

                promisPrivList.add(promisPriv);
                promisPrivFalgList.add(mainPrivFlag + promisPriv);
            }
            //������һ��GUIȨ�޴���Map��
            guiMap.put(guiPriv, promisPrivList);
            guiFlagMap.put(guiPriv, promisPrivFalgList);

            promisPrivList = new ArrayList();
            result.put("guiprivmap", guiMap);
            result.put("guiprivflagmap", guiFlagMap);

            entityListItor.close();

            //��ѯGUI��ʹ�õ�����PROMISȨ��
            entityListItor = delegator.findListIteratorByCondition("PrivsMap", null, null, UtilMisc.toList("promisPriv"), null, new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
            while (entityListItor.hasNext()) {
                gValue = (GenericValue) entityListItor.next();
                promisPriv = gValue.getString("promisPriv");
                promisPrivList.add(promisPriv);
            }
            result.put("promisprivlist", promisPrivList);
            entityListItor.close();

            //��ѯGUI��ʹ�õ�����Local PROMISȨ��
            entityListItor = delegator.findListIteratorByCondition("PrivsMap", new EntityExpr("privType", EntityOperator.EQUALS, "LOCAL"), null, UtilMisc.toList("guiPriv"), null, new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
            while (entityListItor.hasNext()) {
                gValue = (GenericValue) entityListItor.next();
                promisLocalPriv = gValue.getString("guiPriv");
                promisLocalPrivList.add(promisLocalPriv);
            }
            result.put("promisLocalPrivs", promisLocalPrivList);
            entityListItor.close();

        } catch (Exception e) {
            Debug.logError(e, "Error in queryAllPrivsMap service", module);
            e.printStackTrace();
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "");
            return result;
        } finally {
            try {
                if (entityListItor != null) {
                    entityListItor.close();
                }
            } catch (Exception e) {
                Debug.logError(e, "Error in close entityListItor service", module);
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "");
            }
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    public static Map getAccountGroup(String accountId,
                                      GenericDelegator delegator, GenericValue userLogin)
            throws TPServiceException {
        //����TP������User��Ϣ
        Map result = null;

        result = FabAdapter.runCallService(
                delegator, userLogin,
                UtilMisc.toMap("accountid", accountId),
                Constants.ACCOUNT_INFO_QUERY);

        return result;
    }

    public static Set getAccountAllGroup(Collection groupList,
                                         Set groupSet, Set categorySet, GenericDelegator delegator, GenericValue userLogin)
            throws TPServiceException {

        groupSet.addAll(groupList);
        for (Iterator it = groupList.iterator(); it.hasNext(); ) {
            String groupName = (String) it.next();
            Map result = getAccountGroup(groupName, delegator, userLogin);
            List groups = (List) result.get("grouplist");
            List category = (List) result.get("catglist");
            if (category != null) {
                categorySet.addAll(category);
            }
            if (groups != null) {
                getAccountAllGroup(groups, groupSet, categorySet, delegator, userLogin);
            } else {
                continue;
            }
        }
        return groupSet;
    }

    public static List getGuiNotPromisPriv(DispatchContext ctx, GenericValue userLogin) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericDelegator pmDelegator = delegator;
        List guiNotPromis = null;
        try {
            guiNotPromis = pmDelegator.findByAnd("AccountPrivs", UtilMisc.toMap("accountId", (String) userLogin.get("userLoginId")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return guiNotPromis;
    }


}
