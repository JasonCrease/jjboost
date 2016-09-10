package com.jasoncrease;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by jason on 28/08/2016.
 */
public class Splitter implements ISplitter {

    @Override
    public SplitInfo getBestSplit(double[][] xs, double[] ys) {
        SplitInfo[] splits = getSplits(xs, ys);

        SplitInfo bestSplit = splits[0];

        for (int i = 1; i < splits.length; i++)
            if(bestSplit == null || (splits[i] != null && splits[i]._deviance < bestSplit._deviance))
                bestSplit = splits[i];

        return bestSplit;
    }

    @Override
    public SplitInfo[] getSplits(double[][] xs, double ys[]) {
        int numCols = xs.length;
        int numRows = xs[0].length;

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

            double leftSumY = 0;
            double leftSumYSq = 0;
            double rightSumY = 0;
            double rightSumYSq = 0;

            // Calculate totals if everything is classified as 1
            for (int row = 0; row < numRows; row++) {
                int index = es[col][row]._pos;
                double y = ys[index];

                rightSumY   += y;
                rightSumYSq += y * y;
            }

            // Move decision point, recalculating entropy as we go
            // Intialize previousX to the 0th X value
            double previousX = xs[col][es[col][0]._pos];
            SplitInfo bestSplitSoFar = null;
            double bestDevianceSoFar = sumDeviance(0, 0, rightSumY, rightSumYSq, 0, numRows);

            // Note here that the split is at this row, but divides everything to the left
            for (int row = 0; row < numRows; row++) {
                int leftRows = row;
                int rightRows = numRows - row;

                int index = es[col][row]._pos;
                double y = ys[index];
                double x = xs[col][index];

                double deviance = sumDeviance(leftSumY, leftSumYSq, rightSumY, rightSumYSq, leftRows, rightRows);

                // Note that the x != previousX condition is always true for the 0th row
                if (deviance < bestDevianceSoFar && x != previousX)
                {
                    bestDevianceSoFar = deviance;
                    bestSplitSoFar = new SplitInfo(deviance, es[col][row]._val, col, leftSumY / leftRows, rightSumY / rightRows, leftRows, rightRows);
                }

                previousX = x;

                leftSumY    += y;
                leftSumYSq  += y*y;
                rightSumY   -= y;
                rightSumYSq -= y*y;
            }

            bestSplits[col] = bestSplitSoFar;
        }


        // Calculate entropy given split points
        return bestSplits;
    }

    private double sumDeviance(double leftSumY, double leftSumYSq, double rightSumY, double rightSumYSq, int leftRows, int rightRows) {
        double a = 0;
        if(leftRows > 0)
            a = leftSumYSq  - ((leftSumY * leftSumY) / leftRows );
        double b = 0;
        if(rightRows > 0)
            b = rightSumYSq - ((rightSumY * rightSumY) / rightRows);

        return a + b;
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
