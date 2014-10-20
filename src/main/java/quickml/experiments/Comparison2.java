package quickml.experiments;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import quickml.Utilities.CSVToInstanceReader;
import quickml.Utilities.CSVToInstanceReaderBuilder;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModelWithDataBuilder;
import quickml.supervised.calibratedPredictiveModel.CalibratedPredictiveModelBuilder;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.classifier.twoStageModel.TwoStageModelBuilder;
import quickml.supervised.crossValidation.ClassifierOutOfTimeCrossValidator;
import quickml.supervised.crossValidation.LabelConverter;

import quickml.supervised.crossValidation.crossValLossFunctions.*;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/7/14.
 */
public class Comparison2 {
    public static void main(String[] args) {
        //Get Auc loss of a 2stage model and a one stage
        CSVToInstanceReader csvToInstanceReader = new CSVToInstanceReaderBuilder().collumnNameForLabel("outcome").buildCsvReader();
        ArrayList<Instance<AttributesMap>> instancesWithLabelsForAllStages = Lists.newArrayList();
        ArrayList<Instance<AttributesMap>> firstStageInstances = Lists.newArrayList();
        ArrayList<Instance<AttributesMap>> firstStageInstances2 = Lists.newArrayList();
        ArrayList<Instance<AttributesMap>> instancesForCompositeModel = Lists.newArrayList();

        try {
          //instancesWithLabelsForAllStages = csvToInstanceReader.readCsv("allLabelsR.csv");//("cShort.csv");
          //firstStageInstances = csvToInstanceReader.readCsv("clickLabelDx.csv");//("cShort.csv");
          //firstStageInstances2 = csvToInstanceReader.readCsv("clickLabelDel.csv");//("cShort.csv");
          //  instancesForCompositeModel = csvToInstanceReader.readCsv("clickPageViewLabelR.csv");//("cpShort.csv");

          instancesWithLabelsForAllStages = csvToInstanceReader.readCsv("aShortDx.csv");
      //    firstStageInstances = csvToInstanceReader.readCsv("cShortDx.csv");
          instancesForCompositeModel = csvToInstanceReader.readCsv("cpShortDx.csv");

        } catch (Exception e) {
            System.exit(2);
        }
        firstStageInstances.addAll(firstStageInstances2);

        RandomForestBuilder cRandomForestBuilder = new RandomForestBuilder(new TreeBuilder().minCategoricalAttributeValueOccurances(29)
                .ignoreAttributeAtNodeProbability(.7).maxDepth(16)).numTrees(32);
        RandomForestBuilder c2pRandomForestBuilder = new RandomForestBuilder(new TreeBuilder().minCategoricalAttributeValueOccurances(22)
                .ignoreAttributeAtNodeProbability(.7).maxDepth(16)).numTrees(32);
        RandomForestBuilder cpRandomForestBuilder = new RandomForestBuilder(new TreeBuilder().minCategoricalAttributeValueOccurances(29)
                .ignoreAttributeAtNodeProbability(.7).maxDepth(16)).numTrees(32);
        DownsamplingClassifierBuilder cDownsamplingClassifierBuilder = new DownsamplingClassifierBuilder(cRandomForestBuilder, 0.30);
        DownsamplingClassifierBuilder c2pDownsamplingClassifierBuilder = new DownsamplingClassifierBuilder(c2pRandomForestBuilder, 0.30);
        DownsamplingClassifierBuilder cpDownsamplingClassifierBuilder = new DownsamplingClassifierBuilder(cpRandomForestBuilder, 0.30);


        CalibratedPredictiveModelBuilder cCalibratedPredictiveModelBuilder = new CalibratedPredictiveModelBuilder(cDownsamplingClassifierBuilder).binsInCalibrator(20).hoursToCalibrateOver(700).dateTimeExtractor(new TestDateTimeExtractor());
        CalibratedPredictiveModelBuilder c2pCalibratedPredictiveModelBuilder = new CalibratedPredictiveModelBuilder(c2pDownsamplingClassifierBuilder).binsInCalibrator(20).hoursToCalibrateOver(700).dateTimeExtractor(new TestDateTimeExtractor());
        CalibratedPredictiveModelBuilder cpCalibratedPredictiveModelBuilder = new CalibratedPredictiveModelBuilder(cpDownsamplingClassifierBuilder).binsInCalibrator(20).hoursToCalibrateOver(700).dateTimeExtractor(new TestDateTimeExtractor());


   //     PredictiveModelWithDataBuilder<AttributesMap, CalibratedPredictiveModel> pmbWithDatac = new PredictiveModelWithDataBuilder<AttributesMap, CalibratedPredictiveModel>(cCalibratedPredictiveModelBuilder).rebuildThreshold(1).splitNodeThreshold(1);
   //     PredictiveModelWithDataBuilder<AttributesMap, CalibratedPredictiveModel> pmbWithDatac2p = new PredictiveModelWithDataBuilder<AttributesMap, CalibratedPredictiveModel>(c2pCalibratedPredictiveModelBuilder).rebuildThreshold(1).splitNodeThreshold(1);
   //     PredictiveModelWithDataBuilder<AttributesMap, CalibratedPredictiveModel> pmbWithDatacp= new PredictiveModelWithDataBuilder<AttributesMap, CalibratedPredictiveModel>(cpCalibratedPredictiveModelBuilder).rebuildThreshold(1).splitNodeThreshold(1);


        PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier> pmbWithDatac = new PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier>(cDownsamplingClassifierBuilder).rebuildThreshold(1).splitNodeThreshold(1);
        PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier> pmbWithDatac2p = new PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier>(c2pDownsamplingClassifierBuilder).rebuildThreshold(1).splitNodeThreshold(1);
        PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier> pmbWithDatacp= new PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier>(cpDownsamplingClassifierBuilder).rebuildThreshold(1).splitNodeThreshold(1);


        PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier> pmbWithDatac2 = new PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier>(cDownsamplingClassifierBuilder).rebuildThreshold(1).splitNodeThreshold(1);
        PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier> pmbWithDatac2p2 = new PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier>(c2pDownsamplingClassifierBuilder).rebuildThreshold(1).splitNodeThreshold(1);
        PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier> pmbWithDatacp2= new PredictiveModelWithDataBuilder<AttributesMap, DownsamplingClassifier>(cpDownsamplingClassifierBuilder).rebuildThreshold(1).splitNodeThreshold(1);



        //TwoStageModelBuilder twoStageModelBuilder = new TwoStageModelBuilder(cCalibratedPredictiveModelBuilder, c2pCalibratedPredictiveModelBuilder);
       TwoStageModelBuilder twoStageModelBuilder = new TwoStageModelBuilder(pmbWithDatac, pmbWithDatac2p);
        TwoStageModelBuilder twoStageModelBuilder2 = new TwoStageModelBuilder(pmbWithDatac2, pmbWithDatac2p2);

        LabelConverter<AttributesMap> labelConverter  = new LabelConverter<AttributesMap>() {
            @Override
            public List<Instance<AttributesMap>> convertLabels(List<Instance<AttributesMap>> initialInstances) {
                List<Instance<AttributesMap>> convertedInstances = Lists.newArrayList();

                for (Instance<AttributesMap> instance : initialInstances) {
                    if (((String) (instance.getLabel())).equals("positive-both")) {
                        convertedInstances.add(new InstanceImpl<AttributesMap>(instance.getAttributes(), 1.0));
                    } else if (instance.getLabel().equals("negative") || instance.getLabel().equals("positive-first")) {
                        convertedInstances.add(new InstanceImpl<AttributesMap>(instance.getAttributes(), 0.0));
                    } else {
                        throw new RuntimeException("missing valid label");
                    }
                }
                return convertedInstances;
            }
        };
// make sure data sets are the same.  Yes.
//first note average click prob different for later data sets
//the number of clicks are different?  confirm these 2 points.



        ClassifierOutOfTimeCrossValidator cv = new ClassifierOutOfTimeCrossValidator(new WeightedAUCCrossValLossFunction(1.0), 0.25, 24, new TestDateTimeExtractor()).labelConverter(labelConverter);
        Map<String, CrossValLossFunction<PredictionMap>> lossFunctions = Maps.newHashMap();
        lossFunctions.put("auc", new NonWeightedAUCCrossValLossFunction());
        lossFunctions.put("rmse", new ClassifierRMSECrossValLossFunction());
        MultiLossFunctionWithModelConfigurations<PredictionMap> multiLossFunctionWithModelConfigurations = new MultiLossFunctionWithModelConfigurations<PredictionMap>(lossFunctions, "Auc");

       multiLossFunctionWithModelConfigurations = cv.getMultipleCrossValidatedLossesWithModelConfiguration(twoStageModelBuilder, instancesWithLabelsForAllStages, multiLossFunctionWithModelConfigurations);
       Map<String, LossWithModelConfiguration> lossWithModelConfigurationMap = multiLossFunctionWithModelConfigurations.getLossesWithModelConfigurations();
       System.out.println(lossWithModelConfigurationMap.toString());
/*
       cv = new ClassifierOutOfTimeCrossValidator(new WeightedAUCCrossValLossFunction(1.0), 0.25, 24, new TestDateTimeExtractor()).labelConverter(labelConverter);
       double clickLoss = cv.getCrossValidatedLoss(twoStageModelBuilder2, instancesWithLabelsForAllStages);
       System.out.println("twoStagelosses: " + clickLoss);
*/
        cv = new ClassifierOutOfTimeCrossValidator(new ClassifierRMSECrossValLossFunction(), 0.25, 24, new TestDateTimeExtractor()).labelConverter(labelConverter);
        double clickLoss = cv.getCrossValidatedLoss(twoStageModelBuilder2, instancesWithLabelsForAllStages);
        System.out.println("twoStagelosses: " + clickLoss);
/*
       cv = new ClassifierOutOfTimeCrossValidator(new WeightedAUCCrossValLossFunction(1.0), 0.25, 24, new TestDateTimeExtractor());

       double compositeModelLoss = cv.getCrossValidatedLoss(pmbWithDatacp, instancesForCompositeModel);
       System.out.println("singleStageloss: " + compositeModelLoss);
*/

    }


    static class TestDateTimeExtractor implements DateTimeExtractor<AttributesMap> {
        @Override
        public DateTime extractDateTime(Instance<AttributesMap> instance) {
            AttributesMap attributes = instance.getAttributes();
            int year = ((Long) attributes.get("timeOfArrival-year")).intValue();
            int month = ((Long) attributes.get("timeOfArrival-monthOfYear")).intValue();
            int day = ((Long) attributes.get("timeOfArrival-dayOfMonth")).intValue();
            int hour = ((Long) attributes.get("timeOfArrival-hourOfDay")).intValue();
            int minute = ((Long) attributes.get("timeOfArrival-minuteOfHour")).intValue();
            return new DateTime(year, month, day, hour, minute, 0, 0);
        }
    }
}
