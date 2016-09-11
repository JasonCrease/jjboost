import com.jasoncrease.GBTrees;
import com.jasoncrease.validation.CrossValidator;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by jason on 11/09/2016.
 */
public class CrossValidatorTests {
    @Test
    public void Test1() throws IOException {

        double[][] xs = DataSets.getIrisData()._xs;
        double[]   ys = DataSets.getIrisData()._ys;

        GBTrees.GBTreesBuilder gbTreesBuilder = new GBTrees.GBTreesBuilder().setMaxTrees(50).setMaxTreeDepth(6);

        CrossValidator.CrossValidatorBuilder crossValidatorBuilder = (new CrossValidator.CrossValidatorBuilder())
                .setFolds(4)
                .setTreeBuilder(gbTreesBuilder)
                .setXs(xs)
                .setYs(ys);

        CrossValidator crossValidator = crossValidatorBuilder.build();
        crossValidator.run();
    }
}
