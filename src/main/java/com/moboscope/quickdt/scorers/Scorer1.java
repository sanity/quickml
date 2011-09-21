package com.moboscope.quickdt.scorers;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Sets;
import com.moboscope.quickdt.Scorer;

public class Scorer1 implements Scorer {

	/*
	 * The best scorer so far, fast with small trees
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
