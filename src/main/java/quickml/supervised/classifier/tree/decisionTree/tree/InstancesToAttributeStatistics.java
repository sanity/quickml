package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.AttributeStats;

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
