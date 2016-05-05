package quickml.utlities.selectors;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;
import quickml.utlities.LinePlotter;
import quickml.utlities.LinePlotterBuilder;

import javax.xml.datatype.Duration;
import java.io.FileReader;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by alexanderhawk on 10/2/14.
 */


/* This class converts the contents of a csv file into a map of lists...where the contents of each collumn are
 stored in a List. Each list is contained in a map, where the column names (or numbers if names are not
 provided in a header) are the keys for their respective lists.
 */

public class CSVToMapOfObjectLists {
    private List<String> header;
    private char delimiter = ',';
    private boolean headerPresent = true;
    private Map<String, List<Object>> dataLists = Maps.newHashMap();

    public CSVToMapOfObjectLists() {
    }

    public CSVToMapOfObjectLists(char delimiter) {
        this.delimiter = delimiter;
    }

    public CSVToMapOfObjectLists(char delimiter, boolean noHeaderPresent) {
        this.delimiter = delimiter;
        this.headerPresent = noHeaderPresent;
    }

    public Map<String, List<Object>> readCsv(String fileName) throws Exception {

        CSVReader reader = new CSVReader(new FileReader(fileName), delimiter, '"');
        List<String[]> csvLines = reader.readAll();

        try {
            header = Lists.newArrayList();
            int startIndex = 1;
            if (!headerPresent) {
                startIndex = 0;
                for (int i = 0; i < csvLines.get(0).length; i++) {
                    header.add(String.valueOf(i));
                    dataLists.put(header.get(i), Lists.<Object>newArrayList());
                }
            } else {
                for (int i = 0; i < csvLines.get(0).length; i++) {
                    header.add(csvLines.get(0)[i]);
                    dataLists.put(header.get(i), Lists.<Object>newArrayList());
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
            List<Object> listToappendTo = dataLists.get(header.get(i));
            try {
                listToappendTo.add(Double.valueOf(dataLine[i]));
            } catch (NumberFormatException e) {
                listToappendTo.add(dataLine[i]);
            }
        }
    }


    public static void main(String[] args) {

        CSVToMapOfObjectLists csvReader = new CSVToMapOfObjectLists(',', true);
        try {
            Map<String, List<Object>> mapOfinstances = csvReader.readCsv("/Users/alexanderhawk/Downloads/20150526-181451.csv");
            for (String key : mapOfinstances.keySet()) {
                System.out.println("list key: " + key + " list Vals" + mapOfinstances.get(key).toString());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        List<PoolAdjacentViolatorsModel.Observation> observations = Lists.newArrayList();
        List<PoolAdjacentViolatorsModel.Observation> predictions = Lists.newArrayList();



        for (int i = 0; i < csvReader.dataLists.get("target_cpc_customers").size(); i++) {

            final Double target_cpc_customers = (Double)csvReader.dataLists.get("target_cpc_customers").get(i);
            Double effective_cust_daily_spend = (Double)csvReader.dataLists.get("actual_spend_customers").get(i)/computeReservationTime(
                    (String)csvReader.dataLists.get("reserved_at").get(i), (String)csvReader.dataLists.get("actual_spend_time").get(i));

            PoolAdjacentViolatorsModel.Observation observation = new PoolAdjacentViolatorsModel.Observation
                    (target_cpc_customers, effective_cust_daily_spend);
            observations.add(observation);
        }

        PoolAdjacentViolatorsModel pav = new PoolAdjacentViolatorsModel(observations, 16);
        PoolAdjacentViolatorsModel.Observation prev = null;
     //   observations.sort(Comparator.<PoolAdjacentViolatorsModel.Observation>naturalOrder());
        for (PoolAdjacentViolatorsModel.Observation observation : observations) {

            PoolAdjacentViolatorsModel.Observation obsToAdd = new PoolAdjacentViolatorsModel.Observation(observation.input, pav.predict(observation.input));
            predictions.add(obsToAdd);

        if (prev !=null && prev.output < observation.output)
                System.out.println("prev out: " + prev.toString() + ". obs.input: " + obsToAdd.toString());
            prev = obsToAdd;
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
    private static double computeReservationTime(String startTime, String endTime) {
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm:ss");
        DateTime startDateTime = dateFormatter.parseDateTime(startTime.substring(0, 19));
        DateTime endDateTime = dateFormatter.parseDateTime(endTime.substring(0,19));
        return ((double) Seconds.secondsBetween(startDateTime, endDateTime).getSeconds())/(24.0*60*60);
    }
}