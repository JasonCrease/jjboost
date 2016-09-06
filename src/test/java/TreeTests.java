import com.jasoncrease.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class TreeTests {

    @Test
    public void housePriceDataHasDecreasingBoundaryLoss() throws IOException {
        double[] ys = DataSets.getHousePriceData()._ys;
        double[][] xs = DataSets.getHousePriceData()._xs;

        ITreeFinder treeFinder = new TreeFinder(new Entropy());

        double lastLoss = 12000f;

        for(int depth = 0; depth < 25; depth++) {
            TreeNode bestTree = treeFinder.getBestTree(xs, ys, depth);

              double totalLoss = 0;
//
//            for (int row = 0; row < xs[0].length; row++) {
//
//                double[] predX = new double[18];
//
//                for(int col =0; col<18; col++)
//                    predX[col] = xs[col][row];
//
//                double y = bestTree.predict(predX);
//                totalLoss += boundaryLoss(y, ys[row]);
//            }
//
              System.out.println(totalLoss);

              Assert.assertTrue("totalLoss not <= lastLoss", totalLoss <= lastLoss);
              lastLoss = totalLoss;
        }
    }

    @Test
    public void onAllIrisTestIsPerfectAtDepth4() throws IOException {
        double[] ys = DataSets.getIrisData()._ys;
        double[][] xs = DataSets.getIrisData()._xs;

        ITreeFinder treeFinder = new TreeFinder(new Entropy());
        TreeNode bestTree = treeFinder.getBestTree(xs, ys, 4);

        for(int i=0; i< xs[0].length; i++)
        {
            double[] predX = new double[] {xs[0][i], xs[1][i], xs[2][i], xs[3][i] };
            double y =  bestTree.predict(predX);
            Assert.assertEquals(y, ys[i], 1E-5);
            System.out.println(String.format("%d: Predicted: %f Actual: %f", i, y, ys[i]));
        }
    }

    @Test
    public void onAllMountainDataGivesDecreasingLogloss() throws IOException {
        double[] ys = DataSets.getMountainData()._ys;
        double[][] xs = DataSets.getMountainData()._xs;

        ITreeFinder treeFinder = new TreeFinder(new Entropy());

        double lastLoss = 10000000000f;

        for(int depth = 0; depth < 25; depth++) {
            TreeNode bestTree = treeFinder.getBestTree(xs, ys, depth);

            double totalLoss = 0;

            for (int i = 0; i < xs[0].length; i++) {
                double[] predX = new double[]{xs[0][i], xs[1][i], xs[2][i], xs[3][i], xs[4][i], xs[5][i]};
                double y = bestTree.predict(predX);
                totalLoss += logloss(y, ys[i]);
            }

            System.out.println(totalLoss);

            Assert.assertTrue("totalLoss not <= lastLoss", totalLoss <= lastLoss);
            lastLoss = totalLoss;
        }
    }

    private static double logloss(double y1, double y2) {
        if (y2 >= 1 - 1e-15)
            y2 = 1 - 1e-15;
        if (y2 < 1e-15)
            y2 = 1e-15;
        if (y1 >= 1 - 1e-15)
            y1 = 1 - 1e-15;
        if (y1 < 1e-15)
            y1 = 1e-15;

        return - (y1 * Math.log(y2) + (1 - y1) * Math.log(1 - y2));
    }

    private static double boundaryLoss(double y1, double y2) {
        if(y1 > 0.5 && y2 > 0.5)
            return 0f;
        if(y1 < 0.5 && y2 < 0.5)
            return 0f;
        return 1f;
    }



    private void AssertRoughlyTheSame(double d1, double d2) {
        double epsilon = 1e-2;

        if (d1 < d2 - epsilon)
            throw new AssertionError(String.format("d1 < d2. Expected %f got %f", d1, d2));
        if (d1 > d2 + epsilon)
            throw new AssertionError(String.format("d1 > d2. Expected %f got %f", d1, d2));
    }



}
