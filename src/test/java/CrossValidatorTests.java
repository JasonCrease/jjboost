import com.jasoncrease.Classifier;
import com.jasoncrease.TreesGrower;
import com.jasoncrease.validation.CrossValidator;
import org.junit.Test;

/**
 * Created by jason on 11/09/2016.
 */
public class CrossValidatorTests {
    @Test
    public void Test1() throws Exception {

        double[][] xs = DataSets.getHousePriceData()._xs;
        double[]   ys = DataSets.getHousePriceData()._ys;

       Classifier.ClassifierBuilder classifierBuilder = new Classifier.ClassifierBuilder()
               .setMaxRounds(50)
               .setMaxTreeDepth(7);

        CrossValidator.CrossValidatorBuilder crossValidatorBuilder = (new CrossValidator.CrossValidatorBuilder())
                .setFolds(3)
                .setClassifier(classifierBuilder)
                .setXs(xs)
                .setYs(ys);

        CrossValidator crossValidator = crossValidatorBuilder.build();
        crossValidator.run();
    }
}
