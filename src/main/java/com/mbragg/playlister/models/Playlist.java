package com.mbragg.playlister.models;

import com.mbragg.playlister.configurations.ApplicationConfiguration;
import com.mbragg.playlister.models.entitys.Track;
import com.mbragg.playlister.tools.externalServices.ExternalApplication;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Playlist. Build method to construct a playlist (a list a Track objects).
 * Includes methods to launch & export the playlist.
 *
 * @author Michael Bragg
 */
@Component
public class Playlist {

    @Value("${playlistFileName}")
    private String playlistFilename;

    @Autowired
    private Logger logger;
    private File playlistFile;
    private List<String> filePaths;

    /**
     * Build a playlist file from a given list of Tracks.
     * File paths are extracted then passed to a write method to batchAudioByteExtraction the playlist file.
     *
     * @param results List of tracks to form the playlist from.
     * @return List<Track>. The list of Tracks are return.
     */
    public List<Track> build(List<Track> results) {

        playlistFile = getSourceDirectoryPath();

        filePaths = results.stream()
                .map(Track::getFilePath)
                .collect(Collectors.toList());

        write(filePaths);
        logger.log(Level.INFO, "\"" + playlistFilename + "\" created successfully. [" + results.size() + " tracks]");

        return results;
    }

    /**
     * Write the supplied list of absolute file paths to a file
     *
     * @param filePaths A list of Strings. Absolute file paths to the audio files.
     */
    protected void write(List<String> filePaths) {

        FileOutputStream outputStream = null;
        try {
            outputStream = FileUtils.openOutputStream(playlistFile);

            // Loop over the file paths to append to the playlist file
            for (String s : filePaths) {
                outputStream.write(s.getBytes());
                outputStream.write(System.getProperty("line.separator").getBytes());
            }
            outputStream.flush();

        } catch (IOException e) {
            logger.log(Level.WARN, "WARNING: \"" + playlistFilename + "\" NOT created");
            logger.log(Level.WARN, Arrays.toString(e.getStackTrace()));
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.log(Level.WARN, Arrays.toString(e.getStackTrace()));
                }
            }
        }
    }


    /**
     * Gets the file path of the current executing code - i.e. the directory of a .jar package
     * Used for determining where to write the new playlist file:
     * If the application is being executed from within a .jar package, the new file needs to be written outside of it.
     *
     * @return a File for this directory.
     */
    protected File getSourceDirectoryPath() {

        // Get the URL of the directory where the application is being executed.
        URL url = ApplicationConfiguration.class.getProtectionDomain().getCodeSource().getLocation();

        // Construct the absolute path to the new playlist file.
        StringBuilder sb = new StringBuilder();

        try {
            File file = new File(url.toURI().getSchemeSpecificPart()).getParentFile();
            sb.append(file.getPath()).append(File.separator).append(playlistFilename);
        } catch (URISyntaxException e) {
            logger.log(Level.WARN, e.getMessage());
        }

        logger.log(Level.INFO, sb.toString());
        String path = sb.toString();

        // Remove to file: prefix (If the code is being executed from within a .jar)
        String prefix = "file:";
        if (path.startsWith(prefix)) path = path.substring(prefix.length(), path.length());

        return new File(path);
    }


    /**
     * Launch an external media player to player the playlist file.
     */
    public void launch() {
        try {
            if (ExternalApplication.launch(playlistFile)) {
                logger.log(Level.INFO, "External media player launched.");
            }
        } catch (IOException e) {
            logger.log(Level.WARN, "External media player not available.");
        }
    }


    /**
     * Ability to write a playlist to a supplied file.
     *
     * @param file The directory where the file will be written to.
     */
    public void export(File file) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);

            for (String s : filePaths) {
                fileWriter.write(s);
                fileWriter.write(System.getProperty("line.separator"));
            }

        } catch (IOException ex) {
            logger.log(Level.WARN, "Playlist file export IO Error ");
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    logger.log(Level.ERROR, "Error closing the fileWriter in PlaylistBuilder export method:" + e.getMessage());
                }

            }
        }

    }
}


