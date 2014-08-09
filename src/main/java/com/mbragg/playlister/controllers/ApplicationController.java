package com.mbragg.playlister.controllers;

import com.mbragg.playlister.entitys.Track;
import org.springframework.scheduling.annotation.Async;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Application single entry interface
 *
 * @author Michael Bragg
 */
public interface ApplicationController {
    List<Track> query(String fileName, int sizeOfResult, boolean restrictByGenre) throws InterruptedException, ExecutionException;

    boolean trackExists(String fileName);

    void launchPlaylist();

    List<File> directoryBatchBuild(String musicDirectoryFilePath);

    List<File> queryFileBatchBuild(File file);

    Map<Future<byte[]>, AudioFormat> extractAudioBatch(List<File> filesToProcessBuffer) throws InterruptedException;

    @Async
    Future<Track> buildTrack(File file, byte[] bytes, AudioFormat format);

    void deleteDB();

    void exportPlaylist(File file);

//    void testPlaylistQuality() throws ExecutionException, InterruptedException;
}
