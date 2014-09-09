package com.mbragg.playlister.models;

import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Audio Stream class.
 * <p>
 * Methods for extracting and formatting a AudioInputStream from a File.
 *
 * @author Michael Bragg
 */
@Component
public class AudioStream {

    private float sampleRate;
    private int sampleSizeInBits;
    private int channels;
    private float frameRate;

    public AudioStream() {
    }

    /**
     * Gets a audio input stream from a file.
     *
     * @param file File. Input audio file.
     * @return AudioInputStream. Formatted audio input stream.
     * @throws IOException                   If the supplied file encounters a IO error.
     * @throws UnsupportedAudioFileException If the supplied file is unsupported.
     */
    public AudioInputStream getAudioInputStream(File file) throws IOException, UnsupportedAudioFileException {

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat audioFormat = audioInputStream.getFormat();

        sampleRate = audioFormat.getSampleRate();
        sampleSizeInBits = audioFormat.getSampleSizeInBits();
        channels = audioFormat.getChannels();
        frameRate = audioFormat.getFrameRate();
        AudioFormat decodedAudioFormat = getAudioFormat();

        return AudioSystem.getAudioInputStream(decodedAudioFormat, audioInputStream);
    }

    /**
     * Returns a formatted audio format.
     *
     * @return AudioFormat.
     */
    public float getSampleRate() {
        return sampleRate;
    }

    private AudioFormat getAudioFormat() {

        // Create a new PCM_SIGNED audio format
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate, sampleSizeInBits, channels,
                channels * 2, frameRate,
                true);
    }
}
