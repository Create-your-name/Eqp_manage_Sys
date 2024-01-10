/*
 * 创建日期 2007-10-15
 *
 * @author dinghh
 * 已由PmsChartApplet代替此程序
 */
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
import org.jfree.ui.TextAnchor;

public class PmParamAnalysisChart extends PmsChartApplet {

	private static final long serialVersionUID = -813414337839549645L;
	
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
			Map dataMap = (HashMap)dataList.get(i);
			//column keys...
			dataset.addValue(Double.parseDouble(dataMap.get("ITEM_VALUE").toString()), seriesName, dataMap.get("UPDATE_TIME").toString());
					
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
    private JFreeChart createChart(List dataList, String seriesName, double lowerSpec, double upperSpec) {
        //Creates a Dataset
    	CategoryDataset dataset = createDataset(dataList, seriesName);
    	
    	JFreeChart jfreechart = ChartFactory.createLineChart("机台保养纪录参数分析图", "日期", "参数值", dataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.setBackgroundPaint(Color.white);
        
        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
        
        // 设置chart样式
        setCategoryLineChart(categoryplot);
        
        //添加点的注释eqpid
        for (int i=0;i<dataList.size();i++){
			Map dataMap = (HashMap) dataList.get(i);
			
			CategoryPointerAnnotation categoryPointerAnnotation = new CategoryPointerAnnotation(
					dataMap.get("EQUIPMENT_ID").toString(), 
					dataMap.get("UPDATE_TIME").toString(),
					Double.parseDouble(dataMap.get("ITEM_VALUE").toString()),
					-2.3561944901923448D);
			categoryPointerAnnotation.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
			
			categoryplot.addAnnotation(categoryPointerAnnotation);		
		}
        
        //添加规范线
        List categorieList = jfreechart.getCategoryPlot().getCategories();
        if (categorieList.size() > 0) {
        	if (0 != lowerSpec) {
        		CategoryAnnotation lowerAnnotation = new CategoryLineAnnotation(
			        		"0",
			        		lowerSpec,
							categorieList.get(categorieList.size() - 1).toString(),
							lowerSpec,
							Color.BLACK,
							new BasicStroke(1.0f));
        		categoryplot.addAnnotation(lowerAnnotation);
        		
        	}
        	
        	if (0 != upperSpec) {
		        CategoryAnnotation upperAnnotation = new CategoryLineAnnotation(
		        		"0",
		        		upperSpec,
						categorieList.get(categorieList.size() - 1).toString(),
						upperSpec,
						Color.BLACK,
						new BasicStroke(1.0f));
		        categoryplot.addAnnotation(upperAnnotation);
        	}
        }
        
        return jfreechart;
	}
    
    public void init() {
    	
    	initParameter();    	
    	
    	String su = getAgentServletPath();
    	su = su + "?startDate="	+ this.getParameter("startDate")
    			+ "&endDate=" + this.getParameter("endDate")
    			+ "&eqpId="+ this.getParameter("eqpId")
    			+ "&periodIndex=" + this.getParameter("periodIndex")
    			+ "&itemIndex=" + this.getParameter("itemIndex");
    	//su="http://localhost:8080/csmcgui/control/getPmParamItemPoints?startDate=2007-05-06&endDate=2007-10-20&eqpId='DFPADG2','MCS7281'&periodIndex=10080&itemIndex=10014";
    	//su = "http://192.1.1.24:8080/csmcgui/control/getPmParamItemPoints?startDate=2008-02-01&endDate=2008-02-25&equipmentType=FPUR&eqpId='FPURN1','FPURN2','FPURN3','FPURO1','FPURO2'&periodIndex=1540&itemIndex=13047";
        List dataList = getPointList(su);
        
    	String itemName = PmsChartAppletUtil.checkEmpty(this.getParameter("itemName"),"itemName");
    	double lowerSpec = Double.parseDouble(PmsChartAppletUtil.checkEmpty(this.getParameter("itemLowerSpec"), "0"));
    	double upperSpec = Double.parseDouble(PmsChartAppletUtil.checkEmpty(this.getParameter("itemUpperSpec"), "0"));
      	
        JFreeChart chart = createChart(dataList, itemName, lowerSpec, upperSpec);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));

        getContentPane().add(chartPanel);
    }   
}