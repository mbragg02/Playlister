package com.mbragg.playlister.builders;

import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.entitys.Genre;
import com.mbragg.playlister.tools.json.JSONParser;
import com.mbragg.playlister.tools.strings.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * GenreBuilder class
 *
 * @author Michael Bragg
 */
@Component
public class GenreBuilder {

    private final JSONParser genreJSONParser;
    private final DAO dao;

    @Autowired
    public GenreBuilder(DAO dao, JSONParser genreJSONParser) {
        this.dao = dao;
        this.genreJSONParser = genreJSONParser;
    }

    /**
     * Builds a Map of genre relationships from a given filename (JSON)
     *
     * @param filename String. The name of the JSON file containing the genre relationships
     * @return A Map with the generic genres as keys, with lists of sub genres as values.
     */
    public Map<String, List<String>> build(String filename) {

        Map<String, List<String>> allGenres = genreJSONParser.parse(filename);
        insert(allGenres);
        return allGenres;
    }

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