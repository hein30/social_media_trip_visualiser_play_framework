import weka.estimators.KernelEstimator;

/**
 * Created by Hein Min Htike on 7/31/2017.
 */
public class WekaTest {

  public static void main(String args[]) {
    KernelEstimator estimator = new KernelEstimator(1);

    estimator.addValue(1, 1);
    estimator.addValue(2, 1);
    estimator.addValue(3, 1);
    estimator.addValue(10, 1);
    System.out.println("haha");
  }
}
