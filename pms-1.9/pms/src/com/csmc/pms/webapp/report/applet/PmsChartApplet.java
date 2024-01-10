package com.csmc.pms.webapp.report.applet;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

abstract public class PmsChartApplet extends JApplet {
	private static final String AGENT_SERVLET_PATH = "AgentServletPath";	//page request param name
	
	private String _AgentServletPath = null;
	
	protected String getAgentServletPath() {
        return _AgentServletPath;
    }
	
	protected void setAgentServletPath(String agentServletPath) {
        _AgentServletPath = agentServletPath;
    }
	
	//set _AgentServletPath value
	protected void initParameter() {
		String protocol = getCodeBase().getProtocol();
		String host = getCodeBase().getHost();
		int port = getCodeBase().getPort();

		String path = this.getParameter(AGENT_SERVLET_PATH);

		setAgentServletPath(protocol + "://" + host + ":" + port + path);
	}
	
	protected List getPointList(String servletUrl) {
		ObjectInputStream is = null;
		is = callServlet(servletUrl);			//get points info
		
		List pointList = null;
	    try {        	
	    	pointList =  (List)is.readObject();
	        is.close();
	    } catch (Exception e) {               
			JOptionPane.showMessageDialog(null, "ERR-Chart-003:\nAppletConfigServlet transfer (Object) error!\nPlease ask for Administrator!", "Alert", JOptionPane.WARNING_MESSAGE);
	        throw new RuntimeException("ERR-Chart-003");
	    }
	    
	    if (pointList == null){
	        JOptionPane.showMessageDialog(null, "ERR-Chart-004:\n The Applet Config file is not found or invalid format!\nPlease ask for Administrator!", "Alert", JOptionPane.WARNING_MESSAGE);
	        throw new RuntimeException("ERR-Chart-004");
	    }
	    
	    return pointList;
	}

	protected ObjectInputStream callServlet(String su) {
		URL url = null;
		System.out.println("Getting servlet...url: " + su);
		
		try {
			//encode special charset and Chinese
			su = su.replaceAll(" ", "%20");
			StringBuffer tmpSu = new StringBuffer("");
			for (int i = 0; i < su.length(); i++) {
			   String s = su.substring(i, i + 1);
			   if (s.length() == s.getBytes().length) {
				   tmpSu.append(s);
			   } else {
				   tmpSu.append(URLEncoder.encode(s, "utf-8"));
			   }
			}
			su = tmpSu.toString();
			
			url = new URL(su);

		} catch (UnsupportedEncodingException e) {
			JOptionPane
			.showMessageDialog(
					null,
					"convert gbk error",
					"Alert", JOptionPane.WARNING_MESSAGE);
			throw new RuntimeException("CharsetConvert-Exception");
		} catch (MalformedURLException me) {
			JOptionPane
					.showMessageDialog(
							null,
							"ERR-Chart-001:\n The servlet url error!\nPlease ask for Administrator!",
							"Alert", JOptionPane.WARNING_MESSAGE);
			throw new RuntimeException("ERR-Chart-001");
		}
		
		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(url.openStream());
		} catch (IOException ioe) {
			JOptionPane
					.showMessageDialog(
							null,
							"ERR-Chart-002:\n The Servlet transfer (Stream) error!\nPlease ask for Administrator!",
							"Alert", JOptionPane.WARNING_MESSAGE);
			ioe.printStackTrace();
			throw new RuntimeException("ERR-Chart-002");
		}

		return is;
	}
	
	/**
     * set a LineChart.
     * 
     * @param categoryplot
     */
	protected void setCategoryLineChart(CategoryPlot categoryplot) {
		
		categoryplot.setBackgroundPaint(Color.lightGray);//�������񱳾�ɫ
	    categoryplot.setRangeGridlinePaint(Color.white);//�������������ɫ
	    
	    // X��(DomainAxis)ȡ�ü�����
	    CategoryAxis categoryaxis = categoryplot.getDomainAxis();
	    categoryaxis.setTickLabelFont(new Font("Dilog", 0, 12));
	    categoryaxis.setTickLabelPaint(Color.BLACK);
	    // ���ñ�־��ʾ�Ƕ�, createUpRotationLabelRosition,�����Ǵ��µ�������
	    categoryaxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(Math.PI / 4.0));
	    // �������ִ�����������
	    //categoryaxis.setCategoryLabelPositions(CategoryLabelPositions
	    //		.createDownRotationLabelPositions(Math.PI / 2.0));
	    // �������ֽǶ�����
	    //categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
	    // �����������ֿ�����������׼��ȵı���,����ʹ��3������,Ӧ�����ܱ�֤10�����ֵ�������ʾ.
	    categoryaxis.setMaximumCategoryLabelWidthRatio(3F);
	    
	    // Y��(RangeAxis)ȡ�ü�����
	    NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
	    numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	    numberaxis.setAutoRangeIncludesZero(false);
	    numberaxis.setAutoRangeMinimumSize(10);
	    numberaxis.setTickLabelPaint(Color.BLACK);
	    numberaxis.setTickLabelFont(new Font("Dilog", 0, 12));
	    
	    // renderͼ�εĻ��Ƶ�Ԫ����ͼ��,��������
	    LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
	    lineandshaperenderer.setBaseShapesVisible(true);//���������Ƿ���ʾ���ݵ�
	    lineandshaperenderer.setBaseShapesFilled(true);        
	    //lineandshaperenderer.setSeriesShapesVisible(0, true);
	    lineandshaperenderer.setDrawOutlines(true);
	    lineandshaperenderer.setUseFillPaint(true);
	    lineandshaperenderer.setBaseFillPaint(Color.white);//�������ݵ����ɫ
	}
}
