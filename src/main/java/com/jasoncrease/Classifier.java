package com.jasoncrease;

import org.apache.log4j.Logger;

/**
 * Created by jason on 12/09/2016.
 */
public class Classifier {
    private final static Logger LOGGER = Logger.getLogger(Classifier.class);

    private final int _categories;
    private final int _maxRounds;
    private final int _maxTreeDepth;
    private final double[][] _testXs;
    private final double[] _testYs;
    private final double[][] _trainXs;
    private final double[] _trainYs;
    private TreesGrower[] _treesGrowers;
    private double _shrinkage;

    public Classifier(ClassifierBuilder classifierBuilder)
    {
        _categories = classifierBuilder._categories;
        _maxRounds = classifierBuilder._maxRounds;
        _maxTreeDepth = classifierBuilder._maxTreeDepth;
        _testXs = classifierBuilder._testXs;
        _testYs = classifierBuilder._testYs;
        _trainXs = classifierBuilder._trainXs;
        _trainYs = classifierBuilder._trainYs;
        _shrinkage = classifierBuilder._shrinkage;
    }

    public void run()
    {
        setup();

        for (int round = 0; round < _maxRounds; round++) {
            LOGGER.trace(String.format("Training round %d started", round));
            for (int i = 0; i < _categories; i++)
                _treesGrowers[i].advanceOneRound();
        }

        LOGGER.trace(String.format("Training round complete."));
    }

    public void setup() {
        if (_trainXs == null)
            throw new RuntimeException("trainXs is null");
        if (_trainYs == null)
            throw new RuntimeException("trainXs is null");
        if (_trainXs.length == 0)
            throw new RuntimeException("There are no features");
        if (_trainXs[0].length != _trainYs.length)
            throw new RuntimeException("Should be the same number of training Ys as training Xs");
        if (_testXs == null ^ _testYs == null)
            throw new RuntimeException("Initialize both testXs and testYs, or neither");


        LOGGER.info(String.format("Training classifier on %d rows and %d features. %d Categories",
                _trainYs.length, _trainXs.length, _categories));
        if(_testXs != null)
            LOGGER.info(String.format("Testing on %d rows and %d features.",
                    _testXs[0].length, _testXs.length));


        _treesGrowers = new TreesGrower[_categories];

        for(int cat = 0; cat < _categories; cat ++)
        {
            double[] categoryYs = new double[_trainYs.length];

            for(int row = 0; row < _trainYs.length; row++)
                if(_trainYs[row] == cat)
                    categoryYs[row] = 1;
                else
                    categoryYs[row] = 0;

            _treesGrowers[cat] = new TreesGrower.TreesGrowerBuilder()
                    .setMaxTreeDepth(_maxTreeDepth)
                    .setMaxTrees(_maxRounds)
                    .setTestXs(_testXs)
                    .setTestYs(_testYs)
                    .setTrainXs(_trainXs)
                    .setTrainYs(categoryYs)
                    .setShrinkage(_shrinkage)
                    .build();
        }
    }

    public void advanceOneRound()
    {
        for (int i = 0; i < _categories; i++)
            _treesGrowers[i].advanceOneRound();
    }

    public double[] predict(double[] xs) {
        double[] rawPreds = new double[_categories];

        for(int cat = 0; cat < _categories; cat++)
            rawPreds[cat] = _treesGrowers[cat].predict(xs);

        return rawPreds;
    }

    private double[] softmax(double[] vals) {
        int valsLen = vals.length;

        double[] as = new double[valsLen];
        double b = 0;

        for (int i = 0; i < valsLen; i++)
            b += Math.exp(vals[i]);

        for (int i = 0; i < valsLen; i++)
            as[i] = Math.exp(vals[i]) / b;

        return as;
    }

    public double[][] predict(double[][] xs) {
        double[][] preds = new double[xs[0].length][_categories];

        double[][] transXs = MathUtils.transposeArray(xs);

        for (int i = 0; i < preds.length; i++)
            preds[i] = predict(transXs[i]);

        return preds;
    }

    public static class ClassifierBuilder
    {
        private int _categories = 1;
        private int _maxRounds = 50;
        private int _maxTreeDepth = 6;
        private double _shrinkage = 0.3;
        private double[][] _trainXs;
        private double[][] _testXs;
        private double[] _trainYs;
        private double[] _testYs;

        /**
         * Number of categories. Note that a binary classifier has 1 categories. Your data should be categorized
         * as 0, 1, 2, 3... 12, 13, 14. That example has 15 categories.
         */
        public ClassifierBuilder setCategories(int categories)
        {
            _categories = categories;
            return this;
        }
        public ClassifierBuilder setShrinkage(double shrinkage)
        {
            _shrinkage = shrinkage;
            return this;
        }
        public ClassifierBuilder setTrainXs(double[][] trainXs)
        {
            _trainXs = trainXs;
            return this;
        }
        public ClassifierBuilder setTestXs(double[][] testXs)
        {
            _testXs = testXs;
            return this;
        }
        public ClassifierBuilder setTestYs(double[] testYs)
        {
            _testYs = testYs;
            return this;
        }
        public ClassifierBuilder setTrainYs(double[] trainYs)
        {
            _trainYs = trainYs;
            return this;
        }
        public ClassifierBuilder setMaxRounds(int maxRounds) {
            _maxRounds = maxRounds;
            return this;
        }

        public ClassifierBuilder setMaxTreeDepth(int maxTreeDepth) {
            _maxTreeDepth = maxTreeDepth;
            return this;
        }

        public Classifier build() {
            return new Classifier(this);
        }

        public int getMaxRounds() {
            return _maxRounds;
        }
    }
}
