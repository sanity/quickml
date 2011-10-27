Quick Decision Tree Learner
===========================

License
-------

QuickDT is released under the GNU Lesser General Public License version 3.

What is it?
-----------
If you are unfamiliar with Decision Tree Learning, it's the process of building a decision tree to categorize things, based
on training data that you feed it.  [Learn more on Wikipedia](http://en.wikipedia.org/wiki/Decision_tree_learning).

QuickDT is a Java Decision Tree Learning library designed to be flexible, easy to use, fast, and effective.  QuickDT was written by
[Ian Clarke](http://blog.locut.us/).  The initial public release was on 2011-09-21.

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
an "attribute" of the instance, and "overweight", "healthy", and "underweight" are all "classifications".  
Attributes.create() is some syntactic sugar that makes it easier to create Instances (although it's easy even without it).

```java
	import quickdt.*;

	final Set<Instance> instances = Sets.newHashSet();
	// A male weighing 168lb that is 55 inches tall, they are overweight
	instances.add(Attributes.create("height", 55, "weight", 168, "gender", "male").classification("overweight"));
	instances.add(Attributes.create("height", 75, "weight", 168, "gender", "female").classification("healthy"));
	instances.add(Attributes.create("height", 74, "weight", 143, "gender", "male").classification("underweight"));
	instances.add(Attributes.create("height", 49, "weight", 144, "gender", "female").classification("underweight"));
	instances.add(Attributes.create("height", 83, "weight", 223, "gender", "male").classification("healthy"));
```

In reality 5 examples wouldn't be enough to learn a useful tree, but you get the idea.  Note that QuickDT can handle two types
of data:

* nominal

These values are categories, for example the attribute "gender" in the example above has possible values "male" and "female".
It is ok for an attribute to have hundreds of possible values, but thousands or tens of thousands may slow down the tree
building process significantly.  Values don't have to be strings, they can be almost any object type that isn't a number.

* ordinal

Numbers like 1.3, -25, or 3.1415.  These can be integers, or floating point numbers, positive or negative, any object that
implements the java.lang.Number class.  Values like Double.NaN, Double.POSITIVE_INFINITY and other non-real numbers aren't 
supported and will probably result in an exception or unexpected behavior.

**WARNING:** Currently QuickDT assumes that the same attribute names are present in every instance, and that their type, ordinal or 
nominal, doesn't change from one instance to the next.  QuickDT may crash with an exception if this is not the case.

Next we create a TreeBuilder, and use it to build a tree using this data:

```java
	TreeBuilder treeBuilder = new TreeBuilder();
	Node tree = treeBuilder.buildTree(instances);
```

That's it!  So, let's say that we have a new person and we'd like to use our decision tree to tell us if they are healthy:

```java
	Leaf leaf = tree.getLeaf(Attributes.create("height", 62, "weight", 201, "gender", "female"));
	if (leaf.classification.equals("healthy")) {
		System.out.println("They are healthy!");
	} else if (leaf.classification.equals("underweight")) {
		System.out.println("They are underweight!");
	} else {
		System.out.println("They are overweight!");
	}
```

Its as simple as that!  But what if you wanted to see what the tree looks like?  That's easy too:

```java
	tree.dump(System.out);
```

This is what the output might look like for a larger dataset:

	height > 66.0
	  weight > 174.0
	    height > 72.0
	      weight > 193.0
	        [classification=healthy, depth=4, exampleCount=9880, probability=0.5719635627530364]
	      weight <= 193.0
	        [classification=healthy, depth=4, exampleCount=5231, probability=0.7056012234754349]
	    height <= 72.0
	      weight > 193.0
	        [classification=overweight, depth=4, exampleCount=5514, probability=1.0]
	      weight <= 193.0
	        [classification=overweight, depth=4, exampleCount=2864, probability=0.8837290502793296]
	  weight <= 174.0
	    height > 72.0
	      weight > 157.0
	        [classification=underweight, depth=4, exampleCount=4810, probability=0.6498960498960499]
	      weight <= 157.0
	        weight > 139.0
	          [classification=underweight, depth=5, exampleCount=5032, probability=0.9600556438791733]
	        weight <= 139.0
	          [classification=underweight, depth=5, exampleCount=5664, probability=1.0]
	    height <= 72.0
	      weight > 139.0
	        weight > 157.0
	          [classification=healthy, depth=5, exampleCount=2582, probability=0.7153369481022464]
	        weight <= 157.0
	          [classification=healthy, depth=5, exampleCount=2672, probability=0.8944610778443114]
	      weight <= 139.0
	        [classification=underweight, depth=4, exampleCount=3041, probability=0.8122328181519237]
	height <= 66.0
	  weight > 174.0
	    [classification=overweight, depth=2, exampleCount=26366, probability=1.0]
	  weight <= 174.0
	    height > 60.0
	      weight > 139.0
	        weight > 157.0
	          [classification=overweight, depth=5, exampleCount=2568, probability=1.0]
	        weight <= 157.0
	          [classification=overweight, depth=5, exampleCount=2802, probability=0.6912919343326196]
	      weight <= 139.0
	        [classification=healthy, depth=4, exampleCount=3113, probability=0.8801798907805974]
	    height <= 60.0
	      height > 54.0
	        weight > 139.0
	          [classification=overweight, depth=5, exampleCount=5178, probability=1.0]
	        weight <= 139.0
	          [classification=overweight, depth=5, exampleCount=3026, probability=0.8978849966953073]
	      height <= 54.0
	        [classification=overweight, depth=4, exampleCount=9657, probability=1.0]

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

As QuickDT is still under very active development, it is not yet available via any public Maven repositories.  Once I'm committing
less frequently I'll do a point release and get it into a Maven repo.

Benchmarking
------------

I've done limited benchmarking, but by way of example QuickDT is able to build a tree with 100% recall on a test set with
8,500 instances, where each instance contains 46 attributes, a mixture of nominal and ordinal.  On my several-years-old
MacBook Pro laptop it required only 8 seconds to produce a well-balanced tree with over 500 nodes.

Under the hood
--------------

**Split scoring formula**

Like all decision tree learners, QuickDT uses a formula to determine the quality of a "split" at each branch.  I've tested a wide
variety of formulae, and eventually settled on the one implemented [here](https://github.com/sanity/quickdt/blob/master/src/main/java/quickdt/scorers/Scorer1.java).
So far as I know its a novel approach.  

The basic idea is that the best split is the one with the greatest differences in the 
proportions of the outcomes in each of the two subsets created by the split, multiplied by the size of the smaller set.  In tests 
this performed better than Gini impurity, and various other approaches I tried.  Its easy to try your own, just implement [Scorer](https://github.com/sanity/quickdt/blob/master/src/main/java/quickdt/Scorer.java)
and pass it to the TreeBuilder constructor.

**Finding the best nominal branch**

The algorithm I came up with for proposing a nominal branch may also be novel:

Given a nominal attribute (ie. one where the values are strings), we start by scoring the splits produced by a set of size one, 
testing each value in turn.

We take the best of these, and then try adding another value to this set, again, trying each of the remaining values in turn and
scoring them.

We keep adding to the set until adding a new value results in a lower score than the set has without it.  Once that happens we
terminate, and that's our set.