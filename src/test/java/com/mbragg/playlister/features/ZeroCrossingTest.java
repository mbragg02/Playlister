package com.mbragg.playlister.features;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Michael Bragg
 */
public class ZeroCrossingTest {

    private static final double DELTA = 1e-15;
    private ZeroCrossing zeroCrossing;

    @Before
    public void setUp() throws Exception {
        zeroCrossing = new ZeroCrossing();
    }

    @Test
    public void testExtractFeature() throws Exception {
        double[] testData = {0, 1, -2, 3, 4, -5, 6};
        double[] actual = zeroCrossing.extractFeature(testData, 0.0);
        assertEquals(5.0, actual[0], DELTA);
    }
}