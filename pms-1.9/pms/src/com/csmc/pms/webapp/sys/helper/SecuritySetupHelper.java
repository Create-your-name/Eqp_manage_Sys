/** 
  * 版权归上揚软件（上海）有限公司所有
  * 本程序属上揚软件的私有机要资料
  * 未经本公司授权，不得非法传播和盗用
  * 可在本公司授权范围内，使用本程序
  * 保留所有权利
  */
package com.csmc.pms.webapp.sys.helper;

import java.util.*;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.eqp.helper.FabAdapter;
import com.csmc.pms.webapp.util.*;
import com.fa.object.*;

import org.ofbiz.base.util.*;
/**
   *类 SecuritySetupHelper.java Security操作辅助类
   *@version  1.0  2004-8-9
   *@author   Sky
   */

public class SecuritySetupHelper {
	public static final String module = SecuritySetupHelper.class.getName();
	/**
	 *根据Account Id 通过TP取得Account的信息，并把Priv变为String的Set，以privSet为Key存入resultMap中。
	 *@param accountId user or usergroup id
	 *@return Map 有如下Key：accountid,privlist,grouplist,privSet,flaglist
	*/
	public static Map getAccountInfo(
		String accountId,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		//调用TP，返回User信息
		Map result = null;
		try {
			result =
				FabAdapter.runCallService(
					delegator,
					userLogin,
					UtilMisc.toMap("accountid", accountId),
					Constants.ACCOUNT_INFO_QUERY);
		} catch (Exception e) {
			return null;
		}
		Debug.logInfo("result map is " + result, "User Setup Info");
		result.put("description", result.get("owner"));
		List userPrivCommandList = (List) result.get("privlist");
		Set accountPrivSet = new HashSet();
		if (userPrivCommandList != null) {
			for (int i = 0; i < userPrivCommandList.size(); i++) {
				TPCommand tc = (TPCommand) userPrivCommandList.get(i);
				accountPrivSet.add(tc.getName());
			}
		}
		result.put("privSet", accountPrivSet);
		//		System.out.println("test1");

		return result;
	}

	/**
	 *根据Group Name通过TP取得Group中所属的Group的信息，并循环取得所有的promis priv。
	 *@param Collection groupList
	 *@return Set 所有的prive set，其中个体单元为String类型
	*/
	private static Set getAllGroupPromisPriv(
		Collection groupList,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException, SecuritySetupHelperException {
		Set groupPriv = new HashSet();
		while (groupList != null && groupList.size() > 0) {
			Set groupSet = new HashSet();
			for (Iterator it = groupList.iterator(); it.hasNext();) {
				String groupName = (String) it.next();
				Map accountInfo =
					getAccountInfo(groupName, delegator, userLogin);
				if (accountInfo.get("flaglist") != null
					&& ((Collection) accountInfo.get("flaglist")).contains(
						"AUTH_SUPERUSER")) {
					throw new SecuritySetupHelperException();
				}
				Set privSet = (Set) accountInfo.get("privSet");
				groupPriv.addAll(privSet);
				List groups = (List) accountInfo.get("grouplist");
				if (groups != null) {
					groupSet.addAll(groups);
				}

			}
			groupList = groupSet;
		}
		return groupPriv;
	}
	/**
	 *根据Group Name取得promis priv，并转换成GUI Priv，同时加入local priv
	 *@param Collection groupList
	 *@return Set 所有的prive set，其中个体单元为String类型
	*/
	public static Set getAllGroupGuiPriv(
		Collection groupList,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException, SecuritySetupHelperException {
		Set groupPromisPriv =getAllGroupPromisPriv(groupList, delegator, userLogin);
		Set guiPriv = convertPromisPrivToGuiPriv(groupPromisPriv);
		Set localPriv = getAllGroupLocalPriv(groupList, delegator, userLogin);
		guiPriv.addAll(localPriv);
		return guiPriv;
	}
	/**
	 *根据Group Name取得local priv
	 *@param Collection groupList
	 *@return Set 所有的local prive set，其中个体单元为String类型
	*/
	public static Set getAllGroupLocalPriv(
		Collection groupList,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		Set groupPriv = new HashSet();
		while (groupList != null && groupList.size() > 0) {
			Set groupSet = new HashSet();
			for (Iterator it = groupList.iterator(); it.hasNext();) {
				String groupName = (String) it.next();
				Map result = getAccountInfo(groupName, delegator, userLogin);
				List localPrivList = getLocalPriv(groupName, delegator);
				groupPriv.addAll(localPrivList);
				List groups = (List) result.get("grouplist");
				if (groups != null) {
					groupSet.addAll(groups);
				}

			}
			groupList = groupSet;
		}
		return groupPriv;  
	}
	/**
	 *取得Group列表
	 *@param Collection groupList
	 *@return List 所有的Group 名字，其中个体单元为String类型
	*/
	public static List getAllGroupNames(
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		return getAccountNameList("G", delegator, userLogin);
	}
	/**
	 *取得User列表
	 *@param Collection groupList
	 *@return List 所有的User 名字，其中个体单元为String类型
	*/
	public static List getAllUserNames(
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		return getAccountNameList("U", delegator, userLogin);
	}

	/**
	 *更新user信息
	 *@param userName user id
	 *@param description 
	 *@param groupList 用户选择的group
	 *@param privList 用户选择的promis priv list
	 *@return Map 返回的结果
	*/
	public static Map updateAccountInfo(
		String accountId,
		String accounttype,
		String description,
		Collection groupList,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		Map map = new HashMap();
		map.put("accountid", accountId);
		map.put("owner", description);

		try {
			Map userInfo = getAccountInfo(accountId, delegator, userLogin);
			if(userInfo == null){
				map.put("accounttype", accounttype);
				map.put("grouplist", new ObjectCollection());			
			
			}else{
				map.put(
					"grouplist",
					splitCollection(
						(Collection) userInfo.get("grouplist"),
						groupList));
			}

		} catch (Exception e) {
			e.printStackTrace();
			map.put("accounttype", accounttype);
			map.put("grouplist", splitCollection(null, groupList));
		}
		Map result =
			FabAdapter.runCallService(
				delegator,
				userLogin,
				map,
				Constants.ACCOUNT_UPDATE);
		return result;
	}
	
	public static Map updateAccountPriv(
		String accountId,
		String accounttype,
		Collection newPrivList,Collection oldPrivList,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		Map map = new HashMap();
		Map userInfo = null;
		map.put("accountid", accountId);

		map.put("grouplist", new ObjectCollection());	
		try {
			userInfo = getAccountInfo(accountId, delegator, userLogin);
		
			if (isSuperUser(accountId, delegator, userLogin)) {
				map.put("privlist", new ArrayList());
			} else {
				try {
					map.put(
						"privlist",
						splitPrivCollection(
					oldPrivList,
					newPrivList));
				} catch (RuntimeException e1) {
					e1.printStackTrace();

				}    

			}

		} catch (Exception e) {
			Debug.logError(e,module);

		}
		map.put(
			"privlist",
			converPrivListToTpCommandList((Collection) map.get("privlist")));
		Map result =
			FabAdapter.runCallService(
				delegator,
				userLogin,
				map,
				Constants.ACCOUNT_UPDATE);
		return result;
	}	
	private static ObjectCollection converPrivListToTpCommandList(Collection privList) {
		if (privList == null) {
			return null;
		}
		ObjectCollection tpList = new ObjectCollection();
		for (Iterator it = privList.iterator(); it.hasNext();) {
			String priv = (String) it.next();
			TPCommand tc = new TPCommand();
			tc.setName(priv);
			tpList.add(tc);
		}
		return tpList;

	}

	private static Collection splitPrivCollection(
		Collection oldCollection,
		Collection newCollection) {
		
		Collection add = new ObjectCollection();
		Collection remove = new ObjectCollection();
		
		if (oldCollection == null) {
			if (newCollection == null) {
				return null;
			} else {
				add = newCollection;   
			}
		} else {
			if (newCollection == null) {   
				remove = oldCollection;
			} else {
				Set set = new HashSet();
				try {
					set.addAll(oldCollection);
					set.addAll(newCollection);
					for (Iterator it = set.iterator(); it.hasNext();) {
						String o = (String) it.next();

						if (o != null) {
							//如果新的集合中含有该元素，则加入
							if (newCollection.contains(o)
								&& oldCollection.contains(o)) {
								//oc.add(o);
								//如果新的集合中没有包含，而老的集合中有，则意味着这一项需要除去
							} else if (oldCollection.contains(o)) {
								//如果是减权限，则只把p开头的减掉
								remove.add(o);
							} else if (newCollection.contains(o)) {
								//如果是加权限，则加上所有的				
								add.add(o);
							}
						}  
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Collection addPromisPirv = getPromisPrivFlagSet(add,false);
		Collection removePromisPriv =getPromisPrivFlagSet(remove,true);
		
		return buildPrivCollection(addPromisPirv,removePromisPriv);

	}
	
	private static Set buildPrivCollection(Collection addPriv,Collection removePriv){
		Set privSet = new HashSet();
		

		for(Iterator it = addPriv.iterator();it.hasNext();){
				String guiPriv = (String) it.next();
			privSet.add(guiPriv.substring(1));	
		}
		for(Iterator it = removePriv.iterator();it.hasNext();){
				String guiPriv = (String) it.next();
				if(privSet.contains(guiPriv)){
					privSet.remove(guiPriv);
				}
				privSet.add("-" + guiPriv.substring(1));
		}
		return privSet;
		
	}	
	private static Set getPromisPrivFlagSet(Collection guiPrivs,boolean wantPrimary){
		Map promisToguiMap = Constants.GUI_PROMIS_PRIV;
		Set promisPrivs = new HashSet();
		Map guiPrivFlagMap = (Map) promisToguiMap.get("guiprivflagmap");
		for(Iterator it = guiPrivs.iterator();it.hasNext();){
				String guiPriv = (String) it.next();
				List promisPrivList = (List) guiPrivFlagMap.get(guiPriv);
				if (promisPrivList != null) {
					promisPrivs.addAll(promisPrivList);

				} 
		
		}
		if(wantPrimary){
			Set primaryPrivs = new HashSet();
			for(Iterator it = promisPrivs.iterator();it.hasNext();){
				String priv = (String)it.next();
				if(priv.startsWith("P")){
					primaryPrivs.add(priv);
				}
			}
			return primaryPrivs;
		}else{
			return promisPrivs;
		}
		
	
	}

	private static ObjectCollection splitCollection(
		Collection oldCollection,
		Collection newCollection) {

		ObjectCollection oc = new ObjectCollection();
		if (oldCollection == null && newCollection == null)
			return oc;
		if (oldCollection == null) {
			for (Iterator it = newCollection.iterator(); it.hasNext();) {
				String o = (String) it.next();
				oc.add(o);
			}
			return oc;
		} else if (newCollection == null) {
			for (Iterator it = oldCollection.iterator(); it.hasNext();) {
				String o = (String) it.next();
				oc.add("-" + o);
			}
		} else {
			Set set = new HashSet();
			set.addAll(oldCollection);
			set.addAll(newCollection);
			for (Iterator it = set.iterator(); it.hasNext();) {
				String o = (String) it.next();
				//如果新的集合中含有该元素，则加入
				if (newCollection.contains(o) && oldCollection.contains(o)) {
					//oc.add(o);
					//如果新的集合中没有包含，而老的集合中有，则意味着这一项需要除去
				} else if (oldCollection.contains(o)) {

					oc.add("-" + o);
				} else if (newCollection.contains(o)) {
					oc.add(o);
				}
			}
		}
		return oc;

	}
	/**
	 *转换PromisPriv到GUI priv
	 *@param Collection promisPrivs
	 *@return Set  Guipriv
	*/
	public static Set convertPromisPrivToGuiPriv(Collection promisPrivs) {
		Set guiPrivSet = new HashSet();
		
		Map map = Constants.GUI_PROMIS_PRIV;
		Map promisToguiMap = (Map) map.get("guiprivmap");
		Collection promisLocalPrivs = (Collection) map.get("promisLocalPrivs");
		
		Iterator iterator = promisToguiMap.keySet().iterator();
		while (iterator.hasNext()) {
			String guiPriv = (String) iterator.next();
			List promisPrivList = (List) promisToguiMap.get(guiPriv);
			if (promisPrivs.containsAll(promisPrivList)
				&& !promisLocalPrivs.contains(guiPriv)) {
				guiPrivSet.add(guiPriv);
			}
		}
		
		return guiPrivSet;
	}
	/**
	 *转换Gui priv 到promis priv及local priv
	 *@param Collection guiPrivs
	 *@return Map  中包含两个key：promisPriv, localPriv
	*/
	public static Map splitGuiPriv(Collection guiPrivs) {
		Map promisToguiMap = Constants.GUI_PROMIS_PRIV;
		Set promisPriv = new HashSet();
		Set localPriv = new HashSet();
		Iterator iterator = guiPrivs.iterator();
		Map guiPrivFlagMap = (Map) promisToguiMap.get("guiprivflagmap");
		Collection promisLocalPrivs =
			(Collection) promisToguiMap.get("promisLocalPrivs");
		while (iterator.hasNext()) {
			String guiPriv = (String) iterator.next();

			List promisPrivList = (List) guiPrivFlagMap.get(guiPriv);
			if (promisPrivList != null) {
				promisPriv.add(guiPriv);
				if(promisLocalPrivs.contains(guiPriv)){
					localPriv.add(guiPriv);
				}
			} else {
				localPriv.add(guiPriv);
			}

		}
		Map map = new HashMap();
		map.put("promisPriv", promisPriv);
		map.put("localPriv", localPriv);
		return map;
	}

	private static List getAccountNameList(
		String type,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		//取出所有user列表，设置类型为“U”，以区别“G”
		Map attributes = new HashMap();
		attributes.put("rectype", type);
		//调用TP，取出User信息列表
		Map result =
			FabAdapter.runCallService(
				delegator,
				userLogin,
				attributes,
				Constants.ACCOUNT_LIST_QUERY);
		Debug.logInfo("result map is " + result, "User Setup Info");
		//结果以List形式存在
		List accounts = (List) result.get("accountlist");
		List accountnames = null;
		if (accounts != null) {
			accountnames = new ArrayList();
			for (int i = 0; i < accounts.size(); i++) {
				accountnames.add(((Account) accounts.get(i)).getAccountID());
			}
		}
		return accountnames;

	}

	public static List getAccountList(
		String type,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		//取出所有user列表，设置类型为“U”，以区别“G”
		Map attributes = new HashMap();
		attributes.put("rectype", type);
		//调用TP，取出User信息列表
		Map result =
			FabAdapter.runCallService(
				delegator,
				userLogin,
				attributes,
				Constants.ACCOUNT_LIST_QUERY);
		Debug.logInfo("result map is " + result, "User Setup Info");
		//结果以List形式存在
		List accounts = (List) result.get("accountlist");
		return accounts;

	}
	
	/**
	 *将传进来的group从所有group列表中去除后，得到可选择的group列表
	 *@param Collection groupList
	 *@return List 
	*/
	public static List getAvailableGroupList(
		HttpServletRequest request,
		GenericDelegator delegator,
		GenericValue userLogin,
		List groupList)
		throws TPServiceException {

		List availableGroupList;
		//从Session中取所有Group的集合
		List allGroupList =
			(List) request.getSession().getServletContext().getAttribute(
				"_$AllGroupList");
		if (allGroupList == null) {
			allGroupList = getAllGroupNames(delegator, userLogin);
			request.getSession().getServletContext().setAttribute(
				"_$AllGroupList",
				allGroupList);
		}
		if (groupList != null) {
			availableGroupList = new ArrayList();
			for (int i = 0; i < allGroupList.size(); i++) {
				if (!groupList.contains(allGroupList.get(i))) {
					availableGroupList.add(allGroupList.get(i));
				}
			}
		} else {
			availableGroupList = allGroupList;
		}
		return availableGroupList;
	}
	
	public static List getLocalPriv(
		String accountId,
		GenericDelegator delegator) {
		List guiNotPromis = new ArrayList();
		try {
			GenericDelegator pmDelegator = delegator;
			List localPromis =
				pmDelegator.findByAnd(
					"AccountPrivs",
					UtilMisc.toMap("accountId", accountId));
			for (int i = 0; i < localPromis.size(); i++) {
				GenericValue gv = (GenericValue) localPromis.get(i);
				guiNotPromis.add(gv.get("localPriv"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return guiNotPromis;
	}
	
	public static void updateLocalPriv(
		String accountId,
		Collection localPriv,Collection oldLocalPriv,
		GenericDelegator delegator)
		throws GenericEntityException {
		Collection addCollection = new ArrayList();
		Collection removeCollection = new ArrayList();
		ObjectCollection oc = new ObjectCollection();
		Set set = new HashSet();
		set.addAll(localPriv);
		if (oldLocalPriv != null) {
			set.addAll(oldLocalPriv);
			for (Iterator it = set.iterator(); it.hasNext();) {
				Object o = it.next();
				//如果新的集合中含有该元素，则加入
				if (localPriv.contains(o) && !oldLocalPriv.contains(o)) {
					addCollection.add(o);
					//如果新的集合中没有包含，而老的集合中有，则意味着这一项需要除去
				} else if (
					!localPriv.contains(o) && oldLocalPriv.contains(o)) {
					removeCollection.add(o);
				}
			}
		} else {
			addCollection.addAll(localPriv);
		}
		GenericDelegator pmDelegator = delegator;
		for (Iterator it = addCollection.iterator(); it.hasNext();) {
			Object o = it.next();
			pmDelegator.create(
				"AccountPrivs",
				UtilMisc.toMap("accountId", accountId, "localPriv", o));
		}
		for (Iterator it = removeCollection.iterator(); it.hasNext();) {
			Object o = it.next();
			pmDelegator.removeByAnd(
				"AccountPrivs",
				UtilMisc.toMap("accountId", accountId, "localPriv", o));

		}
	}



	/**
	 *根据accountId 取得所有的GUIPriv
	 *@param accountId 
	 *@return Map ,包含两个Key accountPriv,groupGuiPriv
	*/
	public static Map getAllGuiPriv(
		String accountId,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {

		Map accountInfo = getAccountInfo(accountId, delegator, userLogin);
		List groupList = (List) accountInfo.get("grouplist");

		if (isSuperUser(accountId, delegator, userLogin)) {
			return buildSuperUserPrivMap(
				accountId,
				delegator,
				userLogin,
				groupList);

		} else {
			Map privMap =
				buildNormalUserPrivMap(
					accountId,
					delegator,
					userLogin,
					accountInfo,
					groupList);
			return privMap;
		}

	}

	private static Map buildNormalUserPrivMap(
		String accountId,
		GenericDelegator delegator,
		GenericValue userLogin,
		Map accountInfo,
		List groupList)
		throws TPServiceException {
		Map privMap = new HashMap();
		Set accountGuiPriv = null;
		Set groupGuiPriv = null;
		Set userPromisPriv = (Set) accountInfo.get("privSet");
		accountGuiPriv = convertPromisPrivToGuiPriv(userPromisPriv);
		List localPriv = getLocalPriv(accountId, delegator);
		accountGuiPriv.addAll(localPriv);
		try {
			groupGuiPriv = getAllGroupGuiPriv(groupList, delegator, userLogin);
		} catch (SecuritySetupHelperException e) {
			return buildSuperUserPrivMap(
				accountId,
				delegator,
				userLogin,
				groupList);
		}
		accountGuiPriv.addAll(groupGuiPriv);
		privMap.put("accountPriv", accountGuiPriv);
		privMap.put("groupGuiPriv", groupGuiPriv);
		return privMap;
	}

	private static boolean isSuperUser(
		String accountId,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		Map accountInfo = getAccountInfo(accountId, delegator, userLogin);
		if (accountInfo.get("flaglist") != null
			&& ((Collection) accountInfo.get("flaglist")).contains(
				"AUTH_SUPERUSER")) {
			return true;
		} else {
			Collection groupList = (Collection) accountInfo.get("grouplist");
			while (groupList != null && groupList.size() > 0) {
				Set groupSet = new HashSet();
				for (Iterator it = groupList.iterator(); it.hasNext();) {
					String groupName = (String) it.next();
					accountInfo =
						getAccountInfo(groupName, delegator, userLogin);
					if (accountInfo.get("flaglist") != null
						&& ((Collection) accountInfo.get("flaglist")).contains(
							"AUTH_SUPERUSER")) {
						return true;
					}
					List groups = (List) accountInfo.get("grouplist");
					if (groups != null) {
						groupSet.addAll(groups);
					}

				}
				groupList = groupSet;
			}
		}
		return false;
	}

	private static Map buildSuperUserPrivMap(
		String accountId,
		GenericDelegator delegator,
		GenericValue userLogin,
		List groupList)
		throws TPServiceException {
		Map privMap = new HashMap();
		Set accountGuiPriv = null;
		Set groupGuiPriv = new HashSet();
		groupGuiPriv.addAll(getAllGuiPromisPriv());



		Set localPriv = getAllGroupLocalPriv(groupList, delegator, userLogin);
		if (localPriv != null) {
			groupGuiPriv.addAll(localPriv);
		}
		accountGuiPriv = new HashSet();
		accountGuiPriv.addAll(getLocalPriv(accountId, delegator));
		accountGuiPriv.addAll(groupGuiPriv);		
		privMap.put("accountPriv", accountGuiPriv);
		privMap.put("groupGuiPriv", groupGuiPriv);
		return privMap;
	}
	/**
	 *修改用户password
	 *@param accountId ,password
	 *@return void
	*/
	public static void changePassword(
		String accountId,
		String password,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		Map map = new HashMap();
		map.put("accountid", accountId);
		map.put("newpassword", password);
		Map result =
			FabAdapter.runCallService(
				delegator,
				userLogin,
				map,
				Constants.CHANGE_PASSWORD);
		return;
	}

	public static Set getAllGuiPromisPriv() {
		Map promisToguiMap = Constants.GUI_PROMIS_PRIV;
		return ((Map) promisToguiMap.get("guiprivmap")).keySet();
	}
	public static List getAllPromisPriv() {
		Map promisToguiMap = Constants.GUI_PROMIS_PRIV;
		return (List) promisToguiMap.get("promisprivlist");
	}

	public static void getAllOnlineUser(
		String accountId,
		String password,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		Map map = new HashMap();
		map.put("accountid", accountId);
		map.put("newpassword", password);
		Map result =
			FabAdapter.runCallService(
				delegator,
				userLogin,
				map,
				Constants.CHANGE_PASSWORD);
		return;
	}
}
