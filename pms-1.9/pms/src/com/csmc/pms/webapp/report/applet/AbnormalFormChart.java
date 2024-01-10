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

public class AbnormalFormChart extends PmsChartApplet {

	private static final long serialVersionUID = 2220944920218680950L;
	
	private static final String DOWN_TIME = "DOWN_TIME";
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
			if (null != dataMap.get(DOWN_TIME)) {
				dataset.addValue(Double.parseDouble(dataMap.get(DOWN_TIME).toString()), seriesName, dataMap.get(START_TIME).toString());
			}
		}

        return dataset; 
    }
    
	/**
     * Creates a chart.
     * 
     * @param dataList  the data for the chart.
     * @param seriesName
     * 
     * @return a chart.
     */
    private JFreeChart createChart(List dataList, String seriesName) {
        //Creates a Dataset
    	CategoryDataset dataset = createDataset(dataList, seriesName);
    	
    	JFreeChart jfreechart = ChartFactory.createLineChart("设备异常用时图", "开始日期", "Down Time(小时)", dataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.setBackgroundPaint(Color.white);
        
        //得到图形的绘制结构plot
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        
        //设置chart样式
        setCategoryLineChart(categoryplot);
        
        String tmpEqpId = "";
        //添加点的注释eqpid
        for (int i=0;i<dataList.size();i++){
			Map dataMap = (HashMap) dataList.get(i);
			 
			if (null != dataMap.get(DOWN_TIME) && !tmpEqpId.equals((String) dataMap.get(EQUIPMENT_ID))) {
				CategoryPointerAnnotation categoryPointerAnnotation = new CategoryPointerAnnotation(
						dataMap.get(EQUIPMENT_ID).toString(), 
						dataMap.get(START_TIME).toString(),
						Double.parseDouble(dataMap.get(DOWN_TIME).toString()),
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
    			+ "&equipmentDept=" + this.getParameter("equipmentDept");
    	//su="http://localhost:8080/csmcgui/control/getAbnormalFormItemPoints?startDate=2007-05-06&endDate=2007-10-20&equipmentType=PDNS&eqpId='CDNS21','EMAL1VI','EMAL2VI','EMTINVI','ENSINVI','EOPADVI','EOPEK01','EOPLN','EOPLNVI','EOQBDVI','EOROMVI','EOSOGVI','EOSPCVI','EOVIAVI','EOWINVI','EPPOLVI','EPWSIVI','ER210101','EWWEBVI','MT4501','PCDNS01','PCDNS02','PCDNS06','PCDNS11','PCDNS13','PCDNS14','PCDNS15','PCDNS16','PCDNS20','PCDNS21','PCDNS22','PCDNS23','PCDNS24','PCDNS25','PCDNS26','PCDNS27','PCDNS28','PDDNS03','PDDNS04','PDDNS05','PDDNS07','PDDNS08','PDDNS09','PDDNS10','PDDNS11','PDDNS12','PDDNS17','PDDNS18','PDDNS19','PDDNS20','PDDNS21','PDDNS22','PDDNS23','PDDNS24','PDDNS25','PDDNS26','PDDNS27','PDDNS28','PPCDNS22','VMPVD44'&equipmentDept=";
    	//su = "http://192.1.1.24:8080/csmcgui/control/getAbnormalFormItemPoints?startDate=2007-10-28&endDate=2007-11-13&equipmentType=&eqpId=&equipmentDept=光刻部";
        List dataList = getPointList(su);
        
    	String seriesName = PmsChartAppletUtil.checkEmpty(this.getParameter("equipmentType"),this.getParameter("equipmentDept"));
    	
        JFreeChart chart = createChart(dataList, seriesName);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));

        getContentPane().add(chartPanel);
    }   
}