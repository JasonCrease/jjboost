import com.jasoncrease.GBTrees;
import com.jasoncrease.validation.CrossValidator;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by jason on 11/09/2016.
 */
public class CrossValidatorTests {
    @Test
    public void Test1() throws Exception {

        double[][] xs = DataSets.getHousePriceData()._xs;
        double[]   ys = DataSets.getHousePriceData()._ys;

        GBTrees.GBTreesBuilder gbTreesBuilder = new GBTrees.GBTreesBuilder().setMaxTrees(550).setMaxTreeDepth(7);

        CrossValidator.CrossValidatorBuilder crossValidatorBuilder = (new CrossValidator.CrossValidatorBuilder())
                .setFolds(3)
                .setTreeBuilder(gbTreesBuilder)
                .setXs(xs)
                .setYs(ys);

        CrossValidator crossValidator = crossValidatorBuilder.build();
        crossValidator.run();
    }
}
