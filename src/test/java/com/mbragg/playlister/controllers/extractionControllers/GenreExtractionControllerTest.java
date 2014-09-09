package com.mbragg.playlister.controllers.extractionControllers;

import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.tools.file.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Michael Bragg
 */
public class GenreExtractionControllerTest {

    public static final String GENRES_JSON = "genres.json";

    @Mock
    private DAO dao;

    @Mock
    private JSONParser jsonParser;

    private GenreExtractionController genreExtractionController;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        Map<String, List<String>> testGenres = new HashMap<>();
        testGenres.put("Rock", new ArrayList<>());

        when(jsonParser.parse(GENRES_JSON)).thenReturn(testGenres);

        genreExtractionController = new GenreExtractionController(dao, jsonParser);
    }

    @Test
    public void testBuildNoNewGenresToAdd() throws Exception {
        when(dao.genreExists("rock")).thenReturn(true);

        genreExtractionController.build();

        verify(dao, never()).saveGenre(any());
    }

    @Test
    public void testBuildWithNewGenresToSave() {
        when(dao.genreExists("rock")).thenReturn(false);

        genreExtractionController.build();

        verify(dao, times(1)).saveGenre(any());
    }
}