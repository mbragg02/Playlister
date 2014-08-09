package com.mbragg.playlister.features;

public class RMS extends Feature {

    @Override
    public double[] extractFeature(double[] samples, double samplingRate) {
        double sum = 0.0;
        double[] result = new double[1];

        for (double sample : samples) {
            sum += Math.pow(sample, 2);
        }

        double rms = Math.sqrt(sum / samples.length);
        result[0] = rms;
        return result;
    }



}
