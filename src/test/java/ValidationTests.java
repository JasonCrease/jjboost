import com.jasoncrease.*;
import com.jasoncrease.validation.Performance;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jason on 10/09/2016.
 */
public class ValidationTests {

    @Test
    public void IrisValidationStats() throws Exception {

        double[] ys = DataSets.getIrisData()._ys;
        double[][] xs = DataSets.getIrisData()._xs;

        ITreeFinder treeFinder = new TreeFinder(new Splitter());
        TreeNode bestTree = treeFinder.getBestTree(xs, ys, 4);

        double[] yPreds = new double[xs[0].length];

        for (int i = 0; i < xs[0].length; i++) {
            double[] predX = new double[]{xs[0][i], xs[1][i], xs[2][i], xs[3][i]};
            yPreds[i] = bestTree.predict(predX);
        }

        Performance perf = Performance.build(ys, yPreds);
        Assert.assertEquals(0.97, perf.getAucroc(), 1e-2);
    }

}
