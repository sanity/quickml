package quickdt.predictiveModelOptimizer;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.AbstractInstance;
import quickdt.PredictiveModelBuilderBuilder;
import quickdt.experiments.crossValidation.CrossValidator;

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
    }

    public BestOptimum(int numOptima, List<Parameter> parameters, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.numOptima = numOptima;
        this.parameters = parameters;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = new CrossValidator();
        this.trainingData = trainingData;
    }

    public BestOptimum(int numOptima, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.numOptima = numOptima;
        this.parameters = predictiveModelBuilderBuilder.createDefaultParameters();
        this.crossValidator = new CrossValidator();
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.trainingData = trainingData;
    }
    public Map<String, Object> findBestOptimum() {
        Map<String, Object> bestPredictiveModelConfig = null;
        Map<String, Object> trialPredictiveModelConfig = null;

        PredictiveModelOptimizer predictiveModelOptimizer;
        List<Parameter> localParameters;

        double minLoss = 0, loss = 0;
        for (int i = 0; i < numOptima; i++) {
            loss = 0;
            localParameters = Lists.<Parameter>newArrayList();
            for (Parameter parameter : parameters)
                localParameters.add(new Parameter(parameter));
            predictiveModelOptimizer = new PredictiveModelOptimizer(crossValidator, localParameters, predictiveModelBuilderBuilder, trainingData);
            trialPredictiveModelConfig = predictiveModelOptimizer.findOptimalParameters();
            loss = (Double)trialPredictiveModelConfig.get("loss");
            if (i==0 || loss < minLoss)  {
                minLoss = loss;
                bestPredictiveModelConfig = trialPredictiveModelConfig;
            }
        }

        return bestPredictiveModelConfig;

    }
}
