package quickml.supervised.alternative.optimizer;

public interface RunningLoss<T> {

    public void addLoss(T loss);

}
