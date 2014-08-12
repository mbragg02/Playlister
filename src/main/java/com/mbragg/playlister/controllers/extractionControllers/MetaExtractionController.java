package com.mbragg.playlister.controllers.extractionControllers;

import com.mbragg.playlister.factories.TrackMetaFactory;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.mp4.Mp4Tag;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Meta Extraction Controller.
 * <p>
 * Class to manage the extraction of embedded metadata information from a audio file.
 *
 * @author Michael Bragg
 */
@Component
public class MetaExtractionController {

    private final Map<String, String> tags;
    private TrackMetaFactory trackMetaFactory;

    public MetaExtractionController() {
        this.trackMetaFactory = TrackMetaFactory.getInstance();
        this.tags = new HashMap<>();
    }

    /**
     * Extraction method.
     *
     * @param file File. Audio file to parse data from.
     * @return A Map of the metadata information. <String, String>
     * @throws ReadOnlyFileException      if the file is read only.
     * @throws CannotReadException        if the file cannot be read.
     * @throws InvalidAudioFrameException if the audio file is invalid or corrupt.
     * @throws IOException                if a general IO exception is encountered. i.e file cannot be found.
     * @throws TagException               if an error is encountered whilst the meta data tagging is being processed.
     */
    public Map<String, String> extract(File file) throws ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, TagException {

        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);

        AudioFile audioFile = AudioFileIO.read(file);

        Tag id3Tag = audioFile.getTag();
        Mp4Tag mp4Tag = (Mp4Tag) audioFile.getTag();

        for (FieldKey tag : trackMetaFactory.getFieldKeys()) {
            tags.put(tag.toString(), nonEmpty(mp4Tag.getFirst(tag), id3Tag.getFirst(tag)));
        }
        return tags;
    }

    /**
     * Returns which of the two strings is not empty
     *
     * @param s1 String
     * @param s2 String
     * @return the non empty String.
     */
    private String nonEmpty(String s1, String s2) {
        if (s1.isEmpty()) {
            return s2;
        }
        return s1;
    }
}