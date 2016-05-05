package quickml.supervised.regressionModel.LinearRegression2;

        import quickml.data.instances.SparseRegressionInstance;
        import quickml.supervised.classifier.logisticRegression.TransformedDataWithDates;

        import java.util.HashMap;
        import java.util.List;

/**
 * Created by alexanderhawk on 10/28/15.
 */
public abstract class LinearRegressionDTO<D extends LinearRegressionDTO<D>> implements TransformedDataWithDates<SparseRegressionInstance, D> {

    protected List<SparseRegressionInstance> instances;
    protected HashMap<String, Integer> nameToIndexMap;


    @Override
    public List<SparseRegressionInstance> getTransformedInstances() {
        return instances;
    }

    public HashMap<String, Integer> getNameToIndexMap() {
        return nameToIndexMap;
    }




    public LinearRegressionDTO(List<SparseRegressionInstance> instances,
                               HashMap<String, Integer> nameToIndexMap) {
        this.instances = instances;
        this.nameToIndexMap = nameToIndexMap;
    }

    public LinearRegressionDTO(List<SparseRegressionInstance> instances) {
        this.instances = instances;
    }

}
