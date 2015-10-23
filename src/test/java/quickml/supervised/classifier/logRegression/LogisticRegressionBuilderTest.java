package quickml.supervised.classifier.logRegression;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.BenchmarkTest;
import quickml.InstanceLoader;
import quickml.data.OnespotDateTimeExtractor;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.OnespotNormalizedDateTimeExtractor;
import quickml.supervised.classifier.logisticRegression.LogisticRegressionBuilder;
import quickml.supervised.classifier.logisticRegression.SGD;
import quickml.supervised.classifier.logisticRegression.SparseClassifierInstance;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLogCVLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierRMSELossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.dataProcessing.instanceTranformer.CommonCoocurrenceProductFeatureAppender;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.classifier.logisticRegression.LogisticRegressionDataTransformer;
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
        CommonCoocurrenceProductFeatureAppender productFeatureAppender = new CommonCoocurrenceProductFeatureAppender<>()
                .setMinObservationsOfRawAttribute(35)
                .setAllowCategoricalProductFeatures(true)
                .setAllowNumericProductFeatures(false)
                .setApproximateOverlap(true)
                .setMinOverlap(20)
                .setIgnoreAttributesCommonToAllInsances(true);

        LogisticRegressionDataTransformer logisticRegressionDataTransformer = new LogisticRegressionDataTransformer(productFeatureAppender);
        List<SparseClassifierInstance> sparseClassifierInstances = logisticRegressionDataTransformer.transformInstances(instances);
        LogisticRegressionBuilder logisticRegressionBuilder =  new LogisticRegressionBuilder(logisticRegressionDataTransformer.getNameToIndexMap());
        logisticRegressionBuilder.calibrateWithPoolAdjacentViolators(true).gradientDescent(new SGD()
                        .ridgeRegularizationConstant(0.1)
                        .learningRate(.0025)
                        .minibatchSize(3000)
                        .minEpochs(256000)
                        .maxEpochs(256000)
                        .minPredictedProbablity(1E-3)
                        .sparseParallelization(true)
        );
        CrossValidator  crossValidator = new CrossValidator(logisticRegressionBuilder,
                new ClassifierLossChecker(new WeightedAUCCrossValLossFunction(1.0)),
                        new OutOfTimeData<SparseClassifierInstance>(sparseClassifierInstances, 0.25, 48, new OnespotNormalizedDateTimeExtractor(logisticRegressionDataTransformer.getMeanStdMaxMins())));

        logger.info("LR out of time loss: {}", crossValidator.getLossForModel());

        RandomDecisionForestBuilder<ClassifierInstance> randomDecisionForestBuilder = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>().minAttributeValueOccurences(2).maxDepth(12).minLeafInstances(0).minSplitFraction(.005).ignoreAttributeProbability(0.5)).numTrees(64);
           crossValidator = new CrossValidator(randomDecisionForestBuilder,
                new ClassifierLossChecker(new WeightedAUCCrossValLossFunction(1.0)),
                new OutOfTimeData<ClassifierInstance>(instances, 0.25, 48, new OnespotDateTimeExtractor()));

        logger.info("RF out of time loss: {}", crossValidator.getLossForModel());

    }
    @Ignore
    @Test
    public void testDiabetesInstances() {
        //need a builder
        List<ClassifierInstance> instances = BenchmarkTest.loadDiabetesDataset();
        CommonCoocurrenceProductFeatureAppender productFeatureAppender = new CommonCoocurrenceProductFeatureAppender<>().setMinObservationsOfRawAttribute(1)
                .setAllowNumericProductFeatures(true)
                .setApproximateOverlap(true)
                .setMinOverlap(0);
        LogisticRegressionDataTransformer logisticRegressionDataTransformer = new LogisticRegressionDataTransformer(productFeatureAppender);

        List<SparseClassifierInstance> sparseClassifierInstances = logisticRegressionDataTransformer.transformInstances(instances);
        LogisticRegressionBuilder logisticRegressionBuilder = new LogisticRegressionBuilder(logisticRegressionDataTransformer.getNameToIndexMap()).calibrateWithPoolAdjacentViolators(true);
        logisticRegressionBuilder.gradientDescent(new SGD()
                .executorThreadCount(4)
                .sparseParallelization(false)
                .ridgeRegularizationConstant(.1)
                .learningRate(.001)
                .minibatchSize(600)
                .minEpochs(32000)
                .maxEpochs(32000)
                .useBoldDriver(false)
                .learningRateReductionFactor(0.01));
        ClassifierLossFunction lossFunction = new ClassifierLogCVLossFunction(1E-6);//);//new ClassifierRMSELossFunction();//new WeightedAUCCrossValLossFunction(1.0);//new ClassifierRMSELossFunction();//new ClassifierLogCVLossFunction(1E-5);//new WeightedAUCCrossValLossFunction(1.0);
        CrossValidator crossValidator = new CrossValidator(logisticRegressionBuilder,
                new ClassifierLossChecker(lossFunction),
                new FoldedData(sparseClassifierInstances, 4, 4));
        logger.info("LR out of time loss: {}", crossValidator.getLossForModel());

        RandomDecisionForestBuilder<ClassifierInstance> randomDecisionForestBuilder = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>().minAttributeValueOccurences(2).maxDepth(5).minLeafInstances(20).minSplitFraction(.005).ignoreAttributeProbability(0.5)).numTrees(64);
        crossValidator = new CrossValidator(randomDecisionForestBuilder,
                new ClassifierLossChecker(lossFunction),
                new FoldedData(sparseClassifierInstances, 4, 4));

        logger.info("RF out of time loss: {}", crossValidator.getLossForModel());
    }

    }