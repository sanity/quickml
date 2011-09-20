Quick Decision Tree Learner
===========================

By [Ian Clarke](http://blog.locut.us/)

License
-------

QuickDT is released under the GNU Lesser General Public License version 3.

What is it?
-----------
If you are unfamiliar with Decision Tree Learning, read all about it on [Wikipedia](http://en.wikipedia.org/wiki/Decision_tree_learning).

QuickDT is a Java Decision Tree Learning library designed to be flexible, easy to use, fast, and effective.

What are the alternatives?
--------------------------

Prior to starting work on QuickDT, I found two Java decision tree learning libraries.  

[jaDTi](http://www.run.montefiore.ulg.ac.be/~francois/software/jaDTi/) is the first,
it works, its fast, but its API is horrible, it doesn't take advantage of Generics, and forces you to use Vectors (which have been out
of fashion for some years now).  Its last release was in 2004, and it shows.

[Weka](http://www.cs.waikato.ac.nz/ml/weka/) is actually a suite of learning algorithms, which include Decision Tree algorithms.  Its
API is marginally better than jaDTi, but still quite ugly by today's standards.  Worse, it seems to be a memory hog, and had various
other minor annoyances that made it unsuitable for use on Google App Engine, which is what I needed.

Both of these libraries have the annoying limitation that you must pre-process your dataset to determine what values can be contained 
in nominal fields.

How do I use QuickDT?
---------------------

First, we'll create some data.  Let's say we know someone's height, weight, and gender, and we want to create a decision tree
that tells us whether they are underweight, healthy, or overweight.  Each "instance" is a training example.  `"height", 55` is
an "attribute" of the instance, and "overweight", "healthy", and "underweight" are all "outputs".  
Instance.create() is a helper method that makes it easier to create Instances.

	import com.moboscope.quickdt.*;

	final Set<Instance> instances = Sets.newHashSet();
	// A male weighing 168lb that is 55 inches tall, they are overweight
	instances.add(Instance.create("overweight", "height", 55, "weight", 168, "gender", "male"));
	instances.add(Instance.create("healthy", "height", 75, "weight", 168, "gender", "female"));
	instances.add(Instance.create("underweight", "height", 74, "weight", 143, "gender", "male"));
	instances.add(Instance.create("overweight", "height", 49, "weight", 144, "gender", "female"));
	instances.add(Instance.create("healthy", "height", 83, "weight", 223, "gender", "male"));

In reality 5 examples wouldn't be enough to learn a useful tree, but you get the idea.  Note that QuickDT can handle two types
of data, *"nominal"*, like "male", "female", and *"ordinal"*, numbers.  These can be integers, or floating point numbers.

Next we create a TreeBuilder, and use it to build a tree using this data:

	TreeBuilder treeBuilder = new TreeBuilder();
	Node tree = treeBuilder.buildTree(instances);

That's it!  So, let's say that we have a new person and we'd like to use our decision tree to tell us if they are healthy:

	Label label = tree.getLabel(Attributes.create("height", 62, "weight", 201, "gender", "female"));
	if (label.output.equals("healthy")) {
		System.out.println("They are healthy!");
	} else if (label.output.equals("underweight")) {
		System.out.println("They are underweight!");
	} else {
		System.out.println("They are overweight!");
	}

Its as simple as that!  But what if you wanted to see what the tree looks like?  That's easy too:

	tree.dump(System.out);

This is what the output might look like:

	height in [69.0, 80.0, 81.0, 78.0, 74.0, 83.0]
	  gender in [male]
	    weight in [133.0]
	      [output=underweight, depth=3, exampleCount=1, probability=1.0]
	    weight not in [133.0]
	      [output=healthy, depth=3, exampleCount=3, probability=1.0]
	  gender not in [male]
	    [output=underweight, depth=2, exampleCount=5, probability=1.0]
	height not in [69.0, 80.0, 81.0, 78.0, 74.0, 83.0]
	  [output=overweight, depth=1, exampleCount=11, probability=1.0]

Note that there are two types of decisions, depending on whether its an ordinal or nominal field.  If its ordinal, then
QuickDT will normally do a less-than or greater-than decision, if its nominal (or sometimes when its ordinal) it will
be a test to see if the value is or isn't a member of a set.

How do I build QuickDT?
-----------------------

QuickDT is built using [Apache Maven](http://maven.apache.org/) so if you don't have that, you should grab it.  You'll also
need [git](http://git-scm.com/).

First, grab the code from github:

	$ git clone git@github.com:sanity/quickdt.git

Next, build a jar file:

	$ cd quickdt
	$ mvn assembly:assembly

If all goes well, you'll find a file called something like quickdt-0.0.1-SNAPSHOT-jar-with-dependencies.jar in the target/ directory.
Just add this to your classpath and you're off to the races!

As QuickDT is still under very active development, it is not yet available via any public repositories.

Benchmarking
------------

I've done limited benchmarking, but by way of example QuickDT is able to build a tree with 100% recall on a test set with
8,500 instances, where each instance contains 46 attributes, a mixture of nominal and ordinal.  On my several-years-old
MacBook Pro laptop it required only 8 seconds to produce a well-balanced tree with over 500 nodes.

Under the hood
--------------

**Split scoring formula**
Like all decision tree learners, QuickDT uses a formula to determine the quality of a "split" at each branch.  I've tested a wide
variety of formulae, and eventually settled on the one implemented [here](https://github.com/sanity/quickdt/blob/master/src/main/java/com/moboscope/quickdt/scorers/Scorer1.java).
So far as I know its a novel approach.  

The basic idea is that the best split is the one with the greatest differences in the 
proportions of the outcomes in each of the two subsets created by the split, multiplied by the size of the smaller set.  In tests 
this performed better than Gini impurity, and various other approaches I tried.  Its easy to try your own, just implement Scorer
and pass it to the TreeBuilder constructor.

**Finding the best nominal branch**
The algorithm I came up with for proposing a nominal branch may also be novel:

Given a nominal attribute (ie. one where the values are strings), we start by scoring the splits produced by a set of size one, 
testing each value in turn.

We take the best of these, and then try adding another value to this set, again, trying each of the remaining values in turn and
scoring them.

We keep adding to the set until adding a new value results in a lower score than the set has without it.  Once that happens we
terminate, and that's our set.