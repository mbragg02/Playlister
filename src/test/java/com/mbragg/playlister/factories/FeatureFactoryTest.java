package com.mbragg.playlister.factories;

import com.mbragg.playlister.features.Feature;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;

public class FeatureFactoryTest {

    @Test
    public void testGetInstance() throws Exception {
        FeatureFactory featureFactoryA = FeatureFactory.getInstance();
        assertTrue(FeatureFactory.class.isInstance(featureFactoryA));

        // Test that calling get instance again returns the same instance.
        FeatureFactory featureFactoryB = FeatureFactory.getInstance();

        assertSame(featureFactoryA, featureFactoryB);
    }

    @Test
    public void testGetFeatureList() throws Exception {
        FeatureFactory featureFactory = FeatureFactory.getInstance();

        List<Feature> featureList = featureFactory.getFeatureList();

        assertNotNull(featureList);
        assertFalse(featureList.isEmpty());

        // Test calling getFeatureList() again returns a different list
        assertNotSame(featureList, featureFactory.getFeatureList());
    }
}