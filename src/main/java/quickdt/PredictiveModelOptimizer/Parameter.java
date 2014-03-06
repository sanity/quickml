package quickdt.PredictiveModelOptimizer;

import java.util.List;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class Parameter {
    public Properties properties;
    public TrialValues trialValues;
    public TrialErrors trialErrors;

    public Parameter(Properties properties){
        this.properties = properties;
    }

    class TrialValues {
        public Object current;
        public Object previous;

        public void setPrevious() {
            previous = current;
        }
    }

    class TrialErrors {
        public double current;
        public double previous;

        public void setPrevious() {
            previous = current;
        }
    }
}
