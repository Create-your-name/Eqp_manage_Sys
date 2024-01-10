package com.csmc.pms.webapp.report.applet;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class EqpParamChart extends PmsChartApplet {

	private static final long serialVersionUID = 6455293250439372451L;
	
	private static final String VALUE = "VALUE";
	private static final String UPDATE_TIME = "UPDATE_TIME";
	private static final String EQUIPMENT_ID = "EQUIPMENT_ID";

	/**
     * Creates a Dataset.
     * 
     * @param dataList  the data for the chart.
     * @param seriesName

     * @return CategoryDataset.
     */
	private CategoryDataset createDataset(List dataList, String seriesName) {       
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i=0;i<dataList.size();i++){
			Map dataMap = (HashMap) dataList.get(i);
			//column keys...
			if (null != dataMap.get(VALUE)) {
				dataset.addValue(Double.parseDouble(dataMap.get(VALUE).toString()), seriesName, dataMap.get(UPDATE_TIME).toString());
			}
		}

        return dataset; 
    }
    
	/**
     * Creates a chart.
     * 
     * @param dataList  the data for the chart.
     * @param seriesName
     * @param lowerSpec
     * @param upperSpec
     * 
     * @return a chart.
     */
    private JFreeChart createChart(List dataList, String seriesName) {
        //Creates a Dataset
    	CategoryDataset dataset = createDataset(dataList, seriesName);
    	
    	JFreeChart jfreechart = ChartFactory.createLineChart("机台参数图", "日期", "参数值", dataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.setBackgroundPaint(Color.white);
        
        //得到图形的绘制结构plot
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        
        //设置chart样式
        setCategoryLineChart(categoryplot);
        
        String tmpEqpId = "";
        //添加点的注释eqpid
        for (int i=0;i<dataList.size();i++){
			Map dataMap = (HashMap) dataList.get(i);
			 
			if (null != dataMap.get(VALUE) && !tmpEqpId.equals((String) dataMap.get(EQUIPMENT_ID))) {
				CategoryPointerAnnotation categoryPointerAnnotation = new CategoryPointerAnnotation(
						dataMap.get(EQUIPMENT_ID).toString(), 
						dataMap.get(UPDATE_TIME).toString(),
						Double.parseDouble(dataMap.get(VALUE).toString()),
						Math.PI/4);
				//categoryPointerAnnotation.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
				categoryplot.addAnnotation(categoryPointerAnnotation);				
			}
			
			tmpEqpId = (String) dataMap.get(EQUIPMENT_ID);
		}

        return jfreechart;
	}
    
    public void init() {
    	
    	initParameter();    	

    	String su = getAgentServletPath();
    	su = su + "?startDate="	+ this.getParameter("startDate")
    			+ "&endDate=" + this.getParameter("endDate")
    			+ "&equipmentType=" + this.getParameter("equipmentType")
    			+ "&eqpId="+ this.getParameter("eqpId")    			
    			+ "&paramName=" + this.getParameter("paramName");
    	//su="http://localhost:8080/csmcgui/control/getEqpParamPoints?startDate=2007-05-06&endDate=2007-10-20&equipmentType=PSEM&eqpId=&paramName=";

        List dataList = getPointList(su);
        
    	String seriesName = PmsChartAppletUtil.checkEmpty(this.getParameter("equipmentType"),"equipmentType");
      	
        JFreeChart chart = createChart(dataList, seriesName);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));

        getContentPane().add(chartPanel);
    }   
}