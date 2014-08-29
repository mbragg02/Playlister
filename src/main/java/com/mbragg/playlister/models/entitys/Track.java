package com.mbragg.playlister.models.entitys;

import com.mbragg.playlister.relationships.SimilarTo;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.fieldaccess.DynamicProperties;
import org.springframework.data.neo4j.fieldaccess.DynamicPropertiesContainer;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to represent a Track, including its properties and relationships.
 * <p>
 * A Neo4j @NodeEntity
 *
 * @author Michael Bragg
 */
@NodeEntity
public class Track {

    private DynamicProperties modelProps = new DynamicPropertiesContainer();
    // Neo4j identifier
    @SuppressWarnings("UnusedDeclaration")
    @GraphId
    private Long id;
    @Indexed(unique = true)
    private String filename;
    private String filePath;
    private String title;
    private String artist;
    private String album;
    private String subGenre;
    private String year;
    private String genre;

    // relationships used internally by Neo4j
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @RelatedToVia(type = "SIMILARITY", direction = Direction.BOTH)
    private Set<SimilarTo> relationships;

    @RelatedTo(type = "IS_GENRE", direction = Direction.BOTH)
    private Genre genreNode;

    public Track() {
    }

    /**
     * Sets what the tracks relations
     *
     * @param toTrack    Track that this class is related to.
     * @param similarity double. Value property of the relationship.
     * @return RelationshipEntity SimilarTo
     */
    public SimilarTo relateTo(Track toTrack, double similarity) {
        if (relationships == null) {
            relationships = new HashSet<>();
        }
        SimilarTo relationship = new SimilarTo(this, toTrack, similarity);
        relationships.add(relationship);
        return relationship;
    }

    public void isGenre(Genre genreNode) {
        this.genreNode = genreNode;
    }

    public Genre getGenreNode() {
        return genreNode;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSubGenre() {
        return subGenre;
    }

    public void setSubGenre(String subGenre) {
        this.subGenre = subGenre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Object getModelPropertyValue(String key) {
        return modelProps.getProperty(key);
    }

    public void setModelProperty(String key, Object value) {
        this.modelProps.setProperty(key, value);
    }

    @Override
    public String toString() {
        return "Track{" +
                "filename='" + filename + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Track)) return false;

        Track track = (Track) o;

        return filename.equals(track.filename) && filePath.equals(track.filePath);
    }

    @Override
    public int hashCode() {
        int result = filename.hashCode();
        result = 31 * result + filePath.hashCode();
        return result;
    }
}