package com.mbragg.playlister.services;

import com.mbragg.playlister.controllers.ApplicationController;
import com.mbragg.playlister.entitys.Track;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
public class ScanService extends Service {

    public static final int INITIAL = 0;
    public int numberOfConcurrentThreads = 5;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private Logger logger;
    private List<File> listOfFiles;

    public ScanService() {
    }

    @Override
    protected Task createTask() {
        return new Task() {

            private int fileCounter;
            private List<File> filesToProcessBuffer;

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Scan canceled");
                updateProgress(INITIAL, INITIAL);
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Scan failed");
                updateProgress(INITIAL, INITIAL);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateProgress(INITIAL, INITIAL);
            }

            @Override
            protected Object call() throws Exception {

                if (listOfFiles.isEmpty()) {
                    updateMessage("No new files to process");
                    return null;
                }

                updateMessage("Files to process: " + listOfFiles.size());

                int filesToProcessStartIndex = INITIAL;
                int filesToProcessEndIndex = filesToProcessStartIndex + numberOfConcurrentThreads;
                fileCounter = INITIAL;

                while (true) {
                    if (filesToProcessEndIndex > listOfFiles.size()) {
                        filesToProcessEndIndex = listOfFiles.size();
                    }

                    filesToProcessBuffer = listOfFiles.subList(filesToProcessStartIndex, filesToProcessEndIndex);

                    updateMessage("Extracting audio... ");

                    Map<Future<byte[]>, AudioFormat> audioBytesWithAudioFormats;

                    try {
                        audioBytesWithAudioFormats = applicationController.extractAudioBatch(filesToProcessBuffer);
                        updateMessage("Extracting audio complete");

                        createTracksInBatch(audioBytesWithAudioFormats, filesToProcessBuffer);

                    } catch (InterruptedException e) {
                        if (isCancelled()) {
                            break;
                        }
                    }

                    if (filesToProcessEndIndex == listOfFiles.size()) {
                        break;
                    } else {
                        filesToProcessStartIndex += numberOfConcurrentThreads;
                        filesToProcessEndIndex = filesToProcessStartIndex + numberOfConcurrentThreads;
                    }
                }
                updateMessage("Scan complete");
                return null;
            }


            public void createTracksInBatch(Map<Future<byte[]>, AudioFormat> audioBytesWithAudioFormats, List<File> filesToProcessBuffer)
                    throws ExecutionException, InterruptedException {

                int numberOfFilesToProcess = listOfFiles.size();

                int fileBufferIterator = 0;

                for (Map.Entry<Future<byte[]>, AudioFormat> entry : audioBytesWithAudioFormats.entrySet()) {
                    File file = filesToProcessBuffer.get(fileBufferIterator++);

                    int progress = ++fileCounter;
                    logger.log(Level.INFO, "[" + progress + " of " + numberOfFilesToProcess + "] " + file.getName());
                    updateMessage("[" + progress + " of " + numberOfFilesToProcess + "] " + file.getName());
                    updateProgress(progress, numberOfFilesToProcess);

                    Future<Track> track = applicationController.buildTrack(file, entry.getKey().get(), entry.getValue());
                    while (!track.isDone()) {
                        Thread.sleep(10);
                    }

                }

//                for (Future<Track> t : futureTasks) {
//                    while (!t.isDone()) {
//                        Thread.sleep(10);
//                    }
//                }
            }

        };
    }


    public void setNumberOfConcurrentThreads(int numberOfConcurrentThreads) {
        this.numberOfConcurrentThreads = numberOfConcurrentThreads;
    }


    public void setListOfFiles(List<File> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

}
