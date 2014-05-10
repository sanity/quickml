package quickdt.crossValidation;

import com.google.common.base.Supplier;

import java.io.Serializable;

/**
 * AUCCrossValLoss requires binary classification
 *
 * Created by Chris on 5/5/2014.
 */
public class AUCCrossValLossSupplier implements Supplier<AUCCrossValLoss> {
    private final Serializable positiveClassification;

    public AUCCrossValLossSupplier(Serializable positiveClassification) {
        this.positiveClassification = positiveClassification;
    }

    @Override
    public AUCCrossValLoss get() {
        return new AUCCrossValLoss(positiveClassification);
    }
}
