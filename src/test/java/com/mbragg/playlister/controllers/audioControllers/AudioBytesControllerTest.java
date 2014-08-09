package com.mbragg.playlister.controllers.audioControllers;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = ApplicationConfiguration.class)
public class AudioBytesControllerTest {

    private  AudioBytesController audioBytesController;

    @Mock
    private Logger logger;

    @Mock
    private ByteArrayOutputStream byteArrayOutputStream;

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

        byte[] expected = new byte[10];
        when(byteArrayOutputStream.toByteArray()).thenReturn(expected);

        audioBytesController = new AudioBytesController(logger, byteArrayOutputStream);
    }

    @Test
    public void testGetEmptyBytesFromAudioInputStream() throws Exception {
        when(audioInputStream.read(any(), anyInt(), anyInt())).thenReturn(0);
        Future<byte[]> actual = audioBytesController.getBytesFromAudioInputStream(audioInputStream);
        assertTrue(actual.get().length == 10);
    }

    @Test
    public void testGetNumberBytesNeeded() {
        int actual = audioBytesController.getNumberBytesNeeded(2.0, audioFormat);
        assertEquals(20, actual);
    }
}