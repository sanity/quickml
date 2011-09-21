package com.uprizer.quickdt;

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import com.moboscope.quickdt.*;
import com.moboscope.quickdt.scorers.Scorer1;

public class TreeBuilderTest {
	@Test
	public void simpleBmiTest() {
		final Set<Instance> instances = Sets.newHashSet();
		for (int x = 0; x < 10000; x++) {
			final double height = (4 * 12) + Misc.random.nextInt(3 * 12);
			final double weight = 120 + Misc.random.nextInt(110);
			instances.add(Instance.create(bmiHealthy(weight, height), "weight", weight, "height", height));
		}
		final TreeBuilder tb = new TreeBuilder();
		final long startTime = System.currentTimeMillis();
		final Node tree = tb.buildTree(instances, 100, 1.0);

		Assert.assertTrue(tree.fullRecall(), "Confirm that the tree achieves full recall on the training set");
		Assert.assertTrue(tree.size() < 400, "Tree size should be less than 350 nodes");
		Assert.assertTrue(tree.meanDepth() < 6, "Mean depth should be less than 6");
		Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,
				"Building this tree should take far less than 20 seconds");
	}

	@Test(enabled = false)
	public void multiScorerBmiTest() {
		final Set<Instance> instances = Sets.newHashSet();

		for (int x = 0; x < 10000; x++) {
			final double height = (4 * 12) + Misc.random.nextInt(3 * 12);
			final double weight = 120 + Misc.random.nextInt(110);
			final Instance instance = Instance.create(bmiHealthy(weight, height), "weight", weight, "height", height);
			instances.add(instance);
		}
		{
			final TreeBuilder tb = new TreeBuilder(new Scorer1());
			final Node tree = tb.buildTree(instances, 100, 1.0);
			System.out.println("Scorer1 tree size: " + tree.size());
		}
	}

	public String bmiHealthy(final double weightInPounds, final double heightInInches) {
		final double bmi = bmi(weightInPounds, heightInInches);
		if (bmi < 20)
			return "underweight";
		else if (bmi > 25)
			return "overweight";
		else
			return "healthy";
	}

	public double bmi(final double weightInPounds, final double heightInInches) {
		return (weightInPounds / (heightInInches * heightInInches)) * 703;
	}
}
