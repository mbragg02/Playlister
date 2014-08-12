package com.mbragg.playlister.models.entitys;

import com.mbragg.playlister.models.entitys.Genre;
import com.mbragg.playlister.models.entitys.Track;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class TrackTest {

    private static final double DELTA = 1e-15;
    private Track track;
    private double[] features;

    @Mock
    private Genre genre;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        track = new Track();
        features = new double[]{1.0, 2.0, 2.0};
    }

    @Test
    public void testGetAndSetFileName() {
        track.setFilename("filename");
        assertEquals("filename", track.getFilename());
    }

    @Test
    public void testGetAndSetFilePath() {
        track.setFilePath("filepath");
        assertEquals("filepath", track.getFilePath());
    }

    @Test
    public void testGetAndSetArtist() {
        track.setArtist("artist");
        assertEquals("artist", track.getArtist());
    }

    @Test
    public void testGetAndSetTitle() {
        track.setTitle("title");
        assertEquals("title", track.getTitle());
    }

    @Test
    public void testGetAndSetAlbum() {
        track.setAlbum("album");
        assertEquals("album", track.getAlbum());
    }

    @Test
    public void testGetAndSetSubGenre() {
        track.setSubGenre("subgenre");
        assertEquals("subgenre", track.getSubGenre());
    }

    @Test
    public void testGetAndSetYear() {
        track.setYear("year");
        assertEquals("year", track.getYear());
    }

    @Test
    public void testGetAndSetGenre() {
        track.setGenre("genre");
        assertEquals("genre", track.getGenre());
    }

    @Test
    public void testGetAndSetGenreNode() {
        track.isGenre(genre);
        assertEquals(genre, track.getGenreNode());
    }

}