package quickml.supervised.inspection;

import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.tree.CategoricalBranch;
import quickml.supervised.classifier.decisionTree.tree.Node;
import quickml.supervised.classifier.decisionTree.tree.NumericBranch;
import quickml.supervised.classifier.randomForest.RandomForest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.zip.GZIPInputStream;


public class RandomForestDumper {

    public void dumpModel(final String dumpFile, RandomForest randomForest) throws IOException {
        PrintStream out = new PrintStream(dumpFile);
        randomForest.dump(out, randomForest.trees.size());
    }

    public void summarizeForest(PrintStream out, RandomForest randomForest) {
        summarizeModel(out, randomForest);
    }

    public void summarizeForest(PrintStream out, String file) {
        RandomForest randomForest = loadModelFromFile(file);
        summarizeModel(out, randomForest);
    }

    public void summarizeModel(PrintStream out, RandomForest forest) {

        List<TreeSummary> summaries = new ArrayList<>();
        for (Tree t : forest.trees) {
            TreeSummary summary = new TreeSummary();
            summary.summarizeNode(t.node, 0);
            summaries.add(summary);
        }

        TreeSummary summary = new TreeSummary();
        for (TreeSummary t : summaries) {
            summary.splits += t.splits;
            for (AttributeSummary as : t.attributes.values()) {
                AttributeSummary fas = summary.attributes.get(as.name);
                if (fas == null) {
                    fas = new AttributeSummary();
                    fas.name = as.name;
                    summary.attributes.put(as.name, fas);
                }
                fas.splitCount+= as.splitCount;
                fas.weightedSplitCount +=as.weightedSplitCount;
                fas.treeCount++;
                for (int i = 0; i < as.depths.length; i++) {
                    fas.depths[i]+= as.depths[i];
                }
            }
        }

        // Output trees, total splits, distinct attributes
        out.format("%d trees, %d total splits, %d distinct attributes\n", forest.trees.size(), summary.splits, summary.attributes.size());

        // Get attributes, sort, emit:
        // - name, # trees, # splits, depths
        List<AttributeSummary> attributes = new ArrayList<>(summary.attributes.values());
        Collections.sort(attributes);
        for (AttributeSummary s : attributes) {
            out.format("%s : %f weightedSplits, %d trees, %d splits\n", s.name, s.weightedSplitCount, s.treeCount, s.splitCount);
            out.format("    depths = %s\n", Arrays.toString(s.depths));
        }

    }

    private static class TreeSummary {
        private int splits;
        private Map<String, AttributeSummary> attributes = new HashMap<>();

        private void summarizeNode(Node node, int currentDepth) {
            if (node instanceof CategoricalBranch) {
                summarizeCategoricalNode((CategoricalBranch)node, currentDepth);
            }
            else if (node instanceof NumericBranch) {
                summarizeNumericNode((NumericBranch) node, currentDepth);
            }
        }

        private void addAttribute(String name, int depth) {
            AttributeSummary attrSummary = attributes.get(name);
            if (attrSummary == null) {
                attrSummary = new AttributeSummary();
                attrSummary.name = name;
                attributes.put(name, attrSummary);
            }
            attrSummary.splitCount++;
            attrSummary.weightedSplitCount = attrSummary.weightedSplitCount + Math.max(0.00000001, 1.0/Math.pow(2, depth));
            attrSummary.depths[depth]++;
        }

        private void summarizeCategoricalNode(CategoricalBranch node, int currentDepth) {
            splits++;
            addAttribute(node.attribute, currentDepth);
            summarizeNode(node.trueChild, currentDepth+1);
            summarizeNode(node.falseChild, currentDepth+1);
        }

        private void summarizeNumericNode(NumericBranch node, int currentDepth) {
            splits++;
            addAttribute(node.attribute, currentDepth);
            summarizeNode(node.trueChild, currentDepth+1);
            summarizeNode(node.falseChild, currentDepth + 1);
        }
    }

    private static class AttributeSummary implements Comparable<AttributeSummary> {
        private String name;

        private int treeCount;
        private int splitCount;
        private double weightedSplitCount;
        private int[] depths= new int[20];

        public int compareTo(AttributeSummary other) {

            int result = -Double.compare(weightedSplitCount, other.weightedSplitCount);
            if (result == 0) {
                result = -Integer.compare(treeCount, other.treeCount);
            }
            if (result == 0) {
                result = -Integer.compare(splitCount, other.splitCount);
            }
            if (result == 0) {
                result = name.compareTo(other.name);
            }
            return result;
        }
    }

    private RandomForest loadModelFromFile(final String modelFile) {
        try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(modelFile)));) {
            return (RandomForest) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error reading predictive model", e);
        }
    }



}
