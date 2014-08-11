package com.mbragg.playlister.features;

/**
 * Root mean Square (RMS) feature.
 *
 * @author Michael Bragg
 */
public class RMS extends Feature {

    @Override
    public double[] extractFeature(double[] samples, double samplingRate) {
        double sum = 0.0;
        // result is a single value so will be contained within an array to be consistent with other multivalued features.
        double[] result = new double[1];

        for (double sample : samples) {
            sum += Math.pow(sample, 2);
        }

        result[0] = Math.sqrt(sum / samples.length);
        return result;
    }
}