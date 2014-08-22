package com.mbragg.playlister.models;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Michael Bragg
 */
public class AudioBytesTest {

    private AudioBytes audioBytes;

    @Mock
    private Logger logger;

    @Mock
    private AudioFormat audioFormat;

    @Mock
    private AudioInputStream audioInputStream;

    @Before
    public void setUp() throws IOException {

        initMocks(this);
        when(audioFormat.getFrameSize()).thenReturn(10);
        when(audioFormat.getFrameRate()).thenReturn(1.0f);
        when(audioInputStream.getFormat()).thenReturn(audioFormat);

        audioBytes = new AudioBytes(logger);
    }

    @Test
    public void testExtract() throws Exception {
        when(audioInputStream.read(any(), anyInt(), anyInt())).thenReturn(0);

        Future<byte[]> actual = audioBytes.extract(audioInputStream);

        assertTrue(actual.get().length == 0);
    }

    @Test
    public void testReadIntoByteArrayOutputStream() throws IOException {
        when(audioInputStream.read(any(), anyInt(), anyInt())).thenReturn(3).thenReturn(3).thenReturn(0);

        byte[] buffer = {1, 2, 3};
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ByteArrayOutputStream actual = audioBytes.readIntoByteArrayOutputStream(audioInputStream, buffer, byteArrayOutputStream);

        assertEquals(buffer[0], actual.toByteArray()[0]);
        assertEquals(buffer[1], actual.toByteArray()[1]);
        assertEquals(buffer[2], actual.toByteArray()[2]);
    }

    @Test
    public void testGetNumberBytesNeeded() {
        int actual = audioBytes.getNumberBytesNeeded(2.0, audioFormat);
        assertEquals(20, actual);
    }
}