package com.jasoncrease;

/**
 * Created by jason on 29/08/2016.
 */
public class SplitInfo {
    public double _deviance;
    public double _value;
    public int    _col;
    public double _leftMean;
    public double _rightMean;
    public int _leftSize;
    public int _rightSize;

    public SplitInfo(double deviance, double value, int col, double leftMean, double rightMean, int leftSize, int rightSize)
    {
        _deviance = deviance;
        _value = value;
        _col = col;
        _leftMean = leftMean;
        _rightMean = rightMean;
        _leftSize = leftSize;
        _rightSize = rightSize;
    }

}
