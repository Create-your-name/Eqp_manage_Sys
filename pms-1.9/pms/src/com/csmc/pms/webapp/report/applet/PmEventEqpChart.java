package com.csmc.pms.webapp.report.applet;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

public class PmEventEqpChart extends PmsChartApplet {

	private static final long serialVersionUID = 1737500951082409381L;

	/**
     * Creates barDataset and lineDataset.
     * 
     * @param dataList  the data for the chart.
     * @param barDataset
     * @param lineDataset
     */
	private void createDataset(List dataList, DefaultCategoryDataset barDataset, DefaultCategoryDataset lineDataset) {       
		String seryName1 = "平均时数/天";
        String seryName2 = "平均时数/次";
        
        String seryName3 = "事件数";
		
        for (int i = 0; i < dataList.size(); i++) {
			Map dataMap = (HashMap) dataList.get(i);
			//column keys...
			barDataset.addValue(Double.parseDouble(dataMap.get("AVGDAY").toString()), seryName1, dataMap.get("EQUIPMENT_ID").toString());
			barDataset.addValue(Double.parseDouble(dataMap.get("AVGTIME").toString()), seryName2, dataMap.get("EQUIPMENT_ID").toString());
	
			lineDataset.addValue(Double.parseDouble(dataMap.get("EVENTCOUNT").toString()), seryName3, dataMap.get("EQUIPMENT_ID").toString());
		}
    }
    
	/**
     * Creates a chart.
     * 
     * @param dataList  the data for the chart.
     * 
     * @return a chart.
     */
    private JFreeChart createChart(List dataList) {
        //Creates 2 CategoryDataset
    	DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
    	DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
    	createDataset(dataList, barDataset, lineDataset);
    	
    	JFreeChart jfreechart = ChartFactory.createBarChart("保养事件统计图", "EqpId", "平均时数", barDataset, PlotOrientation.VERTICAL, false, true, false);
        jfreechart.setBackgroundPaint(Color.white);
        
        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
        categoryplot.setBackgroundPaint(new Color(238, 238, 255));
        categoryplot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        
        CategoryAxis categoryaxis = categoryplot.getDomainAxis();
        categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        
        // new a RangeAxis to LineDataSet
        NumberAxis numberaxis = new NumberAxis("事件数");
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        categoryplot.setRangeAxis(1, numberaxis);        
        
        categoryplot.setDataset(1, lineDataset);
        categoryplot.mapDatasetToRangeAxis(1, 1);        
        
        // new a LineAndShapeRenderer to LineDataSet
        LineAndShapeRenderer lineandshaperenderer = new LineAndShapeRenderer();
        lineandshaperenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        categoryplot.setRenderer(1, lineandshaperenderer);
        categoryplot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        
        LegendTitle legendtitle = new LegendTitle(categoryplot.getRenderer(0));
        legendtitle.setMargin(new RectangleInsets(2D, 2D, 2D, 2D));
        legendtitle.setFrame(new BlockBorder());
        
        LegendTitle legendtitle1 = new LegendTitle(categoryplot.getRenderer(1));
        legendtitle1.setMargin(new RectangleInsets(2D, 2D, 2D, 2D));
        legendtitle1.setFrame(new BlockBorder());
        
        BlockContainer blockcontainer = new BlockContainer(new BorderArrangement());
        blockcontainer.add(legendtitle, RectangleEdge.LEFT);
        blockcontainer.add(legendtitle1, RectangleEdge.RIGHT);
        blockcontainer.add(new EmptyBlock(2000D, 0.0D));
        
        CompositeTitle compositetitle = new CompositeTitle(blockcontainer);
        compositetitle.setPosition(RectangleEdge.BOTTOM);
        
        jfreechart.addSubtitle(compositetitle);
        
        return jfreechart;
	}
    
    public void init() {
    	
    	initParameter();    	

    	String su = getAgentServletPath();
    	su = su + "?startDate="	+ this.getParameter("startDate")
    			+ "&endDate=" + this.getParameter("endDate")
    			+ "&equipmentType=" + this.getParameter("equipmentType")
    			+ "&eqpId="+ this.getParameter("eqpId")    			
    			+ "&keyEqp=" + this.getParameter("keyEqp")
    			+ "&adjustEqp=" + this.getParameter("adjustEqp")
    			+ "&measureEqp=" + this.getParameter("measureEqp")
    			+ "&dayCount=" + this.getParameter("dayCount")
    			+ "&periodIndex=" + this.getParameter("periodIndex");
    	//su="http://localhost:8080/csmcgui/control/getPmEventEqpHist?startDate=2007-08-06&endDate=2007-11-02&equipmentType=PSEM&eqpId=&keyEqp=&adjustEqp=&measureEqp=&dayCount=10&periodIndex=10080";

        List dataList = getPointList(su);        
    	
        JFreeChart chart = createChart(dataList);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setPreferredSize(new Dimension(500, 270));

        getContentPane().add(chartPanel);
    }   
}