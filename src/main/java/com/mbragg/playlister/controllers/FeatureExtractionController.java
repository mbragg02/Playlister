package com.mbragg.playlister.controllers;

import com.mbragg.playlister.features.MultivariateNormalDistributionModel;
import com.mbragg.playlister.controllers.audioControllers.AudioSampleController;
import com.mbragg.playlister.controllers.audioControllers.AudioStreamController;
import com.mbragg.playlister.factories.FeatureFactory;
import com.mbragg.playlister.features.Feature;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Component
public class FeatureExtractionController {

    private static final int WINDOW_SIZE = 512;
    private static final double WINDOW_OVERLAP = 0.0;
    private int windowOverlapOffset;

    private AudioStreamController audioStreamController;
    private AudioSampleController audioSampleController;
    private MultivariateNormalDistributionModel multivariateNormalDistributionModel;

    @Autowired
    public FeatureExtractionController(AudioStreamController audioStreamController, AudioSampleController audioSampleController, MultivariateNormalDistributionModel multivariateNormalDistributionModel) {
        this.audioStreamController = audioStreamController;
        this.audioSampleController = audioSampleController;
        this.multivariateNormalDistributionModel = multivariateNormalDistributionModel;

        this.windowOverlapOffset = (int) (WINDOW_OVERLAP * (double) WINDOW_SIZE);
    }

    public MultivariateNormalDistribution extract(byte[] audioBytes, AudioFormat audioFormat) throws Exception {
        double[] samples = extractSamples(audioBytes, audioFormat);

        List<Feature> featuresToExtract = FeatureFactory.getInstance().getFeatureList();

        double[][][] results = getFeatures(samples, featuresToExtract);

        return multivariateNormalDistributionModel.build(getFeatureVectorList(results));
    }

    public AudioInputStream getFormattedAudioInputStream(File file) throws IOException, UnsupportedAudioFileException {
        return audioStreamController.setAudioInputStream(file);
    }

    protected double[] extractSamples(byte[] audioBytes, AudioFormat audioFormat) throws Exception {
        return audioSampleController.getSamplesInMono(audioBytes, audioFormat);
    }

    protected double[][][] getFeatures(double[] samples, List<Feature> featuresToExtract) {
        int[] windowStartPositions = calculateWindowStartPositions(samples.length);

        double samplingRate = audioStreamController.getSampleRate();

        double[][][] results = new double[windowStartPositions.length][featuresToExtract.size()][];

        for (int win = 0; win < windowStartPositions.length; win++) {

            double[] window = new double[WINDOW_SIZE];

            // Set window sample positions
            int startSample = windowStartPositions[win];
            int endSample = startSample + WINDOW_SIZE - 1;

            // Get the samples for the current window
            if (endSample < samples.length) {
                System.arraycopy(samples, startSample, window, 0, endSample + 1 - startSample);
            } else {
                // Case when end of window is larger than the number of samples. i.e reached then end of the file
                // Pad the end of the window with zeros.
                for (int sample = startSample; sample <= endSample; sample++) {
                    if (sample < samples.length)
                        window[sample - startSample] = samples[sample];
                    else
                        window[sample - startSample] = 0.0;
                }
            }

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

    protected List<double[]> getFeatureVectorList(double[][][] featureResults) {

        // featureResults structure: [window][feature][values]

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










//    Unused at the moment! Possible delete later


    // Used by async method
//    public double[] extract(byte[] audioBytes, AudioFormat audioFormat) throws Exception {
//        extractSamples(audioBytes, audioFormat);
//        return getAverageVector(getFeatures());
//    }

//    public double[] extract(File file) throws Exception {
//        extractSamples(file);
//        double[][][] vals = getFeatures();
//        return getAverageVector(vals);
//    }

//    private void extractSamples(File file) throws Exception {
//        AudioInputStream formattedAudioInputStream = audioStreamController.setAudioInputStream(file);
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
