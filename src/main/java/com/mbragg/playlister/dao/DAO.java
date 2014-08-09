package com.mbragg.playlister.dao;

import com.mbragg.playlister.entitys.Genre;
import com.mbragg.playlister.entitys.Track;

import java.util.List;

/**
 * Data access object interface for data backend.
 *
 * @author Michael Bragg
 */
public interface DAO {
    boolean genreExists(String genre);

    boolean trackExists(String filename);

    void saveGenre(Genre genre);

    void saveTrack(Track track);

    List<Track> query(String filename, int sizeOfResult, boolean restrictByGenre);

    void deleteDatabase();
}
