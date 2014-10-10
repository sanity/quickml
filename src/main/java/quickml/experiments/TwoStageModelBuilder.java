package quickml.experiments;

import com.google.common.collect.Lists;
import org.javatuples.Pair;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 10/7/14.
 */
public class TwoStageModelBuilder implements PredictiveModelBuilder<AttributesMap,TwoStageModel> {
    PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedModelBuilder1;
    PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedModelBuilder2;

    public TwoStageModelBuilder(PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedModelBuilder1,
            PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedModelBuilder2)  {
        this.wrappedModelBuilder1 = wrappedModelBuilder1;
        this.wrappedModelBuilder2 = wrappedModelBuilder2;
    }

    @Override
    public TwoStageModel buildPredictiveModel(Iterable<? extends Instance<AttributesMap>> trainingData) {
        Pair<Iterable<? extends Instance<AttributesMap>>, Iterable<? extends Instance<AttributesMap>>> trainingPair = separateTrainingData(trainingData);
        Classifier c1 = wrappedModelBuilder1.buildPredictiveModel(trainingPair.getValue0());
        Classifier c2 = wrappedModelBuilder2.buildPredictiveModel(trainingPair.getValue1());
        return new TwoStageModel(c1, c2);
    }

    private Pair<Iterable<? extends Instance<AttributesMap>>, Iterable<? extends Instance<AttributesMap>>> separateTrainingData( Iterable<? extends Instance<AttributesMap>> trainingData) {
        List<Instance<AttributesMap>> t1 = Lists.newArrayList();
        List<Instance<AttributesMap>> t2 = Lists.newArrayList();
        boolean foundMarkerLabel = false;
        for (Instance<AttributesMap> instance : trainingData) {
            if (((Double)(instance.getLabel())).equals(Double.valueOf(-100))) {
                foundMarkerLabel = true;
                continue;
            }

            if (foundMarkerLabel) {
                t2.add(instance);
            }
            else {
                t1.add(instance);
            }
        }
        return new Pair<Iterable<? extends Instance<AttributesMap>>, Iterable<? extends Instance<AttributesMap>>>(t1, t2);
    }



    @Override
    public TwoStageModelBuilder updatable(boolean updatable) {
        wrappedModelBuilder2.updatable(updatable);
        wrappedModelBuilder1.updatable(updatable);
        return this;
    }
    @Override
    public void setID(Serializable id){
        return;
    }

}
