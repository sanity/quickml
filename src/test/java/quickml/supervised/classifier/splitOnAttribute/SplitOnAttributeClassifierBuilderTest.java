package quickml.supervised.classifier.splitOnAttribute;


import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import quickml.supervised.InstanceLoader;
import quickml.supervised.crossValidation.attributeImportance.LossFunctionTracker;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.ClassifierRMSELossFunction;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.predictiveModelOptimizer.MultiLossModelTester;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.tree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder;
import quickml.supervised.ensembles.RandomForestBuilder;
import quickml.supervised.crossValidation.lossfunctions.LossFunctionCorrectedForDownsampling;
import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertTrue;


public class SplitOnAttributeClassifierBuilderTest {

    private List<InstanceWithAttributesMap> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances();

    }

    @Test
    public void advertisingDataTest() {
        List<SplitOnAttributeClassifierBuilder.SplitModelGroup> splitModelGroupCollection = new ArrayList<>();
        splitModelGroupCollection.add(createSplitModelGroup(0, newHashSet("_830", "_833"), 0.1, 2000));
        splitModelGroupCollection.add(createSplitModelGroup(1, newHashSet("_792"), 0.4, 100));
        int defaultGroup = 0;

        RandomForestBuilder randomForestBuilder = new RandomForestBuilder();
        DownsamplingClassifierBuilder downsamplingBuilder = new DownsamplingClassifierBuilder(randomForestBuilder, 0.30D);
        SplitOnAttributeClassifierBuilder splitOnAttributeClassifierBuilder = new SplitOnAttributeClassifierBuilder("campaignId", splitModelGroupCollection, defaultGroup, downsamplingBuilder);
        splitOnAttributeClassifierBuilder.updateBuilderConfig(createModelConfig());

        List<ClassifierLossFunction> lossFunctions = Lists.newArrayList();
        lossFunctions.add(new LossFunctionCorrectedForDownsampling(new WeightedAUCCrossValLossFunction(1.0), 0.99, Double.valueOf(0.0)));
        lossFunctions.add(new LossFunctionCorrectedForDownsampling(new ClassifierLogCVLossFunction(0.000001), 0.99, Double.valueOf(0.0)));
        lossFunctions.add(new LossFunctionCorrectedForDownsampling(new ClassifierRMSELossFunction(), 0.99, Double.valueOf(0.0)));
        lossFunctions.add(new WeightedAUCCrossValLossFunction(1.0));
        lossFunctions.add(new ClassifierLogCVLossFunction(0.00001));
        lossFunctions.add(new ClassifierRMSELossFunction());


        // Get the losses for a split model
        MultiLossModelTester splitModelTester = new MultiLossModelTester(splitOnAttributeClassifierBuilder, new OutOfTimeData<>(instances, 0.15, 24, new OnespotDateTimeExtractor()));
        LossFunctionTracker splitLosses = splitModelTester.getMultilossForModel(lossFunctions);

        // Get the losses for a non split model
        MultiLossModelTester singleModelTester = new MultiLossModelTester(downsamplingBuilder, new OutOfTimeData<>(instances, 0.15, 24, new OnespotDateTimeExtractor()));
        LossFunctionTracker singleLosses = singleModelTester.getMultilossForModel(lossFunctions);

        // Log losses
        splitLosses.logLosses();
        singleLosses.logLosses();

        // Verify that split model is no worse than regular model
        for (String function : splitLosses.lossFunctionNames()) {
            assertTrue(val1NotWorseThanVal2(0.1, singleLosses.getLossForFunction(function), splitLosses.getLossForFunction(function)));
        }


    }

    private SplitOnAttributeClassifierBuilder.SplitModelGroup createSplitModelGroup(int id, Set<String> group0Campaigns, double percentageOfCrossData, int minTotalSamples) {
        HashMap<Integer, Double> relativeImportance = new HashMap<>();
        relativeImportance.put(1, 1.0);
        return new SplitOnAttributeClassifierBuilder.SplitModelGroup(id, group0Campaigns, minTotalSamples, percentageOfCrossData, relativeImportance);
    }

    private Map<String, Object> createModelConfig() {
        Map<String, Object> predictiveModelParameters = new HashMap<>();
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
        return predictiveModelParameters;
    }

    private boolean val1NotWorseThanVal2(double tolerance, double val1, double val2) {
        return Math.abs((val1 - val2) / val1) < tolerance || val1 > val2;
    }

}
