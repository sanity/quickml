package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Lists;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.data.ClassifierInstance;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.classifier.Classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.max;
import static quickml.supervised.Utils.calcResultPredictions;
import static quickml.supervised.Utils.calcResultpredictionsWithoutAttrs;

public class AttributeImportanceFinder <T extends ClassifierInstance> {
    private final PredictiveModelBuilder<Classifier, T> modelBuilder;
    private final TrainingDataCycler<T> dataCycler;
    private final int numAttributesToRemovePerIteration;
    private final int numberOfIterations;
    private Set<String> attributesToNotRemove;
    private final List<ClassifierLossFunction> lossFunctions;
    private final ClassifierLossFunction primaryLossFunction;


    /**
     * Protected, use AttributeImportanceFinderBuilder to create a new instance
     */
    protected AttributeImportanceFinder(PredictiveModelBuilder<Classifier, T> modelBuilder,
                                     TrainingDataCycler<T> dataCycler, double percentToRemovePerIteration,
                                     int numberOfIterations, Set<String> attributesToNotRemove,
                                     List<ClassifierLossFunction> lossFunctions, ClassifierLossFunction primaryLossFunction) {
        this.modelBuilder = modelBuilder;
        this.dataCycler = dataCycler;
        this.numberOfIterations = numberOfIterations;
        this.attributesToNotRemove = attributesToNotRemove;
        this.lossFunctions = lossFunctions;
        this.primaryLossFunction = primaryLossFunction;
        this.numAttributesToRemovePerIteration = (int) (getAllAttributes(dataCycler).size() * percentToRemovePerIteration);
    }

    public AttributeLossSummary determineAttributeImportance() {
        ArrayList<AttributeLossTracker> attributeLossTrackers = Lists.newArrayList();

        for (int i = 0; i < numberOfIterations; i++) {
            AttributeLossTracker lossTracker = calcLossForAttributes();
            removeLowestPerformingAttributes(lossTracker.getOrderedAttributes());
            attributeLossTrackers.add(lossTracker);
            lossTracker.logResults();
        }

        return new AttributeLossSummary(attributeLossTrackers);
    }

    private void removeLowestPerformingAttributes(List<String> orderedAttributes) {
        for (int i = orderedAttributes.size() - 1; i >= max(0, orderedAttributes.size() - 1 - numAttributesToRemovePerIteration); i--) {
            if (!attributesToNotRemove.contains(orderedAttributes.get(i))) {
                for (ClassifierInstance instance : dataCycler.getAllData()) {
                    instance.getAttributes().remove(orderedAttributes.get(i));
                }
            }
        }
        dataCycler.reset();
    }

    /**
     *
     * We use the attributeLossTracker to keep track of the loss when we remove each individual attribute, and the
     * overall loss when no attribute is removed. This is updated on each cycle of the training/validation set
     *
     */
    private AttributeLossTracker calcLossForAttributes() {
        Set<String> allAttributes = getAllAttributes(dataCycler);
        AttributeLossTracker lossTracker = new AttributeLossTracker(allAttributes, lossFunctions, primaryLossFunction);

        do {
            Classifier model = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
            trackLossForEachAttribute(allAttributes, lossTracker, model);
            trackLossForNoMissingAttribute(lossTracker, model);
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return lossTracker;
    }

    private void trackLossForNoMissingAttribute(AttributeLossTracker lossTracker, Classifier model) {
        lossTracker.noMissingAttributeLoss(calcResultPredictions(model, dataCycler.getValidationSet()));
    }

    private void trackLossForEachAttribute(Set<String> allAttributes, AttributeLossTracker lossTracker, Classifier model) {
        for (String attribute : allAttributes) {
            lossTracker.updateAttribute(attribute,
                    calcResultpredictionsWithoutAttrs(model, dataCycler.getValidationSet(), newHashSet(attribute)));
        }
    }

    private Set<String> getAllAttributes(TrainingDataCycler<T> dataCycler) {
        Set<String> attributes = newHashSet();

        for (T instance : dataCycler.getAllData()) {
            attributes.addAll(instance.getAttributes().keySet());
        }
        return attributes;
    }




}
