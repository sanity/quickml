package quickdt.predictiveModelOptimizer;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class Parameter {
    public Properties properties;
    public valueWithPreviousValue trialValues;
    public valueWithPreviousValue trialErrors;

    public Parameter(Properties properties){
        this.properties = properties;
        trialValues = new valueWithPreviousValue();
        trialErrors = new valueWithPreviousValue();
    }

    class valueWithPreviousValue {
        public Object current;
        public Object previous;

        public void setPrevious() {
            previous = current;
        }
    }
}
