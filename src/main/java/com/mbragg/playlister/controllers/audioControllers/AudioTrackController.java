package com.mbragg.playlister.controllers.audioControllers;

import com.mbragg.playlister.controllers.extractionControllers.FeatureExtractionController;
import com.mbragg.playlister.controllers.extractionControllers.MetaExtractionController;
import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.models.entitys.Track;
import com.mbragg.playlister.factories.TrackFactory;
import com.mbragg.playlister.controllers.extractionControllers.GenreExtractionController;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Class for coordinating the construction and saving of full Track objects.
 *
 * @author Michael Bragg
 */
@Component
public class AudioTrackController {

    private final DAO dao;
    private MetaExtractionController metaExtractionController;
    private FeatureExtractionController featureExtractionController;

    @Autowired
    private Logger logger;

    @Value("${genresJSONFilename}")
    private String genresJSONFilename;
    private Map<String, List<String>> allGenres;

    @Autowired
    public AudioTrackController(DAO dao, MetaExtractionController metaExtractionController, FeatureExtractionController featureExtractionController, GenreExtractionController genreExtractionController) {
        this.dao = dao;
        this.metaExtractionController = metaExtractionController;
        this.featureExtractionController = featureExtractionController;
        allGenres = genreExtractionController.build();
    }

    /**
     * Build method. From the supplied audio file parameters, a model and track object are parse, and saved with the DAO.
     *
     * @param file        File. The audio file.
     * @param audioBytes  byte[]. Array of bytes representing the audio.
     * @param audioFormat AudioFormat. The extracted and formatted audio file format information.
     * @return a Track object.
     */
    public Track build(File file, byte[] audioBytes, AudioFormat audioFormat) {

        Track track = TrackFactory.getInstance().getTrack();

        track = addMetaDataToTrack(file, track);
        track = addDistributionModelToTrack(track, audioBytes, audioFormat);

        dao.saveTrack(track);

        return track;
    }

    /**
     * Set the metadata information for the supplied Track object from the supplied audio file.
     *
     * @param file  File. The audio file.
     * @param track Track. The Track object to add the metadata to.
     * @return a reference to the track object that was passed in.
     */
    protected Track addMetaDataToTrack(File file, Track track) {

        try {
            Map<String, String> meta = metaExtractionController.extract(file);
            track.setFilename(file.getName());
            track.setFilePath(file.getAbsolutePath());
            track.setTitle(meta.get("TITLE"));
            track.setArtist(meta.get("ARTIST"));
            track.setAlbum(meta.get("ALBUM"));
            track.setSubGenre(meta.get("GENRE"));
            track.setYear(meta.get("YEAR"));
            track.setGenre(getGenreCategory(meta.get("GENRE")));
        } catch (CannotReadException | InvalidAudioFrameException | TagException e) {
            logger.log(Level.WARN, "Invalid file exception: " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.WARN, "IO Exception parsing the audio file: " + e.getMessage());
        } catch (ReadOnlyFileException e) {
            logger.log(Level.WARN, "Supplied file is read only: " + e.getMessage());
        }
        return track;
    }

    /**
     * Method to return the high-level genre category for the supplied sub genre.
     *
     * @param subGenre String. The genre string extracted from an audio file.
     * @return String. The high-level genre category, or an empty string if none found.
     */
    protected String getGenreCategory(String subGenre) {

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

    /**
     * For the supplied Track, builds and sets the Multivariate Normal Distribution model.
     *
     * @param track       Track. The track to parse the model for.
     * @param audioBytes  byte[]. Array of bytes representing the audio.
     * @param audioFormat AudioFormat. The extracted and formatted audio file format information.
     * @return Track. A reference to the track object.
     */
    protected Track addDistributionModelToTrack(Track track, byte[] audioBytes, AudioFormat audioFormat) {

        try {
            MultivariateNormalDistribution model = featureExtractionController.extract(audioBytes, audioFormat);

            RealMatrix matrix = model.getCovariances();
            double[] means = model.getMeans();

            for (int i = 0; i < matrix.getColumnDimension(); i++) {
                track.setModelProperty("co_row" + i, matrix.getRow(i));
            }
            track.setModelProperty("means", means);

        } catch (Exception e) {
            logger.log(Level.WARN, "MultivariateNormalDistribution construction exception: " + e.getMessage());
        }
        return track;
    }

}