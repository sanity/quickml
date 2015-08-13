package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Lists;
import quickml.data.AttributesMap;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.max;
import static quickml.supervised.Utils.calcResultPredictions;
import static quickml.supervised.Utils.calcResultpredictionsWithoutAttrs;

public class AttributeImportanceFinder <I extends InstanceWithAttributesMap<?>> {
    private final PredictiveModelBuilder<? extends PredictiveModel<AttributesMap, ?>, I> modelBuilder;
    private final TrainingDataCycler<I> dataCycler;
    private final int numAttributesToRemovePerIteration;
    private final int numberOfIterations;
    private Set<String> attributesToNotRemove;
    private final List<ClassifierLossFunction> lossFunctions;
    private final ClassifierLossFunction primaryLossFunction;

   //TODO: upgrade class to work for non classifiers. 2 static methods presently assume a classifier is being used.
    /**
     * Protected, use AttributeImportanceFinderBuilder to create a new instance
     */
    protected AttributeImportanceFinder(PredictiveModelBuilder<? extends PredictiveModel<AttributesMap, ?>, I> modelBuilder,
                                     TrainingDataCycler< I> dataCycler, double percentToRemovePerIteration,
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
                for (InstanceWithAttributesMap instance : dataCycler.getAllData()) {
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
            PredictiveModel<AttributesMap, ?> model = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
            trackLossForEachAttribute(allAttributes, lossTracker, model);
            if (model instanceof Classifier) {
                trackLossForNoMissingAttribute(lossTracker, (Classifier)model);
            }
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return lossTracker;
    }

    private void trackLossForNoMissingAttribute(AttributeLossTracker lossTracker, Classifier model) {
        lossTracker.noMissingAttributeLoss(calcResultPredictions(model, dataCycler.getValidationSet()));
    }

    private void trackLossForEachAttribute(Set<String> allAttributes, AttributeLossTracker lossTracker, PredictiveModel<AttributesMap, ?> model) {
       if (model instanceof Classifier) {
           for (String attribute : allAttributes) {
               lossTracker.updateAttribute(attribute,
                       calcResultpredictionsWithoutAttrs((Classifier)model, dataCycler.getValidationSet(), newHashSet(attribute)));
           }
       }
    }

    private Set<String> getAllAttributes(TrainingDataCycler<I> dataCycler) {
        Set<String> attributes = newHashSet();

        for (I instance : dataCycler.getAllData()) {
            attributes.addAll(instance.getAttributes().keySet());
        }
        return attributes;
    }




}
