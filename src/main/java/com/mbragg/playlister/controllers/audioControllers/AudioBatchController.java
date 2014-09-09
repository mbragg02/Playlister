package com.mbragg.playlister.controllers.audioControllers;

import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.models.AudioBytes;
import com.mbragg.playlister.models.AudioStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Class to control the process of extracting audio data byte arrays from files.
 *
 * @author Michael Bragg
 */
@Component
public class AudioBatchController {

    public static final int NUMBER_OF_THREADS = 5;
    private final DAO dao;
    private final AudioBytes audioBytes;
    private final AudioStream audioStream;

    @Autowired
    Logger logger;

    @Autowired
    public AudioBatchController(DAO dao, AudioBytes audioBytes, AudioStream audioStream) {
        this.dao = dao;
        this.audioStream = audioStream;
        this.audioBytes = audioBytes;
    }

    /**
     * For a given list of files, determines which have already been processed by the application.
     *
     * @param files Complete list of files from a users music directory.
     * @return List of files that have not yet been processed by the application.
     */
    public List<File> getFilesToProcess(List<File> files) {

        logger.log(Level.INFO, "Total files in your library: " + files.size());

        List<File> filesToProcess = files.stream()
                .filter(f -> !dao.trackExists(f.getName()))
                .collect(Collectors.toList());

        if (filesToProcess.isEmpty())
            logger.log(Level.INFO, "No new files to process");
        else
            logger.log(Level.INFO, "Files to process: " + filesToProcess.size());

        return filesToProcess;
    }

    /**
     * Method to coordinate the extraction of byte[] arrays from files in a batch list.
     *
     * @param filesToProcessBuffer Files in a current batch that are to be processed asynchronously.
     * @return A Map of byte[] data (key) with AudioFormat data (value)
     * @throws InterruptedException If the batch job is cancelled at any point.
     */
    public Map<Future<byte[]>, AudioFormat> batchAudioByteExtraction(List<File> filesToProcessBuffer) throws InterruptedException {

        Map<Future<byte[]>, AudioFormat> result = new HashMap<>();

        logger.log(Level.INFO, "Audio Extraction batch jobs starting [" + NUMBER_OF_THREADS + "]");

        try {

            for (File file : filesToProcessBuffer) {
                AudioInputStream audioInputStream = audioStream.getAudioInputStream(file);
                result.put(audioBytes.extract(audioInputStream), audioInputStream.getFormat());
            }

            for (Future<byte[]> audioByteExtractionThread : result.keySet()) {
                while (!audioByteExtractionThread.isDone()) Thread.sleep(10);
            }

        } catch (UnsupportedAudioFileException | IOException e) {
            logger.log(Level.WARN, "Unsupported file format: \n" + e.getMessage());
        }

        logger.log(Level.INFO, "Batch jobs complete");

        return result;
    }
}
