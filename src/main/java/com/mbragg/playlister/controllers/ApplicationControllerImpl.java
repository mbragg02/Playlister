package com.mbragg.playlister.controllers;

import com.mbragg.playlister.builders.DirectoryBuilder;
import com.mbragg.playlister.builders.GenreBuilder;
import com.mbragg.playlister.builders.PlaylistBuilder;
import com.mbragg.playlister.builders.TrackBuilder;
import com.mbragg.playlister.dao.DAO;
import com.mbragg.playlister.entitys.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Application single entry point.
 *
 * @author Michael Bragg
 */
@Component
public class ApplicationControllerImpl implements ApplicationController {

    private final TrackBuilder trackBuilder;
    private final GenreBuilder genreBuilder;
    private final PlaylistBuilder playlistBuilder;
    private final DirectoryBuilder directoryBuilder;
    private final DAO dao;

    @Value("${suffix}")
    private String suffix;

    @Value("${playlistFileName}")
    private String playlistFile;

    @Value("${genresJSONFilename}")
    private String genresJSONFilename;

    @Autowired
    public ApplicationControllerImpl(TrackBuilder trackBuilder, GenreBuilder genreBuilder, PlaylistBuilder playlistBuilder, DirectoryBuilder directoryBuilder, DAO dao) {
        this.trackBuilder = trackBuilder;
        this.genreBuilder = genreBuilder;
        this.playlistBuilder = playlistBuilder;
        this.directoryBuilder = directoryBuilder;
        this.dao = dao;
    }

//    @Override
//    public void testPlaylistQuality() throws ExecutionException, InterruptedException {
//        List<File> queries = directoryBuilder.build("/Users/mbragg/Music/iTunes/iTunes Media/Music/GTZAN", suffix);
//        queries = queries.subList(900,1000);
//
//        int blues, classical, country, disco, hiphop, jazz, metal, pop, reggae, rock;
//        int totalBlues = 0;
//        int totalClassical = 0;
//        int totalCountry = 0;
//        int totalDisco = 0;
//        int totalHipHop = 0;
//        int totalJazz = 0;
//        int totalMetal = 0;
//        int totalPop = 0;
//        int totalReggae = 0;
//        int totalRock = 0;
//        for (File f: queries) {
//            System.out.println(f.getName());
//            List<Track> results = query(f.getName(), 10, false);
//            blues = 0;
//            classical = 0;
//            country = 0;
//            disco = 0;
//            hiphop = 0;
//            jazz = 0;
//            metal = 0;
//            pop = 0;
//            reggae = 0;
//            rock = 0;
//
//
//            for (Track t : results.subList(1, results.size())) {
//                if (t.getSubGenre().equals("Blues")){
//                    blues+=10;
//                }
//                else if (t.getSubGenre().equals("Classical")){
//                    classical+=10;
//                }
//                else if (t.getSubGenre().equals("Country")){
//                    country+=10;
//                }
//                else if (t.getSubGenre().equals("Disco")){
//                    disco+=10;
//                }
//                else if (t.getSubGenre().equals("Hip Hop")){
//                    hiphop+=10;
//                }
//                else if (t.getSubGenre().equals("Jazz")){
//                    jazz+=10;
//                }
//                else if (t.getSubGenre().equals("Metal")){
//                    metal+=10;
//                }
//                else if (t.getSubGenre().equals("Pop")){
//                    pop+=10;
//                }
//                else if (t.getSubGenre().equals("Reggae")){
//                    reggae+=10;
//                }
//                else if (t.getSubGenre().equals("Rock")){
//                    rock+=10;
//                }
//
//            }
//            totalBlues +=blues;
//            totalClassical += classical;
//            totalCountry +=country;
//            totalHipHop +=hiphop;
//            totalJazz +=jazz;
//            totalMetal += metal;
//            totalPop +=pop;
//            totalReggae +=reggae;
//            totalDisco += disco;
//            totalRock +=rock;
//
//
//        }
//        final double querySize= 100.00;
//        System.out.println("Blues: " + totalBlues/ querySize);
//        System.out.println("Classical: " + totalClassical/ querySize);
//
//        System.out.println("Country: " +totalCountry/ querySize);
//        System.out.println("Hip Hop: " + totalHipHop/querySize);
//        System.out.println("Jazz: " + totalJazz/querySize);
//        System.out.println("Metal: " +totalMetal/querySize);
//        System.out.println("Pop: " + totalPop/querySize);
//        System.out.println("Rock: " +totalRock/querySize);
//        System.out.println("Reggae: " +totalReggae/querySize);
//        System.out.println("Disco: " + totalDisco/querySize);
//
//    }

    @Override
    public boolean trackExists(String fileName) {
        return dao.trackExists(fileName);
    }

    @Override
    public List<Track> query(String fileName, int sizeOfResult, boolean restrictByGenre) throws InterruptedException, ExecutionException {
        if (dao.trackExists(fileName)) {
                return playlistBuilder.build(dao.query(fileName, sizeOfResult, restrictByGenre));
        }
         return new ArrayList<>();
    }

    @Override
    public void launchPlaylist() {
            playlistBuilder.launch();
    }

    // For command line application
//    public void buildLibrary(String musicDirectoryFilePath) {
//        trackBuilder.build(directoryBuilder.build(musicDirectoryFilePath, suffix), genreBuilder.build(genresJSONFilename));
//    }

    @Override
    public List<File> queryFileBatchBuild(File file) {
        List<File> singleFileList = new ArrayList<>();
        singleFileList.add(file);
        return trackBuilder.batchBuild(singleFileList, genreBuilder.build(genresJSONFilename));
    }

    @Override
    public List<File> directoryBatchBuild(String musicDirectoryFilePath) {
        return trackBuilder.batchBuild(directoryBuilder.build(musicDirectoryFilePath, suffix), genreBuilder.build(genresJSONFilename));
    }

    @Override
    public Map<Future<byte[]>, AudioFormat> extractAudioBatch(List<File> filesToProcessBuffer) throws InterruptedException {
        return trackBuilder.batchExtraction(filesToProcessBuffer);
    }

    @Override
    @Async
    public Future<Track> buildTrack(File file, byte[] bytes, AudioFormat format) {
        return new AsyncResult<>(trackBuilder.buildTrack(file, bytes, format));
    }

    @Override
    public void deleteDB() {
        dao.deleteDatabase();
    }


    @Override
    public void exportPlaylist(File file) {
        playlistBuilder.export(file);
    }

}