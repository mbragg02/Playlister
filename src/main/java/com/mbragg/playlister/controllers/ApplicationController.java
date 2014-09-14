package com.mbragg.playlister.controllers;

import com.mbragg.playlister.models.BatchTrack;
import com.mbragg.playlister.models.entitys.Track;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Single entry point for the application.
 *
 * @author Michael Bragg
 */
public interface ApplicationController {
    /**
     * Query the application to get a playlist.
     *
     * @param fileName        String. Name of the file to act as the query
     * @param sizeOfResult    int. The number of files in the playlist
     * @param restrictByGenre Boolean. Whether or ont to restrict the the query to the same genre meta tag as the query track.
     * @return A List of tracks for the generated playlist.
     */
    List<Track> query(String fileName, int sizeOfResult, boolean restrictByGenre);

    /**
     * Check whether a track exists in the database.
     *
     * @param fileName String. The name of the file to check.
     * @return Boolean. whether it is in the database or not.
     */
    boolean trackExists(String fileName);

    /**
     * Launch a media player to play the generated playlist.
     * The media player is the default on whatever system the application is currently being run on.
     */
    void launchPlaylist();

    /**
     * Given a String to a local music directory, return a list of .m4a files that have not
     * yet been processed and stored in the application's database.
     *
     * @param musicDirectoryFilePath String. Path to local music directory
     * @return List of files found in the directory (and sub directory's) that are also not yet
     * processed and stored in the applications database.
     */
    List<File> directoryBatchBuild(String musicDirectoryFilePath);

    /**
     * Given a single file, returns a list containing the file if it has not yet been processed and stored on the applications database.
     * [Allows for the case when a user tries to build a playlist from a file that has not yet been proceed and stored]
     *
     * @param file File. File to check against the database.
     * @return List containing the file if is not yet been processed and stored in the applications database.
     */
    List<File> queryFileBatchBuild(File file);

    /**
     * Given a list of files to process, execute a batch job to return a Map of the extracted audio bytes and audio formats.
     *
     * @param filesToProcessBuffer List of files to process i.e. file buffer
     * @return A Map of the extracted audio bytes and audio formats.
     * @throws InterruptedException if the batch job is interrupted at any time. i.e A user has chosen to cancel the job.
     */
    List<BatchTrack> extractAudioBatch(List<File> filesToProcessBuffer) throws InterruptedException;


    /**
     * Build a Track from a BatchTrack
     * @param batchTrack A BatchTrack i.e. Wraps parameters needed to build a Track object.
     * @return A new Track
     */
    @Async
    Future<Track> buildTrack(BatchTrack batchTrack);

    /**
     * Call to delete all the data in the database
     */
    void deleteDB();

    /**
     * Export and save the generated playlist to new file
     *
     * @param file File. The new for the generated playlist
     */
    void exportPlaylist(File file);

    /**
     * Shutdown the database cleanly
     */
    void dbShutdown();
}