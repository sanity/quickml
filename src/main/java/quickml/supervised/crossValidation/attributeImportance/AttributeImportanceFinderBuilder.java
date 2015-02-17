package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;

import java.util.List;
import java.util.Set;

public class AttributeImportanceFinderBuilder<T extends ClassifierInstance> {


    private PredictiveModelBuilder<Classifier, T> modelBuilder;
    private TrainingDataCycler<T> dataCycler;
    private double percentAttributesToRemovePerIteration;
    private int numberOfIterations;
    private Set<String> attributesToKeep = Sets.newHashSet();
    private List<ClassifierLossFunction> lossFunctions = Lists.newArrayList();
    private ClassifierLossFunction primaryLossFunction;


    public AttributeImportanceFinderBuilder<T> modelBuilder(PredictiveModelBuilder<Classifier, T> modelBuilder) {
        this.modelBuilder = modelBuilder;
        return this;
    }

    public AttributeImportanceFinderBuilder<T> dataCycler(TrainingDataCycler<T> dataCycler) {
        this.dataCycler = dataCycler;
        return this;
    }

    public AttributeImportanceFinderBuilder<T> numOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
        return this;
    }

    public AttributeImportanceFinderBuilder<T> percentAttributesToRemovePerIteration(double attributesToRemovePerIteration) {
        this.percentAttributesToRemovePerIteration = attributesToRemovePerIteration;
        return this;
    }

    public AttributeImportanceFinderBuilder<T> primaryLossFunction(ClassifierLossFunction primaryLossFunction) {
        lossFunctions.add(primaryLossFunction);
        this.primaryLossFunction = primaryLossFunction;
        return this;
    }

    public AttributeImportanceFinderBuilder<T> lossFunction(ClassifierLossFunction lossFunction) {
        this.lossFunctions.add(lossFunction);
        return this;
    }

    public AttributeImportanceFinderBuilder<T> attributesToKeep(Set<String> attributesToKeep) {
        this.attributesToKeep = attributesToKeep;
        return this;
    }

    public AttributeImportanceFinder<T> build() {
        return new AttributeImportanceFinder<>(modelBuilder, dataCycler, percentAttributesToRemovePerIteration,
                numberOfIterations, attributesToKeep, lossFunctions, primaryLossFunction.getName());
    }


}
