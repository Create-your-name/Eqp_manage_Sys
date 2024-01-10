package com.csmc.pms.webapp.report.applet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.annotations.CategoryLineAnnotation;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class PmFormChart extends PmsChartApplet {

	private static final long serialVersionUID = -989611043191381274L;
	
	private static final String PM_TIME = "PM_TIME";
	private static final String START_TIME = "START_TIME";
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
			if (null != dataMap.get(PM_TIME)) {
				dataset.addValue(Double.parseDouble(dataMap.get(PM_TIME).toString()), seriesName, dataMap.get(START_TIME).toString());
			}
		}

        return dataset; 
    }
    
	/**
     * Creates a chart.
     * 
     * @param dataList  the data for the chart.
     * @param seriesName
     * @param standardHour
     * 
     * @return a chart.
     */
    private JFreeChart createChart(List dataList, String seriesName, double standardHour) {
        //Creates a Dataset
    	CategoryDataset dataset = createDataset(dataList, seriesName);
    	
    	JFreeChart jfreechart = ChartFactory.createLineChart("设备保养用时图", "开始日期", "PM Time(小时)", dataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.setBackgroundPaint(Color.white);
        
        //得到图形的绘制结构plot
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        
        //设置chart样式
        setCategoryLineChart(categoryplot);
        
        String tmpEqpId = "";
        //添加点的注释eqpid
        for (int i=0;i<dataList.size();i++){
			Map dataMap = (HashMap) dataList.get(i);
			 
			if (null != dataMap.get(PM_TIME) && !tmpEqpId.equals((String) dataMap.get(EQUIPMENT_ID))) {
				CategoryPointerAnnotation categoryPointerAnnotation = new CategoryPointerAnnotation(
						dataMap.get(EQUIPMENT_ID).toString(), 
						dataMap.get(START_TIME).toString(),
						Double.parseDouble(dataMap.get(PM_TIME).toString()),
						Math.PI/4);
				//categoryPointerAnnotation.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
				categoryplot.addAnnotation(categoryPointerAnnotation);				
			}
			
			tmpEqpId = (String) dataMap.get(EQUIPMENT_ID);
		}
        
        // 添加标准工时参考线
        List categorieList = jfreechart.getCategoryPlot().getCategories();
        if (categorieList.size() > 0) {
        	if (0 != standardHour) {
        		CategoryAnnotation annotation = new CategoryLineAnnotation(
			        		"0",
			        		standardHour,
							categorieList.get(categorieList.size() - 1).toString(),
							standardHour,
							Color.BLACK,
							new BasicStroke(1.0f));
        		categoryplot.addAnnotation(annotation);
        	}
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
    			+ "&periodIndex=" + this.getParameter("periodIndex")
    			+ "&equipmentDept=" + this.getParameter("equipmentDept");
    	//su="http://localhost:8080/csmcgui/control/getPmFormItemPoints?startDate=2007-05-06&endDate=2007-10-20&equipmentType=PSEM&eqpId=&periodIndex=10080&equipmentDept=";

        List dataList = getPointList(su);
        
    	String seriesName = PmsChartAppletUtil.checkEmpty(this.getParameter("periodName"),"periodName");
    	double standardHour = Double.parseDouble(PmsChartAppletUtil.checkEmpty(this.getParameter("standardHour"), "0"));
    	
        JFreeChart chart = createChart(dataList, seriesName, standardHour);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));

        getContentPane().add(chartPanel);
    }   
}