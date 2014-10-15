package quickml.Utilities;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import java.io.FileReader;
import java.util.*;

/**
 * Created by alexanderhawk on 10/2/14.
 */


/* This class converts the contents of a csv file into a map of lists...where the contents of each collumn are
 stored in a List. Each list is contained in a map, where the column names (or numbers if names are not
 provided in a header) are the keys for their respective lists.
 */

public class CSVToMapOfNumericLists {
    private List<String> header;
    private char delimiter = ',';
    private boolean headerPresent = true;
    private Map<String, List<Double>> dataLists = Maps.newHashMap();

    public CSVToMapOfNumericLists() {
    }

    public CSVToMapOfNumericLists(char delimiter) {
        this.delimiter = delimiter;
    }

    public CSVToMapOfNumericLists(char delimiter, boolean noHeaderPresent) {
        this.delimiter = delimiter;
        this.headerPresent = noHeaderPresent;
    }

    public Map<String, List<Double>> readCsv(String fileName) throws Exception {

        CSVReader reader = new CSVReader(new FileReader(fileName), delimiter, '"');
        List<String[]> csvLines = reader.readAll();

        try {
            header = Lists.newArrayList();
            int startIndex = 1;
            if (!headerPresent) {
                startIndex = 0;
                for (int i = 0; i < csvLines.get(0).length; i++) {
                    header.add(String.valueOf(i));
                    dataLists.put(header.get(i), Lists.<Double>newArrayList());
                }
            } else {
                for (int i = 0; i < csvLines.get(0).length; i++) {
                    header.add(csvLines.get(0)[i]);
                    dataLists.put(header.get(i), Lists.<Double>newArrayList());
                }
                for (int i = startIndex; i < csvLines.size(); i++) {
                    appendLineToLists(csvLines.get(i));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return dataLists;
    }

    private void appendLineToLists(String[] dataLine) {
        for (int i = 0; i < dataLine.length; i++) {
            if (dataLine[i].isEmpty()) {
                continue;
            }
            List<Double> listToappendTo = dataLists.get(header.get(i));
            try {
                listToappendTo.add(Double.valueOf(dataLine[i]));
            } catch (NumberFormatException e) {
                listToappendTo.add(Double.NaN);
            }
        }
    }


    public static void main(String[] args) {

        CSVToMapOfNumericLists csvReader = new CSVToMapOfNumericLists(',', true);
        try {
            Map<String, List<Double>> mapOfinstances = csvReader.readCsv("wfRes");
            for (String key : mapOfinstances.keySet()) {
                System.out.println("list key: " + key + "list Vals" + mapOfinstances.get(key).toString());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        List<PoolAdjacentViolatorsModel.Observation> observations = Lists.newArrayList();
        List<PoolAdjacentViolatorsModel.Observation> predictions = Lists.newArrayList();



        for (int i = 0; i < csvReader.dataLists.get("target_cpc_customers").size(); i++) {
            PoolAdjacentViolatorsModel.Observation observation = new PoolAdjacentViolatorsModel.Observation
                    (csvReader.dataLists.get("target_cpc_customers").get(i), csvReader.dataLists.get("effective_cust_daily_spend").get(i));
            observations.add(observation);
        }

        PoolAdjacentViolatorsModel pav = new PoolAdjacentViolatorsModel(observations, 4);
        for (PoolAdjacentViolatorsModel.Observation observation : observations) {
            predictions.add(new PoolAdjacentViolatorsModel.Observation(observation.input, pav.predict(observation.input)));
        }

        TreeSet<PoolAdjacentViolatorsModel.Observation> calibrationSet = pav.getCalibrationSet();
        TreeSet<PoolAdjacentViolatorsModel.Observation> preSmoothingSet = pav.getPreSmoothingSet();
        System.out.println("calibration set: " + calibrationSet.toString());
        LinePlotter linePlotter = new LinePlotterBuilder().chartTitle("pav trial").xAxisLabel("cpc").yAxisLabel("rate").buildLinePlotter();
        linePlotter.addSeries(calibrationSet, "unweigted linear interpolation");
        linePlotter.addSeries(predictions, "weighted linear interpolation");
        linePlotter.addSeries(preSmoothingSet, "binned observations with weight 4");
        linePlotter.displayPlot();
    }
}