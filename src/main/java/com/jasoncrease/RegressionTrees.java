package com.jasoncrease;

import org.apache.log4j.Logger;

public class RegressionTrees {
    final static Logger LOGGER = Logger.getLogger(RegressionTrees.class);

    // The number of decision trees grown so far
    int _numTrees = 0;
    // The maximum number of decision trees to grow
    int _maxTrees = 3;
    // The maximum permitted depth of tree
    int _maxDepth = 3;
    // How much to multiply gamma (tree weight) by
    double _shrinkage = 0.1;
    //
    boolean showDebug = false;

    TreeFinder _treeFinder;
    TreeNode[] _trees;
    double[] _treeWeights;


    public RegressionTrees(RegressionTreesBuilder gbTreesBuilder) {
        _maxTrees = gbTreesBuilder._maxTrees;
        _maxDepth = gbTreesBuilder._maxTreesDepth;

        _treeFinder = new TreeFinder(new Splitter());
        _trees = new TreeNode[_maxTrees];
        _treeWeights = new double[_maxTrees];
    }

    public void train(double[][] xs, double[] ys) throws Exception {
        train(xs, ys, null, null);
    }

    public void train(double[][] xs, double[] ys, double[][] testXs, double[] testYs) throws Exception {
        if (xs.length == 0)
            throw new Exception("There are no features");
        if (xs[0].length != ys.length)
            throw new Exception("Should be the same number of Ys as Xs");

        int numRows = xs[0].length;
        int numFeatures = xs.length;

        LOGGER.info(String.format("Training on %d rows and %d features.", numRows, numFeatures));
        if(testXs != null)
            LOGGER.info(String.format("Testing on %d rows and %d features.", testXs[0].length, testXs.length));

        double[] residualYs = new double[numRows];
        double[] resEffects = new double[numRows];
        double[] predictions = new double[numRows];
        double[][] transXs = transposeArray(xs); // The transpose if useful for some operations

        double[][] transTestXs = null;
        if(testXs != null)
            transTestXs = transposeArray(testXs);


        // Initialise residuals to the ys
        for (int i = 0; i < numRows; i++)
            residualYs[i] = ys[i];

        // First tree is just the first best tree
        _trees[0] = _treeFinder.getBestTree(xs, ys, _maxDepth);
        _treeWeights[0] = 1;
        _numTrees = 1;
        for (int row = 0; row < numRows; row++)
            residualYs[row] = ys[row] - _trees[0].predict(transXs[row]);


        if(transTestXs == null)
            LOGGER.trace(String.format("%d trees. Train error %.6f", _numTrees, rms(residualYs)));
        else
        {
            double[] yPreds = predict(testXs);
            LOGGER.trace(String.format("%d trees. Train error %.6f. Test error %.6f", _numTrees, rms(residualYs), loss(yPreds, testYs)));
        }



        // Build all further trees by finding diff and weighting
        for (int treeNum = 1; treeNum < _maxTrees; treeNum++) {
            TreeNode bestTree = _treeFinder.getBestTree(xs, residualYs, _maxDepth);

            // Find best gamma by:
            // 1. Build effects of bestTree on the residuals
            for (int row = 0; row < numRows; row++)
                resEffects[row] = bestTree.predict(transXs[row]);
            // 2. Get all predictions
            for (int row = 0; row < numRows; row++)
                predictions[row] = predict(transXs[row]);
            // 3. Do a linear search to get a gamma that best matches the residuals
            //double gamma = getBestFactor(ys, predictions, resEffects);
            double gamma = 1;

            // Update residuals
            for (int row = 0; row < numRows; row++)
                residualYs[row] -= resEffects[row] * gamma * _shrinkage;

            _trees[treeNum] = bestTree;
            _treeWeights[treeNum] = gamma * _shrinkage;
            _numTrees++;


            if(transTestXs == null)
                LOGGER.trace(String.format("Trees %d. Train error %.6f", _numTrees, rms(residualYs) / numRows));
            else
            {
                double[] yTrainPreds = predict(xs);
                double[] yTestPreds  = predict(testXs);
                LOGGER.trace(String.format("Trees %d. Train error %.6f. Test error %.6f",
                        _numTrees,
                        loss(yTrainPreds, ys) / numRows,
                        loss(yTestPreds , testYs) / testXs[0].length ));
            }
        }
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

    public static class RegressionTreesBuilder {
        int _maxTrees = 3;
        private int _maxTreesDepth;

        public RegressionTreesBuilder setMaxTrees(int maxTrees)
        {
            _maxTrees = maxTrees;
            return this;
        }

        public RegressionTreesBuilder setMaxTreeDepth(int maxTreeDepth) {
            this._maxTreesDepth = maxTreeDepth;
            return this;
        }

        public RegressionTrees build() {
            return new RegressionTrees(this);
        }
    }
}