package com.mbragg.playlister.tools.file;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to parse the specific genre JSON file, to parse the genre/sub genre relationships.
 *
 * @author Michael Bragg
 */
@Component
public class GenreJSONParser implements JSONParser {

    private org.json.simple.parser.JSONParser parser;
    private Map<String, List<String>> allGenres;

    public GenreJSONParser() {
        parser = new org.json.simple.parser.JSONParser();
        allGenres = new HashMap<>();
    }

    @Override
    public Map<String, List<String>> parse() {

        try {
            Resource json = new ClassPathResource("genres.json");

            InputStream jsonInputStream = json.getInputStream();

            Object jsonObject = parser.parse(new InputStreamReader(jsonInputStream));


            JSONObject genres = (JSONObject) jsonObject;

            for (Object obj : genres.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;

                JSONArray subGenres = (JSONArray) entry.getValue();

                List<String> subGenresList = new ArrayList<>();
                for (Object sub : subGenres) {
                    subGenresList.add((String) sub);
                }

                allGenres.put(entry.getKey().toString(), subGenresList);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return allGenres;
    }


}
