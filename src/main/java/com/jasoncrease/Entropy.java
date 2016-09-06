package com.jasoncrease;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by jason on 28/08/2016.
 */
public class Entropy implements IEntropy {

    @Override
    public SplitInfo getBestSplit(double[][] xs, double[] ys) {
        SplitInfo[] splits = getSplits(xs, ys);

        SplitInfo bestSplit = splits[0];

        for (int i = 1; i < splits.length; i++)
            if(bestSplit == null || (splits[i] != null && splits[i]._gain > bestSplit._gain))
                bestSplit = splits[i];

        return bestSplit;
    }

    @Override
    public SplitInfo[] getSplits(double[][] xs, double ys[]) {
        int numCols = xs.length;
        int numRows = xs[0].length;

        // Old implemementation. To return to.
//        // References into the original arrays so we can find which indexes those numbers originally referred to
//        int[][] indexReferences = new int[numCols][numRows];
//        // Fill index-arrays with starting numbers
//        for (int row = 0; row < numRows; row++)
//            indexReferences[0][row] = row;
//        for (int col = 1; col < numCols; col++)
//            indexReferences[col] = indexReferences[0].clone();
//        // Sort the arrays
//        for (int col = 0; col < numCols; col++)
//            Arrays.sort(indexReferences[col], new OtherArrayComparator(xsSorted[col]));

        // Fill entry arrays
        Entry[][] es = new Entry[numCols][numRows];
        for (int col = 0; col < numCols; col++)
            for (int row = 0; row < numRows; row++)
                es[col][row] = new Entry(row, xs[col][row]);


        // Sort them
        for (int col = 0; col < numCols; col++)
            Arrays.sort(es[col], new EntryComparator());


        // Find best split point
        SplitInfo[] bestSplits = new SplitInfo[numCols];

        for (int col = 0; col < numCols; col++) {

            int fps = 0;
            int fns = 0;
            int tps = 0;
            int tns = 0;

            // Calculate totals if everything is classified as 1
            for (int row = 0; row < numRows; row++) {
                int index = es[col][row]._pos;
                double y = ys[index];

                if (y > 0.5)
                    tps++;
                else
                    fps++;
            }

            double initialEntropy = entropy(tps, fps);
            SplitInfo bestSplitSoFar = null;


            // Move decision point, recalculating entropy as we go
            // Intialize previousX to the 0th X value
            double previousX = xs[col][es[col][0]._pos];
            double bestGainSoFar = 0;

            for (int row = 0; row < numRows; row++) {
                int index = es[col][row]._pos;
                double y = ys[index];
                double x = xs[col][index];

                if (y < 0.5) // false-positive is now a true-negative
                {
                    fps--;
                    tns++;
                } else       // true-positive is now a false-negative
                {
                    tps--;
                    fns++;
                }

                double gain = initialEntropy - (entropy(tns, fns) * row  +  entropy(tps, fps) * (numRows - row)) / numRows;

                // Note that the x != previousX condition is always true for the 0th row
                if (gain > bestGainSoFar && x != previousX)
                {
                    int leftRows = row;
                    int rightRows = numRows - row;
                    bestGainSoFar = gain;
                    double leftPurity  = (double)tns / leftRows;
                    double rightPurity = (double)tps / rightRows;

                    bestSplitSoFar = new SplitInfo(gain, es[col][row]._val, col, leftPurity, rightPurity, leftRows, rightRows);
                }

                previousX = x;
            }

            bestSplits[col] = bestSplitSoFar;
        }


        // Calculate entropy given split points
        return bestSplits;
    }



    private double entropy(int a, int b) {
        if(a == 0 || b == 0) return 0;

        double total = a + b;

        double ratio1 = (double) a / total;
        double ratio2 = (double) b / total;

        return -(ratio1 * Math.log(ratio1) + ratio2 * Math.log(ratio2));
    }

    private static class Entry {
        private int _pos;
        private double _val;

        public Entry(int pos, double val) {
            _pos = pos;
            _val = val;
        }
    }

    public class EntryComparator implements Comparator<Entry> {

        @Override
        public int compare(Entry e1, Entry e2) {
            return Double.compare(e1._val, e2._val);
        }
    }
}
