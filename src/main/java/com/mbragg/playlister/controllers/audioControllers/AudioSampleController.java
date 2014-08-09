package com.mbragg.playlister.controllers.audioControllers;

import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

@Component
public class AudioSampleController {

    public static final float BUFFER_DURATION = 0.25F;
    public static final int BUFFER_OVERLAP = 2;
    public static final int SIXTEEN_BIT = 16;
    public static final int EIGHT_BIT = 8;

    public double[] getSamplesInMono(byte[] audioBytes, AudioFormat audioFormat) {
        double[][] audioSamples = getSamplesInStereo(audioBytes, audioFormat);
        return getSamplesInMono(audioSamples);
    }

    public double[] getSamplesInMono(double[][] audioSamples) {

        if (audioSamples.length == 1)
            return audioSamples[0];

        double numberOfSamples = (double) audioSamples.length;
        int channels = audioSamples[0].length;

        double[] samplesInMono = new double[channels];

        for (int sample = 0; sample < channels; sample++) {
            double runningSampleTotal = 0.0;
            for (int chan = 0; chan < numberOfSamples; chan++) {
                runningSampleTotal += audioSamples[chan][sample];
            }
            samplesInMono[sample] = runningSampleTotal / numberOfSamples;
        }

        return samplesInMono;

    }

    public double[][] getSamplesInStereo(byte[] audioBytes, AudioFormat format) throws IllegalArgumentException {

        int numberOfChannels = format.getChannels();
        int bitDepth = format.getSampleSizeInBits();

        if ((bitDepth != SIXTEEN_BIT && bitDepth != EIGHT_BIT) || !format.isBigEndian() || format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
            throw new IllegalArgumentException("Only 8 or 16 bit signed PCM samples with a big-byte order can be analyzed");

        int numberOfBytes = audioBytes.length;
        int bytesPerSample = bitDepth / EIGHT_BIT;
        int numberOfSamples = numberOfBytes / bytesPerSample / numberOfChannels;

        if (((numberOfSamples == 2 || bytesPerSample == 2) && (numberOfBytes % 2 != 0)) ||
                ((numberOfSamples == 2 && bytesPerSample == 2) && (numberOfBytes % 4 != 0)))
            throw new IllegalArgumentException("Uneven number of bytes for given bit depth and number of channels");

        return convertBytesToDoubles(numberOfChannels, numberOfSamples, bitDepth, audioBytes);
    }

    protected double[][] convertBytesToDoubles(int numberOfChannels, int numberOfSamples, int bitDepth, byte[] audioBytes) {
        double[][] samples = new double[numberOfChannels][numberOfSamples];

        double maximumSampleValue = findMaximumSampleValue(bitDepth) + 2.0;

        ByteBuffer byteBuffer = ByteBuffer.wrap(audioBytes);

        if (bitDepth == EIGHT_BIT) {
            for (int sample = 0; sample < numberOfSamples; sample++) {
                for (int chan = 0; chan < numberOfChannels; chan++) {
                    samples[chan][sample] = (double) byteBuffer.get() / maximumSampleValue;
                }
            }
        } else if (bitDepth == SIXTEEN_BIT) {
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            for (int sample = 0; sample < numberOfSamples; sample++) {
                for (int chan = 0; chan < numberOfChannels; chan++) {
                    samples[chan][sample] = (double) shortBuffer.get() / maximumSampleValue;
                }
            }
        } else {
            throw new IllegalArgumentException("Bit depth must be either 8 bit or 16 bit");
        }
        return samples;

    }

    protected int getNumberBytesNeeded(AudioFormat audioFormat) {
        int frameSizeInBytes = audioFormat.getFrameSize();
        float frameRate = audioFormat.getFrameRate();
        return (int) (frameSizeInBytes * frameRate * (double) BUFFER_DURATION);
    }

    protected double findMaximumSampleValue(int bitDepth) {
        int maxSampleValue = 1;
        for (int i = 0; i < (bitDepth - 1); i++) {
            maxSampleValue *= 2;
        }
        maxSampleValue--;
        return ((double) maxSampleValue) - 1.0;
    }


// Currently unused below this point


//    public double[][] getSamplesInStereo(AudioInputStream audioInputStream) throws Exception {
//
//        byte[] audioBytes = getBytesFromAudioInputStream(audioInputStream);
//        AudioFormat format = audioInputStream.getFormat();
//
//        return getSamplesInStereo(audioBytes, format);
//    }
//
//
//    public double[] getSamplesInMono(AudioInputStream audioInputStream) throws Exception {
//
//        double[][] audioSamples = getSamplesInStereo(audioInputStream);
//        return getSamplesInMono(audioSamples);
//    }
//
//
//    private byte[] getBytesFromAudioInputStream(AudioInputStream audioInputStream) throws IOException {
//
//        // Calculate the buffer size to use
//
//        int bufferSize = getNumberBytesNeeded(audioInputStream.getFormat());
//        byte[] byteBuffer = new byte[bufferSize + BUFFER_OVERLAP];
//
//        // Read the bytes into the byteBuffer and then into the ByteArrayOutputStream
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        int position = audioInputStream.read(byteBuffer, 0, byteBuffer.length);
//
//        while (position > 0) {
//            position = audioInputStream.read(byteBuffer, 0, byteBuffer.length);
//            byteArrayOutputStream.write(byteBuffer, 0, position);
//        }
//
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//        try {
//            byteArrayOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return byteArray;
//    }


}
