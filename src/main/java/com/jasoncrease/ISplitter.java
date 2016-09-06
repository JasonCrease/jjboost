package com.jasoncrease;

import javafx.util.Pair;

/**
 * Created by jason on 28/08/2016.
 */
public interface ISplitter {
    SplitInfo[] getSplits(double[][] xs, double[] ys) ;

    SplitInfo getBestSplit(double[][] xs, double[] ys);

}
