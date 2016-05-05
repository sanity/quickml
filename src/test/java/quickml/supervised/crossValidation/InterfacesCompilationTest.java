package quickml.supervised.crossValidation;

import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.classifier.logisticRegression.LogisticRegression;
import quickml.supervised.classifier.logisticRegression.SparseClassifierInstance;

import java.util.List;

/**
 * Created by alexanderhawk on 11/11/15.
 */
public class InterfacesCompilationTest {

        public interface CopyableData<R extends Instance, T extends Transformed<R>> {
            T copyWithJustTrainingSet(List<R> trainingSet);
        }

        public interface Transformed<R extends Instance> {
            public abstract List<R> getTransformedInstances();
        }


        public interface TransformedData<R extends Instance, D extends TransformedData<R, D>> {//extends Transformed<R>, CopyableData<R, D> {
            D copyWithJustTrainingSet(List<R> trainingSet);
            public abstract List<R> getTransformedInstances();

//        public TransformedData() {
//            super();
//        }
        }

//what is the problem?  copy needs to return the dynamic type of the object, and right now it returns
// a TransformedDataWithDates<I, D>.  What can i do differently?  Move where copy is being called...no a TransformededData object needs to be copyable
        //could reduce the type constraints on D with the documentation saying what it should be.  But this means i can't do meta programming later, on presumed methods.
        //Note in python...can't write generic methods with the assumption that certain objects will have methods?  or can we.  Can't do it safely.



        public interface TransformedDataWithDates<I extends Instance, D extends TransformedDataWithDates<I, D>> extends TransformedData<I, D> {
//        public TransformedDataWithDates() {
//            super();
//        }

            public abstract void getDateTimeExtractor();
        }

        //what type of D is inherited?
        public static abstract class LogisticRegressionDTO<D extends LogisticRegressionDTO<D>> implements TransformedDataWithDates<SparseClassifierInstance, D>
        {

        }

        public static class MeanNormalizedAndDatedLogisticRegressionDTO extends LogisticRegressionDTO<MeanNormalizedAndDatedLogisticRegressionDTO> {
            @Override
            public void getDateTimeExtractor() {

            }

            @Override
            public MeanNormalizedAndDatedLogisticRegressionDTO copyWithJustTrainingSet(final List<SparseClassifierInstance> trainingSet) {
                return null;
            }

            @Override
            public List<SparseClassifierInstance> getTransformedInstances() {
                return null;
            }
        }


        ///gen datatransformers
        public interface DataTransformer<I extends Instance, R extends Instance, D extends TransformedData<R, D>> {//TransformedData<R, D>> {

            void transformData(List<I> rawInstance, D data);
        }

        public static class DatedAndMeanNormalizedLogisticRegressionDataTransformer implements DataTransformer<ClassifierInstance, SparseClassifierInstance, MeanNormalizedAndDatedLogisticRegressionDTO> {
            @Override
            public void transformData(final List<ClassifierInstance> rawInstance, final MeanNormalizedAndDatedLogisticRegressionDTO data) {

            }
        }

        //model builders
        public interface EnhancedPredictiveModelBuilder<P extends PredictiveModel, I extends Instance, R extends Instance, D extends TransformedData<R, D>>
                extends DataTransformer<I, R, D> {

            P buildPredictiveModel(D transformedData);
        }



        public static class LogisticRegressionBuilder<D extends LogisticRegressionDTO<D>> implements EnhancedPredictiveModelBuilder<LogisticRegression, ClassifierInstance, SparseClassifierInstance, D> {
            @Override
            public LogisticRegression buildPredictiveModel(final D transformedData) {
                return null;
            }

            @Override
            public void transformData(final List<ClassifierInstance> rawInstance, final D data) {

            }
        }
        public static void main(String[] args) {
            LogisticRegressionBuilder<MeanNormalizedAndDatedLogisticRegressionDTO> logisticRegressionBuilder = new LogisticRegressionBuilder<MeanNormalizedAndDatedLogisticRegressionDTO>();
        }

        //ok, so what is the problem now.  The methods are not able to use the generics.
        //options: i can change the interface of EnhancedPredictiveModelBuilder.  Or, I can make TransformedDataWithDates do something else in it's interface...i.e. i can make it implement copyable of a different generic type.

    }
