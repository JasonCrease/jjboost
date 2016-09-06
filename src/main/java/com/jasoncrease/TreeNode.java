package com.jasoncrease;

public class TreeNode {
    public TreeNode _left = null;
    public TreeNode _right = null;

    // The index of the feature vector that we're splitting on
    public int _splitIndex = -1;
    // The value at which we split. < than this go left. >= go right
    public double _splitValue = 0f;
    // True iff this is a leaf node
    public boolean _isLeaf;
    // If this is a leaf node, then this is the mean of the values seen here
    public double _value;

    public double predict(double[] xs) {
        if(_isLeaf)
            return _value;
        else
            if(xs[_splitIndex] < _splitValue)
                return _left.predict(xs);
            else
                return _right.predict(xs);
    }
}
