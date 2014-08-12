package com.mbragg.playlister.controllers.extractionControllers;

import com.mbragg.playlister.models.Samples;
import com.mbragg.playlister.models.AudioStream;
import com.mbragg.playlister.factories.FeatureFactory;
import com.mbragg.playlister.features.Feature;
import com.mbragg.playlister.features.MultivariateNormalDistributionModel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Feature Extraction controller.
 * <p>
 * Controls the feature extraction processes and model construction
 *
 * @author Michael Bragg
 */
@Component
public class FeatureExtractionController {

    private static final int WINDOW_SIZE = 512;
    private static final double WINDOW_OVERLAP = 0.0;
    private int windowOverlapOffset;

    private AudioStream audioStream;
    private Samples samples;
    private MultivariateNormalDistributionModel multivariateNormalDistributionModel;

    @Autowired
    public FeatureExtractionController(AudioStream audioStream, Samples samples, MultivariateNormalDistributionModel multivariateNormalDistributionModel) {
        this.audioStream = audioStream;
        this.samples = samples;
        this.multivariateNormalDistributionModel = multivariateNormalDistributionModel;
        this.windowOverlapOffset = (int) (WINDOW_OVERLAP * (double) WINDOW_SIZE);
    }

    /**
     * Extract method. Main method to run the feature extraction process.
     *
     * @param audioBytes  byte[]. The byte array for an audio file.
     * @param audioFormat AudioFormat. The audio format information for a audio file
     * @return a MultivariateNormalDistribution model that represents an audio file.
     * @throws Exception
     */
    public MultivariateNormalDistribution extract(byte[] audioBytes, AudioFormat audioFormat) throws Exception {

        double[] samples = getSamples(audioBytes, audioFormat);

        List<Feature> featuresToExtract = FeatureFactory.getInstance().getFeatureList();

        double[][][] results = getFeatures(samples, featuresToExtract);

        return multivariateNormalDistributionModel.build(getFeatureVectorList(results));
    }

    /**
     * Extracts audio samples from a given audio byte[] array.
     *
     * @param audioBytes  byte[] extracted from a audio file.
     * @param audioFormat AudioFormat of a audio file
     * @return a double[] of samples from a audio file.
     * @throws Exception
     */
    protected double[] getSamples(byte[] audioBytes, AudioFormat audioFormat) throws Exception {
        return samples.getSamplesInMono(audioBytes, audioFormat);
    }

    /**
     * Main method to parse the audio features.
     *
     * @param samples           double[] samples of a audio file.
     * @param featuresToExtract List<Feature>. List of features to parse.
     * @return double[][][]. Arrays containing all the feature values for each window of audio.
     * Array format is: [window][feature][values]
     */
    protected double[][][] getFeatures(double[] samples, List<Feature> featuresToExtract) {
        int[] windowStartPositions = calculateWindowStartPositions(samples.length);

        double samplingRate = audioStream.getSampleRate();

        double[][][] results = new double[windowStartPositions.length][featuresToExtract.size()][];

        for (int win = 0; win < windowStartPositions.length; win++) {

            double[] window = fillWindowWithSamples(samples, windowStartPositions, win);

            List<double[]> values = featuresToExtract.stream()
                    .map(f -> f.extractFeature(window, samplingRate))
                    .collect(Collectors.toList());

            for (int i = 0; i < values.size(); i++) {
                // Loops over list of features extracted values. Add them to result array.
                results[win][i] = values.get(i);
            }
        }

        return results;
    }

    /**
     * Method to return an array of samples for a single window.
     *
     * @param samples              double[] All the samples of a file.
     * @param windowStartPositions int[] All the window start positions for the file.
     * @param win                  int. The current window position
     * @return double[]. Samples for the current window position.
     */
    private double[] fillWindowWithSamples(double[] samples, int[] windowStartPositions, int win) {

        double[] window = new double[WINDOW_SIZE];

        // Set window sample positions
        int startSample = windowStartPositions[win];
        int endSample = startSample + WINDOW_SIZE - 1;

        // Get the samples for the current window
        if (endSample < samples.length) {
            System.arraycopy(samples, startSample, window, 0, endSample + 1 - startSample);
        } else {

            for (int sample = startSample; sample <= endSample; sample++) {
                if (sample < samples.length)
                    window[sample - startSample] = samples[sample];
                else {
                    // Case when end of window is larger than the number of samples. i.e reached then end of the file
                    // Pad the end of the window with zeros.
                    window[sample - startSample] = 0.0;
                }
            }
        }

        return window;
    }

    /**
     * Given the length of all the samples, calculate the positions of the start of all the windows.
     *
     * @param samplesLength int. Length of all the samples
     * @return int[]. All the window start positions.
     */
    protected int[] calculateWindowStartPositions(int samplesLength) {

        LinkedList<Integer> windowStartPositionsList = new LinkedList<>();

        int currentStartPosition = 0;
        while (currentStartPosition < samplesLength) {
            windowStartPositionsList.add(currentStartPosition);
            currentStartPosition += WINDOW_SIZE - windowOverlapOffset;
        }

        Integer[] windowStartIndices = windowStartPositionsList.toArray(new Integer[1]);
        int[] windowStartPositions = new int[windowStartIndices.length];

        for (int i = 0; i < windowStartPositions.length; i++)
            windowStartPositions[i] = windowStartIndices[i];

        return windowStartPositions;
    }

    /**
     * Translates the [window][feature][value] results array to a list of double[] vectors.
     * Each single double[] vector represents a combination of the different feature values for each window.
     * For example: [MFCCs & RMS & Zero crossing rate]
     *
     * @param featureResults Full results array. [window][feature][value]
     * @return List<double[]> List of complete feature vector for each window.
     */
    protected List<double[]> getFeatureVectorList(double[][][] featureResults) {

        // featureResults array structure: [window][feature][values]

        List<double[]> aggregatedFeatureVectorList = new ArrayList<>();
        List<double[]> featureVectorList = new ArrayList<>();
        List<Double> featureValues = new ArrayList<>();

        for (double[][] features : featureResults) {
            Collections.addAll(featureVectorList, features);

            featureVectorList
                    .stream()
                    .map(Arrays::stream)
                    .map(DoubleStream::boxed)
                    .forEach(d -> d.forEach(featureValues::add));
//
//            for (double[] featureVector : featureVectorList) {
//                for (double featureVectorValue : featureVector) {
//                    featureValues.add(featureVectorValue);
//                }
//            }

            aggregatedFeatureVectorList.add(ArrayUtils.toPrimitive(featureValues.toArray(new Double[featureValues.size()])));
            featureVectorList.clear();
            featureValues.clear();
        }
        return aggregatedFeatureVectorList;
    }




//    Code from previous prototypes. Not currently used!


    // Used by async method
//    public double[] parse(byte[] audioBytes, AudioFormat audioFormat) throws Exception {
//        getSamples(audioBytes, audioFormat);
//        return getAverageVector(getFeatures());
//    }

//    public double[] parse(File file) throws Exception {
//        getSamples(file);
//        double[][][] vals = getFeatures();
//        return getAverageVector(vals);
//    }

//    private void getSamples(File file) throws Exception {
//        AudioInputStream formattedAudioInputStream = audioStreamController.getAudioInputStream(file);
//        samples = audioSampleController.getSamplesInMono(formattedAudioInputStream);
//
//    }

//    private double[] getAverageVector(double[][][] windowFeatureValues) {
//        // windowFeatureValues [window][feature][values]
//
//        double[] result = new double[0];
//
//        for (int feat = 0; feat < featuresToExtract.size(); feat++) {
//
//            double averages;
//            int numberOfWindows = windowFeatureValues.length - 1;
//
//            result = new double[windowFeatureValues[numberOfWindows][feat].length];
//
//            // change val to 1 to avoid low frequency bias
//            for (int val = 1; val < windowFeatureValues[numberOfWindows][feat].length; val++) {
//                // Find the values to find the average and standard deviations of
//                double[] valuesToProcess = new double[windowFeatureValues.length];
//
//                int current = 0;
//                for (double[][] windowFeatureValue : windowFeatureValues) {
//                    if (windowFeatureValue[feat] != null) {
//                        valuesToProcess[current] = windowFeatureValue[feat][val];
//                        current++;
//                    }
//                }
//                averages = Arrays.stream(valuesToProcess).average().getAsDouble();
//                result[val] = averages;
//            }
//        }
//        return result;
//    }


}
