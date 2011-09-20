package com.uprizer.quickdt;

import java.util.Set;

import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;

import com.moboscope.quickdt.*;
import com.moboscope.quickdt.scorers.*;

public class TreeBuilderTest {
	@Test
	public void simpleBmiTest() {
		final Set<Instance> instances = Sets.newHashSet();

		for (int x = 0; x < 20; x++) {
			final double height = (4 * 12) + Misc.random.nextInt(3 * 12);
			final double weight = 120 + Misc.random.nextInt(110);
			instances.add(Instance.create(bmiHealthy(weight, height), "weight", weight, "height", height));
		}
		final TreeBuilder tb = new TreeBuilder();
		final Node tree = tb.buildTree(instances, 100, 1.0);
		System.out.println("Tree size: " + tree.size());
		tree.dump(System.out);

	}

	@Test()
	public void multiScorerBmiTest() {
		final Set<Instance> instances = Sets.newHashSet();

		for (int x = 0; x < 10000; x++) {
			final double height = (4 * 12) + Misc.random.nextInt(3 * 12);
			final double weight = 120 + Misc.random.nextInt(110);
			final Instance instance = Instance.create(bmiHealthy(weight, height), "weight", weight, "height", height);
			System.out.println(instance);
			instances.add(instance);
		}
		{
			final TreeBuilder tb = new TreeBuilder(new Scorer1());
			final Node tree = tb.buildTree(instances, 100, 1.0);
			System.out.println("Scorer1 tree size: " + tree.size());
		}
		{
			final TreeBuilder tb = new TreeBuilder(new Scorer2());
			final Node tree = tb.buildTree(instances, 100, 1.0);
			System.out.println("Scorer2 tree size: " + tree.size());
		}
		{
			final TreeBuilder tb = new TreeBuilder(new Scorer3());
			final Node tree = tb.buildTree(instances, 100, 1.0);
			System.out.println("Scorer3 tree size: " + tree.size());
		}
		{
			final TreeBuilder tb = new TreeBuilder(new CorrectClassificationProbScorer());
			final Node tree = tb.buildTree(instances, 100, 1.0);
			System.out.println("CorrectClassificationProbScorer tree size: " + tree.size());
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
