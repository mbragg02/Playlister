package com.mbragg.playlister.controllers;

import com.mbragg.playlister.controllers.audioControllers.AudioBatchController;
import com.mbragg.playlister.controllers.audioControllers.AudioTrackController;
import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.models.entitys.Track;
import com.mbragg.playlister.tools.file.DirectoryParser;
import com.mbragg.playlister.models.Playlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Application controller implementation.
 *
 * @author Michael Bragg
 */
@Component
public class PlaylistGeneratorApplicationController implements ApplicationController {

    private final AudioTrackController audioTrackController;
    private final Playlist playlist;
    private final DirectoryParser directoryParser;
    private final DAO dao;
    private final AudioBatchController audioBatchController;

    @Value("${suffix}")
    private String suffix;

    @Autowired
    public PlaylistGeneratorApplicationController(AudioTrackController audioTrackController, Playlist playlist, DirectoryParser directoryParser, AudioBatchController audioBatchController, DAO dao) {
        this.audioTrackController = audioTrackController;
        this.playlist = playlist;
        this.directoryParser = directoryParser;
        this.audioBatchController = audioBatchController;
        this.dao = dao;
    }

    @Override
    public boolean trackExists(String fileName) {
        return dao.trackExists(fileName);
    }

    @Override
    public List<Track> query(String fileName, int sizeOfResult, boolean restrictByGenre) {
        if (dao.trackExists(fileName)) {
            return playlist.build(dao.query(fileName, sizeOfResult, restrictByGenre));
        }
        return new ArrayList<>();
    }

    @Override
    public void launchPlaylist() {
        playlist.launch();
    }


    @Override
    public List<File> queryFileBatchBuild(File file) {
        List<File> singleFileList = new ArrayList<>();
        singleFileList.add(file);
        return audioBatchController.getFilesToProcess(singleFileList);
    }

    @Override
    public List<File> directoryBatchBuild(String musicDirectoryFilePath) {
        return audioBatchController.getFilesToProcess(directoryParser.parse(musicDirectoryFilePath, suffix));
    }

    @Override
    public Map<Future<byte[]>, AudioFormat> extractAudioBatch(List<File> filesToProcessBuffer) throws InterruptedException {
        return audioBatchController.batchAudioByteExtraction(filesToProcessBuffer);
    }

    @Override
    @Async
    public Future<Track> buildTrack(File file, byte[] bytes, AudioFormat format) {
        return new AsyncResult<>(audioTrackController.build(file, bytes, format));
    }

    @Override
    public void deleteDB() {
        dao.deleteDatabase();
    }


    @Override
    public void exportPlaylist(File file) {
        playlist.export(file);
    }

}