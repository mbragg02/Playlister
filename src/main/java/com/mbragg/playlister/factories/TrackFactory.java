package com.mbragg.playlister.factories;

import com.mbragg.playlister.models.entitys.Track;

/**
 * Factory to create Track objects
 *
 * @author Michael Bragg
 */
public final class TrackFactory {

    private static TrackFactory instance;

    private TrackFactory(){}

    public static TrackFactory getInstance() {
        if(instance == null) {
            instance = new TrackFactory();
        }
        return instance;
    }

    public Track getTrack() {
        return new Track();
    }
}
