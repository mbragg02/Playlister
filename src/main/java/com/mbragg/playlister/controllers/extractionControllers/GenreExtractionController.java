package com.mbragg.playlister.controllers.extractionControllers;

import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.models.entitys.Genre;
import com.mbragg.playlister.tools.file.JSONParser;
import com.mbragg.playlister.tools.strings.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Genre extraction controller. Single public Build method that returns a Map of genres/sub genres.
 *
 * @author Michael Bragg
 */
@Component
public class GenreExtractionController {

    private final JSONParser genreJSONParser;
    private final DAO dao;
    public static final String GENRES_JSON = "genres.json";


    @Autowired
    public GenreExtractionController(DAO dao, JSONParser genreJSONParser) {
        this.dao = dao;
        this.genreJSONParser = genreJSONParser;
    }

    /**
     * Builds a Map of genre relationships from a given filename (JSON)
     *
     * @return A Map with the generic genres as keys, with lists of sub genres as values.
     */
    public Map<String, List<String>> build() {

        Map<String, List<String>> allGenres = genreJSONParser.parse(GENRES_JSON);
        insert(allGenres);
        return allGenres;
    }

    /**
     * Saves to genre Map through the DAO to the database.
     *
     * @param allGenres Map<String, List<String>>. The genre/sub genre mapping.
     */
    private void insert(Map<String, List<String>> allGenres) {

        for (String genreName : allGenres.keySet()) {
            String cleanGenre = StringTools.formatter(genreName);
            if (!dao.genreExists(cleanGenre)) {
                Genre newGenre = new Genre();
                newGenre.setName(cleanGenre);
                dao.saveGenre(newGenre);
            }
        }

    }

}