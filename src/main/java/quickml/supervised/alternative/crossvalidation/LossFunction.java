package quickml.supervised.alternative.crossvalidation;

public interface LossFunction <L, R> {

    public L getLoss(R results);

    public String getName();

}
