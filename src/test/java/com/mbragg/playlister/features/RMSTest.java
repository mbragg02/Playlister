package com.mbragg.playlister.features;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RMSTest {

    private static final double DELTA = 1e-15;
    private RMS rms;

    @Before
    public void setUp() {
        rms = new RMS();
    }

    @Test
    public void testExtractFeature() throws Exception {
        double[] testData = {0.2, 0.3, 0.6};
        double[] actual = rms.extractFeature(testData, 0.0);

        double expected = Math.sqrt((0.2 * 0.2 + 0.3 * 0.3 + 0.6 * 0.6) / 3);

        assertEquals(expected, actual[0], DELTA);

    }
}