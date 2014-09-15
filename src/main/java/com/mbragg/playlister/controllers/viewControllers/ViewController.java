package com.mbragg.playlister.controllers.viewControllers;

import com.mbragg.playlister.controllers.ApplicationController;
import com.mbragg.playlister.models.entitys.Track;
import com.mbragg.playlister.services.PlaylistService;
import com.mbragg.playlister.services.ScanService;
import com.mbragg.playlister.tools.externalServices.OperatingSystemDetector;
import com.mbragg.playlister.tools.strings.StringTools;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * Manages the communication between the view.fxml view and the application controller.
 *
 * @author Michael Bragg
 */
public class ViewController {

    public static final String FILTER_M4A = "*.m4a";
    public static final String M4A_DESCRIPTION = "m4a files (*.m4a)";
    public static final String FILTER_M3U = "*.m3u";
    public static final String M3U_DESCRIPTION = "(*.m3u)";
    // Buttons
    public Button directoryScanButton;
    public Button createPlaylistButton;
    public Button cancelButton;

    // Text properties
    public TextField directoryTextField;
    public Label queryLabel;

    public ProgressBar progressBar;

    // Table view
    public TableView<Track> playlistTable;
    public TableColumn<Track, String> titleColumn;
    public TableColumn<Track, String> artistColumn;
    public TableColumn<Track, String> albumColumn;
    public TableColumn<Track, String> genreColumn;

    // Menu tool bar
    public CheckMenuItem autoPlayerMenuCheckBox;
    public CheckMenuItem restrictByGenreCheckBox;

    // Playlist tool bar
    public ToolBar playlistToolbar;
    public CheckBox autoCheckbox;
    public Slider playlistNumberSlider;

    @FXML
    private Node view;

    @Autowired
    private ApplicationController applicationController;
    @Autowired
    private ScanService scanService;
    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private AboutViewController aboutViewController;
    @Autowired
    private HelpViewController helpViewController;

    private Stage stage;
    private File queryFile;

    public Node getView() {
        return view;
    }

    /**
     * Initializes the view by setting global properties and bindings.
     * @param stage Stage. The JavaFX view stage
     */
    public void initializeViewController(Stage stage) {
        this.stage = stage;

        scanService.setNumberOfConcurrentThreads(5);

        autoPlayerMenuCheckBox.selectedProperty().bindBidirectional(autoCheckbox.selectedProperty());

        playlistTable.setPlaceholder(new Text("Empty playlist"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("subGenre"));
    }

    /**
     * Called when the select directory button is pressed.
     * - Opens a directory selector dialog box.
     * - If on a mac, the default music directory folder is set.
     */
    public void selectDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();

        if (OperatingSystemDetector.isMac()) {
            File defaultDirectory = new File(StringTools.defaultMacMusicDirectory());
            if (defaultDirectory.exists())
                chooser.setInitialDirectory(defaultDirectory);
        }

        File selectedDirectory = chooser.showDialog(stage);

        if (selectedDirectory != null) {
            directoryTextField.setText(selectedDirectory.getAbsolutePath());
            directoryScanButton.setDisable(false);
        }
    }

    /**
     * Called when the select query button is pressed.
     * - Opens a file chooser dialog box.
     * - If on a mac, the default music directory folder is set.
     */
    public void selectQuery() {
        playlistToolbar.setDisable(true);
        playlistTable.setDisable(true);

        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(M4A_DESCRIPTION, FILTER_M4A);
        fileChooser.getExtensionFilters().add(extFilter);

        if (OperatingSystemDetector.isMac()) {
            File defaultDirectory = new File(StringTools.defaultMacMusicDirectory());
            if (defaultDirectory.exists())
                fileChooser.setInitialDirectory(defaultDirectory);
        }

        queryFile = fileChooser.showOpenDialog(stage);

        if (queryFile != null) {
            if (queryLabel.textProperty().isBound()) {
                queryLabel.textProperty().unbind();
            }
            queryLabel.setText(queryFile.getName());
            createPlaylistButton.setDisable(false);
        }
    }

    /**
     * Called when the scan button is pressed.
     * - Sets which files are to be processed.
     * - Calls the startScan method.
     */
    public void scanDirectory() {
        scanService.reset();

        scanService.setListOfFiles(applicationController.directoryBatchBuild(directoryTextField.getText()));
        scanService.setOnSucceeded(workerStateEvent -> {
            cancelButton.setDisable(true);
            directoryScanButton.setDisable(false);
        });

        startScan();
    }

    /**
     * Called when a file is selected to form a playlist from that has not yet been scanned by the application.
     * - The single query file is passed through the scan processes
     * - Calls the startScan method.
     */
    public void scanQueryTrack() {
        scanService.reset();

        scanService.setListOfFiles(applicationController.queryFileBatchBuild(queryFile));
        scanService.setOnSucceeded(workerStateEvent -> {
            cancelButton.setDisable(true);
            directoryScanButton.setDisable(false);
            createPlaylist();
        });

        startScan();
    }

    /**
     * Method that handles the life cycle of the scan service process.
     * - Sets properties and bindings between the service and the GUI
     */
    private void startScan() {

        if (queryLabel.textProperty().isBound()) {
            queryLabel.textProperty().unbind();
        }
        queryLabel.textProperty().bind(scanService.messageProperty());

        progressBar.progressProperty().bind(scanService.progressProperty());

        scanService.start();

        scanService.setOnRunning(workerStateEvent -> {
            playlistToolbar.setDisable(true);
            playlistTable.setDisable(true);
            cancelButton.setDisable(false);
            directoryScanButton.setDisable(true);
        });

        scanService.setOnCancelled(workerStateEvent -> {
            cancelButton.setDisable(true);
            directoryScanButton.setDisable(false);
        });
    }

    /**
     * Called when the create playlist button is pressed.
     * - If the query file is known by the application, then the playlist service is initiated and bindings managed.
     * - If unknown, passed to the scanQueryTrack method before returning to pass through the playlist generation process.
     */
    public void createPlaylist() {

        if (applicationController.trackExists(queryFile.getName())) {

            playlistService.reset();

            int playlistSize = (int) playlistNumberSlider.getValue();

            playlistService.initializePlaylistService(queryFile.getName(), playlistSize, restrictByGenreCheckBox.selectedProperty().get());

            playlistTable.itemsProperty().bind(playlistService.valueProperty());
            queryLabel.textProperty().bind(playlistService.messageProperty());

            playlistService.start();

            playlistService.setOnSucceeded(workerStateEvent -> {
                if (!playlistTable.getItems().isEmpty()) {
                    playlistToolbar.setDisable(false);
                    playlistTable.setDisable(false);
                }

                if (autoCheckbox.isSelected()) {
                    ViewController.this.launchPlayer();
                }

            });

        } else {
            scanQueryTrack();
        }
    }

    /**
     * Called when the export playlist button is pressed.
     * - Shows a dialog box allowing the user to select a destination to save a playlist to.
     */
    public void exportPlaylist() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(M3U_DESCRIPTION, FILTER_M3U);
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            applicationController.exportPlaylist(file);
            if (queryLabel.textProperty().isBound()) {
                queryLabel.textProperty().unbind();
            }
            queryLabel.textProperty().set("File saved");
        }
    }

    /**
     * Called when the cancel scan button is pressed.
     */
    public void cancelScan() {
        scanService.cancel();
    }

    /**
     * Called when the launch player button is pressed
     */
    public void launchPlayer() {
        applicationController.launchPlaylist();
    }

    /**
     * Called when the "about" link is selected
     */
    public void showAboutInfo() {
        aboutViewController.show(stage);
    }

    /**
     * Called when the "help" link is selected
     */
    public void showHelp() {
        helpViewController.show(stage);
    }

    /**
     * Called when the "Delete database" link is selected.
     */
    public void deleteDatabase() {
        applicationController.deleteDB();
    }

    /**
     * Called when the application is instructed to exit.
     */
    public void exitApplication() {
        applicationController.dbShutdown();
        stage.close();
    }

}