package com.mbragg.playlister.relationships;

import com.mbragg.playlister.models.entitys.Track;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

/**
 * @author Michael Bragg
 */
public class SimilarToTest {

    private static final double DELTA = 1e-15;
    private SimilarTo similarTo;
    @Mock
    private Track fromTrack;

    @Mock
    private Track toTrack;
    private double similarity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        similarity = 1.0;

        similarTo = new SimilarTo(); // Make sure there is an empty constructor present
        similarTo = new SimilarTo(fromTrack, toTrack, similarity);
    }

    @Test
    public void testGetFromTrack() {
        assertEquals(fromTrack, similarTo.getFromTrack());
    }

    @Test
    public void testGetToTrack() {
        assertEquals(toTrack, similarTo.getToTrack());
    }

    @Test
    public void testGetSimilarity() {
        assertEquals(similarity, similarTo.getSimilarity(), DELTA);
    }
}