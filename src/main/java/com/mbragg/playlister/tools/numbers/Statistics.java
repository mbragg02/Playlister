package com.mbragg.playlister.tools.numbers;

import java.util.Arrays;

/**
 * Class containing static methods relating to numbers.
 *
 * @author Michael Bragg
 */
public final class Statistics {

    private Statistics() {
        // Empty private constructor to hide Statistics utility class.
    }

    /**
     * Cosine similarity between two vectors.
     *
     * @param x double[]. Vector x
     * @param y double[]. Vector y
     * @return double. Cosine similarity
     */
    public static double cosineSimilarity(double[] x, double[] y) {
        return dotProduct(x, y) / (vectorMagnitude(x) * vectorMagnitude(y));
    }

    /**
     * Cosine similarity between two vectors, with explicit vector magnitudes.
     *
     * @param x    double[]. Vector x
     * @param y    double[]. Vector y
     * @param xMag double. Vector x magnitude
     * @param yMag double. Vector y magnitude
     * @return double. Cosine similarity
     */
    public static double cosineSimilarity(double[] x, double[] y, double xMag, double yMag) {
        return dotProduct(x, y) / (xMag * yMag);
    }

    /**
     * Vector Magnitude
     *
     * @param vector double[]. Vector
     * @return double. Vector magnitude
     */
    public static double vectorMagnitude(double[] vector) {
        double val = Arrays.stream(vector)
                .map(v -> Math.pow(v, 2))
                .reduce(Double::sum)
                .getAsDouble();

        return Math.sqrt(val);
    }

    /**
     * Dot product between two vectors.
     *
     * @param x double[]. Vector x
     * @param y double[]. Vector y
     * @return double. Dot product
     */
    public static double dotProduct(double[] x, double[] y) {

        int minLength = x.length <= y.length ? x.length : y.length;

        double dotProduct = 0.0;
        for (int i = 0; i < minLength; i++) {
            dotProduct = dotProduct + (x[i] * y[i]);
        }
        return dotProduct;
    }
}