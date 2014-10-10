package quickml.experiments;

import com.google.common.collect.Lists;
import com.twitter.common.util.Random;
import org.joda.time.DateTime;
import quickml.Utilities.CSVToInstanceReader;
import quickml.Utilities.CSVToInstanceReaderBuilder;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.supervised.PredictiveModelWithDataBuilder;
import quickml.supervised.calibratedPredictiveModel.CalibratedPredictiveModel;
import quickml.supervised.calibratedPredictiveModel.CalibratedPredictiveModelBuilder;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.ClassifierOutOfTimeCrossValidator;
import quickml.supervised.crossValidation.LabelConverter;
import quickml.supervised.crossValidation.OutOfTimeCrossValidator;
import quickml.supervised.crossValidation.crossValLossFunctions.ClassifierRMSECrossValLossFunction;
import quickml.supervised.crossValidation.crossValLossFunctions.NonWeightedAUCCrossValLossFunction;
import quickml.supervised.crossValidation.crossValLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderhawk on 10/7/14.
 */
public class Comparison2 {
    public static void main(String[] args) {
        //Get Auc loss of a 2stage model and a one stage
        CSVToInstanceReader csvToInstanceReader = new CSVToInstanceReaderBuilder().collumnNameForLabel("outcome").buildCsvReader();
        ArrayList<Instance<AttributesMap>> allLabels = Lists.newArrayList();
        ArrayList<Instance<AttributesMap>> clickLabels = Lists.newArrayList();
        ArrayList<Instance<AttributesMap>> clickToPageViewLabels = Lists.newArrayList();
        ArrayList<Instance<AttributesMap>> clickPageViewLabels = Lists.newArrayList();

        try {
     //       allLabels = csvToInstanceReader.readCsv("allLabelsW.csv");//("cShort.csv");
     //       clickLabels = csvToInstanceReader.readCsv("clickLabelW.csv");//("cShort.csv");
     //       clickToPageViewLabels = csvToInstanceReader.readCsv("targetpageViewLabelW.csv");//;("c2pShort.csv");
     //       clickPageViewLabels = csvToInstanceReader.readCsv("clickPageViewLabelW.csv");//("cpShort.csv");

           allLabels = csvToInstanceReader.readCsv("aShortW.csv");
            clickLabels = csvToInstanceReader.readCsv("cShortW.csv");
           clickToPageViewLabels = csvToInstanceReader.readCsv("c2pShortW.csv");
         clickPageViewLabels = csvToInstanceReader.readCsv("cpShortW.csv");

        } catch (Exception e) {
            //throw new IOException();
        }
        int numClickPageViews = 0, numPageViews = 0, numClicks=0;
        for (Instance<AttributesMap> instance : clickPageViewLabels) {
            if(((Double)instance.getLabel()).equals(1.0))
                numClickPageViews++;
        }
        //System.out.println("num Clickpvs: " + numClickPageViews);

        for (Instance<AttributesMap> instance : clickToPageViewLabels) {
            if(((Double)instance.getLabel()).equals(1.0))
                numPageViews++;
        }
        //System.out.println("numpvs: " + numPageViews);

        for (Instance<AttributesMap> instance : clickLabels) {
            if(((Double)instance.getLabel()).equals(1.0))
                numClicks++;
        }
       // System.out.println("numClicks: " + numClicks);
       // System.exit(0);

        RandomForestBuilder cRandomForestBuilder = new RandomForestBuilder(new TreeBuilder().minCategoricalAttributeValueOccurances(29)
                .ignoreAttributeAtNodeProbability(.7).maxDepth(16)).numTrees(32);
        RandomForestBuilder c2pRandomForestBuilder = new RandomForestBuilder(new TreeBuilder().minCategoricalAttributeValueOccurances(29)
                .ignoreAttributeAtNodeProbability(.7).maxDepth(16)).numTrees(32);
        RandomForestBuilder cpRandomForestBuilder = new RandomForestBuilder(new TreeBuilder().minCategoricalAttributeValueOccurances(29)
                .ignoreAttributeAtNodeProbability(.7).maxDepth(16)).numTrees(40);
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



        // TwoStageModelBuilder twoStageModelBuilder = new TwoStageModelBuilder(cCalibratedPredictiveModelBuilder, c2pCalibratedPredictiveModelBuilder);
       TwoStageModelBuilder twoStageModelBuilder = new TwoStageModelBuilder(pmbWithDatac, pmbWithDatac2p);
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


        ClassifierOutOfTimeCrossValidator cv = new ClassifierOutOfTimeCrossValidator(new ClassifierRMSECrossValLossFunction(), 0.25, 24, new TestDateTimeExtractor()).labelConverter(labelConverter);
        double clickLoss = cv.getCrossValidatedLoss(twoStageModelBuilder, allLabels);
        System.out.println("twoStageloss: " + clickLoss);
        cv = new ClassifierOutOfTimeCrossValidator(new ClassifierRMSECrossValLossFunction(), 0.25, 24, new TestDateTimeExtractor());

        //double clickPageViewLoss = cv.getCrossValidatedLoss(pmbWithDatacp, clickPageViewLabels);
        double clickPageViewLoss = cv.getCrossValidatedLoss(pmbWithDatacp, clickPageViewLabels);

        System.out.println("singleStageloss: " + clickPageViewLoss);

       // ClassifierOutOfTimeCrossValidator cv2 = new ClassifierOutOfTimeCrossValidator(new NonWeightedAUCCrossValLossFunction(), 0.25, 24, new TestDateTimeExtractor());
       // double clickOnlyLoss = cv2.getCrossValidatedLoss(cRandomForestBuilder, clickLabels);
       // System.out.println("clickRandomForest Loss: " + clickOnlyLoss);


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
