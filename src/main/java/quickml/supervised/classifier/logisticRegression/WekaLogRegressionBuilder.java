package quickml.supervised.classifier.logisticRegression;

    import com.google.common.collect.Sets;
    import quickml.data.instances.ClassifierInstance;
    import quickml.supervised.PredictiveModelBuilder;
    import weka.classifiers.functions.Logistic;
    import weka.core.*;

    import java.io.Serializable;
    import java.util.HashMap;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Map;

    /**
     * Created by alexanderhawk on 10/9/15.
     */
    public class WekaLogRegressionBuilder implements PredictiveModelBuilder<WekaLogRegression,ClassifierInstance> {
        public static final String LABEL = "label";
        Logistic logisticModelBuilder;
        double ridgeRegularizationConstant=0;
        public static final String RIDGE = "ridge";
        public static final String MAX_ITS = "maxIts";

        public int maxIts;
        private HashMap<String, Integer> nameToIndexMap;

        public WekaLogRegressionBuilder() {
            logisticModelBuilder = new Logistic();
        }

        public WekaLogRegressionBuilder ridgeRegularizationConstant(final double ridgeRegularizationConstant) {
            this.ridgeRegularizationConstant = ridgeRegularizationConstant;
            logisticModelBuilder.setRidge(ridgeRegularizationConstant);
            return this;
        }

        public WekaLogRegressionBuilder maxIts(final int maxIts) {
            this.maxIts = maxIts;
            logisticModelBuilder.setMaxIts(maxIts);
            return this;
        }

        @Override
        public WekaLogRegression buildPredictiveModel(final Iterable<ClassifierInstance> trainingData) {
            Instances instances = getWekaInstances((List<ClassifierInstance>) trainingData);
            try {
                logisticModelBuilder.buildClassifier(instances);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new WekaLogRegression(logisticModelBuilder,nameToIndexMap, instances);
        }

        private Instances getWekaInstances(final List<ClassifierInstance> trainingData) {
            List<ClassifierInstance> trainingDataList = trainingData;
            HashSet<String> attrNames = Sets.newHashSet();
            for (ClassifierInstance instance : trainingDataList) {
                for (String key : instance.getAttributes().keySet()){
                    attrNames.add(key);
                }
            }
            attrNames.add(LABEL);
            FastVector attInfo = new FastVector();
            nameToIndexMap = new HashMap<>();
            int i = 0;
            for (String name : attrNames) {
                attInfo.addElement(new Attribute(name, i));
                nameToIndexMap.put(name, i);
                i++;
            }
            Instances instances = new Instances("name", attInfo, trainingDataList.size());
            instances.setClassIndex(nameToIndexMap.get(LABEL));
            for (ClassifierInstance instance : trainingData) {
                instances.add(WekaUtils.convertClassifierInstanceToSparseInstance(nameToIndexMap, instance, instances));
            }

            return instances;
        }

        @Override
        public void updateBuilderConfig(final Map<String, Serializable> config) {
            if (config.containsKey(RIDGE)) {
                ridgeRegularizationConstant((Double) config.get(RIDGE));
            }
            if (config.containsKey(MAX_ITS)) {
                maxIts((Integer) config.get(MAX_ITS));
            }
        }


    }


