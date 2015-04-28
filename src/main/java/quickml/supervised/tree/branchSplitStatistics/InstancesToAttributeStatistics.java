package quickml.supervised.tree.branchSplitStatistics;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.branchFinders.AttributeStatisticsProducer;
import quickml.supervised.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.branchSplitStatistics.TermStatistics;

import java.util.List;

/**
 * Created by alexanderhawk on 4/16/15.
 */
public abstract class InstancesToAttributeStatistics<L, I extends InstanceWithAttributesMap<L>, TS extends TermStatistics> implements AttributeStatisticsProducer<TS> {

  List<I> trainingData;


  public void setTrainingData(List<I> trainingData) {
    this.trainingData = trainingData;
  }

  public abstract AttributeStats<TS> getAttributeStats(String attribute);
}
