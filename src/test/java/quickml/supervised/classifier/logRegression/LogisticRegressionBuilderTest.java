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
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.classifier.logisticRegression.LogisticRegressionDataTransformer;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

import java.util.List;

/**
 * Created by alexanderhawk on 10/13/15.
 */
public class LogisticRegressionBuilderTest {
    public static final Logger logger = LoggerFactory.getLogger(LogisticRegressionBuilderTest.class);

    @Ignore //test takes too long
    @Test
    public void testAdInstances() {
        List<ClassifierInstance> instances = InstanceLoader.getAdvertisingInstances();
        LogisticRegressionDataTransformer logisticRegressionDataTransformer = new LogisticRegressionDataTransformer();
        boolean approximateOverlap = true;
        boolean useProductFeatures = true;
        int minOverlap = 30;
        int minimumOccurencesOfAttribute = 45;
        List<SparseClassifierInstance> sparseClassifierInstances = logisticRegressionDataTransformer.transformInstances(instances, minimumOccurencesOfAttribute, minOverlap, approximateOverlap, useProductFeatures);
        LogisticRegressionBuilder logisticRegressionBuilder =  new LogisticRegressionBuilder(logisticRegressionDataTransformer.getNameToIndexMap());
        logisticRegressionBuilder.gradientDescent(new SGD()
                .ridgeRegularizationConstant(0.00001)
                .learningRate(.1).minibatchSize(1000)
                .minEpochs(1000)
                .maxEpochs(1000)
                .useBoldDriver(true)
                .learningRateReductionFactor(0.2))
        ;
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
        List<ClassifierInstance> instances = null;//InstanceLoader.getAdvertisingInstances();
        instances = BenchmarkTest.loadDiabetesDataset();
        LogisticRegressionDataTransformer logisticRegressionDataTransformer = new LogisticRegressionDataTransformer();
        boolean approximateOverlap = true;
        boolean useProductFeatures = false;
        List<SparseClassifierInstance> sparseClassifierInstances = logisticRegressionDataTransformer.transformInstances(instances, 5, 10, approximateOverlap, useProductFeatures);
        LogisticRegressionBuilder logisticRegressionBuilder = new LogisticRegressionBuilder(logisticRegressionDataTransformer.getNameToIndexMap());
        logisticRegressionBuilder.gradientDescent(new SGD()
                .maxGradientNorm(.3)
///              .lassoRegularizationConstant(.001)
                .ridgeRegularizationConstant(.0002)
                .learningRate(.01).minibatchSize(9000)
                .minEpochs(4000)
                .maxEpochs(4000)
                .useBoldDriver(false)
                .learningRateReductionFactor(0.2));
        ClassifierLossFunction lossFunction = new WeightedAUCCrossValLossFunction(1.0);//new ClassifierRMSELossFunction();//new ClassifierLogCVLossFunction(1E-5);//new WeightedAUCCrossValLossFunction(1.0);
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