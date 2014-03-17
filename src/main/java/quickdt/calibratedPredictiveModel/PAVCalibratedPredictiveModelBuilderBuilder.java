package quickdt.calibratedPredictiveModel;

import com.google.common.collect.Lists;
import quickdt.*;
import quickdt.predictiveModelOptimizer.ParameterToOptimize;
import quickdt.predictiveModelOptimizer.PropertiesBuilder;
import quickdt.randomForest.RandomForestBuilderBuilder;

import java.util.*;

/**
 * Created by alexanderhawk on 3/10/14.
 */
public class PAVCalibratedPredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<CalibratedPredictiveModel, PAVCalibratedPredictiveModelBuilder> {
    private PredictiveModelBuilderBuilder<? extends PredictiveModel, ? extends PredictiveModelBuilder<? extends PredictiveModel>> predictiveModelBuilderBuilder;

    public PAVCalibratedPredictiveModelBuilderBuilder() {
        this.predictiveModelBuilderBuilder = new RandomForestBuilderBuilder();
    }

    public PAVCalibratedPredictiveModelBuilderBuilder(Map<String, Object> predictiveModelParameters) {
        this.predictiveModelBuilderBuilder = new RandomForestBuilderBuilder(predictiveModelParameters);
    }


    public PAVCalibratedPredictiveModelBuilderBuilder(PredictiveModelBuilderBuilder<? extends PredictiveModel, PredictiveModelBuilder<? extends PredictiveModel>> predictiveModelBuilderBuilder) {
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
    }



    @Override
    public List<ParameterToOptimize> createDefaultParametersToOptimize(){
        List<ParameterToOptimize> parameters = predictiveModelBuilderBuilder.createDefaultParametersToOptimize();

        List<Object> binsInCalibratorRange = Lists.<Object>newArrayList(5, 10, 20, 40, 100);
        PropertiesBuilder binsInCalibratorPropertyBuilder = new PropertiesBuilder().setName("binsInCalibrator").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(true).setInitialGuessOfOptimalValue(5).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.9).setRange(binsInCalibratorRange);
        parameters.add(new ParameterToOptimize(binsInCalibratorPropertyBuilder.createProperties()));

        return parameters;
    }

    public Map<String, Object> createPredictiveModelConfig(List<ParameterToOptimize> parametersToOptimize) {
        return predictiveModelBuilderBuilder.createPredictiveModelConfig(parametersToOptimize);
    }

    @Override
    public PAVCalibratedPredictiveModelBuilder buildBuilder (Map<String, Object> predictiveModelParameters){
       PredictiveModelBuilder predictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(predictiveModelParameters);
       return new PAVCalibratedPredictiveModelBuilder(predictiveModelBuilder).binsInCalibrator((Integer)predictiveModelParameters.get("binsInCalibrator"));
    }
}


