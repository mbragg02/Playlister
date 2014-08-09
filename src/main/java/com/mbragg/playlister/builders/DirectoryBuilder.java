package com.mbragg.playlister.builders;

import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FileBuilder class
 *
 * @author Michael Bragg
 */
@Component
public class DirectoryBuilder {

    /**
     * Builds and returns a list of files.
     *
     * @param musicDirectoryFilePath String. The directory to scan
     * @param suffix                 String. The file extension to filter by.
     * @return List of Files in the given directory with the matching suffix
     */
    public List<File> build(String musicDirectoryFilePath, String suffix) {

        List<File> files = null;
        SuffixFileFilter suffixFileFilter = new SuffixFileFilter(suffix);

        try {
            files = Files.walk(new File(musicDirectoryFilePath).toPath())
                    .map(Path::toFile)
                    .filter(suffixFileFilter::accept)
                    .filter(HiddenFileFilter.VISIBLE::accept)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

}
