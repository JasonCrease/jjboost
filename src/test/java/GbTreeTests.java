import com.jasoncrease.TreesGrower;
import com.jasoncrease.validation.Performance;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;


public class GbTreeTests {

    Random random = new Random(2);

    @Test
    public void RandomNumbers() throws Exception {

        double[][] dataXs = DataSets.getRandomXs(10, 200000);
        double[]   dataYs = DataSets.getRandomYs(200000);

        TreesGrower gbTrees = (new TreesGrower.GBTreesBuilder()).setMaxTrees(50).setMaxTreeDepth(2).build();
        gbTrees.train(dataXs, dataYs);
    }

    @Test
    public void HouseData() throws Exception {
        double[][] dataXs = DataSets.getHousePriceData()._xs;
        double[]   dataYs = DataSets.getHousePriceData()._ys;

        TreesGrower gbTrees = (new TreesGrower.GBTreesBuilder()).setMaxTrees(20).setMaxTreeDepth(3).build();
        gbTrees.train(dataXs, dataYs);
        double[] yPreds = gbTrees.predict(dataXs);

        Performance perf = Performance.build(dataYs, yPreds);
        Assert.assertEquals(0.97, perf.getAucroc(), 1e-4);
    }

    @Test
    public void IrisData() throws Exception {
        double[][] dataXs = DataSets.getIrisData()._xs;
        double[]   dataYs = DataSets.getIrisData()._ys;

        TreesGrower gbTrees = (new TreesGrower.GBTreesBuilder()).setMaxTrees(25).setMaxTreeDepth(3).build();
        gbTrees.train(dataXs, dataYs);
    }

}
