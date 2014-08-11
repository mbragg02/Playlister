package com.mbragg.playlister.features;

/**
 * Zero crossing rate feature.
 *
 * @author Michael Bragg
 */
public class ZeroCrossing extends Feature {

    @Override
    public double[] extractFeature(double[] samples, double samplingRate) {
        long count = 0;

        // result is a single value so will be contained within an array to be consistent with other multivalued features.
        double[] result = new double[1];

        for (int sample = 0; sample < samples.length - 1; sample++) {
            if (samples[sample] > 0.0 && samples[sample + 1] < 0.0)
                count++;
            else if (samples[sample] < 0.0 && samples[sample + 1] > 0.0)
                count++;
            else if (samples[sample] == 0.0 && samples[sample + 1] != 0.0)
                count++;
        }
        result[0] = (double) count;
        return result;
    }
}
