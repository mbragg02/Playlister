package com.mbragg.playlister.tools.file;

import java.util.List;
import java.util.Map;

/**
 * JSON Parser interface
 *
 * @author Michael Bragg
 */
public interface JSONParser {

    /**
     * Parse a specific JSON file
     *
     * @param filename String. The name of the JSON file.
     * @return A Map containing the extracted JSON data.
     */
    Map<String, List<String>> parse(String filename);
}
