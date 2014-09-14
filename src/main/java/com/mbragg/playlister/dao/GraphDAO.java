package com.mbragg.playlister.dao;

import com.mbragg.playlister.models.MultivariateNormalDistributionModel;
import com.mbragg.playlister.models.entitys.Genre;
import com.mbragg.playlister.models.entitys.Track;
import com.mbragg.playlister.relationships.SimilarTo;
import com.mbragg.playlister.repositories.GenreRepository;
import com.mbragg.playlister.repositories.TrackRepository;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Neo4j graph Implementation of the DAO interface.
 *
 * @author Michael Bragg
 */
@Component
public class GraphDAO implements DAO {

    @Autowired
    private Logger logger;

    private MultivariateNormalDistributionModel trackModel;

    @Value("${dbName}")
    private String dbName;

    private GraphDatabase graphDatabase;
    private TrackRepository trackRepository;
    private GenreRepository genreRepository;

    @Autowired
    public GraphDAO(GraphDatabase graphDatabase, GenreRepository genreRepository, TrackRepository trackRepository, MultivariateNormalDistributionModel model) {
        this.graphDatabase = graphDatabase;
        this.genreRepository = genreRepository;
        this.trackRepository = trackRepository;
        this.trackModel = model;
    }

    /**
     * Does a genreName node exists in the database.
     *
     * @param genreName String. The genreName string to test.
     * @return boolean. Yes or no.
     */
    @Override
    public boolean genreExists(String genreName) {
        return genreRepository.findByName(genreName) != null;
    }

    /**
     * Does a track node exists in the database.
     *
     * @param fileName String. The fileName of a track node to test.
     * @return boolean. Yes or no.
     */
    @Override
    public boolean trackExists(String fileName) {
        return trackRepository.findByFilename(fileName) != null;
    }

    /**
     * Save a genreName node into the database
     *
     * @param genre Genre. A genreName node entity.
     */
    @Override
    public void saveGenre(Genre genre) {
        try (Transaction saveGenresTransaction = graphDatabase.beginTx()) {
            genreRepository.save(genre);

            saveGenresTransaction.success();
        }
    }

    /**
     * Save a track node into the database.
     *
     * @param track Track. A Track node entity.
     */
    @Override
    public void saveTrack(Track track) {

        try (Transaction newTrackTransaction = graphDatabase.beginTx()) {
            trackRepository.save(track);

            // Set correct Genre node relation
            Iterator<Genre> allGenres = genreRepository.findAll().iterator();
            Genre genreNode;
            while (allGenres.hasNext()) {
                genreNode = allGenres.next();
                if (track.getGenre().equals(genreNode.getName())) {
                    track.isGenre(genreNode);
                    break;
                }
            }
            // Add similarity relationships to all other track nodes
            for (Track nextTrack : trackRepository.findAll()) {

                if (!nextTrack.equals(track)) {
                    double similarity = trackModel.getSimilarity(createDistributionFromProperties(track), createDistributionFromProperties(nextTrack));
                    SimilarTo relationship = track.relateTo(nextTrack, similarity);
                    logger.log(Level.INFO, "relationships: " + relationship);
                }
            }

            trackRepository.save(track);
            newTrackTransaction.success();
        }

    }

    /*
     * For a given track returned from the database, reconstruct a distribution trackModel from its properties values.
     */
    private MultivariateNormalDistribution createDistributionFromProperties(Track track) {

        double[] means = (double[]) track.getModelPropertyValue("means");
        RealMatrix covarianceMatrix = new Array2DRowRealMatrix(means.length, means.length);

        for (int i = 0; i < means.length; i++) {
            covarianceMatrix.setRow(i, (double[]) track.getModelPropertyValue("co_row" + i));
        }
        return new MultivariateNormalDistribution(means, covarianceMatrix.getData());
    }

    /**
     * Query the database with a fileName.
     *
     * @param filename        String. The fileName of a track to query.
     * @param sizeOfResult    int.
     * @param restrictByGenre boolean. Whether to limit the scope of the search to within the same genreName as the query track.
     * @return List of most similar tracks.
     */
    @Override
    public List<Track> query(String filename, int sizeOfResult, boolean restrictByGenre) {
        List<Track> tracks = new ArrayList<>();

        try (Transaction executeQuery = graphDatabase.beginTx()) {
            Result<Track> results;
            if (restrictByGenre) {
                results = trackRepository.findAllSimilarTracksWithGenre(filename, sizeOfResult);

            } else {
                results = trackRepository.findAllSimilarTracks(filename, sizeOfResult);
            }

            // Add the query track to the start of the playlist
            tracks.add(trackRepository.findByFilename(filename));

            // Add the resulting similar tracks to the playlist
            for (Track track : results) {
                tracks.add(track);
            }

            executeQuery.success();
        }
        return tracks;
    }

    /**
     * Removes all the nodes from the database.
     */
    @Override
    public void deleteDatabase() {
        trackRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Override
    public void shutdown() {
        graphDatabase.shutdown();
    }

}