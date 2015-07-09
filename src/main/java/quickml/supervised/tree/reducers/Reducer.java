package quickml.supervised.tree.reducers;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public abstract class Reducer<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>> implements AttributeStatisticsProducer<VC> {
  private final List<I> trainingData;

  public Reducer(List<I> trainingData) {
    this.trainingData = trainingData;
  }

  public List<I> getTrainingData() {
    return trainingData;
  }

  public abstract Optional<AttributeStats<VC>> getAttributeStats(String attribute);


}
