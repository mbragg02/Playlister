package com.mbragg.playlister.builders;

import com.mbragg.playlister.configurations.ApplicationConfiguration;
import com.mbragg.playlister.entitys.Track;
import com.mbragg.playlister.tools.externalApplicationLauncher.LaunchExternalApplication;
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
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PlaylistBuilder
 *
 * @author Michael Bragg
 */
@Component
public class PlaylistBuilder {

    @Value("${playlistFileName}")
    private String playlistFilename;

    @Autowired
    private Logger logger;
    private File playlistFile;
    private List<String> filePaths;

    /**
     * Build a playlist file from a given list of tracks.
     *
     * @param results List of tracks to form the playlist from.
     * @return the playlist file.
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
     * @param filePaths A list of Strings.
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
     *
     * @return a File for this directory.
     */
    protected File getSourceDirectoryPath() {

        // Get the URL of the directory where the application is being executed.
        URL url = ApplicationConfiguration.class.getProtectionDomain().getCodeSource().getLocation();

        // Construct the absolute path to the new playlist file.
        File file = new File(url.getFile()).getParentFile();
        String path = file.toPath().resolve(playlistFilename).toString();

        // Remove to file: prefix (If the code is being executed from within a .jar)
        if (path.startsWith("file:")) path = path.substring(5, path.length());

        return new File(path);
    }


    /**
     * Launch an external media player to player the playlist file.
     */
    public void launch() {
        try {
            if (LaunchExternalApplication.launch(playlistFile)) {
                logger.log(Level.INFO, "External media player launched.");
            }
        } catch (IOException e) {
            logger.log(Level.WARN, "External media player not available.");
        }
    }


    /**
     * Option to write the playlist to a supplied file.
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
                    logger.log(Level.ERROR, e.getStackTrace());
                }

            }
        }

    }
}


