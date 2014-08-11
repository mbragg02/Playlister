package com.mbragg.playlister.features;

/**
 * Abstract feature class.
 *
 * @author Michael Bragg
 */
public abstract class Feature {

    public abstract double[] extractFeature(double[] samples, double sampling_rate);
}
