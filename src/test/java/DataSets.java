import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Created by jason on 31/08/2016.
 */
public class DataSets {

    static Random random = new Random();

    public static double[] getRandomYs(int numRows) {

        double[]   dataYs = new double[numRows];

        for(int row = 0; row < numRows; row++)
            dataYs[row] = random.nextInt(2);

        return dataYs;
    }

    public static double[][] getRandomXs(int numCols, int numRows) {
        double[][] dataXs = new double[numCols][numRows];

        for(int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++)
                dataXs[col][row] = (random.nextDouble() - 0.5) * 200;
        }

        return dataXs;
    }


    public static class DataSet
    {
        double[] _ys;
        double[][] _xs;
        String[] _headers;

        public DataSet(double[] ys, double[][] xs, String[] headers)
        {
            _ys = ys;
            _xs = xs;
            _headers = headers;
        }
    }

    public static DataSet getHousePriceData() throws IOException {
        CSVParser parsed = CSVParser.parse(
                Paths.get((String)"F:\\Github\\jjboost\\data\\kc_house_data.csv").toFile(),
                Charset.defaultCharset(), CSVFormat.EXCEL);

        DataSet houseData = new DataSet(new double[21613], new double[19][21613], new String[19]);
        int rownum = 0;

        for (CSVRecord row : parsed.getRecords()) {
            if(rownum == 0){
                for(int i=1 ; i < 19; i++)
                    houseData._headers[i - 1] = row.get(i);
            }
            else
            {
                houseData._ys[rownum - 1] = Double.parseDouble(row.get(19));

                for(int i=1; i < 19; i++)
                    houseData._xs[i - 1][rownum - 1] = Double.parseDouble(row.get(i));
            }
            rownum++;
        }

        return houseData;
    }

    public static DataSet getIrisData() throws IOException {
        CSVParser parsed = CSVParser.parse(
                Paths.get((String)"F:\\Github\\jjboost\\data\\iris.csv").toFile(),
                Charset.defaultCharset(), CSVFormat.EXCEL);

        int numRows = 150;
        DataSet irisData = new DataSet(new double[numRows], new double[4][numRows], new String[4]);

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

    public static DataSet getMountainData() throws IOException {
        CSVParser parsed = CSVParser.parse(
                Paths.get((String)"F:\\Github\\jjboost\\data\\mountains.csv").toFile(),
                Charset.defaultCharset(), CSVFormat.EXCEL);

        int numRows = 119;
        DataSet mountainData = new DataSet(new double[numRows], new double[6][numRows], new String[6]);

        int rownum = 0;

        for (CSVRecord row : parsed.getRecords()) {
            if(rownum == 0){
                mountainData._headers[0] = row.get(0);
                mountainData._headers[1] = row.get(1);
                mountainData._headers[2] = row.get(2);
                mountainData._headers[3] = row.get(4);
                mountainData._headers[4] = row.get(5);
                mountainData._headers[5] = row.get(6);
            }
            else
            {
                mountainData._ys[rownum - 1] = Double.parseDouble(row.get(7));
                mountainData._xs[0][rownum - 1] = Double.parseDouble(row.get(0));
                mountainData._xs[1][rownum - 1] = Double.parseDouble(row.get(1));
                mountainData._xs[2][rownum - 1] = Double.parseDouble(row.get(2));
                mountainData._xs[3][rownum - 1] = Double.parseDouble(row.get(4));
                mountainData._xs[4][rownum - 1] = Double.parseDouble(row.get(5));
                mountainData._xs[5][rownum - 1] = Double.parseDouble(row.get(6));
            }
            rownum++;
        }

        return mountainData;
    }

}
