package quickml.Utilities;

/**
 * Created by alexanderhawk on 10/2/14.
 */


import com.google.common.collect.Lists;
import org.javatuples.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import javax.swing.*;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LinePlotter extends JFrame {

    List<XYSeries> seriesList = Lists.newArrayList();
    int graphSizeInXDimension = 500;
    int graphSizeInYDimension = 270;
    String xAxisLabel = "X";
    String yAxisLabel = "Y";
    String chartTitle = "";
    JFreeChart chart;

    public LinePlotter(final String chartTitle) {
        super(chartTitle);
        this.chartTitle = chartTitle;
    }

    public LinePlotter(String chartTitle, String xAxisLabel, String yAxisLabel, int graphSizeInXDimension, int graphSizeInYDimension) {
        super(chartTitle);
        this.chartTitle = chartTitle;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.graphSizeInXDimension = graphSizeInXDimension;
        this.graphSizeInYDimension = graphSizeInYDimension;
    }

    public void addSeries(Iterable<? extends Number> xRange, Iterable<? extends Number> yRange, String name) {
        XYSeries series= new XYSeries(name);
        Iterator<? extends Number> yIt = yRange.iterator();
        Iterator<? extends Number> xIt = xRange.iterator();
        while (yIt.hasNext() && xIt.hasNext()) {
            series.add(xIt.next().doubleValue(), yIt.next().doubleValue());
        }
        seriesList.add(series);
        return;
    }

    public void addSeries(Iterable<PoolAdjacentViolatorsModel.Observation> xyPairs, String name) {
        XYSeries series= new XYSeries(name);
        for(PoolAdjacentViolatorsModel.Observation xyPair : xyPairs)  {
            series.add(xyPair.input, xyPair.output);
        }
        seriesList.add(series);
        return;
    }

    public void clearAllSeries(){
        seriesList.clear();
    }

    private void createChartFromData(){
        final XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries series : seriesList) {
            dataset.addSeries(series);
        }
        chart = createChart(dataset);
    }

    public void displayPlot() {
        createChartFromData();
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(graphSizeInXDimension, graphSizeInYDimension));
        setContentPane(chartPanel);
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }


    public void savePlot(String fileName){
        createChartFromData();
        File filename_png = new File(fileName);
        try {
            ChartUtilities.saveChartAsPNG(filename_png, chart, graphSizeInXDimension, graphSizeInYDimension);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving a file",ex);
        }
    }

    public void saveAndDisplayPlot(String fileName) {
        createChartFromData();
        File filename_png = new File(fileName);
        //saving
        try {
            ChartUtilities.saveChartAsPNG(filename_png, chart, graphSizeInXDimension, graphSizeInYDimension);
        } catch (IOException ex) {
            throw new RuntimeException("Error saving a file",ex);
        }
        //display
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(graphSizeInXDimension, graphSizeInYDimension));
        setContentPane(chartPanel);
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    private JFreeChart createChart(final XYDataset dataset) {

        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,      // chart title
                xAxisLabel,                      // x axis label
                yAxisLabel,                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < seriesList.size(); i++) {
            renderer.setSeriesLinesVisible(i, true);
            renderer.setSeriesShapesVisible(i, true);
        }
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }

    public static void main(final String[] args) {
        List<Double> x = Lists.newArrayList();
        x.add(1.0);
        x.add(2.0);
        x.add(3.0);
        List<Double> y1 = Lists.newArrayList();
        y1.add(1.0);
        y1.add(2.0);
        y1.add(3.0);
        List<Double> y2 = Lists.newArrayList();
        y2.add(2.0);
        y2.add(4.0);
        y2.add(6.0);

        LinePlotter linePlotter = new LinePlotterBuilder().chartTitle("2 line plots").xAxisLabel("X").yAxisLabel("Y").buildLinePlotter();
        linePlotter.addSeries(x, y1, "slope of 1");
        linePlotter.addSeries(x,y2,"slope of 2");
        linePlotter.savePlot("2dplot.png");
    }

}
