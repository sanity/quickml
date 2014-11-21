package quickml.supervised.crossValidation;

/**
 * Created by alexanderhawk on 11/20/14.
 */
public interface CrossValidatorBuilder<R, P> {
    public CrossValidator<R, P> createCrossValidator();
}
