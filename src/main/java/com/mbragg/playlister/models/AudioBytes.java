package com.mbragg.playlister.models;

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

/**
 * Audio Bytes Controller.
 * <p>
 * Class that extracts audio data as a byte[] from an AudioInputStream.
 */
@Component
public class AudioBytes {

    private Logger logger;

    @Autowired
    public AudioBytes(Logger logger) {
        this.logger = logger;
    }

    /**
     * Exacts a byte[] from an audio input stream.
     *
     * @param audioInputStream AudioInputStream. The audio input stream to parse the byte[] from.
     * @return The extracted byte[] data
     */
    @Async
    public Future<byte[]> extract(AudioInputStream audioInputStream) {

        logger.log(Level.INFO, "Job #" + Thread.currentThread().getId() + " started... ");

        // Calculate the buffer size to use
        float bufferDuration = 0.25F;
        int bufferOverlap = 2;
        int bufferSize = getNumberBytesNeeded(bufferDuration, audioInputStream.getFormat());


        byte[] byteBuffer = new byte[bufferSize + bufferOverlap];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        byte[] byteArray = readIntoByteArrayOutputStream(audioInputStream, byteBuffer, byteArrayOutputStream).toByteArray();

        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            logger.log(Level.WARN, "Error closing the byte array output stream: " + e.getMessage());
        }

        return new AsyncResult<>(byteArray);
    }

    /**
     * For a given audioInputStream, converts format to ByteArrayOutputStream. By the means of a buffer.
     * @param audioInputStream AudioInputStream.
     * @param byteBuffer byte[] buffer for use in conversion.
     * @param byteArrayOutputStream ByteArrayOutputStream. New byte array output stream for data to be copied to.
     * @return ByteArrayOutputStream. Data from audio input stream.
     */
    protected ByteArrayOutputStream readIntoByteArrayOutputStream(AudioInputStream audioInputStream, byte[] byteBuffer, ByteArrayOutputStream byteArrayOutputStream) {
        // Read the bytes into the byteBuffer and then into the ByteArrayOutputStream
        int offset = 0;
        try {
            int numberOfBytesToWrite = audioInputStream.read(byteBuffer, offset, byteBuffer.length);

            while (numberOfBytesToWrite > 0) {
                numberOfBytesToWrite = audioInputStream.read(byteBuffer, offset, byteBuffer.length);

                byteArrayOutputStream.write(byteBuffer, offset, numberOfBytesToWrite);
            }

        } catch (IOException e) {
            logger.log(Level.WARN, "IO Exception reading from the audio input stream: " + e.getMessage());
        } finally {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                logger.log(Level.WARN, "Error closing audio input stream: " + e.getMessage());
            }
        }

        return byteArrayOutputStream;
    }

    /**
     * Calculates the number of bytes needed for a given buffer duration and audio format.
     *
     * @param bufferDuration double. The size of the buffer.
     * @param audioFormat    AudioFormat. The audio file format information.
     * @return int. The number of bytes needed.
     */
    protected int getNumberBytesNeeded(double bufferDuration, AudioFormat audioFormat) {
        int frameSizeInBytes = audioFormat.getFrameSize();
        float frameRate = audioFormat.getFrameRate();
        return (int) (frameSizeInBytes * frameRate * bufferDuration);
    }
}
