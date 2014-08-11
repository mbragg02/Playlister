package com.mbragg.playlister.controllers.extractionControllers;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MetaExtractionControllerTest {

    private File file;
    private MetaExtractionController metaExtractionController;

    @Before
    public void setUp() {
        /*
        Test file: Testname.m4a
        name: Testname
        artist: Test artist
        album: Test album
        genre: Rock
        year: 2014
         */

        URL url = Thread.currentThread().getContextClassLoader().getResource("Testname.m4a");
        if (url != null) {
            file = new File(url.getPath());
        }
        metaExtractionController = new MetaExtractionController();

    }
    @Test
    public void testParse() throws Exception {
        Map<String, String> meta =  metaExtractionController.extract(file);

        assertEquals("Test artist", meta.get("ARTIST"));
        assertEquals("2013", meta.get("YEAR"));
        assertEquals("Test album", meta.get("ALBUM"));
        assertEquals("Testname", meta.get("TITLE"));
        assertEquals("Rock", meta.get("GENRE"));
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseWithNotFoundExceptions() throws Exception{
        File unknownFile = new File("notfound.m4a");
        metaExtractionController.extract(unknownFile);
    }

}