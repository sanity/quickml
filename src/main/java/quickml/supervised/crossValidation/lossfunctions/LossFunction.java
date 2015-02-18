package quickml.supervised.crossValidation.lossfunctions;

public interface LossFunction <R> {

    public Double getLoss(R results);

    public String getName();

}
