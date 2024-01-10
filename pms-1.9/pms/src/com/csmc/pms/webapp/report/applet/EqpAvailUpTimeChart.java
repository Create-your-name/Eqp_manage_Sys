package com.csmc.pms.webapp.report.applet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.annotations.CategoryLineAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class EqpAvailUpTimeChart extends PmsChartApplet {

	private static final long serialVersionUID = 5499062695264252308L;
	
	private CategoryDataset createDataset(List dataList, String showAvailableTime, String showUpTime) {
		// row keys...
        String series1 = "Available";
        String series2 = "UpTime";
        
        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i=0;i<dataList.size();i++){
			Map dataMap = (HashMap)dataList.get(i);
			//column keys...
			if (null != showAvailableTime && "1".equals(showAvailableTime)) {
				dataset.addValue(Double.parseDouble(dataMap.get("AVABT").toString()), series1, dataMap.get("REPDATE").toString());
			}
			
			if (null != showUpTime && "1".equals(showUpTime)) {
				dataset.addValue(Double.parseDouble((dataMap.get("UPT").toString())), series2, dataMap.get("REPDATE").toString());
			}			
		}
        
        return dataset;        
    }
    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(CategoryDataset dataset, String referLine) {
        
        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            "Available/Up Time Chart",      // chart title
            "时间",              			// domain axis label
            "百分比%",                 		// range axis label
            dataset,                 		// data
            PlotOrientation.VERTICAL, 		// orientation
            true,                     		// include legend
            true,                     		// tooltips?
            false                    		// URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
        
        plot.clearAnnotations();
        double referLineValue = 0;
        if (!"".equals(referLine)) {
        	try {
        		referLineValue = Double.parseDouble(referLine);
        	} catch (NumberFormatException e) {
        		referLineValue = 0;
        	}
        }
        List categorieList = chart.getCategoryPlot().getCategories();
        if (categorieList.size() > 0 && 0 != referLineValue) { 
	        CategoryAnnotation annotation = new CategoryLineAnnotation(
	        		"0",
					referLineValue,
					categorieList.get(categorieList.size() - 1).toString(),
					referLineValue,
					Color.BLACK,
					new BasicStroke(1.0f));
	        plot.addAnnotation(annotation);
        }
	        
        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        // set up gradient paints for series...
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 
                0.0f, 0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 
                0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }

    public void init() {
    	
    	initParameter();
    	
    	String su = getAgentServletPath();
    	su = su + "?Period=" + this.getParameter("Period") + "&StartDate="
				+ this.getParameter("StartDate") + "&EndDate="
				+ this.getParameter("EndDate") + "&EqpId="
				+ this.getParameter("EqpId");
    	//su="http://localhost:8080/csmcgui/control/getEqpAvailUpTime?Period=MONTH&StartDate=2007-05-06&EndDate=2007-07-20&EqpId='MD21221','MD21321','MD21351','MD21381','MD21391'";
    	
    	List dataList = getPointList(su);

        CategoryDataset dataset = createDataset(dataList, this.getParameter("ShowAvailableTime"), this.getParameter("ShowUpTime"));
        JFreeChart chart = createChart(dataset, this.getParameter("ReferLine"));
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));

        getContentPane().add(chartPanel);
    }    
}