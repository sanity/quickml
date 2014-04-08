package quickdt.attributeCombiner;

import com.google.common.base.Function;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.predictiveModels.decisionTree.tree.Leaf;

import java.io.Serializable;

/**
 * Created by ian on 3/29/14.
 */
public class AttributeEnricher implements Function<Attributes, Attributes>, Serializable {
    private static final long serialVersionUID = -1954046068778575676L;
    private final Iterable<AttributePreprocessor> preprocessors;

    public AttributeEnricher(Iterable<AttributePreprocessor> preprocessors) {
        this.preprocessors = preprocessors;
    }

    @Override
    public Attributes apply(final Attributes originalAtributes) {
        Attributes enrichedAttributes = new HashMapAttributes();
        enrichedAttributes.putAll(originalAtributes);
        for (AttributePreprocessor attributePreprocessor : preprocessors) {
            // Note: No need to only give it the attributes it needs, it will pick them out
            final Leaf leaf = attributePreprocessor.tree.node.getLeaf(originalAtributes);
            long guid = leaf.guid;
            enrichedAttributes.put(attributePreprocessor.key, guid);
        }
        return enrichedAttributes;
    }
}
