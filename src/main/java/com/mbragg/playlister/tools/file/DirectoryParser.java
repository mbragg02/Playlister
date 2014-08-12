package com.mbragg.playlister.tools.file;

import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FileBuilder. Single Build method to scan a directory to return a list of files.
 *
 * @author Michael Bragg
 */
@Component
public class DirectoryParser {

    @Autowired
    Logger logger;

    /**
     * Traverses a supplied file directory to batchAudioByteExtraction/return a list of files.
     * Files must have to same extension as the supplied String suffix.
     * Hidden files are also ignored.
     *
     * @param musicDirectoryFilePath String. The directory to scan
     * @param suffix                 String. The file extension to filter by.
     * @return List of Files in the given directory with the matching suffix
     */
    public List<File> parse(String musicDirectoryFilePath, String suffix) {

        List<File> files = null;
        SuffixFileFilter suffixFileFilter = new SuffixFileFilter(suffix);

        try {
            files = Files.walk(new File(musicDirectoryFilePath).toPath())
                    .map(Path::toFile)
                    .filter(suffixFileFilter::accept)
                    .filter(HiddenFileFilter.VISIBLE::accept)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.log(Level.WARN, "Exception scanning music directory:" + e.getMessage());
        }
        return files;
    }

}
