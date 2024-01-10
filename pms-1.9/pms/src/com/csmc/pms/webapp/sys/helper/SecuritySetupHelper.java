/** 
  * ��Ȩ���ϓP������Ϻ������޹�˾����
  * ���������ϓP�����˽�л�Ҫ����
  * δ������˾��Ȩ�����÷Ƿ������͵���
  * ���ڱ���˾��Ȩ��Χ�ڣ�ʹ�ñ�����
  * ��������Ȩ��
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
   *�� SecuritySetupHelper.java Security����������
   *@version  1.0  2004-8-9
   *@author   Sky
   */

public class SecuritySetupHelper {
	public static final String module = SecuritySetupHelper.class.getName();
	/**
	 *����Account Id ͨ��TPȡ��Account����Ϣ������Priv��ΪString��Set����privSetΪKey����resultMap�С�
	 *@param accountId user or usergroup id
	 *@return Map ������Key��accountid,privlist,grouplist,privSet,flaglist
	*/
	public static Map getAccountInfo(
		String accountId,
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		//����TP������User��Ϣ
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
	 *����Group Nameͨ��TPȡ��Group��������Group����Ϣ����ѭ��ȡ�����е�promis priv��
	 *@param Collection groupList
	 *@return Set ���е�prive set�����и��嵥ԪΪString����
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
	 *����Group Nameȡ��promis priv����ת����GUI Priv��ͬʱ����local priv
	 *@param Collection groupList
	 *@return Set ���е�prive set�����и��嵥ԪΪString����
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
	 *����Group Nameȡ��local priv
	 *@param Collection groupList
	 *@return Set ���е�local prive set�����и��嵥ԪΪString����
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
	 *ȡ��Group�б�
	 *@param Collection groupList
	 *@return List ���е�Group ���֣����и��嵥ԪΪString����
	*/
	public static List getAllGroupNames(
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		return getAccountNameList("G", delegator, userLogin);
	}
	/**
	 *ȡ��User�б�
	 *@param Collection groupList
	 *@return List ���е�User ���֣����и��嵥ԪΪString����
	*/
	public static List getAllUserNames(
		GenericDelegator delegator,
		GenericValue userLogin)
		throws TPServiceException {
		return getAccountNameList("U", delegator, userLogin);
	}

	/**
	 *����user��Ϣ
	 *@param userName user id
	 *@param description 
	 *@param groupList �û�ѡ���group
	 *@param privList �û�ѡ���promis priv list
	 *@return Map ���صĽ��
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
							//����µļ����к��и�Ԫ�أ������
							if (newCollection.contains(o)
								&& oldCollection.contains(o)) {
								//oc.add(o);
								//����µļ�����û�а��������ϵļ������У�����ζ����һ����Ҫ��ȥ
							} else if (oldCollection.contains(o)) {
								//����Ǽ�Ȩ�ޣ���ֻ��p��ͷ�ļ���
								remove.add(o);
							} else if (newCollection.contains(o)) {
								//����Ǽ�Ȩ�ޣ���������е�				
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
				//����µļ����к��и�Ԫ�أ������
				if (newCollection.contains(o) && oldCollection.contains(o)) {
					//oc.add(o);
					//����µļ�����û�а��������ϵļ������У�����ζ����һ����Ҫ��ȥ
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
	 *ת��PromisPriv��GUI priv
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
	 *ת��Gui priv ��promis priv��local priv
	 *@param Collection guiPrivs
	 *@return Map  �а�������key��promisPriv, localPriv
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
		//ȡ������user�б���������Ϊ��U����������G��
		Map attributes = new HashMap();
		attributes.put("rectype", type);
		//����TP��ȡ��User��Ϣ�б�
		Map result =
			FabAdapter.runCallService(
				delegator,
				userLogin,
				attributes,
				Constants.ACCOUNT_LIST_QUERY);
		Debug.logInfo("result map is " + result, "User Setup Info");
		//�����List��ʽ����
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
		//ȡ������user�б���������Ϊ��U����������G��
		Map attributes = new HashMap();
		attributes.put("rectype", type);
		//����TP��ȡ��User��Ϣ�б�
		Map result =
			FabAdapter.runCallService(
				delegator,
				userLogin,
				attributes,
				Constants.ACCOUNT_LIST_QUERY);
		Debug.logInfo("result map is " + result, "User Setup Info");
		//�����List��ʽ����
		List accounts = (List) result.get("accountlist");
		return accounts;

	}
	
	/**
	 *����������group������group�б���ȥ���󣬵õ���ѡ���group�б�
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
		//��Session��ȡ����Group�ļ���
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
				//����µļ����к��и�Ԫ�أ������
				if (localPriv.contains(o) && !oldLocalPriv.contains(o)) {
					addCollection.add(o);
					//����µļ�����û�а��������ϵļ������У�����ζ����һ����Ҫ��ȥ
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
	 *����accountId ȡ�����е�GUIPriv
	 *@param accountId 
	 *@return Map ,��������Key accountPriv,groupGuiPriv
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
	 *�޸��û�password
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
