package com.mbragg.playlister.features;

/**
 * Abstract feature class.
 * i.e All subclasses must implement the extractFeature method.
 *
 * @author Michael Bragg
 */
public abstract class Feature {

    public abstract double[] extractFeature(double[] samples, double samplingRate);
}