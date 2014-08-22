package com.mbragg.playlister.factories;

import junit.framework.TestCase;
import org.jaudiotagger.tag.FieldKey;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.TestCase.assertTrue;

/**
 * @author Michael Bragg
 */
public class TrackMetaFactoryTest {

    @Test
    public void testGetInstance() throws Exception {
        TrackMetaFactory trackMetaFactoryA = TrackMetaFactory.getInstance();

        assertTrue(TrackMetaFactory.class.isInstance(trackMetaFactoryA));

        // Test that calling get instance again returns the same instance.
        TrackMetaFactory trackMetaFactoryB = TrackMetaFactory.getInstance();

        assertSame(trackMetaFactoryA, trackMetaFactoryB);

    }

    @Test
    public void testGetFieldKeys() throws Exception {
        TrackMetaFactory trackMetaFactory = TrackMetaFactory.getInstance();

        List<FieldKey> fieldKeyList = trackMetaFactory.getFieldKeys();
        assertNotNull(fieldKeyList);
        assertFalse(fieldKeyList.isEmpty());

        // Test calling getFieldKeys again returns the same list
        TestCase.assertSame(fieldKeyList, TrackMetaFactory.getInstance().getFieldKeys());


    }
}