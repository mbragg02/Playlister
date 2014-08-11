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

@Component
public class MetaExtractionController {

    private final Map<String, String> tags;

    private TrackMetaFactory trackMetaFactory;


    public MetaExtractionController() {
        this.trackMetaFactory = TrackMetaFactory.getInstance();
        this.tags = new HashMap<>();
    }

    public Map<String, String> parse(File file) throws ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException, TagException {

        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);

        AudioFile audioFile = AudioFileIO.read(file);

        Tag id3Tag = audioFile.getTag();
        Mp4Tag mp4Tag = (Mp4Tag) audioFile.getTag();

        for(FieldKey tag : trackMetaFactory.getFieldKeys()) {
            tags.put(tag.toString(), nonEmpty(mp4Tag.getFirst(tag), id3Tag.getFirst(tag)));
        }
        return tags;
    }

    private String nonEmpty(String s1, String s2) {
        if(s1.isEmpty()) {
            return s2;
        }
        return s1;
    }
}
