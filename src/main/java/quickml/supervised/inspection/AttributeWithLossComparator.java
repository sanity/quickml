package quickml.supervised.inspection;

import org.javatuples.Pair;
import quickml.supervised.crossValidation.crossValLossFunctions.MultiLossFunctionWithModelConfigurations;

import java.util.Comparator;

/**
 * Created by alexanderhawk on 11/24/14.
 */
public class AttributeWithLossComparator<L,P> implements Comparator<Pair<String, MultiLossFunctionWithModelConfigurations<L,P>>> {
    public final String primaryLossFunction;

    public AttributeWithLossComparator(final String primaryLossFunction) {
        this.primaryLossFunction = primaryLossFunction;
    }

    @Override
    public int compare(Pair<String, MultiLossFunctionWithModelConfigurations<L,P>> o1, Pair<String, MultiLossFunctionWithModelConfigurations<L,P>> o2) {
        return -Double.compare(o1.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss(),
                o2.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss());
    }
}

