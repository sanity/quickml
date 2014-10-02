package quickml.plotting;

/**
 * Created by alexanderhawk on 10/2/14.
 */
/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -------------------
 * LineChartDemo6.java
 * -------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LineChartDemo6.java,v 1.5 2004/04/26 19:11:55 taqua Exp $
 *
 * Changes
 * -------
 * 27-Jan-2004 : Version 1 (DG);
 *
 */


import com.google.common.collect.Lists;
import org.javatuples.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.Color;
import java.util.*;

/**
 * A simple demonstration application showing how to create a line chart using data from an
 * {@link org.jfree.data.xy.XYDataset}.
 *
 */
public class LinePlotter extends JFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    List<XYSeries> seriesList = Lists.newArrayList();
    int GraphSizeInXDimension = 500;
    int GraphSizeInYDimension = 270;
    String xAxisLabel = "X";
    String yAxisLabel = "Y";
    String chartTitle = "";

    public LinePlotter(final String chartTitle) {
        super(chartTitle);
        this.chartTitle = chartTitle;
    }

    public LinePlotter addSeries(Iterable<? extends Number> xRange, Iterable<? extends Number> yRange, String name) {
        XYSeries series= new XYSeries(name);
        Iterator<? extends Number> yIt = yRange.iterator();
        Iterator<? extends Number> xIt = xRange.iterator();
        while (yIt.hasNext() && xIt.hasNext()) {
            series.add(xIt.next().doubleValue(), yIt.next().doubleValue());
        }
        seriesList.add(series);
        return this;
    }

    public LinePlotter addSeries(Iterable<Pair<? extends Number, ? extends Number>> xyPairs, String name) {
        XYSeries series= new XYSeries(name);
       for(Pair<? extends Number, ? extends Number> xyPair : xyPairs)  {
            series.add(xyPair.getValue0().doubleValue(), xyPair.getValue1().doubleValue());
       }
       seriesList.add(series);
       return this;
    }

    public LinePlotter xyGraphDimensions(int xDim, int yDim) {
        this.GraphSizeInXDimension = xDim;
        this.GraphSizeInYDimension = yDim;
        return this;
    }

    public LinePlotter xAxisLabel(String xLabel) {
        this.xAxisLabel = xLabel;
        return this;
    }

    public LinePlotter yAxisLabel(String xLabel) {
        this.yAxisLabel = yAxisLabel;
        return this;
    }

    public void createPlot() {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries series : seriesList) {
            dataset.addSeries(series);
        }
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(GraphSizeInXDimension, GraphSizeInYDimension));
        setContentPane(chartPanel);
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }



    /**
     * Creates a chart.
     *
     * @param dataset  the data for the chart.
     *
     * @return a chart.
     */
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

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    *
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
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

        final LinePlotter linePlotter = new LinePlotter("linear plots");
        linePlotter.xAxisLabel("X").yAxisLabel("Y").addSeries(x, y1, "slope of 1").addSeries(x,y2,"slope of 2").createPlot();
    }

}
