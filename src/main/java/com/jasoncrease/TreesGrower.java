package com.jasoncrease;

import org.apache.log4j.Logger;

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
    double _shrinkage = 0.1;

    boolean showDebug = false;

    private TreeFinder _treeFinder;
    private TreeNode[] _trees;
    private double[] _treeWeights;

    private double[] _residualYs;
    private int _numRows;
    private int _numFeatures;
    private double[][] _transXs;

    public TreesGrower(TreesGrowerBuilder treesGrowerBuilder) {
        _maxTrees = treesGrowerBuilder._maxTrees;
        _maxDepth = treesGrowerBuilder._maxTreesDepth;

        _trainXs = treesGrowerBuilder._trainXs;
        _trainYs = treesGrowerBuilder._trainYs;
        _testXs = treesGrowerBuilder._testXs;
        _testYs = treesGrowerBuilder._testYs;

        _treeFinder = new TreeFinder(new Splitter());
        _trees = new TreeNode[_maxTrees];
        _treeWeights = new double[_maxTrees];


        if (_trainXs.length == 0)
            throw new RuntimeException("There are no features");
        if (_trainXs[0].length != _trainYs.length)
            throw new RuntimeException("Should be the same number of training Ys as training Xs");

        _numRows = _trainXs[0].length;
        _numFeatures = _trainXs.length;
        _residualYs = new double[_numRows];
    }


    public void zerothRound()
    {
        LOGGER.info(String.format("Training on %d rows and %d features.", _numRows, _numFeatures));
        if(_testXs != null)
            LOGGER.info(String.format("Testing on %d rows and %d features.", _testXs[0].length, _testXs.length));

        _transXs = transposeArray(_trainXs); // The transpose if useful for some operations
        if(_testXs != null)
            _transTestXs = transposeArray(_testXs);

        // Initialise residuals to the ys
        for (int i = 0; i < _numRows; i++)
            _residualYs[i] = _trainYs[i];

        // First tree is just the first best tree
        _trees[0] = _treeFinder.getBestTree(_trainXs, _trainYs, _maxDepth);
        _treeWeights[0] = 1;
        _numTrees = 1;
        for (int row = 0; row < _numRows; row++)
            _residualYs[row] = _trainYs[row] - _trees[0].predict(_transXs[row]);
    }


    public void advanceOneRound() {

        // For the 0th round, we grow the optimal tree aggressively and return.
        if (_numTrees == 0) {
            zerothRound();
            return;
        }

        TreeNode bestTree = _treeFinder.getBestTree(_trainXs, _residualYs, _maxDepth);

        double[] resEffects  = new double[_numRows];
        double[] predictions = new double[_numRows];

        // Find best gamma by:
        // 1. Build effects of bestTree on the residuals
        for (int row = 0; row < _numRows; row++)
            resEffects[row] = bestTree.predict(_transXs[row]);
        // 2. Get all predictions
        for (int row = 0; row < _numRows; row++)
            predictions[row] = predict(_transXs[row]);
        // 3. Do a linear search to get a gamma that best matches the residuals
        //double gamma = getBestFactor(ys, predictions, resEffects);
        double gamma = 1;

        // Update residuals
        for (int row = 0; row < _numRows; row++)
            _residualYs[row] -= resEffects[row] * gamma * _shrinkage;

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

            double loss = loss(ys, currentYds);

            if (loss < bestLoss) {
                bestK = K;
                bestLoss = loss;
            }
        }

        return bestK;
    }

    private static double logloss(double y1, double y2) {
        if (y2 >= 1 - 1e-15)
            y2 = 1 - 1e-15;
        if (y2 < 1e-15)
            y2 = 1e-15;
        if (y1 >= 1 - 1e-15)
            y1 = 1 - 1e-15;
        if (y1 < 1e-15)
            y1 = 1e-15;

        return y1 * Math.log(y2) +
                (1 - y1) * Math.log(1 - y2);
    }

    private static double loss(double[] y1, double[] y2) {
        double totalLoss = 0f;
        for (int i = 0; i < y1.length; i++)
            //totalLoss += -squaredloss(y1[i], y2[i]);
            totalLoss += logloss(y1[i], y2[i]);
        return -totalLoss;
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

        return prediction;
    }

    // http://stackoverflow.com/questions/8422374/java-multi-dimensional-array-transposing
    private double[][] transposeArray(double[][] arr) {
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


    public static class TreesGrowerBuilder {
        private int _maxTrees = 100;
        private int _maxTreesDepth = 5;
        private double[][] _trainXs;
        private double[][] _testXs;
        private double[] _trainYs;
        private double[] _testYs;

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

        public TreesGrower build() {
            return new TreesGrower(this);
        }
    }
}
