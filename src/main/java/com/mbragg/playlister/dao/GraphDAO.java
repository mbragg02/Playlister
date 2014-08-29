package com.mbragg.playlister.dao;

import com.mbragg.playlister.features.MultivariateNormalDistributionModel;
import com.mbragg.playlister.models.entitys.Genre;
import com.mbragg.playlister.models.entitys.Track;
import com.mbragg.playlister.repositories.GenreRepository;
import com.mbragg.playlister.repositories.TrackRepository;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
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
 * <p>
 * Class for communicating with the database.
 *
 * @author Michael Bragg
 */
@Component
public class GraphDAO implements DAO {

    @Autowired
    private MultivariateNormalDistributionModel model;

    @Value("${dbName}")
    private String dbName;

    private GraphDatabase graphDatabase;
    private TrackRepository trackRepository;
    private GenreRepository genreRepository;

    @Autowired
    public GraphDAO(GraphDatabase graphDatabase, GenreRepository genreRepository, TrackRepository trackRepository) {
        this.graphDatabase = graphDatabase;
        this.genreRepository = genreRepository;
        this.trackRepository = trackRepository;
    }

    /**
     * Does a genre node exists in the database.
     *
     * @param genre String. The genre string to test.
     * @return boolean. Yes or no.
     */
    @Override
    public boolean genreExists(String genre) {
        return genreRepository.findByName(genre) != null;
    }

    /**
     * Does a track node exists in the database.
     *
     * @param filename String. The filename of a track node to test.
     * @return boolean. Yes or no.
     */
    @Override
    public boolean trackExists(String filename) {
        return trackRepository.findByFilename(filename) != null;
    }

    /**
     * Save a genre node into the database
     *
     * @param genre Genre. A genre node entity.
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
                    double similarity = this.model.getSymmetricKullbackLeiblerDivergence(createDistributionFromProperties(track), createDistributionFromProperties(nextTrack));
                    track.relateTo(nextTrack, similarity);
                }
            }

            // Add similarity relationships to all other track nodes (with the same genre)
//            for (Track nextTrack : trackRepository.findByGenreNode(genreNode)) {
//                if (!nextTrack.equals(track)) {
//                    double cosineSimilarity = Statistics.cosineSimilarity(track.getFeatures(), nextTrack.getFeatures());
//                    track.relateTo(nextTrack, cosineSimilarity);
//                }
//            }
            trackRepository.save(track);
            newTrackTransaction.success();
        }

    }

    /*
     * For a given track returned from the database, reconstruct a distribution model from its properties values.
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
     * Query the database with a filename.
     *
     * @param filename        String. The filename of a track to query.
     * @param sizeOfResult    int.
     * @param restrictByGenre boolean. Whether to limit the scope of the search to within the same genre as the query track.
     * @return List<Track>. List of most similar tracks.
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
//        try {
//            FileUtils.deleteRecursively(new File(dbName));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}
