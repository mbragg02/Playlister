package com.mbragg.playlister.factories;

import com.mbragg.playlister.models.entitys.Track;
import org.junit.Test;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static junit.framework.TestCase.assertTrue;

public class TrackFactoryTest {

    @Test
    public void testGetInstance() throws Exception {

        TrackFactory trackFactoryA = TrackFactory.getInstance();

        assertTrue(TrackFactory.class.isInstance(trackFactoryA));

        // Test that calling get instance again returns the same instance.
        TrackFactory trackFactoryB = TrackFactory.getInstance();

        assertSame(trackFactoryA, trackFactoryB);
    }

    @Test
    public void testGetTrack() throws Exception {
        TrackFactory trackFactory = TrackFactory.getInstance();
        Track trackA = trackFactory.getTrack();
        assertTrue(Track.class.isInstance(trackA));

        // Test that calling getTrack again returns a new Track
        Track trackB = trackFactory.getTrack();
        assertNotSame(trackA, trackB);
    }
}