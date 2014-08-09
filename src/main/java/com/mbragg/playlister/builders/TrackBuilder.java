package com.mbragg.playlister.builders;

import com.mbragg.playlister.controllers.FeatureExtractionController;
import com.mbragg.playlister.controllers.MetaExtractionController;
import com.mbragg.playlister.controllers.audioControllers.AudioBytesController;
import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.entitys.Track;
import com.mbragg.playlister.factories.TrackFactory;
import com.mbragg.playlister.tools.strings.StringTools;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
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


@Component
public class TrackBuilder {

    public static final int NUMBER_OF_THREADS = 5;
    private final DAO dao;
    private MetaExtractionController metaExtractionController;
    private FeatureExtractionController featureExtractionController;

    @Autowired
    private Logger logger;

    @Autowired
    private AudioBytesController audioBytesController;

    private Map<String, List<String>> allGenres;

    @Autowired
    public TrackBuilder(DAO dao, MetaExtractionController metaExtractionController, FeatureExtractionController featureExtractionController) {
        this.dao = dao;
        this.metaExtractionController = metaExtractionController;
        this.featureExtractionController = featureExtractionController;
    }

    // For command line application
//    public void build(List<File> files, Map<String, List<String>> allGenres) {
//
//        this.allGenres = allGenres;
//        logger.log(Level.INFO, "Total files in your library: " + files.size());
//
//        int fileCounter = 0;
//
//        for (File file : files) {
//            if (!dao.trackExists(file.getName())) {
//                logger.log(Level.INFO, "[" + ++fileCounter + " of " + files.size() + "] " + file.getName());
//
//                buildTrack(file);
//            }
//        }
//    }


    public List<File> batchBuild(List<File> files, Map<String, List<String>> allGenres) {
        this.allGenres = allGenres;

        logger.log(Level.INFO, "Total files in your library: " + files.size());

        List<File> filesToProcess = files.stream().filter(f -> !dao.trackExists(f.getName())).collect(Collectors.toList());

        if (filesToProcess.isEmpty()) {
            logger.log(Level.INFO, "No new files to process");
        } else {
            logger.log(Level.INFO, "Files to process: " + filesToProcess.size());

        }

        return filesToProcess;
    }


    public Map<Future<byte[]>, AudioFormat> batchExtraction(List<File> filesToProcessBuffer) throws InterruptedException {

        Map<Future<byte[]>, AudioFormat> result = new HashMap<>();

        logger.log(Level.INFO, "Audio Extraction batch jobs starting [" + NUMBER_OF_THREADS + "]");

        try {

            for (File file : filesToProcessBuffer) {
                AudioInputStream audioInputStream = featureExtractionController.getFormattedAudioInputStream(file);
                result.put(audioBytesController.getBytesFromAudioInputStream(audioInputStream), audioInputStream.getFormat());
            }

            for (Future<byte[]> audioByteExtractionThread : result.keySet()) {
                while (!audioByteExtractionThread.isDone()) Thread.sleep(10);
            }

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        logger.log(Level.INFO, "Batch jobs complete");

        return result;

    }




    // For command line application
//    public Track buildTrack(File file) {
//        Track track = setTrackMetaData(TrackFactory.getInstance().getTrack(), file);
//
//
//        try {
//            track.setFeatures(featureExtractionController.extract(file));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        dao.saveTrack(track);
//
//        return track;
//    }


    public Track buildTrack(File file, byte[] audioBytes, AudioFormat audioFormat) {

        Track track = setTrackMetaData(TrackFactory.getInstance().getTrack(), file);

        try {
//            track.setFeatures(featureExtractionController.extract(audioBytes, audioFormat));
            MultivariateNormalDistribution model = featureExtractionController.extract(audioBytes, audioFormat);

            RealMatrix matrix = model.getCovariances();
            double[] means = model.getMeans();
            for (int i = 0; i < matrix.getColumnDimension(); i++) {
                track.setModelProperty("co_row" + i, matrix.getRow(i));
            }
            track.setModelProperty("means", means);

            dao.saveTrack(track);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return track;
    }

    private Track setTrackMetaData(Track track, File file) {

        try {
            Map<String, String> meta = metaExtractionController.parse(file);
            track.setFilename(file.getName());
            track.setFilePath(file.getAbsolutePath());
            track.setTitle(meta.get("TITLE"));
            track.setArtist(meta.get("ARTIST"));
            track.setAlbum(meta.get("ALBUM"));
            track.setSubGenre(meta.get("GENRE"));
            track.setYear(meta.get("YEAR"));
            track.setGenre(getGenreCategory(meta.get("GENRE")));
        } catch (ReadOnlyFileException | CannotReadException | InvalidAudioFrameException | IOException | TagException e) {
            e.printStackTrace();
        }
        return track;
    }

    String getGenreCategory(String subGenre) {

        String formattedSubGenre = StringTools.formatter(subGenre);

        // iterate over all genres. return the key genre for the sub genre
        for (Map.Entry<String, List<String>> entry : allGenres.entrySet()) {
            String keyGenre = StringTools.formatter(entry.getKey());

            if (StringTools.fuzzyEquals(formattedSubGenre, keyGenre)) return keyGenre;

            for (String subGenreValue : entry.getValue()) {

                if (StringTools.fuzzyEquals(formattedSubGenre, StringTools.formatter(subGenreValue))) return keyGenre;
            }
        }
        return "";
    }


}