package com.mbragg.playlister.factories;

import com.mbragg.playlister.models.BatchTrack;
import com.mbragg.playlister.models.entitys.Track;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.util.concurrent.Future;

/**
 * Factory to create Track objects
 *
 * @author Michael Bragg
 */
public final class TrackFactory {

    private static TrackFactory instance;

    private TrackFactory() {
    }

    public static TrackFactory getInstance() {
        if (instance == null) {
            instance = new TrackFactory();
        }
        return instance;
    }

    public Track getTrack() {
        return new Track();
    }

    public BatchTrack getBatchTrack(Future<byte[]> audio, AudioFormat audioFormat, File file) {
        return new BatchTrack(audio, audioFormat, file);
    }
}
