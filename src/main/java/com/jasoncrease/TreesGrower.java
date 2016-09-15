package com.jasoncrease;

import org.apache.log4j.Logger;
import sun.nio.cs.ext.MacHebrew;

public class TreesGrower {
    private final static Logger LOGGER = Logger.getLogger(TreesGrower.class);

    private final double[][] _trainXs;
    private final double[] _trainYs;
    private final double[][] _testXs;
    private final double[] _testYs;

    private double[][] _transTestXs = null;

    // The number of decision trees grown so far
    int _numTrees = 0;
    // The maximum number of decision trees to grow
    int _maxTrees = 3;
    // The maximum permitted depth of tree
    int _maxDepth = 3;
    // How much to multiply gamma (tree weight) by
    double _shrinkage = 0.5;

    boolean showDebug = false;

    private TreeFinder _treeFinder;
    private TreeNode[] _trees;
    private double[] _treeWeights;

    private double[][] _residualYs;
    private int _numRows;
    private int _numFeatures;
    private double[][] _transXs;

    public TreesGrower(TreesGrowerBuilder treesGrowerBuilder) {
        _maxTrees = treesGrowerBuilder._maxTrees;
        _maxDepth = treesGrowerBuilder._maxTreesDepth;
        _shrinkage = treesGrowerBuilder._shrinkage;

        _trainXs = treesGrowerBuilder._trainXs;
        _trainYs = treesGrowerBuilder._trainYs;
        _testXs = treesGrowerBuilder._testXs;
        _testYs = treesGrowerBuilder._testYs;

        _treeFinder = new TreeFinder(new Splitter());
        _trees = new TreeNode[_maxTrees];
        _treeWeights = new double[_maxTrees];

        _numRows = _trainXs[0].length;
        _numFeatures = _trainXs.length;

        _transXs = MathUtils.transposeArray(_trainXs); // The transpose if useful for some operations
        if(_testXs != null)
            _transTestXs = MathUtils.transposeArray(_testXs);
    }

    // First derivative wrt y2
    public double dlDy(double y1, double y2)
    {
        return y1 - y2;
    }

    public void advanceOneRound() {

        double[] objective = new double[_numRows];
        double[] resEffects  = new double[_numRows];
        double[] predictions = new double[_numRows];

        for (int row = 0; row < _numRows; row++) {
            if(_numTrees > 0)
                predictions[row] = predict(_transXs[row]);
            objective[row] = dlDy(_trainYs[row], predictions[row]);
        }

        TreeNode bestTree = _treeFinder.getBestTree(_trainXs, objective, _maxDepth);

        double gamma = 1;

        _trees[_numTrees] = bestTree;
        _treeWeights[_numTrees] = gamma * _shrinkage;
        _numTrees++;
    }

    private double rms(double[] vals) {
        double retValue = 0;

        for (int i = 0; i < vals.length; i++)
            retValue += vals[i] * vals[i];

        return Math.sqrt(retValue);
    }

    // Find K to minimize L(ys, yds + K * fs)
    public static double getBestFactor(double[] ys, double[] yds, double[] fs) {
        double bestK = 0f;
        double bestLoss = Double.MAX_VALUE;
        double[] currentYds = yds.clone();
        double Kdiff = 0.1;

        for (double K = -2; K < 2; K += Kdiff) {

            for (int i = 0; i < yds.length; i++)
                currentYds[i] = yds[i] + (K * fs[i]);

            double loss = MathUtils.logLoss(ys, currentYds);

            if (loss < bestLoss) {
                bestK = K;
                bestLoss = loss;
            }
        }

        return bestK;
    }



    private static double squaredloss(double v1, double v2) {
        return (v1 - v2) * (v1 - v2);
    }

    public double[] predict(double[][] dataXs) throws RuntimeException {
        int cols = dataXs.length;
        int rows = dataXs[0].length;
        double[] preds = new double[rows];

        for (int row = 0; row < rows; row++) {
            double[] vector = new double[cols];
            for (int col = 0; col < cols; col++)
                vector[col] = dataXs[col][row];

            preds[row] = predict(vector);
        }

        return preds;
    }

    public double predict(double[] vector) throws RuntimeException {
        if(_numTrees == 0)
            throw new RuntimeException("No tree grown yet. Train me first.");

        double prediction = 0f;

        for (int i = 0; i < _numTrees; i++)
            prediction += _trees[i].predict(vector) * _treeWeights[i];

        return logistic(prediction);
    }

    public double logistic(double x)
    {
        return 1 / (1 + Math.exp(-x));
    }


    public static class TreesGrowerBuilder {
        private int _maxTrees = 100;
        private int _maxTreesDepth = 5;
        private double[][] _trainXs;
        private double[][] _testXs;
        private double[] _trainYs;
        private double[] _testYs;
        private double _shrinkage;

        public TreesGrowerBuilder setMaxTrees(int maxTrees)
        {
            _maxTrees = maxTrees;
            return this;
        }
        public TreesGrowerBuilder setTrainXs(double[][] trainXs)
        {
            _trainXs = trainXs;
            return this;
        }
        public TreesGrowerBuilder setTestXs(double[][] testXs)
        {
            _testXs = testXs;
            return this;
        }
        public TreesGrowerBuilder setTestYs(double[] testYs)
        {
            _testYs = testYs;
            return this;
        }
        public TreesGrowerBuilder setTrainYs(double[] trainYs)
        {
            _trainYs = trainYs;
            return this;
        }
        public TreesGrowerBuilder setMaxTreeDepth(int maxTreeDepth) {
            this._maxTreesDepth = maxTreeDepth;
            return this;
        }

        public TreesGrowerBuilder setShrinkage(double shrinkage) {
            this._shrinkage = shrinkage;
            return this;
        }

        public TreesGrower build() {
            return new TreesGrower(this);
        }

    }
}
