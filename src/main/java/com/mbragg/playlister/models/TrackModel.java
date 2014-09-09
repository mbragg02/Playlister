package com.mbragg.playlister.models;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

import java.util.List;

/**
 * Defines operations for the model used to represent a Track.
 *
 * @author Michael Bragg
 */
public interface TrackModel {

    /**
     * Build the model for a track for a given list of track data.
     * @param data List of double values for a specific track.
     * @return A model to represent a track's data values.
     */
    MultivariateNormalDistribution build(List<double[]> data);

    /**
     * Gets the similarity between two track models.
     * @param dx First track model.
     * @param dy Second track model.
     * @return double value of similarity.
     */
    double getSimilarity(MultivariateNormalDistribution dx, MultivariateNormalDistribution dy);
}
