package com.mbragg.playlister.builders;

import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.tools.json.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class GenreBuilderTest {

    @Mock
    private DAO dao;

    @Mock
    private JSONParser jsonParser;

    private GenreBuilder genreBuilder;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        Map<String, List<String>> testGenres = new HashMap<>();
        testGenres.put("Rock", new ArrayList<>());

        when(jsonParser.parse(anyString())).thenReturn(testGenres);

        genreBuilder = new GenreBuilder(dao, jsonParser);
    }

    @Test
    public void testBuildNoNewGenresToAdd() throws Exception {
        when(dao.genreExists("rock")).thenReturn(true);

        genreBuilder.build(anyString());

        verify(dao, never()).saveGenre(any());
    }

    @Test
    public void testBuildWithNewGenresToSave() {
        when(dao.genreExists("rock")).thenReturn(false);

        genreBuilder.build(anyString());

        verify(dao, times(1)).saveGenre(any());
    }
}