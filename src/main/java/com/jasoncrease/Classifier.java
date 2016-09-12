package com.jasoncrease;

/**
 * Created by jason on 12/09/2016.
 */
public class Classifier {

    private final int _categories;
    private final int _maxRounds;
    private final int _maxTreeDepth;
    private final double[][] _testXs;
    private final double[] _testYs;
    private final double[][] _trainXs;
    private final double[] _trainYs;

    public Classifier(ClassifierBuilder classifierBuilder)
    {
        _categories = classifierBuilder._categories;
        _maxRounds = classifierBuilder._maxRounds;
        _maxTreeDepth = classifierBuilder._maxTreeDepth;
        _testXs = classifierBuilder._testXs;
        _testYs = classifierBuilder._testYs;
        _trainXs = classifierBuilder._trainXs;
        _trainYs = classifierBuilder._trainYs;
    }

    public void run()
    {
        TreesGrower[] treesGrowers = new TreesGrower[_categories];

        for(int i=0; i < _categories; i++)
            treesGrowers[i] = new TreesGrower.TreesGrowerBuilder()
                    .setMaxTreeDepth(_maxTreeDepth)
                    .setMaxTrees(_maxRounds)
                    .setTestXs(_testXs)
                    .setTestYs(_testYs)
                    .setTrainXs(_trainXs)
                    .setTrainYs(_trainYs)
                    .build();

        for(int round = 0; round < _maxRounds; round++)
        {
            for(int i=0; i < _categories; i++)
                treesGrowers[i].advanceOneRound();
        }
    }

    public double[] predict(double[][] dataXs) {
        return new double[0];
    }

    public static class ClassifierBuilder
    {
        private int _categories = 2;
        private int _maxRounds = 50;
        private int _maxTreesDepth = 5;
        private double[][] _trainXs;
        private double[][] _testXs;
        private double[] _trainYs;
        private double[] _testYs;
        private int _maxTreeDepth;

        /**
         * Number of categories. Note that a binary classifier has 2 categories. Your data should be categorized
         * as 0, 1, 2, 3... 12, 13, 14. That example has 15 categories.
         */
        public ClassifierBuilder setCategories(int categories)
        {
            _categories = categories;
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

    }
}
