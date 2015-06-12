package quickml.supervised.tree.branchSplitStatistics;

import quickml.data.InstanceWithAttributesMap;

import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.branchSplitStatistics.TermStatistics;

import java.util.List;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public abstract class InstancesToAttributeStatistics<L, I extends InstanceWithAttributesMap<L>, TS extends TermStatsAndOperations<TS>> implements AttributeStatisticsProducer<TS> {

  protected List<I> trainingData;

  //TODO: don't use setter...make new copy of the class.
  public void setTrainingData(List<I> trainingData) {
    this.trainingData = trainingData;
  }

  public abstract AttributeStats<TS> getAttributeStats(String attribute);
}
