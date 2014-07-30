package quickdt;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class Label<L extends Serializable> {
    L label;

    public Label(L labal) {
        this.label = labal;
    }
}
