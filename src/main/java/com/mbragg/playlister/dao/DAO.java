package com.mbragg.playlister.dao;

import com.mbragg.playlister.models.entitys.Genre;
import com.mbragg.playlister.models.entitys.Track;

import java.util.List;

/**
 * Data access object interface for data backend.
 *
 * @author Michael Bragg
 */
public interface DAO {
    /**
     * Checks if a particular genre exists in the database.
     *
     * @param genreName String. The name of the genre to check.
     * @return Boolean. Yes if it exists. No if it does not.
     */
    boolean genreExists(String genreName);

    /**
     * Checks if a particular track exists in the database.
     *
     * @param fileName String. The name of the file to check.
     * @return Boolean. Yes if it exists. No if it does not.
     */
    boolean trackExists(String fileName);

    /**
     * Save a Genre to the database
     *
     * @param genre Genre. Genre to save
     */
    void saveGenre(Genre genre);

    /**
     * Save a Track to the database.
     *
     * @param track Track to save
     */
    void saveTrack(Track track);

    /**
     * Query the database to generate a new 'Playlist'
     *
     * @param filename        String. Name of the file to query.
     * @param sizeOfResult    int. Number of files in the playlist
     * @param restrictByGenre Boolean. Whether to restrict the playlist to be all the same genre meta tag as the query file.
     * @return A list of Tracks
     */
    List<Track> query(String filename, int sizeOfResult, boolean restrictByGenre);

    /**
     * Delete the data in the database.
     */
    void deleteDatabase();

    /**
     * Shut down the database cleanly
     */
    public void shutdown();
}
