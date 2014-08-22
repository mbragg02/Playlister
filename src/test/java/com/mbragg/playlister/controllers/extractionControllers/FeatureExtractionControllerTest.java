package com.mbragg.playlister.controllers.extractionControllers;

import com.mbragg.playlister.models.Samples;
import com.mbragg.playlister.models.AudioStream;
import com.mbragg.playlister.features.Feature;
import com.mbragg.playlister.features.MultivariateNormalDistributionModel;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Michael Bragg
 */
public class FeatureExtractionControllerTest {

    private static final double DELTA = 1e-15;
    private FeatureExtractionController featureExtractionController;

    @Mock
    private AudioStream audioStream;

    @Mock
    private Samples samples;

    @Mock
    private MultivariateNormalDistributionModel multivariateNormalDistributionModel;

    @Mock
    private MultivariateNormalDistribution multivariateNormalDistribution;

    @Mock
    private AudioInputStream audioInputStream;

    @Mock
    private File file;

    @Mock
    private AudioFormat audioFormat;

    @Mock
    private Feature feature;


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(audioInputStream.available()).thenReturn(1);
        when(audioStream.getAudioInputStream(any())).thenReturn(audioInputStream);
        when(audioStream.getSampleRate()).thenReturn(44.0f);

        double[] testDoubleArray = {1.0, 2.0, 3.0};
        when(samples.getSamplesInMono(any(), any())).thenReturn(testDoubleArray);

        featureExtractionController = new FeatureExtractionController(audioStream, samples, multivariateNormalDistributionModel);
    }

//    @Test
//    public void testGetFormattedAudioInputStream() throws Exception {
//        AudioInputStream actual = featureExtractionController.getFormattedAudioInputStream(file);
//
//        verify(audioStreamController, times(1)).getAudioInputStream(file);
//        assertEquals(1, actual.available());
//    }

    @Test
    public void testExtract() throws Exception {
        when(multivariateNormalDistributionModel.build(any())).thenReturn(multivariateNormalDistribution);
        when(multivariateNormalDistribution.toString()).thenReturn("test model");

        byte[] bytes = {1, 2, 3, 4};
        MultivariateNormalDistribution actual = featureExtractionController.extract(bytes, audioFormat);

        assertEquals("test model", actual.toString());
        verify(multivariateNormalDistributionModel).build(any());
    }

    @Test
    public void testExtractSamples() throws Exception {
        byte[] audioBytes = {1, 2, 3, 4};
        double[] actual = featureExtractionController.getSamples(audioBytes, audioFormat);

        verify(samples, times(1)).getSamplesInMono(audioBytes, audioFormat);
        assertEquals(1.0, actual[0], DELTA);
        assertEquals(2.0, actual[1], DELTA);
        assertEquals(3.0, actual[2], DELTA);
    }

    @Test
    public void testGetFeatures() throws Exception {
        double[] testFeatureResults = {10.0, 20.0};
        when(feature.extractFeature(any(), anyDouble())).thenReturn(testFeatureResults);

        double[] testSamples = new double[1000];

        for (int i = 0; i < 1000; i++) {
            testSamples[i] = 1.0;
        }

        List<Feature> listOfMockFeatures = new ArrayList<>();
        listOfMockFeatures.add(feature);

        double[][][] actual = featureExtractionController.getFeatures(testSamples, listOfMockFeatures);
        // First window, the only feature, two values feature extraction vector [10.0, 20.0]
        assertEquals(10.0, actual[0][0][0], DELTA);
        assertEquals(20.0, actual[0][0][1], DELTA);

        // Second window, the only feature, the same two values feature extraction vector
        assertEquals(10.0, actual[1][0][0], DELTA);
        assertEquals(20.0, actual[1][0][1], DELTA);

    }

    @Test
    public void testCalculateWindowStartPositions() throws Exception {
        int[] actual = featureExtractionController.calculateWindowStartPositions(2000);

        assertEquals(0, actual[0]);
        assertEquals(512, actual[1]);
        assertEquals(1024, actual[2]);
        assertEquals(1536, actual[3]);
    }

    @Test
    public void testGetFeatureVectorList() throws Exception {
        double[][][] testData = {{{10.0, 20.0}, {30.0, 40.0}}, {{1.0, 2.0}, {3.0, 4.0}}};

        List<double[]> actual = featureExtractionController.getFeatureVectorList(testData);

        assertEquals(10.0, actual.get(0)[0], DELTA);
        assertEquals(20.0, actual.get(0)[1], DELTA);
        assertEquals(30.0, actual.get(0)[2], DELTA);
        assertEquals(40.0, actual.get(0)[3], DELTA);

        assertEquals(1.0, actual.get(1)[0], DELTA);
        assertEquals(2.0, actual.get(1)[1], DELTA);
        assertEquals(3.0, actual.get(1)[2], DELTA);
        assertEquals(4.0, actual.get(1)[3], DELTA);
    }
}