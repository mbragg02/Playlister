package com.mbragg.playlister.tools.json;

import java.util.List;
import java.util.Map;

/**
 * JSON Parser interface
 *
 * @author Michael Bragg
 */
public interface JSONParser {
    Map<String, List<String>> parse(String filename);
}
