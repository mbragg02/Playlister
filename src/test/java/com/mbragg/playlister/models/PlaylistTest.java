package com.mbragg.playlister.models;

import com.mbragg.playlister.configurations.ApplicationConfiguration;
import com.mbragg.playlister.models.Playlist;
import com.mbragg.playlister.models.entitys.Track;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Michael Bragg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class PlaylistTest {

    @Autowired
    private Playlist playlist;

    @Mock
    private Track track;

    private List<Track> listOfMockTracks;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        listOfMockTracks = new ArrayList<>();
        listOfMockTracks.add(track);

        when(track.getFilePath()).thenReturn("a/test/track/filePath");
    }

    @Test
    public void testBuild() throws Exception {
        List<Track> actual = playlist.build(listOfMockTracks);

        assertEquals("a/test/track/filePath", actual.get(0).getFilePath());
    }

    @Test
    public void testGetSourceDirectory() {
        File actual = playlist.getSourceDirectoryPath();
        File testClassFile = new File(PlaylistTest.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        assertEquals(testClassFile.getParent() + "/playlist.m3u", actual.toString());
    }
}