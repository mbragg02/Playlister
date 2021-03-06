package com.mbragg.playlister.configurations;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring configuration for the application.
 * <p>
 * Configures beans, configuration file dependencies, application logger and the Neo4j Graph database service.
 *
 * @author Michael Bragg
 */
@Configuration
@EnableAutoConfiguration
@EnableAsync
@PropertySource("classpath:application.properties")
@PropertySource("classpath:log4j.properties")
@PropertySource("classpath:about.properties")
@EnableNeo4jRepositories(basePackages = "com.mbragg.playlister.repositories")
@ComponentScan(basePackages = "com.mbragg.playlister",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GUIApplicationConfiguration.class)
)
public class ApplicationConfiguration extends Neo4jConfiguration {


    public ApplicationConfiguration() {
        setBasePackage("com.mbragg.playlister");
    }

    @Bean
    Logger logger() {
        return Logger.getLogger(ApplicationConfiguration.class.getName());
    }

    @Bean
    GraphDatabaseService graphDatabaseService(@Value("${dbName}") String dbName) {
        return new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(dbName)
                .newGraphDatabase();
    }

}