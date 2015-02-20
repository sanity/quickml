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
    public static final String NO_MISSING_ATTRIBUTE = "NO_MISSING_ATTRIBUTE";
    private final PredictiveModelBuilder<Classifier, T> modelBuilder;
    private final TrainingDataCycler<T> dataCycler;
    private int numAttributesToRemoveInPresentIteration;
    private final int numberOfIterations;
    private Set<String> attributesToNotRemove;
    private final List<ClassifierLossFunction> lossFunctions;
    private final String primaryLossFunction;
    private final double percentToRemovePerIteration;


    /**
     * Protected, use AttributeImportanceFinderBuilder to create a new instance
     */
    protected AttributeImportanceFinder(PredictiveModelBuilder<Classifier, T> modelBuilder,
                                     TrainingDataCycler<T> dataCycler, double percentToRemovePerIteration,
                                     int numberOfIterations, Set<String> attributesToNotRemove,
                                     List<ClassifierLossFunction> lossFunctions, String primaryLossFunction) {
        this.modelBuilder = modelBuilder;
        this.dataCycler = dataCycler;
        this.numberOfIterations = numberOfIterations;
        this.attributesToNotRemove = attributesToNotRemove;
        this.lossFunctions = lossFunctions;
        this.primaryLossFunction = primaryLossFunction;
        this.percentToRemovePerIteration = percentToRemovePerIteration;
        this.numAttributesToRemoveInPresentIteration = (int) (getAllAttributes(dataCycler).size() * percentToRemovePerIteration);
    }

    public List<AttributeLossTracker> determineAttributeImportance() {
        ArrayList<AttributeLossTracker> attributeLossTrackers = Lists.newArrayList();

        for (int i = 0; i < numberOfIterations; i++) {
            AttributeLossTracker lossTracker = calcLossForAttributes();
            removeLowestPerformingAttributes(lossTracker.getOrderedAttributes());
            attributeLossTrackers.add(lossTracker);
            lossTracker.logResults();
        }

        return attributeLossTrackers;
    }

    private void removeLowestPerformingAttributes(List<String> orderedAttributes) {
        for (int i = orderedAttributes.size() - 1; i >= max(0, orderedAttributes.size() - 1 - numAttributesToRemoveInPresentIteration); i--) {
            if (!attributesToNotRemove.contains(orderedAttributes.get(i))) {
                for (ClassifierInstance instance : dataCycler.getAllData()) {
                    instance.getAttributes().remove(orderedAttributes.get(i));
                }
            }
        }
        numAttributesToRemoveInPresentIteration = (int)((orderedAttributes.size()-numAttributesToRemoveInPresentIteration)*percentToRemovePerIteration);
        dataCycler.reset();
    }

    private AttributeLossTracker calcLossForAttributes() {
        Set<String> allAttributes = getAllAttributes(dataCycler);
        AttributeLossTracker lossTracker = new AttributeLossTracker(allAttributes, lossFunctions, primaryLossFunction);

        do {
            Classifier model = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
            for (String attribute : allAttributes) {
                PredictionMapResults results = calcResultpredictionsWithoutAttrs(model, dataCycler.getValidationSet(), newHashSet(attribute));
                lossTracker.updateAttribute(attribute, results);
            }
            lossTracker.updateAttribute(NO_MISSING_ATTRIBUTE, calcResultPredictions(model, dataCycler.getValidationSet()));
            dataCycler.nextCycle();

        } while (dataCycler.hasMore());

        return lossTracker;
    }

    private Set<String> getAllAttributes(TrainingDataCycler<T> dataCycler) {
        Set<String> attributes = newHashSet();

        for (T instance : dataCycler.getAllData()) {
            attributes.addAll(instance.getAttributes().keySet());
        }
        return attributes;
    }




}
