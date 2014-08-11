package com.mbragg.playlister.entitys;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * Class to represent a Genre
 * <p>
 * A Neo4j @NodeEntity
 *
 * @author Michael Bragg
 */
@NodeEntity
public class Genre {

    // Neo4j identifier
    @SuppressWarnings("UnusedDeclaration")
    @GraphId
    private Long id;

    @Indexed
    private String name;

    public Genre(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;

        Genre genre = (Genre) o;

        return !(name != null ? !name.equals(genre.name) : genre.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}