package com.jasoncrease;

public class TreeNode {
    public TreeNode _left = null;
    public TreeNode _right = null;

    // The index of the feature vector that we're splitting on
    public int _splitIndex = -1;
    // The value at which we split. < than this go left. >= go right
    public double _splitValue = 0f;
    // Size of class 0
    public int _class0Size;
    // Size of class 1
    public int _class1Size;
    // True iff this is a leaf node
    public boolean _isLeaf;

    public TreeNode()
    {

    }

    public double predict(double[] xs) {
        if(_isLeaf)
            return (double)_class1Size / (double)(_class0Size + _class1Size);
        else
            if(xs[_splitIndex] < _splitValue)
                return _left.predict(xs);
            else
                return _right.predict(xs);
    }
}
