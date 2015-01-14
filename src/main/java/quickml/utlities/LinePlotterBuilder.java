package quickml.utlities;

/**
 * Created by alexanderhawk on 10/2/14.
 */

import javax.swing.*;

public class LinePlotterBuilder extends JFrame {

    int graphSizeInXDimension = 500;
    int graphSizeInYDimension = 270;
    String xAxisLabel = "X";
    String yAxisLabel = "Y";
    String chartTitle = "";

    public LinePlotterBuilder chartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
        return this;
    }

    public LinePlotterBuilder xyGraphDimensions(int xDim, int yDim) {
        this.graphSizeInXDimension = xDim;
        this.graphSizeInYDimension = yDim;
        return this;
    }

    public LinePlotterBuilder xAxisLabel(String xLabel) {
        this.xAxisLabel = xLabel;
        return this;
    }

    public LinePlotterBuilder yAxisLabel(String xLabel) {
        this.yAxisLabel = yAxisLabel;
        return this;
    }

    public LinePlotter buildLinePlotter(){
        return new LinePlotter(chartTitle, xAxisLabel, yAxisLabel, graphSizeInXDimension, graphSizeInYDimension);
    }

}