package com.jasoncrease;

public class TreeFinder implements ITreeFinder {

    IEntropy _entropy;

    public TreeFinder(IEntropy entropy)
    {
        _entropy = entropy;
    }

    public TreeNode getBestTree(double[][] xs, double[] ys, int depth)
    {
        TreeNode retNode = new TreeNode();

        // If we're really deep, make this a leaf node
        if(depth == 0) {
            retNode._isLeaf = true;
            for(double y : ys) {
                if(y < 0.5)
                    retNode._class0Size++;
                else
                    retNode._class1Size++;
            }

            return retNode;
        }

        SplitInfo bestSplit = _entropy.getBestSplit(xs, ys);

        // There is no possible split, or the gain is too poor. Make this a leaf
        if(bestSplit == null || bestSplit._gain < 0.0001) {
            retNode._isLeaf = true;
            for(double y : ys) {
                if(y < 0.5)
                    retNode._class0Size++;
                else
                    retNode._class1Size++;
            }

            return retNode;
        }

        int splitCol = bestSplit._col;
        double splitValue = bestSplit._value;


        // Divide xs and ys by the best split

        int numCols = xs.length;
        int numRows = xs[0].length;
        double[][] xsLeft  = new double[numCols][bestSplit._leftSize];
        double[][] xsRight = new double[numCols][bestSplit._rightSize];
        double[] ysLeft  = new double[bestSplit._leftSize];
        double[] ysRight = new double[bestSplit._rightSize];

        int rightRow = 0;
        int leftRow  = 0;

        for(int row = 0; row < numRows; row++)
        {
            if(xs[splitCol][row] < splitValue) {
                for(int col = 0; col < numCols; col++)
                    xsLeft[col][leftRow] = xs[col][row];
                ysLeft[leftRow] = ys[row];
                leftRow++;
            }
            else {
                for(int col = 0; col < numCols; col++)
                    xsRight[col][rightRow] = xs[col][row];
                ysRight[rightRow] = ys[row];
                rightRow++;
            }
        }

        retNode._splitIndex = splitCol;
        retNode._splitValue = splitValue;

        if(leftRow > 0)
            retNode._left = getBestTree(xsLeft, ysLeft, depth - 1);
        if(rightRow > 0)
            retNode._right = getBestTree(xsRight, ysRight, depth - 1);

        return retNode;
    }


}
