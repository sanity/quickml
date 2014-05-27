package quickdt.experiments;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.*;
import quickdt.csvReader.CSVToMap;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.downsamplingPredictiveModel.Utils;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 1/16/14.
 */
public class csvReaderExp {
    private static final Logger logger =  LoggerFactory.getLogger(csvReaderExp.class);


    public static void main(String[] args) {
        int numTraniningExamples = 40100;
        String inputFile = "redshift_training_data.csv";
        List<Map<String, Serializable>> instanceMaps = CSVToMap.loadRows(inputFile);
        List<Instance> instances = convertRawMapToInstance(instanceMaps);
        logger.info("got instances");

        /*HashMap<String, ClicksAndImps> uniqueBrowsers = new HashMap<>();
        for (Instance instance : instances) {
            String browser = (String) instance.getAttributes().get("an_browser");
            double click = (Double)instance.getClassification();

            if (browser != null) {
                int count = 0;
                if (!uniqueBrowsers.containsKey(browser)) {
                    uniqueBrowsers.put(browser, new ClicksAndImps());
                }
                uniqueBrowsers.get(browser).imps++;
                uniqueBrowsers.get(browser).imps+=click;

                if ()
                count++;
            }
        }

        for (String key : uniqueBrowsers) {
            uniqueBrowsers.get(key).doubleValue() / count;
            RandomForest randomForest = getRandomForest(instances, 4, 8);


            double dropProbability = .01;
            Utils.correctProbability(dropProbability, uncorrectedProbability);
        }*/
    }

    public static List<Instance> convertRawMapToInstance(List<Map<String,Serializable>> rawMaps) {
        List<Instance> instances = Lists.newArrayList();
        Attributes attributes;
        Instance instance;
        for (Map<String, Serializable> rawMap : rawMaps) {
            attributes = new HashMapAttributes();
            for (String key : rawMap.keySet()) {
                if (!key.equals("is_click")) {
                    attributes.put(key, rawMap.get(key));
                }
            }
            double clickVal = (rawMap.get("is_click").equals("f")) ? 0.0 : 1.0;
            instance = new Instance(attributes, clickVal);
            instances.add(instance);
        }
        return instances;


    }
}

