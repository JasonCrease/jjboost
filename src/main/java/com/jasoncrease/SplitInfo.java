package com.jasoncrease;

/**
 * Created by jason on 29/08/2016.
 */
public class SplitInfo {
    public double _gain;
    public double _value;
    public int    _col;
    public double _leftPurity;
    public double _rightPurity;
    public int _leftSize;
    public int _rightSize;

    public SplitInfo(double gain, double value, int col, double leftPurity, double rightPurity, int leftSize, int rightSize)
    {
        _gain = gain;
        _value = value;
        _col = col;
        _leftPurity = leftPurity;
        _rightPurity = rightPurity;
        _leftSize = leftSize;
        _rightSize = rightSize;
    }

}
