package com.mbragg.playlister.controllers.audioControllers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Future;


@Component
public class AudioBytesController {

    private Logger logger;
    private ByteArrayOutputStream byteArrayOutputStream;

    @Autowired
    public AudioBytesController(Logger logger, ByteArrayOutputStream byteArrayOutputStream){
        this.byteArrayOutputStream = byteArrayOutputStream;
        this.logger = logger;
    }

    @Async
    public Future<byte[]> getBytesFromAudioInputStream(AudioInputStream audioInputStream) throws IOException {

        logger.log(Level.INFO,  "Job #" + Thread.currentThread().getId() + " started... ");

        // Calculate the buffer size to use
        float bufferDuration = 0.25F;
        int bufferOverlap = 2;

        int bufferSize = getNumberBytesNeeded(bufferDuration, audioInputStream.getFormat());
        byte[] byteBuffer = new byte[bufferSize + bufferOverlap];

        // Read the bytes into the byteBuffer and then into the ByteArrayOutputStream

        int position = audioInputStream.read(byteBuffer, 0, byteBuffer.length);

        // Bottleneck here
        while (position > 0) {
            position = audioInputStream.read(byteBuffer, 0, byteBuffer.length);
            byteArrayOutputStream.write(byteBuffer, 0, position);
        }
        // End bottleneck

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            logger.log(Level.WARN, e.getMessage());
        }
        return new AsyncResult<>(byteArray);
    }

    protected int getNumberBytesNeeded(double bufferDuration, AudioFormat audioFormat) {
        int frameSizeInBytes = audioFormat.getFrameSize();
        float frameRate = audioFormat.getFrameRate();
        return (int) (frameSizeInBytes * frameRate * bufferDuration);
    }
}
