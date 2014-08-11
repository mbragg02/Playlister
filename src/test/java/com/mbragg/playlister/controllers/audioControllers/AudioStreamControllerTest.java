package com.mbragg.playlister.controllers.audioControllers;

import org.junit.Before;
import org.junit.Test;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class AudioStreamControllerTest {

    private static final float DELTA = 1e-15f;

    private AudioStreamController audioStreamController;

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

        audioStreamController = new AudioStreamController();
    }

    @Test
    public void testSetAudioInputStreamGetChannels() throws Exception {
        AudioInputStream audioInputStream = audioStreamController.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getChannels(), 2);
    }

    @Test
    public void testSetAudioInputStreamGetFrameSize() throws Exception {
        AudioInputStream audioInputStream = audioStreamController.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getFrameSize(), 4);
    }

    @Test
    public void testSetAudioInputStreamGetSampleSizeInBits() throws Exception {
        AudioInputStream audioInputStream = audioStreamController.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getSampleSizeInBits(), 16);
    }

    @Test
    public void testSetAudioInputStreamGetEncoding() throws Exception {
        AudioInputStream audioInputStream = audioStreamController.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getEncoding().toString(), "PCM_SIGNED");
    }

    @Test
    public void testSetAudioInputStreamGetFrameRate() throws Exception {
        AudioInputStream audioInputStream = audioStreamController.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getFrameRate(), 44100.0f, DELTA);
    }

    @Test
    public void testSetAudioInputStreamGetSampleRate() throws Exception {
        AudioInputStream audioInputStream = audioStreamController.getAudioInputStream(file);

        assertEquals(audioInputStream.getFormat().getSampleRate(), 44100.0f, DELTA);
    }

    @Test
    public void testGetSampleRate() throws Exception{
        audioStreamController.getAudioInputStream(file);

        assertEquals(audioStreamController.getSampleRate(), 44100.0f, DELTA);
    }

}