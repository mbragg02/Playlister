package com.mbragg.playlister.services;

import com.mbragg.playlister.controllers.ApplicationController;
import com.mbragg.playlister.models.entitys.Track;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Playlist Service.
 * <p>
 * Class that manages the playlist creation service task.
 *
 * @author Michael Bragg
 */
@Component
public class PlaylistService extends Service<ObservableList<Track>> {

    @Autowired
    private ApplicationController applicationController;
    private String queryFileName;
    // Default playlist size, (in case the sizeOfResult is not set)
    private int sizeOfResult = 5;
    private boolean restrictByGenre;

    public void initializePlaylistService(String queryFileName, int sizeOfResult, boolean restrictByGenre) {
        this.queryFileName = queryFileName;
        this.sizeOfResult = sizeOfResult;
        this.restrictByGenre = restrictByGenre;
    }

    /**
     * Called to create the task and start the service.
     *
     * @return an ObservableList of Tracks.
     */
    @Override
    protected Task<ObservableList<Track>> createTask() {
        return new Task<ObservableList<Track>>() {

            /**
             * Called when the task is created
             * @return ObservableList<Track>. The playlist results.
             * @throws ExecutionException if there is an error with the execution of the query.
             * @throws InterruptedException if the service/task is cancelled.
             */
            @Override
            protected ObservableList<Track> call() throws ExecutionException, InterruptedException {
                updateMessage("Building playlist");

                List<Track> tracks = applicationController.query(queryFileName, sizeOfResult, restrictByGenre);

                ObservableList<Track> results = FXCollections.observableArrayList(tracks);

                updateValue(results);

                if (results.isEmpty()) {
                    updateMessage("No matches found");
                } else {
                    updateMessage("Playlist created");
                }

                return results;
            }
        };
    }
}