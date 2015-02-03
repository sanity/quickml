package quickml.supervised.crossValidation;

/**
 * Created by alexanderhawk on 11/20/14.
 */
public interface CrossValidatorBuilder<R, L, P> {
    public CrossValidator<R, L, P> createCrossValidator();
}
