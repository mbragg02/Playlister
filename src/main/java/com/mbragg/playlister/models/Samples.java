package com.mbragg.playlister.models;

import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Methods for conversion from byte[] to double[], in mono and stereo.
 *
 * @author Michael Bragg
 */
@SuppressWarnings("SpellCheckingInspection")
@Component
public class Samples {

    private static final int SIXTEEN_BIT = 16;
    private static final int EIGHT_BIT = 8;
    private static final double MAXIMUM_SAMPLE_OVERHEAD = 2.0;
    private static final int TWO = 2;
    private static final int FOUR = 4;

    /**
     * For a given byte[] and audio format, return a double[] in mono.
     *
     * @param audioBytes  byte[]. Audio byte[] data.
     * @param audioFormat AudioFormat. Audio file format information.
     * @return double[] data in mono.
     */
    public double[] getSamplesInMono(byte[] audioBytes, AudioFormat audioFormat) {
        double[][] audioSamples = getSamplesInStereo(audioBytes, audioFormat);
        return getSamplesInMono(audioSamples);
    }

    /**
     * For double[][] data (stereo), return mono double[]
     *
     * @param audioSamples double[][] stereo audio double data.
     * @return double[]. mono double[]
     */
    protected double[] getSamplesInMono(double[][] audioSamples) {

        if (audioSamples.length == 1)
            return audioSamples[0];

        double numberOfSamples = (double) audioSamples.length;
        int channels = audioSamples[0].length;

        double[] samplesInMono = new double[channels];

        for (int sample = 0; sample < channels; sample++) {
            double runningSampleTotal = 0.0;
            for (int chan = 0; chan < numberOfSamples; chan++) {
                runningSampleTotal += audioSamples[chan][sample];
            }
            samplesInMono[sample] = runningSampleTotal / numberOfSamples;
        }
        return samplesInMono;
    }

    /**
     * For a given byte[] and audio format, return a double[] in stereo.
     *
     * @param audioBytes byte[]. Audio byte[] data.
     * @param format     AudioFormat. Audio file format information.
     * @return double[][]. Stereo double[][] data.
     * @throws IllegalArgumentException If the audio format is not supported.
     *                                  Only 8 or 16 bit signed PCM samples with a big endian, with an even number of bytes for the given bit depth.
     */
    public double[][] getSamplesInStereo(byte[] audioBytes, AudioFormat format) {

        int numberOfChannels = format.getChannels();
        int bitDepth = format.getSampleSizeInBits();

        if ((bitDepth != SIXTEEN_BIT && bitDepth != EIGHT_BIT) || !format.isBigEndian() || format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
            throw new IllegalArgumentException("Only 8 or 16 bit signed PCM samples with a big endian can be processed");

        int numberOfBytes = audioBytes.length;
        int bytesPerSample = bitDepth / EIGHT_BIT;
        int numberOfSamples = numberOfBytes / bytesPerSample / numberOfChannels;

        if (((numberOfSamples == TWO || bytesPerSample == TWO) && (numberOfBytes % TWO != 0)) ||
                ((numberOfSamples == TWO && bytesPerSample == TWO) && (numberOfBytes % FOUR != 0)))
            throw new IllegalArgumentException("Uneven number of bytes for given bit depth and number of channels");

        return convertBytesToDoubles(numberOfChannels, numberOfSamples, bitDepth, audioBytes);
    }

    /**
     * Method to carry out the conversion from byte[] to double[][]
     *
     * @param numberOfChannels int
     * @param numberOfSamples  int
     * @param bitDepth         int
     * @param audioBytes       byte[]. Audio byte[] data.
     * @return double[][] audio data.
     */
    protected double[][] convertBytesToDoubles(int numberOfChannels, int numberOfSamples, int bitDepth, byte[] audioBytes) {
        double[][] samples = new double[numberOfChannels][numberOfSamples];

        double maximumSampleValue = findMaximumSampleValue(bitDepth) + MAXIMUM_SAMPLE_OVERHEAD;

        ByteBuffer byteBuffer = ByteBuffer.wrap(audioBytes);

        if (bitDepth == EIGHT_BIT) {
            for (int sample = 0; sample < numberOfSamples; sample++) {
                for (int chan = 0; chan < numberOfChannels; chan++) {
                    samples[chan][sample] = (double) byteBuffer.get() / maximumSampleValue;
                }
            }
        } else if (bitDepth == SIXTEEN_BIT) {
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            for (int sample = 0; sample < numberOfSamples; sample++) {
                for (int chan = 0; chan < numberOfChannels; chan++) {
                    samples[chan][sample] = (double) shortBuffer.get() / maximumSampleValue;
                }
            }

        } else {
            throw new IllegalArgumentException("Bit depth must be either 8 bit or 16 bit");
        }
        return samples;
    }

    /**
     * For a given bit depth, find the maximum sample value.
     *
     * @param bitDepth int.
     * @return double. The maximum sample value.
     */
    protected double findMaximumSampleValue(int bitDepth) {
        int maxSampleValue = 1;
        for (int i = 0; i < (bitDepth - 1); i++) {
            maxSampleValue *= 2;
        }
        maxSampleValue--;
        return ((double) maxSampleValue) - 1.0;
    }

}