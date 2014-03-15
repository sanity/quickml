package quickdt.calibratedPredictiveModel;

import com.google.common.collect.Lists;
import quickdt.*;
import quickdt.predictiveModelOptimizer.Parameter;
import quickdt.predictiveModelOptimizer.PropertiesBuilder;
import quickdt.randomForest.RandomForestBuilderBuilder;

import java.util.*;

/**
 * Created by alexanderhawk on 3/10/14.
 */
public class PAVCalibratedPredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<CalibratedPredictiveModel, PAVCalibratedPredictiveModelBuilder> {
    private PredictiveModelBuilderBuilder<? extends PredictiveModel, ? extends PredictiveModelBuilder<? extends PredictiveModel>> predictiveModelBuilderBuilder;

    public PAVCalibratedPredictiveModelBuilderBuilder(PredictiveModelBuilderBuilder<? extends PredictiveModel, PredictiveModelBuilder<? extends PredictiveModel>> predictiveModelBuilderBuilder) {
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
    }

    public PAVCalibratedPredictiveModelBuilderBuilder() {
        this.predictiveModelBuilderBuilder = new RandomForestBuilderBuilder();

    }

    @Override
    public List<Parameter> createDefaultParameters(){
        List<Parameter> parameters = predictiveModelBuilderBuilder.createDefaultParameters();

        List<Object> binsInCalibratorRange = Lists.<Object>newArrayList(5, 10, 20, 40, 100);
        PropertiesBuilder binsInCalibratorPropertyBuilder = new PropertiesBuilder().setName("binsInCalibrator").setBinarySearchTheRange(false).setIsMonotonicallyConvergent(true).setInitialGuessOfOptimalValue(5).setIsOrdinal(true).setParameterTolerance(0.39).setErrorTolerance(0.9).setRange(binsInCalibratorRange);
        parameters.add(new Parameter(binsInCalibratorPropertyBuilder.createProperties()));

        return parameters;
    }

    public HashMap<String, Object> createPredictiveModelConfig(List<Parameter> parameters) {
        return predictiveModelBuilderBuilder.createPredictiveModelConfig(parameters);
    }

    @Override
    public PAVCalibratedPredictiveModelBuilder buildBuilder (Map<String, Object> predictiveModelConfig){
       PredictiveModelBuilder predictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(predictiveModelConfig);
       return new PAVCalibratedPredictiveModelBuilder(predictiveModelBuilder).binsInCalibrator((Integer)predictiveModelConfig.get("binsInCalibrator"));
    }
}


