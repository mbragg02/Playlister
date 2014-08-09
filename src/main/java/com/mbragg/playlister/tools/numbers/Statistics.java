package com.mbragg.playlister.tools.numbers;

import java.util.Arrays;

/**
 * Class containing static methods relating to numbers.
 *
 * @author Michael Bragg
 */
public class Statistics {


    public static double cosineSimilarity(double[] x, double[] y) {
        return dotProduct(x, y) / (vectorMagnitude(x) * vectorMagnitude(y));
    }

    public static double cosineSimilarity(double[] x, double[] y, double xMag, double yMag) {
        return dotProduct(x, y) / (xMag * yMag);
    }

    public static double vectorMagnitude(double[] vector) {
        double val = Arrays.stream(vector)
                .map(v -> Math.pow(v, 2))
                .reduce(Double::sum)
                .getAsDouble();

        return Math.sqrt(val);
    }

    public static double dotProduct(double[] x, double[] y) {

        int minLength = x.length <= y.length ? x.length : y.length;

        double dotProduct = 0.0;
        for (int i = 0; i < minLength; i++) {
            dotProduct = dotProduct + (x[i] * y[i]);
        }
        return dotProduct;
    }
}
