package quickdt.predictiveModelOptimizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.AbstractInstance;
import quickdt.PredictiveModelBuilderBuilder;
import quickdt.experiments.crossValidation.CrossValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/12/14.
 */
public class BestOptimum {

    private static final Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizer.class);

    List<Parameter> parameters;
    Map<String, Object> predictiveModelConfig;
    String nameOfPredictiveModel;
    Iterable<AbstractInstance> trainingData;
    private int iterations =0;
    private int maxIterations = 12;
    private int minIterations = 2;
    private PredictiveModelBuilderBuilder predictiveModelBuilderBuilder;
    private CrossValidator crossValidator;
    private int numOptima;

    public BestOptimum(int numOptima, CrossValidator crossValidator, List<Parameter> parameters, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.numOptima = numOptima;
        this.parameters = parameters;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = crossValidator;
        this.trainingData = trainingData;
        this.predictiveModelConfig = predictiveModelBuilderBuilder.createPredictiveModelConfig(parameters);
    }

    public BestOptimum(int numOptima, List<Parameter> parameters, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.numOptima = numOptima;
        this.parameters = parameters;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = new CrossValidator();
        this.trainingData = trainingData;
        this.predictiveModelConfig = predictiveModelBuilderBuilder.createPredictiveModelConfig(parameters);
    }

    public BestOptimum(int numOptima, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.numOptima = numOptima;
        this.parameters = predictiveModelBuilderBuilder.createDefaultParameters();
        this.crossValidator = new CrossValidator();
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.trainingData = trainingData;
        this.predictiveModelConfig = predictiveModelBuilderBuilder.createPredictiveModelConfig(parameters);
    }
    public Map<String, Object> findBestOptimum() {
        Map<String, Object> bestPredictiveModelConfig = null;
        PredictiveModelOptimizer predictiveModelOptimizer;

        for (int i = 0; i < numOptima; i++) {
            double loss = 0, minLoss = 0;
            predictiveModelOptimizer = new PredictiveModelOptimizer(crossValidator, parameters, predictiveModelBuilderBuilder, trainingData);
            if (predictiveModelOptimizer.findOptimalParameters().containsKey("loss"))
                 loss = (Double)predictiveModelOptimizer.findOptimalParameters().get("loss");
            if (i==0 || loss < minLoss)  {
                minLoss = loss;
                bestPredictiveModelConfig = predictiveModelOptimizer.findOptimalParameters();
            }
        }
        //bestPredictiveModelConfig.remove("loss");
        return bestPredictiveModelConfig;

    }
}
