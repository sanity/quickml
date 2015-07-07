package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst;


import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldClassificationCounter;

/**
 * The scorer is responsible for assessing the quality of a "split" of data.
 */
public interface OldScorer {
	/**
	 * Assess the quality of a separation of data
	 * 
	 * @param a
	 *            A count of the number of classifications with a given
	 *            getBestClassification in split a
	 * @param b
	 *            A count of the number of classifications with a given
	 *            getBestClassification in split b
	 * @return A score, where a higher value indicates a better split. A value
	 *         of 0 being the lowest, and indicating no value.
	 */
	public double scoreSplit(OldClassificationCounter a, OldClassificationCounter b);
}