package quickml.supervised.crossValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.instances.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.EnhancedPredictiveModelBuilder;
import quickml.supervised.classifier.logisticRegression.TransformedData;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.data.TrainingDataCyclerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static quickml.supervised.Utils.getInstanceWeights;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public class EnhancedCrossValidator<PM extends PredictiveModel, I extends Instance, R extends Instance, D extends TransformedData<R,D>> implements CrossValidator {
    //justification for generics: PM needs to be generic to ensure type safety with the LossChecker (as classifiers may have different loss models),
    // R enables the use of more generic types of instances within the LossChecker interface (as R is a collection type param there).
    // Even if the implementation doesn't depend on the details of Instance's concrete type...need this.
    //I is needed as a collection param for the transformed data object. ensures type safety with the raw instances and the modelBuilder that consumes them.
    //finally, we need D as generic as it various buildPredictiveModel methods may require DTO level information that is specific to building that particular predictive model, and we would
    //like the PMB interface for a given PMB to take a particular DTO type. Also, D's genericity satisfies the constraint that we can produce a training data cycler over a list<R>
    //justification for D extending the TranssformedData Class is that it ensures it has methods used by the testModel method, and generic methods in the trainingDataCyclerFactory methods.

    //A less generic alternative, that eliminates usage of D, loses type safety, doesn't achieve decoupling (because Finisher object needs internals knowledge of the PMB) and creates more classes interacting. Do not think it is ideal.
    // the fields and their consumers might be able to be managed outsidef of the predictive model builder though...in which case generics won't be needed.  of course, this complicates things and doesn't afford implementations of
    //Also, any external class that actually builds the model would mean the actual modelBuilder doesn't fulfill its contract of producing a PM...so the other class would need to be set in the
    //in the builder as a field.  Ok so what would creating a setter look like here.  First pass the data to this new object.  then set it in the PM.  There would be no type safety with this approach though...in the sense that we would rely upon the
    //finisher objects abilty to take in the correct params and then finish of the model build.  At the very least, this is not a better approach.  ANd this externalized object will still have no clear interface.
    //really, all it does is allow us to avoid type saftey constraits and separate the builder in small measure from the data specific component of the build...but the Finisher does need to know about the the intrnals of the builder's
    // build method  to complete the build method.

    //The danger of generics is that they can spread like wild fire.  But the way I use them, they don't that much. The only awkward thing is that predictiveModelBuilders have to give their DTO type
    //as a generic...even though there is the hard req. that it has a particular type.

    //question: do I need generics instead of Interfaces? If for example we have

    private static final Logger logger = LoggerFactory.getLogger(SimpleCrossValidator.class);


    private LossChecker<PM, R> lossChecker;
    private TrainingDataCyclerFactory<R, D> dataCyclerFactory;
    private final EnhancedPredictiveModelBuilder<PM, I, R, D> modelBuilder;
    private final List<I> rawInstances;

    public EnhancedCrossValidator(EnhancedPredictiveModelBuilder<PM, I, R, D>  modelBuilder, LossChecker<PM, R> lossChecker, TrainingDataCyclerFactory<R, D> dataCyclerFactory,  List<I> rawInstances) {
        this.lossChecker = lossChecker;
        this.dataCyclerFactory = dataCyclerFactory;
        this.modelBuilder = modelBuilder;
        this.rawInstances = rawInstances;
    }


    /**
     * Get the loss for a model without updating the model config
     */
    public double getLossForModel() {
        return getLossForModel(new HashMap<String, Serializable>());
    }

    public double getLossForModel(Map<String, Serializable> config) {
        if (config.size()!=0) {
            modelBuilder.updateBuilderConfig(config);
        }
        double loss = testModel();
        logger.info("Loss {} for config {}", loss, config.toString());
        return loss;
    }
    /**
     * We keep cycling through the test data, updating the running losses for each run.
     */
    private double testModel() {

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        D transformedData = modelBuilder.transformData(rawInstances);
        TrainingDataCycler<R> dataCycler = dataCyclerFactory.getTrainingDataCycler(transformedData);

        do {
            List<R> validationSet = dataCycler.getValidationSet();
            D transformedTrainingData = transformedData.copyWithJustTrainingSet(dataCycler.getTrainingSet());
            PM predictiveModel = modelBuilder.buildPredictiveModel(transformedTrainingData);
            double validationSetWeight = getInstanceWeights(validationSet);
            runningLoss += lossChecker.calculateLoss(predictiveModel, validationSet) * validationSetWeight;
            runningWeightOfValidationSet += validationSetWeight;
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return runningLoss / runningWeightOfValidationSet;
    }

}
