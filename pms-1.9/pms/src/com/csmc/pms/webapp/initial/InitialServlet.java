/** 	1.0  2004-8-10
  * 版权归上揚软件（上海）有限公司所有
  * 本程序属上揚软件的私有机要资料
  * 未经本公司授权，不得非法传播和盗用
  * 可在本公司授权范围内，使用本程序
  * 保留所有权利
  */
package com.csmc.pms.webapp.initial;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
/**
    *类 InitialServlet.java
    *用来在数据库启动时加载一些必要的数据
    *@version  1.0  2004-8-10
    *@author   TONY
    */
public class InitialServlet extends HttpServlet {

	public static final String module = InitialServlet.class.getName();

    public InitialServlet() {
    	super();
    }

    //初始化函数
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (Debug.infoOn()) {
            Debug.logInfo("[ControlServlet.init] Loading Control Servlet mounted on path " + config.getServletContext().getRealPath("/"), module);
        }

        // 初始化Priv Map
        //initPrivsMap(config);

    	//FAB1调用GUI数据库，首次初始执行连接加快第二次调用时的速度
        if (Constants.CALL_TP_FLAG) CommonUtil.getGuiDelegator();

        //FAB5初始化asura接口
        if (Constants.CALL_ASURA_FLAG) initAsuraListen();

        System.getProperties().remove("ofbiz");
    }

    //FAB5初始化asura接口
    private void initAsuraListen() {
//		List handlers = new ArrayList();
//		AsuraHandler tsHandler = new TriggerTsHandler();
//		handlers.add(tsHandler);
//
//		try {
//	        Asura asura = new AsuraClient();
//			asura.pmListen(handlers);
//
//			Debug.logInfo("[pms.init] Start Pms Asura Listener Thread finished", module);
//		} catch (AsuraException e) {
//			e.printStackTrace();
//			Debug.logError(e, module);
//		}
	}

    //初始化Priv Map
    public void initPrivsMap(ServletConfig config) {
    	Debug.logInfo("[InitialServlet] : Start Load All Priv", module);
        //GenericDelegator delegator = null;
        LocalDispatcher dispatcher = (LocalDispatcher)config.getServletContext().getAttribute("dispatcher");
//		printProperties();
        if (dispatcher == null) {
            Debug.logError("[InitialServlet] ERROR: dispatcher not found in ServletContext", module);
        }
    	try {
    		Map pMap = dispatcher.runSync("queryAllPrivsMap", new HashMap());
    		Constants.GUI_PROMIS_PRIV.clear();
    		Constants.GUI_PROMIS_PRIV.putAll(pMap);
    		//TODO  add by sky
    		/*
			Collection promisLocalPrivs = new ArrayList();
			promisLocalPrivs.add("BY_LOCATION");
			promisLocalPrivs.add("BY_CAPAGROUP");
			promisLocalPrivs.add("VIEW_LOCATION");

			Constants.GUI_PROMIS_PRIV.put("promisLocalPrivs",promisLocalPrivs);*/
    	} catch (GenericServiceException e) {
    		e.printStackTrace();
    	}
    }

	public void printProperties(){
		Properties props = System.getProperties();
		for(Iterator it = props.keySet().iterator();it.hasNext();){
			Object pro = it.next();
			System.out.println("==============" + pro +":"+props.get(pro));
		}
	}
}
