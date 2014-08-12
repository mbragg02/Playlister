package com.mbragg.playlister.tools.file;

import java.util.List;
import java.util.Map;

/**
 * JSON Parser interface
 *
 * @author Michael Bragg
 */
public interface JSONParser {
    Map<String, List<String>> parse();
}
