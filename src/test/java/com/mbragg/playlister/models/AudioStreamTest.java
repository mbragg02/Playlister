package com.mbragg.playlister.models;

import com.mbragg.playlister.models.AudioStream;
import org.junit.Before;
import org.junit.Test;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class AudioStreamTest {

    private static final float DELTA = 1e-15f;

    private AudioStream audioStream;

    private File file;

    @Before
    public void setUp() throws Exception {
             /*
        Test file: Testname.m4a
        PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, big-endian
         */

        URL url = Thread.currentThread().getContextClassLoader().getResource("Testname.m4a");
        if (url != null) {
            file = new File(url.getPath());
        }

        audioStream = new AudioStream();
    }

    @Test
    public void testSetAudioInputStreamGetChannels() throws Exception {
        AudioInputStream audioInputStream = audioStream.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getChannels(), 2);
    }

    @Test
    public void testSetAudioInputStreamGetFrameSize() throws Exception {
        AudioInputStream audioInputStream = audioStream.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getFrameSize(), 4);
    }

    @Test
    public void testSetAudioInputStreamGetSampleSizeInBits() throws Exception {
        AudioInputStream audioInputStream = audioStream.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getSampleSizeInBits(), 16);
    }

    @Test
    public void testSetAudioInputStreamGetEncoding() throws Exception {
        AudioInputStream audioInputStream = audioStream.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getEncoding().toString(), "PCM_SIGNED");
    }

    @Test
    public void testSetAudioInputStreamGetFrameRate() throws Exception {
        AudioInputStream audioInputStream = audioStream.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getFrameRate(), 44100.0f, DELTA);
    }

    @Test
    public void testSetAudioInputStreamGetSampleRate() throws Exception {
        AudioInputStream audioInputStream = audioStream.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getSampleRate(), 44100.0f, DELTA);
    }

    @Test
    public void testGetSampleRate() throws Exception{
        audioStream.getAudioInputStream(file);

        assertEquals(audioStream.getSampleRate(), 44100.0f, DELTA);
    }

}