package quickdt.predictiveModelOptimizer;
/**
 * Created by alexanderhawk on 3/4/14.
 */
public class Parameter {
    public Properties properties;
    public ValueWithPreviousValue trialValues;
    public ValueWithPreviousValue trialErrors;

    public Parameter(Properties properties){
        this.properties = properties;
        trialValues = new ValueWithPreviousValue();
        trialErrors = new ValueWithPreviousValue();
    }

    public Parameter(Parameter parameter) {
        this.properties = new Properties(parameter.properties);
        this.trialErrors = new ValueWithPreviousValue(parameter.trialErrors);
        this.trialValues = new ValueWithPreviousValue(parameter.trialValues);
    }
    
    public static Object newStringOrPrimitiveWrapperFactory(Object obj){
        Object returnObj;
        if (obj instanceof String)
            return new String((String)obj);
        else if (obj instanceof Double)
            return new Double((Double)obj);
        else if (obj instanceof Integer)
            return new Integer((Integer)obj);
        else if (obj instanceof Boolean)
            return new Boolean((Boolean)obj);
        return null;
    }

    class ValueWithPreviousValue {
        public Object current;
        public Object previous;
        
        public ValueWithPreviousValue(){}

        public ValueWithPreviousValue(ValueWithPreviousValue valueWithPreviousValue)  {
            this.current = newStringOrPrimitiveWrapperFactory(valueWithPreviousValue.current);
            this.previous = newStringOrPrimitiveWrapperFactory(valueWithPreviousValue.previous);
        }

        public void setPrevious() {
            previous = current;
        }
    }
}
