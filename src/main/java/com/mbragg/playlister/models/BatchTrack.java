package com.mbragg.playlister.models;


import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.util.concurrent.Future;

/**
 * Wrapper class to hold an audio File, it's AudioFormat and it's extracted audio byte[] data.
 *
 * @author Michael Bragg
 */
public class BatchTrack {

    private final Future<byte[]> audio;
    private final File file;
    private final AudioFormat audioFormat;

    public BatchTrack(Future<byte[]> audio, AudioFormat audioFormat, File file) {
        this.audio = audio;
        this.audioFormat = audioFormat;
        this.file = file;
    }

    public Future<byte[]> getAudio() {
        return audio;
    }

    public File getFile() {
        return file;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }
}