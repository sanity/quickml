package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickml.data.AttributesMap;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.regressionLossFunctions.RegressionLossFunction;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

public class RegAttributeImportanceFinderBuilder<T extends RegressionInstance> {


    private PredictiveModelBuilder<? extends PredictiveModel<AttributesMap, Double>, T> modelBuilder;
    private TrainingDataCycler<T> dataCycler;
    private double percentAttributesToRemovePerIteration = 0.2;
    private int numberOfIterations = 5;
    private Set<String> attributesToKeep = Sets.newHashSet();
    private List<RegressionLossFunction> lossFunctions = Lists.newArrayList();
    private RegressionLossFunction primaryLossFunction;


    public RegAttributeImportanceFinderBuilder<T> modelBuilder(PredictiveModelBuilder<? extends PredictiveModel<AttributesMap, Double>, T> modelBuilder) {
        this.modelBuilder = modelBuilder;
        return this;
    }

    public RegAttributeImportanceFinderBuilder<T> dataCycler(TrainingDataCycler<T> dataCycler) {
        this.dataCycler = dataCycler;
        return this;
    }

    public RegAttributeImportanceFinderBuilder<T> numOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
        return this;
    }

    public RegAttributeImportanceFinderBuilder<T> percentAttributesToRemovePerIteration(double attributesToRemovePerIteration) {
        this.percentAttributesToRemovePerIteration = attributesToRemovePerIteration;
        return this;
    }

    public RegAttributeImportanceFinderBuilder<T> primaryLossFunction(RegressionLossFunction primaryLossFunction) {
        lossFunctions.add(primaryLossFunction);
        this.primaryLossFunction = primaryLossFunction;
        return this;
    }

    public RegAttributeImportanceFinderBuilder<T> lossFunction(RegressionLossFunction lossFunction) {
        this.lossFunctions.add(lossFunction);
        return this;
    }

    public RegAttributeImportanceFinderBuilder<T> attributesToKeep(Set<String> attributesToKeep) {
        this.attributesToKeep = attributesToKeep;
        return this;
    }

    public RegAttributeImportanceFinder<T> build() {
        checkArgument(primaryLossFunction != null, "A primary loss function must be set");
        checkArgument(modelBuilder != null, "Must supply a model builder");
        checkArgument(dataCycler != null, "Must supply a data cycler");

        return new RegAttributeImportanceFinder<>(modelBuilder, dataCycler, percentAttributesToRemovePerIteration,
                numberOfIterations, attributesToKeep, lossFunctions, primaryLossFunction);
    }


}
