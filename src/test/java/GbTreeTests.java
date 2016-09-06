import com.jasoncrease.GBTrees;
import org.junit.Test;

import java.util.Random;


public class GbTreeTests {

    Random random = new Random(2);

    @Test
    public void Test1() throws Exception {

        double[][] dataXs = DataSets.getHousePriceData()._xs;
        double[]   dataYs = DataSets.getHousePriceData()._ys;

        GBTrees gbTrees = (new GBTrees.GBTreesBuilder()).build();
        gbTrees.train(dataXs, dataYs);

        double[] predVector = new double[dataYs.length];
        for(int col = 0; col < dataYs.length; col++)
            predVector[col] = random.nextDouble() * 200;

        gbTrees.predict(predVector);
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

        GBTrees gbTrees = (new GBTrees.GBTreesBuilder()).build();
        gbTrees.train(dataXs, dataYs);
        gbTrees.predict(predVector);
    }
}
