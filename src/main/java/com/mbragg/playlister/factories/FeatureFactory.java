package com.mbragg.playlister.factories;

import com.mbragg.playlister.features.Feature;
import com.mbragg.playlister.features.MFCC;
import com.mbragg.playlister.features.RMS;
import com.mbragg.playlister.features.ZeroCrossing;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to build a list of features which are to be extracted from audio data.
 *
 * @author Michael Bragg
 */
public class FeatureFactory {

    private static FeatureFactory instance;

    private FeatureFactory() {
        // Private factory
    }

    public static FeatureFactory getInstance() {
        if (instance == null) {
            instance = new FeatureFactory();
        }
        return instance;
    }

    public List<Feature> getFeatureList() {

        List<Feature> features = new ArrayList<>();
        features.add(getMFCC());
        features.add(getRMS());
        features.add(getZeroCrossing());
        return features;
    }

    private Feature getMFCC() {
        return new MFCC();
    }

    private Feature getRMS() {
        return new RMS();
    }

    private Feature getZeroCrossing() {
        return new ZeroCrossing();
    }
}
