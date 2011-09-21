package com.moboscope.quickdt.scorers;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Sets;
import com.moboscope.quickdt.Scorer;

public class Scorer1 implements Scorer {

	/*
	 * The general idea here is that a good split is one where the proportions
	 * of classifications on each side of the split are as different as
	 * possible. eg. if 50% of the classifications in set A are "dog", then the
	 * further away from 50% the proportion of "dog" classifications in set B
	 * are, the better.
	 * 
	 * We therefore add up the differences between the proportions, however we
	 * have another goal, which is that its preferable for the sets to be of
	 * close to equal size. Without this requirement a split with 0 on one size
	 * would get a high score because all of the proportions on that side would
	 * be 0.
	 * 
	 * So, we multiply the score by the size of the smallest side, which seems
	 * to provide the bias we want against unequal splits.
	 */

	public double scoreSplit(final int aTtl, final Map<Serializable, Integer> a, final int bTtl,
			final Map<Serializable, Integer> b) {
		double score = 0;
		for (final Serializable value : Sets.union(a.keySet(), b.keySet())) {
			Integer aCount = a.get(value);
			if (aCount == null) {
				aCount = 0;
			}
			Integer bCount = b.get(value);
			if (bCount == null) {
				bCount = 0;
			}

			final double aProp = (double) aCount / aTtl;
			final double bProp = (double) bCount / bTtl;

			score += Math.abs(aProp - bProp) * Math.min(aTtl, bTtl);
		}
		return score;
	}

}
