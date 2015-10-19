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
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.dataProcessing.LogisticRegressionDataTransformer;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

import java.util.List;

/**
 * Created by alexanderhawk on 10/13/15.
 */
public class LogisticRegressionBuilderTest {
    public static final Logger logger = LoggerFactory.getLogger(LogisticRegressionBuilderTest.class);

    @Test
    public void testAdInstances() {
        //need a builder

        List<ClassifierInstance> instances = InstanceLoader.getAdvertisingInstances();
//        instances = BenchmarkTest.loadDiabetesDataset();
//        ClassifierInstance2SparseClassifierInstance classifierInstance2SparseClassifierInstance = new ClassifierInstance2SparseClassifierInstance(instances);
//        List<SparseClassifierInstance> sparseClassifierInstances = classifierInstance2SparseClassifierInstance.transformAllInstances(instances);

        LogisticRegressionDataTransformer logisticRegressionDataTransformer = new LogisticRegressionDataTransformer();
        List<SparseClassifierInstance> sparseClassifierInstances = logisticRegressionDataTransformer.transformInstances(instances, 45, true, 30);
        LogisticRegressionBuilder logisticRegressionBuilder =  new LogisticRegressionBuilder(logisticRegressionDataTransformer.getNameToIndexMap());
        logisticRegressionBuilder.gradientDescent(new SGD()
                .maxGradientNorm(1)
                .lassoRegularizationConstant(.000000)
                .ridgeRegularizationConstant(0.00001)
                .learningRate(.03).minibatchSize(1000)
                .minEpochs(5000)
                .maxEpochs(5000)
                .useBoldDriver(false)
                .learningRateReductionFactor(0.2))
        ;
    CrossValidator  crossValidator = new CrossValidator(logisticRegressionBuilder,
                new ClassifierLossChecker(new WeightedAUCCrossValLossFunction(1.0)),
                        new OutOfTimeData<SparseClassifierInstance>(sparseClassifierInstances, 0.25, 48, new OnespotNormalizedDateTimeExtractor(logisticRegressionDataTransformer.getMeanStdMaxMins())));

 logger.info("LR out of time loss: {}", crossValidator.getLossForModel());


//        logger.info("LR out of time loss: {}", crossValidator.getLossForModel());
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
        //ClassifierInstance2SparseClassifierInstance classifierInstance2SparseClassifierInstance = new ClassifierInstance2SparseClassifierInstance(instances);
       // List<SparseClassifierInstance> sparseClassifierInstances = classifierInstance2SparseClassifierInstance.transformAllInstances(instances);
        LogisticRegressionDataTransformer logisticRegressionDataTransformer = new LogisticRegressionDataTransformer();
        List<SparseClassifierInstance> sparseClassifierInstances = logisticRegressionDataTransformer.transformInstances(instances, 5, true, 10);


        // LogisticRegressionDataTransformer logisticRegressionDataTransformer = new LogisticRegressionDataTransformer();
        //  List<SparseClassifierInstance> sparseClassifierInstances = logisticRegressionDataTransformer.transformInstances(instances, 5);
        LogisticRegressionBuilder logisticRegressionBuilder = new LogisticRegressionBuilder(logisticRegressionDataTransformer.getNameToIndexMap());
        logisticRegressionBuilder.gradientDescent(new SGD()
                .maxGradientNorm(.3)
//                .lassoRegularizationConstant(.001)
                .ridgeRegularizationConstant(1)
                .learningRate(.025).minibatchSize(9000)
                .minEpochs(10000)
                .maxEpochs(10000)
                .useBoldDriver(false)
                .learningRateReductionFactor(0.2));
        CrossValidator crossValidator = new CrossValidator(logisticRegressionBuilder,
                new ClassifierLossChecker(new WeightedAUCCrossValLossFunction(1.0)),
                new FoldedData(sparseClassifierInstances, 4, 4));
        logger.info("LR out of time loss: {}", crossValidator.getLossForModel());

        RandomDecisionForestBuilder<ClassifierInstance> randomDecisionForestBuilder = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>().minAttributeValueOccurences(2).maxDepth(12).minLeafInstances(0).minSplitFraction(.005).ignoreAttributeProbability(0.5)).numTrees(64);
        crossValidator = new CrossValidator(randomDecisionForestBuilder,
                new ClassifierLossChecker(new WeightedAUCCrossValLossFunction(1.0)),
                new OutOfTimeData<ClassifierInstance>(instances, 0.25, 48, new OnespotDateTimeExtractor()));

        logger.info("RF out of time loss: {}", crossValidator.getLossForModel());
    }

    }