import com.jasoncrease.Classifier;
import com.jasoncrease.MathUtils;
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

        Classifier classifier = (new Classifier.ClassifierBuilder())
                .setMaxRounds(5)
                .setMaxTreeDepth(2)
                .setTrainXs(dataXs)
                .setTrainYs(dataYs)
                .build();
        classifier.run();
    }

    @Test
    public void HouseData() throws Exception {
        double[][] dataXs = DataSets.getHousePriceData()._xs;
        double[]   dataYs = DataSets.getHousePriceData()._ys;

        Classifier classifier = (new Classifier.ClassifierBuilder())
                .setMaxRounds(1)
                .setMaxTreeDepth(8)
                .setShrinkage(0.2)
                .setTrainXs(dataXs)
                .setTrainYs(dataYs)
                .build();
        classifier.run();

        double[][] yPreds = classifier.predict(dataXs);

        Performance perf = Performance.build(dataYs, MathUtils.transposeArray(yPreds)[0]);
        Assert.assertEquals(0.97, perf.getAucroc(), 1e-4);

        for (int i = 0; i < 100; i++)
            System.out.println(yPreds[i][0]);
    }

    @Test
    public void IrisData() throws Exception {
        double[][] dataXs = DataSets.getIrisData()._xs;
        double[]   dataYs = DataSets.getIrisData()._ys;

        Classifier classifier = (new Classifier.ClassifierBuilder())
                .setMaxRounds(10)
                .setMaxTreeDepth(3)
                .setTrainXs(dataXs)
                .setTrainYs(dataYs)
                .build();
        classifier.run();

        double[][] yPreds = classifier.predict(dataXs);
        for (int i = 0; i < dataXs[0].length; i++)
            System.out.println(yPreds[i][0] + "," + yPreds[i][1] + "  ");
    }

}
