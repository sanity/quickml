package quickml.supervised.tree.branchSplitStatistics;

import quickml.data.InstanceWithAttributesMap;

import quickml.supervised.tree.nodes.AttributeStats;

import java.util.List;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public abstract class TrainingDataReducer<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>> implements AttributeStatisticsProducer<VC> {

  protected List<I> trainingData;

  //TODO: don't use setter...make new copy of the class.
  public void setTrainingData(List<I> trainingData) {
    this.trainingData = trainingData;
  }

  public abstract AttributeStats<VC> getAttributeStats(String attribute);
}
