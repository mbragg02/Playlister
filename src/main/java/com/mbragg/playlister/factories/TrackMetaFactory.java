package com.mbragg.playlister.factories;

import org.jaudiotagger.tag.FieldKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to create TrackMeta objects
 *
 * @author Michael Bragg
 */
public final class TrackMetaFactory {

    private static TrackMetaFactory instance;
    private static List<FieldKey> fieldKeys;

    private TrackMetaFactory() {
        // private constructor
    }

    public static TrackMetaFactory getInstance() {
        if(instance == null) {
            instance = new TrackMetaFactory();
            fieldKeys = new ArrayList<>();
        }
        return instance;
    }

    public List<FieldKey> getFieldKeys() {
        fieldKeys.add(FieldKey.TITLE);
        fieldKeys.add(FieldKey.ARTIST);
        fieldKeys.add(FieldKey.ALBUM);
        fieldKeys.add(FieldKey.GENRE);
        fieldKeys.add(FieldKey.YEAR);
        return fieldKeys;
    }
}
