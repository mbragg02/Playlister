package com.mbragg.playlister.relationships;

import com.mbragg.playlister.models.entitys.Track;
import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

/**
 * Custom Spring Data Neo4j relationship entity for connecting two Track Nodes, based on a similarity property.
 *
 * @author Michael Bragg
 */
@RelationshipEntity(type = "SIMILARITY")
public class SimilarTo {

    // Neo4j identifier
    @SuppressWarnings("UnusedDeclaration")
    @GraphId
    private Long id;

    @StartNode
    private Track fromTrack;

    @EndNode
    private Track toTrack;

    private double similarity;

    public SimilarTo(){ /* Needed empty constructor for spring framework */ }

    public SimilarTo(Track fromTrack, Track toTrack, double similarity) {
        this.fromTrack = fromTrack;
        this.toTrack = toTrack;
        this.similarity = similarity;
    }

    public Track getFromTrack() {
        return fromTrack;
    }

    public Track getToTrack() {
        return toTrack;
    }

    public double getSimilarity() {
        return similarity;
    }

    @Override
    public String toString() {
        return "SimilarTo{" +
                "id=" + id +
                ", fromTrack=" + fromTrack +
                ", toTrack=" + toTrack +
                ", similarity=" + similarity +
                '}';
    }
}