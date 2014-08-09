package com.mbragg.playlister.tools;

import com.mbragg.playlister.tools.strings.StringTools;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringToolsTest {

    @Test
    public void testLevenshteinDistanceEqual() throws Exception {
        int actual = StringTools.levenshteinDistance("test", "test", 1);
        assertEquals(0, actual);
    }

    @Test
    public void testLevenshteinDistanceOneCharacterDifference() throws Exception {
        int actual = StringTools.levenshteinDistance("test", "tesx", 1);
        assertEquals(1, actual);
    }

    @Test
    public void testLevenshteinDistanceTwoCharacterDifference() throws Exception {
        int actual = StringTools.levenshteinDistance("test", "texx", 1);
        // returns -1 if the distance is greater than the threshold (1 in this case)
        assertEquals(-1, actual);
    }

    @Test
    public void testFuzzyEquals() throws Exception {
        // Should return two if there is only 0 or 1 characters different
        assertTrue(StringTools.fuzzyEquals("test", "test"));
        assertTrue(StringTools.fuzzyEquals("test", "tesy"));
        assertFalse(StringTools.fuzzyEquals("test", "texy"));
    }

    @Test
    public void testFormatter() throws Exception {
        assertEquals("test", StringTools.formatter("test"));
        assertEquals("test", StringTools.formatter("Test"));
        assertEquals("test", StringTools.formatter("tes t"));
        assertEquals("test", StringTools.formatter("test -"));
        assertEquals("test", StringTools.formatter("te & st"));
        assertEquals("test", StringTools.formatter("(test)"));
        assertEquals("test", StringTools.formatter("01 test"));
    }
}