package com.jasoncrease;

/**
 * Created by jason on 12/09/2016.
 */
public class MathUtils {

    // http://stackoverflow.com/questions/8422374/java-multi-dimensional-array-transposing
    public static double[][] transposeArray(double[][] arr) {
        int width = arr.length;
        int height = arr[0].length;

        double[][] array_new = new double[height][width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                array_new[y][x] = arr[x][y];
            }
        }
        return array_new;
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

        return y1 * Math.log(y2) +
                (1 - y1) * Math.log(1 - y2);
    }


    public static double logLoss(double[] yActual, double[] yPredicated) {
        double totalLoss = 0f;
        for (int i = 0; i < yActual.length; i++)
            totalLoss += logloss(yActual[i], yPredicated[i]);
        return -totalLoss;
    }
}
