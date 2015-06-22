package quickml.supervised.tree.reducers;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public abstract class Reducer<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>> implements AttributeStatisticsProducer<VC> {

  protected List<I> trainingData;

  //TODO: don't use setter...make new copy of the class.
  public void setTrainingData(List<I> trainingData) {
    this.trainingData = trainingData;
  }

  public abstract AttributeStats<VC> getAttributeStats(String attribute);

  public abstract void update(Map<String, Object> cfg);

}
