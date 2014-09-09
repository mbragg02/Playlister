package com.mbragg.playlister.features;

import org.oc.ocvolume.dsp.featureExtraction;

/**
 * Mel Frequency Cepstral Coefficient (MFCC)
 * <p>
 * MFCC: "a representation of the short-term power spectrum of a sound,
 * based on a linear cosine transform of a log power spectrum on a nonlinear mel scale of frequency."
 * <p>
 * Note: Calling methods from OrangeCow: OC Volume - Java speech recognition engine. Found in package org.oc.ocvolume.dsp.featureExtraction.
 */
public class MFCC extends Feature {

    private featureExtraction fe;

    public MFCC() {
        fe = new featureExtraction();
    }

    @Override
    public double[] extractFeature(double[] samples, double samplingRate) {

        double[] magnitudeSpectrum = fe.magnitudeSpectrum(samples);
        int[] fftBinIndices = fe.fftBinIndices(samplingRate, magnitudeSpectrum.length);
        double[] melFilter = fe.melFilter(magnitudeSpectrum, fftBinIndices);
        double[] nonLinearTransformation = fe.nonLinearTransformation(melFilter);

        return fe.cepCoefficients(nonLinearTransformation);
    }
}