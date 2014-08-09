package com.mbragg.playlister.dao;

import com.mbragg.playlister.features.MultivariateNormalDistributionModel;
import com.mbragg.playlister.entitys.Genre;
import com.mbragg.playlister.entitys.Track;
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

@Component
public class DAOImpl implements DAO {

    @Autowired
    MultivariateNormalDistributionModel model;
    @Value("${dbName}")
    private String dbName;
    private GraphDatabase graphDatabase;
    private TrackRepository trackRepository;
    private GenreRepository genreRepository;

    @Autowired
    public DAOImpl(GraphDatabase graphDatabase, GenreRepository genreRepository, TrackRepository trackRepository) {
        this.graphDatabase = graphDatabase;
        this.genreRepository = genreRepository;
        this.trackRepository = trackRepository;
    }

    @Override
    public boolean genreExists(String genre) {
        return genreRepository.findByName(genre) != null;
    }

    @Override
    public boolean trackExists(String filename) {
        return trackRepository.findByFilename(filename) != null;
    }

    @Override
    public void saveGenre(Genre genre) {
        try (Transaction saveGenresTransaction = graphDatabase.beginTx()) {
            genreRepository.save(genre);

            saveGenresTransaction.success();
        }
    }

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


    private MultivariateNormalDistribution createDistributionFromProperties(Track track) {

        double[] means = (double[]) track.getModelPropertyValue("means");
        RealMatrix covarianceMatrix = new Array2DRowRealMatrix(means.length, means.length);

        for (int i = 0; i < means.length; i++) {
            covarianceMatrix.setRow(i, (double[]) track.getModelPropertyValue("co_row" + i));
        }
        return new MultivariateNormalDistribution(means, covarianceMatrix.getData());

    }

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
