package quickml.supervised.tree.scorers;


import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.nodes.AttributeStats;

/**
 * The scorer is responsible for assessing the quality of a "split" of data.
 */
public abstract class Scorer<TS extends ValueCounter<TS>> {
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

	  public void setIntrinsicValue(AttributeStats<TS> attributeStats) {
			 double intrinsicValue = 0;
			 double attributeValProb = 0;

			 for (TS termStatistics : attributeStats.getTermStats()) {
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