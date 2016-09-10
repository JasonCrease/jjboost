import com.jasoncrease.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class SplitterTests {

    @Test
    public void allXsSame() throws IOException {
        double[] ys = new double[]       { 1, 1, 1, 0, 1, 1, 0, 0, 1};
        double[][] xs = new double[][] { { 2, 2, 2, 2, 2, 2, 2, 2, 2 } };

        SplitInfo[] splits = new Splitter().getSplits(xs, ys);
        Assert.assertNull(splits[0]);
    }

    @Test
    public void twosAnd3s() throws IOException {
        double[] ys = new double[]       { 1, 1, 1, 0, 1, 1, 0, 0, 1};
        double[][] xs = new double[][] { { 2, 2, 2, 2, 3, 3, 3, 3, 3 } };

        SplitInfo split = new Splitter().getBestSplit(xs, ys);
        Assert.assertEquals(3.0, split._value, 1e-5);
    }

    @Test
    public void splitAt6() throws IOException {
        double[] ys = new double[]       { 0, 0, 0, 0, 0, 1, 1, 1, 1 };
        double[][] xs = new double[][] { { 2, 3, 1, 1, 5, 7, 6, 9, 8 } };

        SplitInfo split = new Splitter().getBestSplit(xs, ys);
        Assert.assertEquals(6.0, split._value, 1e-5);
    }

    @Test
    public void allYsSame() throws IOException {
        double[] ys = new double[]       { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        double[][] xs = new double[][] { { 2, 2, 1, 1, 3, 3, 3, 4, 4 } };

        SplitInfo split = new Splitter().getBestSplit(xs, ys);
        Assert.assertNull(split);
    }

    @Test
    public void oneItem() throws IOException {
        double[] ys = new double[]       { 1 };
        double[][] xs = new double[][] { { 2  } };

        SplitInfo split = new Splitter().getBestSplit(xs, ys);
        Assert.assertNull(split);
    }

    @Test
    public void twoItems() throws IOException {
        double[] ys = new double[]       { 1, 0 };
        double[][] xs = new double[][] { { 2, 6  } };

        SplitInfo split = new Splitter().getBestSplit(xs, ys);
        Assert.assertEquals(6.0, split._value, 1e-5);
    }

    @Test
    public void gainsOnAllIrisData() throws IOException {
        double[]   ys = getIrisData()._ys;
        double[][] xs = getIrisData()._xs;
        String[] headers = getIrisData()._headers;

        ISplitter entropy = new Splitter();

        SplitInfo[] splits = entropy.getSplits(xs, ys);

        System.out.println("Feature        Deviance   Split value");
        for(int i=0; i < splits.length; i++)
        {
            System.out.println(String.format("%13s  %.5f     %.5f",
                    headers[i], splits[i]._deviance, splits[i]._value
            ));
        }
    }

    @Test
    public void bestSplitColumnAndValue() throws IOException {
        double[]   ys = getIrisData()._ys;
        double[][] xs = getIrisData()._xs;

        SplitInfo split = new Splitter().getBestSplit(xs, ys);

        Assert.assertEquals(3, split._col, 3);
        Assert.assertEquals(1.8, split._value, 1E-6);
        Assert.assertEquals(104, split._leftSize);
        Assert.assertEquals(0.048, split._leftMean, 1E-2);
        Assert.assertEquals(46, split._rightSize);
        Assert.assertEquals(0.978, split._rightMean, 1E-2);
    }

    class IrisData
    {
        double[] _ys;
        double[][] _xs;
        String[] _headers;

        public IrisData(double[] ys, double[][] xs, String[] headers)
        {
            _ys = ys;
            _xs = xs;
            _headers = headers;
        }
    }

    private IrisData getIrisData() throws IOException {
        CSVParser parsed = CSVParser.parse(
                Paths.get((String)"F:\\Github\\jjboost\\data\\iris.csv").toFile(),
                Charset.defaultCharset(), CSVFormat.EXCEL);

        int numRows = 150;
        IrisData irisData = new IrisData(new double[numRows], new double[4][numRows], new String[4]);

        int rownum = 0;

        for (CSVRecord row : parsed.getRecords()) {
            if(rownum == 0){
                irisData._headers[0] = row.get(0);
                irisData._headers[1] = row.get(1);
                irisData._headers[2] = row.get(2);
                irisData._headers[3] = row.get(3);
            }
            else
            {
                irisData._ys[rownum - 1] = Double.parseDouble(row.get(4));
                irisData._xs[0][rownum - 1] = Double.parseDouble(row.get(0));
                irisData._xs[1][rownum - 1] = Double.parseDouble(row.get(1));
                irisData._xs[2][rownum - 1] = Double.parseDouble(row.get(2));
                irisData._xs[3][rownum - 1] = Double.parseDouble(row.get(3));
            }
            rownum++;
        }

        return irisData;
    }

    private void AssertRoughlyTheSame(double d1, double d2) {
        double epsilon = 1e-2;

        if (d1 < d2 - epsilon)
            throw new AssertionError(String.format("d1 < d2. Expected %f got %f", d1, d2));
        if (d1 > d2 + epsilon)
            throw new AssertionError(String.format("d1 > d2. Expected %f got %f", d1, d2));
    }



}
