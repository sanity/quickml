package quickml.supervised.crossValidation.attributeImportance;

import com.google.common.collect.Lists;
import quickml.data.AttributesMap;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.regressionLossFunctions.RegressionLossFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.max;
import static quickml.supervised.Utils.*;

public class RegAttributeImportanceFinder<I extends RegressionInstance> {
    private final PredictiveModelBuilder<? extends PredictiveModel<AttributesMap, Double>, I> modelBuilder;
    private final TrainingDataCycler<I> dataCycler;
    private final int numAttributesToRemovePerIteration;
    private final int numberOfIterations;
    private Set<String> attributesToNotRemove;
    private final List<RegressionLossFunction> lossFunctions;
    private final RegressionLossFunction primaryLossFunction;

   //TODO: upgrade class to work for non classifiers. 2 static methods presently assume a classifier is being used.
    /**
     * Protected, use AttributeImportanceFinderBuilder to create a new instance
     */
    protected RegAttributeImportanceFinder(PredictiveModelBuilder<? extends PredictiveModel<AttributesMap, Double>, I> modelBuilder,
                                           TrainingDataCycler<I> dataCycler, double percentToRemovePerIteration,
                                           int numberOfIterations, Set<String> attributesToNotRemove,
                                           List<RegressionLossFunction> lossFunctions, RegressionLossFunction primaryLossFunction) {
        this.modelBuilder = modelBuilder;
        this.dataCycler = dataCycler;
        this.numberOfIterations = numberOfIterations;
        this.attributesToNotRemove = attributesToNotRemove;
        this.lossFunctions = lossFunctions;
        this.primaryLossFunction = primaryLossFunction;
        this.numAttributesToRemovePerIteration = (int) (getAllAttributes(dataCycler).size() * percentToRemovePerIteration);
    }

    public RegAttributeLossSummary determineAttributeImportance() {
        ArrayList<RegAttributeLossTracker> attributeLossTrackers = Lists.newArrayList();

        for (int i = 0; i < numberOfIterations; i++) {
            RegAttributeLossTracker lossTracker = calcLossForAttributes();
            removeLowestPerformingAttributes(lossTracker.getOrderedAttributes());
            attributeLossTrackers.add(lossTracker);
            lossTracker.logResults();
        }

        return new RegAttributeLossSummary(attributeLossTrackers);
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
    private RegAttributeLossTracker calcLossForAttributes() {
        Set<String> allAttributes = getAllAttributes(dataCycler);
        RegAttributeLossTracker lossTracker = new RegAttributeLossTracker(allAttributes, lossFunctions, primaryLossFunction);

        do {
            PredictiveModel<AttributesMap, Double> model = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
            trackLossForEachAttribute(allAttributes, lossTracker, model);
            trackLossForNoMissingAttribute(lossTracker, model);
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return lossTracker;
    }

    private void trackLossForNoMissingAttribute(RegAttributeLossTracker lossTracker, PredictiveModel<AttributesMap, Double> model) {
        lossTracker.noMissingAttributeLoss(getRegLabelsPredictionsWeights(model, dataCycler.getValidationSet()));
    }

    private void trackLossForEachAttribute(Set<String> allAttributes, RegAttributeLossTracker lossTracker, PredictiveModel<AttributesMap, Double> model) {
           for (String attribute : allAttributes) {
               lossTracker.updateAttribute(attribute,
                       calcLabelPredictionsWeightsWithoutAttrs(model, dataCycler.getValidationSet(), newHashSet(attribute)));
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
