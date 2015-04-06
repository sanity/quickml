package quickml.supervised.classifier.tree.decisionTree.scorers;


import quickml.supervised.classifier.tree.decisionTree.tree.DataSummerizer;

/**
 * The scorer is responsible for assessing the quality of a "split" of data.
 */
public abstract class Scorer<D extends DataSummerizer> {
	private double degreeOfGainRatioPenalty;
	private double intrinsicValueOfAttribute;

	/**
	 *
	 * @return A score, where a higher value indicates a better split. A value
	 *         of 0 being the lowest, and indicating no value.
	 */
	 public Scorer(double degreeOfGainRatioPenalty) {
		 this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
	 }
	 public abstract void setIntrinsicValue(double intrinsicValue);
	 public abstract double scoreSplit(D a, D b, double parentScore);

	 private double correctScoreForGainRatioPenalty(double uncorrectedScore) {
		 /** call this method from score split only degreeOfGainRatioPenalty is non zero*/
		 return uncorrectedScore * (1 - degreeOfGainRatioPenalty) + degreeOfGainRatioPenalty * (uncorrectedScore / intrinsicValueOfAttribute);
	 }

}