package com.jasoncrease.validation;

import com.jasoncrease.RegressionTrees;

import java.util.Random;

/**
 * Created by jason on 11/09/2016.
 */
public class CrossValidator {


    private final int _folds;
    private final RegressionTrees.RegressionTreesBuilder _gbBuilder;
    private final double[][] _xs;
    private final double[] _ys;

    private CrossValidator(CrossValidatorBuilder crossValidatorBuilder) {
        _folds = crossValidatorBuilder._folds;
        _gbBuilder = crossValidatorBuilder._gbBuilder;
        _xs = crossValidatorBuilder._xs;
        _ys = crossValidatorBuilder._ys;
    }

    private static class TestTrainSet
    {
        double[][] xsTrain;
        double[]   ysTrain;
        double[][] xsTest;
        double[]   ysTest;
    }

    public void run() throws Exception {
        TestTrainSet[] testTrainSets = buildTestTrainSets();

        RegressionTrees[] gbTrees = new RegressionTrees[_folds];
        Performance[] perfs = new Performance[_folds];

        double totalAucRoc = 0f;

        for (int i = 0; i < _folds; i++) {
            gbTrees[i] = _gbBuilder.build();
            gbTrees[i].train(testTrainSets[i].xsTrain,
                    testTrainSets[i].ysTrain,
                    testTrainSets[i].xsTest,
                    testTrainSets[i].ysTest);

            double[] yPreds = gbTrees[i].predict(testTrainSets[i].xsTest);
            perfs[i] = Performance.build(yPreds, testTrainSets[i].ysTest);
            totalAucRoc += perfs[i].getAucroc();
        }

        totalAucRoc /= _folds;
    }

    private TestTrainSet[] buildTestTrainSets() {
        int numCols = _xs.length;
        int numRows = _xs[0].length;

        Random rand = new Random();

        TestTrainSet[] testTrainSets = new TestTrainSet[_folds];

        for (int i = 0; i < _folds; i++) {
            // This awkwardness is necessary to include all the rows precisely once amongst the test sets.
            int foldTestSize = ((numRows - i - 1) / _folds) + 1;
            int foldTrainSize = numRows - foldTestSize;

            testTrainSets[i] = new TestTrainSet();
            testTrainSets[i].xsTest = new double[numCols][foldTestSize];
            testTrainSets[i].xsTrain = new double[numCols][foldTrainSize];
            testTrainSets[i].ysTest = new double[foldTestSize];
            testTrainSets[i].ysTrain = new double[foldTrainSize];
        }

        // Shuffle is a random arrangement of 0, 1.. _folds.
        // It'll look something like 2,4,0,1, ... ,3,4,0,1,3
        int[] shuffle = new int[numRows];
        for (int row = 0; row < numRows; row++)
            shuffle[row] = row % _folds;
        for (int i = 0; i < numRows; i++) {
            int j = rand.nextInt(numRows);
            int temp = shuffle[i];
            shuffle[i] = shuffle[j];
            shuffle[j] = temp;
        }


        // Assign train and test xs and ys to the correct folds.

        int[]  testOffset = new int[_folds];
        int[] trainOffset = new int[_folds];

        for (int row = 0; row < numRows; row++) {
            for(int fold = 0; fold < _folds; fold++)
            {
                int foldTrainOffset = trainOffset[fold];
                int foldTestOffset  = testOffset[fold];

                if(fold  == shuffle[row]) {
                    for(int col =0; col <numCols; col++)
                        testTrainSets[fold].xsTest[col][foldTestOffset] = _xs[col][row];
                    testTrainSets[fold].ysTest[foldTestOffset] = _ys[row];
                    testOffset[fold]++;
                }
                else {
                    for(int col =0; col <numCols; col++)
                        testTrainSets[fold].xsTrain[col][foldTrainOffset] = _xs[col][row];
                    testTrainSets[fold].ysTrain[foldTrainOffset] = _ys[row];
                    trainOffset[fold]++;
                }
            }
        }

        return testTrainSets;
    }

    public static class CrossValidatorBuilder {
        private int _folds;
        private double[][] _xs;
        private double[] _ys;
        private RegressionTrees.RegressionTreesBuilder _gbBuilder;

        public CrossValidatorBuilder setXs(double[][] xs)
        {
            _xs = xs;
            return this;
        }
        public CrossValidatorBuilder setYs(double[] ys) {
            _ys = ys;
            return this;
        }

        public CrossValidatorBuilder setFolds(int folds) {
            if (folds < 1)
                throw new IllegalArgumentException("folds must be > 0");
            this._folds = folds;
            return this;
        }
        public CrossValidatorBuilder setTreeBuilder(RegressionTrees.RegressionTreesBuilder gbBuilder) {
            this._gbBuilder = gbBuilder;
            return this;
        }

        public CrossValidator build() {
            return new CrossValidator(this);
        }
    }
}
