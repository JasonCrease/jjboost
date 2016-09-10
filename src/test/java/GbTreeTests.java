import com.jasoncrease.GBTrees;
import org.junit.Test;

import java.util.Random;


public class GbTreeTests {

    Random random = new Random(2);

    @Test
    public void RandomNumbers() throws Exception {

        double[][] dataXs = DataSets.getRandomXs(10, 200000);
        double[]   dataYs = DataSets.getRandomYs(200000);

        GBTrees gbTrees = (new GBTrees.GBTreesBuilder()).setMaxTrees(50).setMaxTreeDepth(2).build();
        gbTrees.train(dataXs, dataYs);
    }

    @Test
    public void HouseData() throws Exception {
        double[][] dataXs = DataSets.getHousePriceData()._xs;
        double[]   dataYs = DataSets.getHousePriceData()._ys;

        GBTrees gbTrees = (new GBTrees.GBTreesBuilder()).setMaxTrees(50).setMaxTreeDepth(3).build();
        gbTrees.train(dataXs, dataYs);
    }

    @Test
    public void IrisData() throws Exception {
        double[][] dataXs = DataSets.getIrisData()._xs;
        double[]   dataYs = DataSets.getIrisData()._ys;

        GBTrees gbTrees = (new GBTrees.GBTreesBuilder()).setMaxTrees(25).setMaxTreeDepth(3).build();
        gbTrees.train(dataXs, dataYs);
    }

    @Test
    public void Test2() throws Exception {
        int numCols = 5;
        int numRows = 1000;

        double[][] dataXs = DataSets.getRandomXs(numCols, numRows);
        double[]   dataYs = DataSets.getRandomYs(numRows);

        double[] predVector = new double[numCols];
        for(int col = 0; col < numCols; col++)
            predVector[col] = random.nextDouble() * 200;

        GBTrees gbTrees = (new GBTrees.GBTreesBuilder()).setMaxTrees(10).build();
        gbTrees.train(dataXs, dataYs);
        gbTrees.predict(predVector);
    }
}
