package com.mbragg.playlister.factories;

import com.mbragg.playlister.entitys.Track;

/**
 * Factory to create Track objects
 *
 * @author Michael Bragg
 */
public class TrackFactory {

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
