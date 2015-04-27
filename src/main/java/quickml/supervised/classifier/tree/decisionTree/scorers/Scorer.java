package quickml.supervised.classifier.tree.decisionTree.scorers;


import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.AttributeStats;

import java.util.List;

/**
 * The scorer is responsible for assessing the quality of a "split" of data.
 */
public abstract class Scorer<TS extends TermStatistics> {
	public static final double NO_SCORE = Double.MIN_VALUE;
	protected double degreeOfGainRatioPenalty;
	protected double intrinsicValue;
	protected double unSplitScore;

	/**
	 *
	 * @return A score, where a higher value indicates a better split. A value
	 *         of 0 being the lowest, and indicating no value.
	 */
	 public Scorer(double degreeOfGainRatioPenalty) {
		 this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
	 }

	  public void setIntrinsicValue(AttributeStats<? extends TermStatistics> attributeStats) {
			 double intrinsicValue = 0;
			 double attributeValProb = 0;

			 for (TermStatistics termStatistics : attributeStats.getTermStats()) {
				 attributeValProb = termStatistics.getTotal() / attributeStats.getAggregateStats().getTotal();//-insufficientDataInstances);
				 intrinsicValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
			 }

			 this.intrinsicValue = intrinsicValue;
		 }

	 public abstract double scoreSplit(TS a, TS b);
	 public abstract void setUnSplitScore(TS a); //internall call {scoreSplit(a, emptyDataSummurizer )};

	 protected double correctScoreForGainRatioPenalty(double uncorrectedScore) {
		 /** call this method from score split only degreeOfGainRatioPenalty is non zero*/
		 return uncorrectedScore * (1 - degreeOfGainRatioPenalty) + degreeOfGainRatioPenalty * (uncorrectedScore / intrinsicValue);
	 }

}