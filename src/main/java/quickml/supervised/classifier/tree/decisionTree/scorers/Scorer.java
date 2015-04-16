package quickml.supervised.classifier.tree.decisionTree.scorers;


import quickml.supervised.classifier.tree.decisionTree.tree.GroupStatistics;

/**
 * The scorer is responsible for assessing the quality of a "split" of data.
 */
public abstract class Scorer<GS extends GroupStatistics> {
	private double degreeOfGainRatioPenalty;
	private double intrinsicValueOfAttribute;
	private double unSplitScore;

	/**
	 *
	 * @return A score, where a higher value indicates a better split. A value
	 *         of 0 being the lowest, and indicating no value.
	 */
	 public Scorer(double degreeOfGainRatioPenalty) {
		 this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
	 }
	 public abstract void setIntrinsicValue(double intrinsicValue);
	 public abstract double scoreSplit(GS a, GS b);
	 public abstract void setUnSplitScore(GS a); //internall call {scoreSplit(a, emptyDataSummurizer )};

	 private double correctScoreForGainRatioPenalty(double uncorrectedScore) {
		 /** call this method from score split only degreeOfGainRatioPenalty is non zero*/
		 return uncorrectedScore * (1 - degreeOfGainRatioPenalty) + degreeOfGainRatioPenalty * (uncorrectedScore / intrinsicValueOfAttribute);
	 }

}