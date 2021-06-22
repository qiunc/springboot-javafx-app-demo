package com.pdai.javafx.app.utils;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ����ͼ��
 * ʵ��runnable�ӿ�
 */

public class TemperatureChart extends ChartPanel implements Runnable {

    private static TimeSeries timeSeries;
    private static TimeSeries timeSeries2;

    public static Queue<Double> TemperatureQueue = new LinkedList<>();
    public static Queue<Double> SpeedQueue = new LinkedList<>();

    /**
     * @param chartContent  ��һ��������
     * @param chartContent2 �ڶ���������
     * @param title         ͼ�����
     * @param yaxisName     ��һ��Y����
     */
    public TemperatureChart(String chartContent, String chartContent2, String title, String yaxisName) {
        super(createChart(chartContent, chartContent2, title, yaxisName));

    }


    private static JFreeChart createChart(String chartContent, String chartContent2, String title, String yaxisName) {
// ����ʱ��ͼ����

        timeSeries = new TimeSeries(chartContent, Millisecond.class);
        timeSeries2 = new TimeSeries(chartContent2, Millisecond.class);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(timeSeries);
        XYDataset xydataset = timeseriescollection;
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "Current Time", yaxisName, xydataset,
                true, true, false);
        jfreechart.addSubtitle(new TextTitle("Two Line"));

        //��Ⱦ��
        XYPlot xyplot = jfreechart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        //����������ɫ
        renderer.setSeriesPaint(0, Color.RED);
//        renderer.setSeriesPaint(1, Color.BLUE);
        //�������ݵ�ɼ�
        renderer.setBaseShapesVisible(true);
        //�������ݵ���ʾֵ
//        renderer.setBaseItemLabelGenerator( new StandardXYItemLabelGenerator());
//        renderer.setBaseItemLabelsVisible(true);
        xyplot.setBackgroundPaint(ChartColor.WHITE);
        xyplot.setRangeGridlinePaint(ChartColor.BLACK);
        xyplot.setDomainGridlinePaint(ChartColor.BLACK);

        //�ڶ�������
        TimeSeriesCollection timeseriesCollection2 = new TimeSeriesCollection();
        timeseriesCollection2.addSeries(timeSeries2);
        NumberAxis numberaxis1 = new NumberAxis("Speed(rpm/min)");
        //numberaxis1.setLabelPaint(Color.blue);
        //numberaxis1.setTickLabelPaint(Color.blue);
        xyplot.setRangeAxis(2, numberaxis1);
        XYDataset xydataset2 = timeseriesCollection2;
        xyplot.setDataset(2, xydataset2);
        xyplot.mapDatasetToRangeAxis(2, 2);
        StandardXYItemRenderer standardxyitemrenderer1 = new StandardXYItemRenderer();
        standardxyitemrenderer1.setSeriesPaint(0, Color.blue);
        //�������ݵ�ɼ�
        standardxyitemrenderer1.setBaseShapesVisible(true);
        xyplot.setRenderer(2, standardxyitemrenderer1);

        // �������趨
        ValueAxis valueaxis = xyplot.getDomainAxis();
        // �Զ��������������ݷ�Χ
        valueaxis.setAutoRange(true);
        // ������̶����ݷ�Χ 30s
        valueaxis.setFixedAutoRange(30000D);
        //�趨Y�����ݷ�Χ
        valueaxis = xyplot.getRangeAxis();
        // valueaxis.setRange(0.0D,200D);
        return jfreechart;
    }

    @Override
    public void run() {
        while (true) {
            if (!TemperatureQueue.isEmpty()) {
                double tmpQueueNum = TemperatureQueue.poll();
                timeSeries.add(new Millisecond(), tmpQueueNum);
            }
            if (!SpeedQueue.isEmpty()) {
                double speedNum = SpeedQueue.poll();
                timeSeries2.add(new Millisecond(), speedNum);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}