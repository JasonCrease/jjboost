import com.jasoncrease.RegressionTrees;
import org.junit.Test;

public class GetBestFactorTests {

    private void AssertRoughlyTheSame(double d1, double d2) {
        double epsilon = 1e-1;

        if(d1 < d2 - epsilon)
            throw new AssertionError(String.format("d1 < d2. Expected %f got %f", d1, d2));
        if(d1 > d2 + epsilon)
            throw new AssertionError(String.format("d1 > d2. Expected %f got %f", d1, d2));
    }

    @Test
    public void StraightUp()
    {
        double[]  ys = { 1.0, 1.0, 1.0, 1.0, 1.0, 0.0 };
        double[] yds = { 0.4, 0.4, 0.4, 0.4, 0.4, 0.4 };
        double[]  ds = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

        double bestFactor = RegressionTrees.getBestFactor(ys, yds, ds);

        AssertRoughlyTheSame(0.43, bestFactor);
    }


    @Test
    public void StraightDown()
    {
        double[]  ys = { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        double[] yds = { 0.4, 0.4, 0.4, 0.4, 0.4, 0.4 };
        double[]  ds = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

        double bestFactor = RegressionTrees.getBestFactor(ys, yds, ds);

        AssertRoughlyTheSame(-0.23, bestFactor);
    }

    @Test
    public void RoughlyUp()
    {
        double[]  ys = { 1.0, 1.0, 1.0, 1.0, 1.0, 0.0 };
        double[] yds = { 0.5, 0.3, 0.4, 0.5, 0.5, 0.2 };
        double[]  ds = { 0.6, 0.6, 0.7, 0.6, 0.7, 0.5 };

        double bestFactor = RegressionTrees.getBestFactor(ys, yds, ds);

        AssertRoughlyTheSame(0.8, bestFactor);
    }


}
