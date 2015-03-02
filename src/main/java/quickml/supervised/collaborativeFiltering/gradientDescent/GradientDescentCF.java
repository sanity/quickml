package quickml.supervised.collaborativeFiltering.gradientDescent;

import quickml.supervised.collaborativeFiltering.CollaborativeFilter;
import quickml.supervised.collaborativeFiltering.UserItem;

import java.util.Set;

/**
 * Created by ian on 8/16/14.
 */
public class GradientDescentCF extends CollaborativeFilter {

    private static final long serialVersionUID = 301782468956120672L;

    @Override
    public Double predict(final UserItem attributes) {
        return null;
    }

    @Override
    public Double predictWithoutAttributes(final UserItem attributes, Set<String> attributesToIgnore)
    {
        return null;
    }


}
