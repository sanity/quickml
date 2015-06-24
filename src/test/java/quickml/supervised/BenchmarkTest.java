package quickml.supervised;

import static com.google.common.collect.Lists.newArrayList;

public class BenchmarkTest {
/*
    private ClassifierLossChecker<InstanceWithAttributesMap> classifierLossChecker;
    private ArrayList<Scorer> scorers;
    private TreeBuilderHelper treeBuilder;
    private RandomDecisionForestBuilder randomDecisionForestBuilder;

    @Before
    public void setUp() throws Exception {
        classifierLossChecker = new ClassifierLossChecker<>(new ClassifierLogCVLossFunction(0.000001));
        scorers = newArrayList(
                new SplitDiffScorer(),
                new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE),
                new MSEScorer(MSEScorer.CrossValidationCorrection.TRUE));
        treeBuilder = createTreeBuilder();
        randomDecisionForestBuilder = createRandomForestBuilder();
    }

    @Test
    public void testDiaInstances() throws Exception {
        testWithInstances("dia", loadDiabetesDataset());
    }

    @Test
    public void testMoboInstances() throws Exception {
        testWithInstances("mobo", loadMoboDataset());
    }

    @Test
    public void performanceTest() throws Exception {
        Random random = new Random();
        List<InstanceWithAttributesMap> instances = loadDiabetesDataset();
        for (int i =1; i<60000; i++) {
            instances.add(instances.size(), instances.get(random.nextInt(instances.size()-1)));
        }
        double time0 = System.currentTimeMillis();
        TreeBuilderHelper<InstanceWithAttributesMap> treeBuilder = new TreeBuilderHelper<>(new GiniImpurityScorer())
                .numSamplesForComputingNumericSplitPoints(50)
                .ordinalTestSplits(5)
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.0))
                .maxDepth(16)
                .minLeafInstances(5);

        treeBuilder.buildPredictiveModel(instances);

        double time1 = System.currentTimeMillis();
        System.out.println("run time in seconds on numeric data set: " + (time1-time0)/1000);

    }


    private void testWithInstances(String dsName, final List<InstanceWithAttributesMap> instances) {
        FoldedData<InstanceWithAttributesMap> data = new FoldedData<>(instances, 4, 4);

        for (final Scorer scorer : scorers) {
            Map<String, Object> cfg = Maps.newHashMap();
            cfg.put(TreeBuilderHelper.SCORER, scorer);
            CrossValidator<Classifier, InstanceWithAttributesMap> validator = new CrossValidator<>(treeBuilder, classifierLossChecker, data);
            System.out.println(dsName + ", single-tree, " + scorer + ", " + validator.getLossForModel(cfg));
            validator = new CrossValidator<>(randomDecisionForestBuilder, classifierLossChecker, data);
            System.out.println(dsName + ", random-forest, " + scorer + ", " + validator.getLossForModel(cfg));
        }
    }

    private List<InstanceWithAttributesMap> loadDiabetesDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(BenchmarkTest.class.getResourceAsStream("diabetesDataset.txt.gz")))));
        final List<InstanceWithAttributesMap> instances = Lists.newLinkedList();


        String line = br.readLine();
        while (line != null) {
            String[] splitLine = line.split("\\s");
            AttributesMap attributes = AttributesMap.newHashMap();
            for (int x = 0; x < 8; x++) {
                attributes.put("attr" + x, Double.parseDouble(splitLine[x]));
            }
            instances.add(new InstanceWithAttributesMap(attributes, splitLine[8]));
            line = br.readLine();
        }

        return instances;
    }


    private List<InstanceWithAttributesMap> loadMoboDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(BenchmarkTest.class.getResourceAsStream("mobo1.json.gz")))));

        final List<InstanceWithAttributesMap> instances = Lists.newLinkedList();

        String line = br.readLine();
        while (line != null) {
            final JSONObject jo = (JSONObject) JSONValue.parse(line);
            AttributesMap a = AttributesMap.newHashMap();
            a.putAll((JSONObject) jo.get("attributes"));
            String binaryClassification = jo.get("output").equals("none") ? "none" : "notNone";
            instances.add(new InstanceWithAttributesMap(a, binaryClassification));
            line = br.readLine();
        }
        return instances;
    }

    private TreeBuilderHelper createTreeBuilder() {
        return new TreeBuilderHelper();
    }

    private RandomDecisionForestBuilder createRandomForestBuilder() {
        return new RandomDecisionForestBuilder(new TreeBuilderHelper().attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7))).numTrees(5);
    }
    */
}

