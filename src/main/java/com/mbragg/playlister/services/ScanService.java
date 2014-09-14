package com.mbragg.playlister.services;

import com.mbragg.playlister.controllers.ApplicationController;
import com.mbragg.playlister.models.BatchTrack;
import com.mbragg.playlister.models.entitys.Track;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Service to manage the scanning/processing of a users music directory.
 *
 * @author Michael Bragg
 */
@Component
public class ScanService extends Service {

    public static final int INITIAL = 0;
    public static final int THREAD_SLEEP_MS = 10;

    @Autowired
    private ApplicationController applicationController;

    @Autowired
    private Logger logger;

    private List<File> listOfFiles;
    private int numberOfConcurrentThreads;

    public ScanService() {
        // empty public constructor for Spring framework
    }

    public void setNumberOfConcurrentThreads(int numberOfConcurrentThreads) {
        this.numberOfConcurrentThreads = numberOfConcurrentThreads;
    }

    public void setListOfFiles(List<File> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    /**
     * Method that creates and runs the scan task.
     *
     * @return the new Task.
     */
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

            /**
             * Method called when the Task is created
             * @throws Exception if the batch extraction process encounters an error.
             */
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
                    List<BatchTrack> audioBytesWithAudioFormats;

                    try {
                        audioBytesWithAudioFormats = applicationController.extractAudioBatch(filesToProcessBuffer);
                        updateMessage("Extracting audio complete");

                        createTracksInBatch(audioBytesWithAudioFormats);
                    } catch (InterruptedException e) {
                        if (isCancelled()) break;
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

            /**
             * Method to create Tracks from a list of BatchTracks
             * @param batchTracks List of BatchTracks
             */
            public void createTracksInBatch(List<BatchTrack> batchTracks)
                    throws ExecutionException, InterruptedException {

                int numberOfFilesToProcess = listOfFiles.size();

                for (BatchTrack batchTrack : batchTracks) {

                    int progress = ++fileCounter;
                    String buildStatus = "[" + progress + " of " + numberOfFilesToProcess + "] " + batchTrack.getFile().getName();

                    logger.log(Level.INFO, buildStatus);
                    updateMessage(buildStatus);
                    updateProgress(progress, numberOfFilesToProcess);

                    Future<Track> track = applicationController.buildTrack(batchTrack);

                    while (!track.isDone()) {
                        Thread.sleep(THREAD_SLEEP_MS);
                    }
                }

            } // close createTracksInBatch()

        }; // close the new Task()

    } // close createTask()

} // close ScanService()
