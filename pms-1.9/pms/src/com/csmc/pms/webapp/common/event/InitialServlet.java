package com.csmc.pms.webapp.common.event;
/*
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.ofbiz.base.util.Debug;
import com.csmc.asura.core.Asura;
import com.csmc.asura.core.AsuraClient;
import com.csmc.asura.core.AsuraHandler;
import com.csmc.asura.exception.AsuraException;
import com.csmc.pms.webapp.form.model.TriggerTsHandler;

public class InitialServlet extends HttpServlet {
	public static final String module = InitialServlet.class.getName();

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		initAsuraListen();
	}

	public void initAsuraListen() {
		List handlers = new ArrayList();
		
		AsuraHandler tsHandler = new TriggerTsHandler();
		handlers.add(tsHandler);
		
		try {
	        Asura asura = new AsuraClient();
			asura.pmListen(handlers);
			
			Debug.logInfo("[pms.init] Start PmsListener Thread finished", module);
		} catch (AsuraException e) {
			e.printStackTrace();
		} 
	}
}
*/