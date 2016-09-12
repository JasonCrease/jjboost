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

}
