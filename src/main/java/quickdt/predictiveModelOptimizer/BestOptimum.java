package quickdt.predictiveModelOptimizer;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.crossValidation.CrossValidator;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/12/14.
 */
public class BestOptimum {

    private static final Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizer.class);

    List<ParameterToOptimize> parametersToOptimize;
    Map<String, Object> predictiveModelConfig;
    String nameOfPredictiveModel;
    Iterable<AbstractInstance> trainingData;
    private int iterations =0;
    private int maxIterations = 12;
    private int minIterations = 2;
    private PredictiveModelBuilderBuilder predictiveModelBuilderBuilder;
    private CrossValidator crossValidator;
    private int numOptima;
    private int userMaxIterations = 12;

    public BestOptimum(int userMaxIterations, int numOptima, CrossValidator crossValidator, List<ParameterToOptimize> parametersToOptimize, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.userMaxIterations = userMaxIterations;
        this.numOptima = numOptima;
        this.parametersToOptimize = parametersToOptimize;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = crossValidator;
        this.trainingData = trainingData;
    }

    public BestOptimum(int numOptima, CrossValidator crossValidator, List<ParameterToOptimize> parametersToOptimize, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.numOptima = numOptima;
        this.parametersToOptimize = parametersToOptimize;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = crossValidator;
        this.trainingData = trainingData;
    }

    public BestOptimum(int numOptima, List<ParameterToOptimize> parametersToOptimize, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.numOptima = numOptima;
        this.parametersToOptimize = parametersToOptimize;
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = new CrossValidator();
        this.trainingData = trainingData;
    }

    public BestOptimum(int numOptima, PredictiveModelBuilderBuilder predictiveModelBuilderBuilder, Iterable<AbstractInstance> trainingData ) {
        this.numOptima = numOptima;
        this.parametersToOptimize = predictiveModelBuilderBuilder.createDefaultParametersToOptimize();
        this.crossValidator = new CrossValidator();
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.trainingData = trainingData;
    }
    public Map<String, Object> findBestOptimum() {
        Map<String, Object> bestPredictiveModelConfig = null;
        Map<String, Object> trialPredictiveModelConfig = null;

        PredictiveModelOptimizer predictiveModelOptimizer;
        List<ParameterToOptimize> localParameters;

        double minLoss = 0, loss = 0;
        for (int i = 0; i < numOptima; i++) {
            loss = 0;
            localParameters = Lists.<ParameterToOptimize>newArrayList();
            for (ParameterToOptimize parameter : parametersToOptimize)
                localParameters.add(new ParameterToOptimize(parameter));
            predictiveModelOptimizer = new PredictiveModelOptimizer(predictiveModelBuilderBuilder, trainingData)
                    .withCrossValidator(crossValidator)
                    .withMaxIterations(userMaxIterations)
                    .withParametersToOptimize(localParameters);
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
