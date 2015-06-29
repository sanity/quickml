package quickml.supervised.tree.reducers;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public abstract class Reducer<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>> implements AttributeStatisticsProducer<VC> {

  protected List<I> trainingData;

  //TODO: find way to eventually to not use setter (maybe have create a reducer factory that I pass around).
  public void setTrainingData(List<I> trainingData) {
    this.trainingData = trainingData;
  }

  public abstract Optional<AttributeStats<VC>> getAttributeStats(String attribute);

  public abstract void updateBuilderConfig(Map<String, Object> cfg);

}
