package quickml.supervised.classifier.logRegression;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.BenchmarkTest;
import quickml.InstanceLoader;
import quickml.data.OnespotDateTimeExtractor;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.classifier.logisticRegression.*;
import quickml.supervised.crossValidation.EnhancedCrossValidator;
import quickml.supervised.crossValidation.data.FoldedDataFactory;
import quickml.supervised.crossValidation.data.OutOfTimeDataFactory;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.SimpleCrossValidator;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierRMSELossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.dataProcessing.instanceTranformer.CommonCoocurrenceProductFeatureAppender;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

import java.util.List;

/**
 * Created by alexanderhawk on 10/13/15.
 */
public class LogisticRegressionBuilderTest {
    public static final Logger logger = LoggerFactory.getLogger(LogisticRegressionBuilderTest.class);

    @Ignore //test takes too long, but is illustrative of how to build a model
    @Test
    public void testAdInstances() {
        List<ClassifierInstance> instances = InstanceLoader.getAdvertisingInstances();
        logger.info("got instances");
        CommonCoocurrenceProductFeatureAppender productFeatureAppender = new CommonCoocurrenceProductFeatureAppender<>()
                .setMinObservationsOfRawAttribute(35)
                .setAllowCategoricalProductFeatures(true)
                .setAllowNumericProductFeatures(false)
                .setApproximateOverlap(true)
                .setMinOverlap(20)
                .setIgnoreAttributesCommonToAllInsances(true);

        DatedAndMeanNormalizedLogisticRegressionDataTransformer lrdt = new DatedAndMeanNormalizedLogisticRegressionDataTransformer()
                .minObservationsOfAttribute(35)
                .usingProductFeatures(true)
                .productFeatureAppender(productFeatureAppender);

        LogisticRegressionBuilder<MeanNormalizedAndDatedLogisticRegressionDTO> logisticRegressionBuilder =  new LogisticRegressionBuilder<MeanNormalizedAndDatedLogisticRegressionDTO>(lrdt)
                        .calibrateWithPoolAdjacentViolators(false)
                        .gradientDescent(new SparseSGD()
                                        .ridgeRegularizationConstant(0.1)
                                        .learningRate(.0025)
                                        .minibatchSize(1000)
                                        .minEpochs(1000)
                                        .maxEpochs(1000)
                                        .minPredictedProbablity(1E-3)
                                        .sparseParallelization(true)
                        );
        double start = System.nanoTime();
        EnhancedCrossValidator<LogisticRegression, ClassifierInstance, SparseClassifierInstance, MeanNormalizedAndDatedLogisticRegressionDTO> enhancedCrossValidator = new EnhancedCrossValidator<>(logisticRegressionBuilder,
                new ClassifierLossChecker(new WeightedAUCCrossValLossFunction(1.0)),
                        new OutOfTimeDataFactory(0.25, 48), instances);

        double lossForSGD = enhancedCrossValidator.getLossForModel();
        double stop = System.nanoTime();

        logger.info("LR out of time loss: {}, in {} nanoseconds", lossForSGD, stop-start);

        RandomDecisionForestBuilder<ClassifierInstance> randomDecisionForestBuilder = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>().minAttributeValueOccurences(2).maxDepth(12).minLeafInstances(0).minSplitFraction(.005).ignoreAttributeProbability(0.5)).numTrees(64);

           SimpleCrossValidator<LogisticRegression, ClassifierInstance> simpleCrossValidator = new SimpleCrossValidator(randomDecisionForestBuilder,
                new ClassifierLossChecker(new WeightedAUCCrossValLossFunction(1.0)),
                new OutOfTimeData<ClassifierInstance>(instances, 0.25, 48, new OnespotDateTimeExtractor()));

        logger.info("RF out of time loss: {}", simpleCrossValidator.getLossForModel());

    }

    @Test
    public void testDiabetesInstances() {
        //need a builder
        List<ClassifierInstance> instances = BenchmarkTest.loadDiabetesDataset();
        CommonCoocurrenceProductFeatureAppender productFeatureAppender = new CommonCoocurrenceProductFeatureAppender<>().setMinObservationsOfRawAttribute(1)
                .setAllowNumericProductFeatures(true)
                .setApproximateOverlap(true)
                .setMinOverlap(0);
        DatedAndMeanNormalizedLogisticRegressionDataTransformer lrdt = new DatedAndMeanNormalizedLogisticRegressionDataTransformer()
                .minObservationsOfAttribute(1)
                .usingProductFeatures(true)
                .productFeatureAppender(productFeatureAppender);

        LogisticRegressionBuilder<MeanNormalizedAndDatedLogisticRegressionDTO> logisticRegressionBuilder = new LogisticRegressionBuilder<MeanNormalizedAndDatedLogisticRegressionDTO>(lrdt);
        logisticRegressionBuilder.gradientDescent(new SparseSGD()
                .executorThreadCount(3)
                .sparseParallelization(false)
                .ridgeRegularizationConstant(.1)
                .learningRate(.001)
                .minibatchSize(600)
                .minEpochs(16000)
                .maxEpochs(16000)
                .useBoldDriver(false)
                .learningRateReductionFactor(0.01));
        ClassifierLossFunction lossFunction = new ClassifierRMSELossFunction();//);//new ClassifierRMSELossFunction();//new WeightedAUCCrossValLossFunction(1.0);//new ClassifierRMSELossFunction();//new ClassifierLogCVLossFunction(1E-5);//new WeightedAUCCrossValLossFunction(1.0);

        EnhancedCrossValidator<LogisticRegression, ClassifierInstance, SparseClassifierInstance, MeanNormalizedAndDatedLogisticRegressionDTO> enhancedCrossValidator = new EnhancedCrossValidator<>(logisticRegressionBuilder,
                new ClassifierLossChecker(lossFunction),
                new FoldedDataFactory(4, 4), instances);


        logger.info("LR out of time loss: {}", enhancedCrossValidator.getLossForModel());

        RandomDecisionForestBuilder<ClassifierInstance> randomDecisionForestBuilder = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>().minAttributeValueOccurences(2).maxDepth(5).minLeafInstances(20).minSplitFraction(.005).ignoreAttributeProbability(0.5)).numTrees(64);
        SimpleCrossValidator<LogisticRegression, ClassifierInstance> simpleCrossValidator = new SimpleCrossValidator(randomDecisionForestBuilder,
                new ClassifierLossChecker(lossFunction),
                new FoldedData(instances, 4, 4));

        logger.info("RF out of time loss: {}", simpleCrossValidator.getLossForModel());
    }

    }