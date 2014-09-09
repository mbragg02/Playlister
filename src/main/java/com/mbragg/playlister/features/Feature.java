package com.mbragg.playlister.features;

/**
 * Single method abstract feature class that must be implemented by all feature implementations.
 *
 * @author Michael Bragg
 */
public abstract class Feature {

    public abstract double[] extractFeature(double[] samples, double samplingRate);
}