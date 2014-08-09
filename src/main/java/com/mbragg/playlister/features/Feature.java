package com.mbragg.playlister.features;
/**
 * Created by Michael Bragg on 28/02/2014.
 *
 */
public abstract class Feature {

    public abstract double[] extractFeature(double[] samples, double sampling_rate);
}
