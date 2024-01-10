/** 	1.0  2004-8-10
  * ��Ȩ���ϓP������Ϻ������޹�˾����
  * ���������ϓP�����˽�л�Ҫ����
  * δ������˾��Ȩ�����÷Ƿ������͵���
  * ���ڱ���˾��Ȩ��Χ�ڣ�ʹ�ñ�����
  * ��������Ȩ��
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
    *�� InitialServlet.java
    *���������ݿ�����ʱ����һЩ��Ҫ������
    *@version  1.0  2004-8-10
    *@author   TONY
    */
public class InitialServlet extends HttpServlet {

	public static final String module = InitialServlet.class.getName();

    public InitialServlet() {
    	super();
    }

    //��ʼ������
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (Debug.infoOn()) {
            Debug.logInfo("[ControlServlet.init] Loading Control Servlet mounted on path " + config.getServletContext().getRealPath("/"), module);
        }

        // ��ʼ��Priv Map
        //initPrivsMap(config);

    	//FAB1����GUI���ݿ⣬�״γ�ʼִ�����Ӽӿ�ڶ��ε���ʱ���ٶ�
        if (Constants.CALL_TP_FLAG) CommonUtil.getGuiDelegator();

        //FAB5��ʼ��asura�ӿ�
        if (Constants.CALL_ASURA_FLAG) initAsuraListen();

        System.getProperties().remove("ofbiz");
    }

    //FAB5��ʼ��asura�ӿ�
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

    //��ʼ��Priv Map
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
