package quickml.supervised.classifier.splitOnAttribute;


import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.PredictionMap;
import quickml.supervised.AdvertisingInstances;
import quickml.supervised.PredictiveModelWithDataBuilder;
import quickml.supervised.PredictiveModelWithDataBuilderFactory;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilderFactory;
import quickml.supervised.classifier.randomForest.RandomForestBuilderFactory;
import quickml.supervised.crossValidation.ClassifierOutOfTimeCrossValidator;
import quickml.supervised.crossValidation.crossValLossFunctions.*;

import java.io.Serializable;
import java.util.*;


public class SplitOnAttributeClassifierBuilderTest {
    private static final Logger logger = LoggerFactory.getLogger(SplitOnAttributeClassifierBuilderTest.class);

    @org.testng.annotations.Test
    public void advertisingDataTest() {
        List<Instance<AttributesMap>> advertisingInstances = AdvertisingInstances.getAdvertisingInstances();
        Map<String, Object> predictiveModelParameters = new HashMap<String, Object>();
        predictiveModelParameters.put("numTrees", Integer.valueOf(16));
        predictiveModelParameters.put("bagSize", Integer.valueOf(0));//need to clean the builders to not use this since baggingnot used
        predictiveModelParameters.put("ignoreAttrProb", Double.valueOf(0.7));
        predictiveModelParameters.put("minScore", Double.valueOf(0.000001));
        predictiveModelParameters.put("maxDepth", Integer.valueOf(16));
        predictiveModelParameters.put("minCatAttrOcc", Integer.valueOf(29));
        predictiveModelParameters.put("minLeafInstances", Integer.valueOf(0));
        predictiveModelParameters.put("scorer", new GiniImpurityScorer());


        predictiveModelParameters.put("rebuildThreshold", Integer.valueOf(1));
        predictiveModelParameters.put("splitNodeThreshold", Integer.valueOf(1));
        predictiveModelParameters.put("minorityInstanceProportion", Double.valueOf(0.30));
        predictiveModelParameters.put("dateTimeExtractor", new AdvertisingInstances.AdvertisingDateTimeExtractor());


        RandomForestBuilderFactory randomForestBuilderFactory = new RandomForestBuilderFactory();
        DownsamplingClassifierBuilderFactory downsamplingClassifierBuilderFactory = new DownsamplingClassifierBuilderFactory(randomForestBuilderFactory);
        PredictiveModelWithDataBuilderFactory<AttributesMap, DownsamplingClassifier> predictiveModelWithDataBuilderFactory = new PredictiveModelWithDataBuilderFactory<AttributesMap, DownsamplingClassifier>(downsamplingClassifierBuilderFactory);

        //Set up groups
        Set<Serializable> group0Campaigns = Sets.newHashSet();
        group0Campaigns.add("_830");
        group0Campaigns.add("_833");
        Map<Integer, Double> group1RelativeImportancesFromOtherGroups = new HashMap<>();
        group1RelativeImportancesFromOtherGroups.put(1, 1.0);
        double group1PercentageOfCrossData = 0.1;
        int group1MinTotalSamples = 2000;
        int groupId0 = 0;
        SplitOnAttributeClassifierBuilder.SplitModelGroup group0 = new SplitOnAttributeClassifierBuilder.SplitModelGroup(groupId0, group0Campaigns, group1MinTotalSamples, group1PercentageOfCrossData,
                group1RelativeImportancesFromOtherGroups);

        Set<Serializable> group1Campaigns = Sets.newHashSet();
        group1Campaigns.add("_792");
        Map<Integer, Double> group2RelativeImportancesFromOtherGroups = new HashMap<>();
        group2RelativeImportancesFromOtherGroups.put(1, 1.0);
        double group2PercentageOfCrossData = 0.4;
        int group2MinTotalSamples = 100;
        int groupId1 = 1;
        SplitOnAttributeClassifierBuilder.SplitModelGroup group1 = new SplitOnAttributeClassifierBuilder.SplitModelGroup(groupId1, group1Campaigns, group2MinTotalSamples,
                group2PercentageOfCrossData, group2RelativeImportancesFromOtherGroups);

        Map<Integer, SplitOnAttributeClassifierBuilder.SplitModelGroup> splitModelGroupMap = new HashMap<>();
        splitModelGroupMap.put(0, group0);
        splitModelGroupMap.put(1, group1);
        int defaultGroup = 0;


        SplitOnAttributeClassifierBuilderFactory splitOnAttributeClassifierBuilderFactory = new SplitOnAttributeClassifierBuilderFactory("campaignId", splitModelGroupMap, defaultGroup, predictiveModelWithDataBuilderFactory);
        SplitOnAttributeClassifierBuilder splitOnAttributeClassifierBuilder = splitOnAttributeClassifierBuilderFactory.buildBuilder(predictiveModelParameters);


        ClassifierOutOfTimeCrossValidator cv = new ClassifierOutOfTimeCrossValidator(new WeightedAUCCrossValLossFunction(1.0), 0.15, 24, new AdvertisingInstances.AdvertisingDateTimeExtractor());
        Map<String, CrossValLossFunction<PredictionMap>> lossFunctions = Maps.newHashMap();
        lossFunctions.put("AUC", new LossFunctionCorrectedForDownsampling(new WeightedAUCCrossValLossFunction(1.0), 0.99, Double.valueOf(0.0)));
        lossFunctions.put("Log", new LossFunctionCorrectedForDownsampling(new ClassifierLogCVLossFunction(0.000001), 0.99, Double.valueOf(0.0)));
        lossFunctions.put("RMSE", new LossFunctionCorrectedForDownsampling(new ClassifierRMSECrossValLossFunction(), 0.99, Double.valueOf(0.0)));
        lossFunctions.put("AUCRAw", new WeightedAUCCrossValLossFunction(1.0));
        lossFunctions.put("LogRaw", new ClassifierLogCVLossFunction(0.00001));
        lossFunctions.put("RMSERaw", new ClassifierRMSECrossValLossFunction());
        MultiLossFunctionWithModelConfigurations<PredictionMap> multiLossFunctionWithModelConfigurations = new MultiLossFunctionWithModelConfigurations<PredictionMap>(lossFunctions, "Auc");

        multiLossFunctionWithModelConfigurations = cv.getMultipleCrossValidatedLossesWithModelConfiguration(splitOnAttributeClassifierBuilder,advertisingInstances, multiLossFunctionWithModelConfigurations);
        Map<String, LossWithModelConfiguration> lossWithModelConfigurationMap = multiLossFunctionWithModelConfigurations.getLossesWithModelConfigurations();
        for (String lossFunction: lossWithModelConfigurationMap.keySet()) {
            logger.info(lossFunction + ": " + lossWithModelConfigurationMap.get(lossFunction).getLoss());
        }

        ClassifierOutOfTimeCrossValidator cvSingle = new ClassifierOutOfTimeCrossValidator(new WeightedAUCCrossValLossFunction(1.0), 0.15, 24, new AdvertisingInstances.AdvertisingDateTimeExtractor());
        Map<String, CrossValLossFunction<PredictionMap>> lossFunctionsSingle = Maps.newHashMap();
        lossFunctionsSingle.put("AUC", new LossFunctionCorrectedForDownsampling(new WeightedAUCCrossValLossFunction(1.0), 0.99, Double.valueOf(0.0)));
        lossFunctionsSingle.put("Log", new LossFunctionCorrectedForDownsampling(new ClassifierLogCVLossFunction(0.00001), 0.99, Double.valueOf(0.0)));
        lossFunctionsSingle.put("RMSE", new LossFunctionCorrectedForDownsampling(new ClassifierRMSECrossValLossFunction(), 0.99, Double.valueOf(0.0)));
        lossFunctionsSingle.put("AUCRAw", new WeightedAUCCrossValLossFunction(1.0));
        lossFunctionsSingle.put("LogRaw", new ClassifierLogCVLossFunction(0.00001));
        lossFunctionsSingle.put("RMSERaw", new ClassifierRMSECrossValLossFunction());

        MultiLossFunctionWithModelConfigurations<PredictionMap> multiLossFunctionWithModelConfigurationsSingle = new MultiLossFunctionWithModelConfigurations<PredictionMap>(lossFunctionsSingle, "AUC");
        PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier> predictiveModelWithDataBuilder = predictiveModelWithDataBuilderFactory.buildBuilder(predictiveModelParameters);

        multiLossFunctionWithModelConfigurationsSingle = cvSingle.getMultipleCrossValidatedLossesWithModelConfiguration(predictiveModelWithDataBuilder, advertisingInstances, multiLossFunctionWithModelConfigurationsSingle);
        Map<String, LossWithModelConfiguration> lossWithModelConfigurationMapSingle = multiLossFunctionWithModelConfigurationsSingle.getLossesWithModelConfigurations();
        double tolerance = .05;
        for (String lossFunction: lossWithModelConfigurationMapSingle.keySet()) {
            double lossSingleModel = lossWithModelConfigurationMapSingle.get(lossFunction).getLoss();
            double lossSplitModel =  lossWithModelConfigurationMap.get(lossFunction).getLoss();
            Assert.assertTrue(val1NotWorseThanVal2(tolerance, lossSingleModel, lossSplitModel), "split model is not better than single model." +lossFunction + ". Single model: " + lossSingleModel + ". SplitModel: " + lossSplitModel);
            logger.info(lossFunction + ". Single model: " + lossSingleModel + ". SplitModel: " + lossSplitModel);

        }
        logger.info("last print");
    }

    private boolean val1NotWorseThanVal2(double tolerance, double val1, double val2) {
        return Math.abs((val1-val2)/val1) < tolerance || val1 > val2 ;
    }

}
