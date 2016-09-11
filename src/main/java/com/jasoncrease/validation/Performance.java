package com.jasoncrease.validation;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by jason on 10/09/2016.
 */
public class Performance {

    private Performance() {}

    private int _tns;
    private int _tps;

    private int _fns;
    private int _fps;

    private double _aucroc;


    public static Performance build(double[] actualYs, double[] predictedYs)
    {
        if(actualYs.length != predictedYs.length)
            throw new IllegalArgumentException("Arrays must be same length");

        Performance performance = new Performance();
        performance.buildBasicNums(actualYs, predictedYs);
        performance.buildRoc(actualYs, predictedYs);

        return performance;
    }

    private void buildBasicNums(double[] actualYs, double[] predictedYs) {
        double thresh = 0.5;

        for(int i = 0; i< actualYs.length ; i++)
        {
            double actual = actualYs[i];
            double pred   = predictedYs[i];

            if(actual > thresh && pred > thresh)
                _tps++;
            else if(actual < thresh && pred > thresh)
                _fps++;
            else if(actual > thresh && pred < thresh)
                _fns++;
            else if(actual < thresh && pred < thresh)
                _tns++;
        }

    }

    private void buildRoc(double[] actualYs, double[] predictedYs) {
        List<Pair<Double, Double>> pairs =  IntStream.range(0, Math.min(actualYs.length, predictedYs.length))
                .mapToObj(i -> new Pair<>(actualYs[i], predictedYs[i]))
                .collect(Collectors.toList());

        pairs.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        double auc = 0f;
        double height = 0f;
        double tpr = (double)1 / (double)(_tps + _fns);
        double fpr = (double)1 / (double)(_fps + _tns);

        for(Pair<Double, Double> pair : pairs)
        {
            if (pair.getKey() == 1.0)
                height = height + tpr;
            else
                auc = auc + (height * fpr);
        }

        _aucroc = auc;
    }

    public double getAucroc()
    {
        return _aucroc;
    }

    public String getConfusionMatrix()
    {
        return "";
    }
}
