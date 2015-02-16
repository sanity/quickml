package quickml.supervised.crossValidation.lossfunctions;

public interface LossFunction <L, R> {

    public L getLoss(R results);

    public String getName();

}
