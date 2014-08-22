package com.mbragg.playlister.models;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.sound.sampled.AudioFormat;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Michael Bragg
 */
public class SamplesTest {
    private static final double DELTA = 1e-15;

    @Mock
    private AudioFormat audioFormat;

    private Samples samples;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(audioFormat.getFrameSize()).thenReturn(10);
        when(audioFormat.getFrameRate()).thenReturn(1.0f);

        when(audioFormat.getChannels()).thenReturn(2);
        when(audioFormat.getSampleSizeInBits()).thenReturn(8);
        when(audioFormat.isBigEndian()).thenReturn(true);
        when(audioFormat.getEncoding()).thenReturn(AudioFormat.Encoding.PCM_SIGNED);

        samples = new Samples();
    }

    @Test
    public void testGetSamplesInMonoFromBytes() {
        byte[] audioBytes = {1, 2, 3, 4};
        double[] actual = samples.getSamplesInMono(audioBytes, audioFormat);

        assertEquals(1.5 / 128, actual[0], DELTA);
        assertEquals(3.5 / 128, actual[1], DELTA);
    }

    @Test
    public void testGetSamplesInMonoFromStereoDoubleArray() {

        double[][] stereoArray = {{1,3},{2,4}};
        double[] actual = samples.getSamplesInMono(stereoArray);

        assertEquals(((1 + 2) / 2.0), actual[0], DELTA);
        assertEquals(((3 + 4) / 2.0), actual[1], DELTA);
    }

    @Test
    public void testGetSingleSampleInMonoFromStereoDoubleArray() {
        // If it is mono already, just return mono.
        double[][] stereoArray = {{1}};
        double[] actual = samples.getSamplesInMono(stereoArray);
        assertEquals(1.0, actual[0], DELTA);
    }


    @Test
    public void testGetSamplesInStereo() {
        byte[] audioBytes = {1,2};

        double[][] samples = this.samples.getSamplesInStereo(audioBytes, audioFormat);

        assertEquals(1.0 / 128, samples[0][0], DELTA);
        assertEquals(2.0 / 128, samples[1][0], DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSamplesInStereoLittleEndian() {
        byte[] audioBytes = {1,2};
        when(audioFormat.isBigEndian()).thenReturn(false);
        samples.getSamplesInStereo(audioBytes,audioFormat);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSamplesInStereoUnsigned() {
        byte[] audioBytes = {1,2};
        when(audioFormat.getEncoding()).thenReturn(AudioFormat.Encoding.PCM_UNSIGNED);
        samples.getSamplesInStereo(audioBytes,audioFormat);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSamplesInStereo12Bit() {
        byte[] audioBytes = {1,2};
        when(audioFormat.getSampleSizeInBits()).thenReturn(12);
        samples.getSamplesInStereo(audioBytes,audioFormat);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSamplesInStereoOddNumberOfSamplesFor16Bit() {
        byte[] audioBytes = {1,2,3};
        when(audioFormat.getSampleSizeInBits()).thenReturn(16);
        samples.getSamplesInStereo(audioBytes,audioFormat);
    }

    @Test
    public void testConvertBytesToDoubles8Bit() {
        byte[] audioBytes = {1, 2};
        double[][] actual = samples.convertBytesToDoubles(1, audioBytes.length, 8, audioBytes);
        assertEquals(1.0 / 128, actual[0][0], DELTA);
        assertEquals(2.0 / 128, actual[0][1], DELTA);
    }

    @Test
    public void testConvertBytesToDoubles16Bit() {
        byte[] audioBytes = {1, 1, 2, 2};
        double[][] actual = samples.convertBytesToDoubles(1, audioBytes.length / 2, 16, audioBytes);
        assertEquals(0.007843017578125, actual[0][0], DELTA);
        assertEquals(0.01568603515625, actual[0][1], DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertBytesToDoubles24Bit() {
        byte[] audioBytes = {1, 1, 2, 2};
        samples.convertBytesToDoubles(1, audioBytes.length, 24, audioBytes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertBytesToDoubles6Bit() {
        byte[] audioBytes = {1, 1, 2, 2};
        samples.convertBytesToDoubles(1, audioBytes.length, 6, audioBytes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertBytesToDoubles12Bit() {
        byte[] audioBytes = {1, 1, 2, 2};
        samples.convertBytesToDoubles(1, audioBytes.length, 12, audioBytes);
    }

    @Test
    public void testFindMaximumSampleValue() throws Exception {
        double actual = samples.findMaximumSampleValue(8);

        assertEquals(126.0, actual, DELTA);
    }

}