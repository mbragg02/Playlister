package com.mbragg.playlister.entitys;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GenreTest {

    @Test
    public void testGetAndSetName() throws Exception {
        Genre genre = new Genre();
        assertNull(genre.getName());
        genre.setName("name");
        assertEquals("name", genre.getName());
    }
}