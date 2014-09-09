package com.mbragg.playlister.tools;

import com.mbragg.playlister.tools.file.GenreJSONParser;
import com.mbragg.playlister.tools.file.JSONParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * @author Michael Bragg
 */
public class JSONParserTest {

    private JSONParser genreJSONParser;
    public static final String GENRES_JSON = "genres.json";


    @Before
    public void setUp() throws Exception {
        this.genreJSONParser = new GenreJSONParser();
    }

    @Test
    public void testParse() throws Exception {
        Map<String, List<String>> result = genreJSONParser.parse(GENRES_JSON);

        for (Map.Entry<String, List<String>> entry : result.entrySet()) {
            List<String> values = entry.getValue();

            assertNotNull(entry.getKey());
            assertNotNull(values);

            values.forEach(org.junit.Assert::assertNotNull);
        }

    }
}