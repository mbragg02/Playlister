package com.mbragg.playlister.models.entitys;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

/**
 * @author Michael Bragg
 */
public class TrackTest {

    private Track track;

    @Mock
    private Genre genre;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        track = new Track();
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