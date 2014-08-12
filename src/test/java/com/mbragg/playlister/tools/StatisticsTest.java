package com.mbragg.playlister.tools;

import com.mbragg.playlister.tools.numbers.Statistics;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class StatisticsTest {

    private static final double DELTA = 1e-15;


    @Test
    public void testCosineSimilaritySimple() throws Exception {
        double[] a = {1, 2, 2};
        double[] b = {2, 4, 4};
        double aMagnitude = 3.0;
        double bMagnitude = 6.0;
        double actual = Statistics.cosineSimilarity(a, b);
        double expected = ( 2 + (2 * 4) + (2 * 4)) / (aMagnitude * bMagnitude);

        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void testCosineSimilarityFull() {
        double[] a = {1, 2, 2};
        double[] b = {2, 4, 4};
        double aMagnitude = 3.0;
        double bMagnitude = 6.0;
        double actual = Statistics.cosineSimilarity(a, b, aMagnitude, bMagnitude);
        double expected = ( 2 + (2 * 4) + (2 * 4)) / (aMagnitude * bMagnitude);

        assertEquals(expected, actual, DELTA);

    }

    @Test
    public void testDotProduct() throws Exception {
        double[] a = {1, 2, 3};
        double[] b = {4, 5, 6};
        double expected = (4) + (2 * 5) + (3 * 6);
        double actual = Statistics.dotProduct(a, b);

        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void testDotProductWithDifferentSizeArrays() {
        double[] a = {1, 2, 3, 4};
        double[] b = {5, 6, 7};
        double expected = (5) + (2 * 6) + (3 * 7);

        // As the larger array as the first argument
        double actual = Statistics.dotProduct(a, b);
        assertEquals(expected, actual, DELTA);

        // with the larger array as the second argument
        actual = Statistics.dotProduct(b, a);
        assertEquals(expected, actual, DELTA);
    }
}