package com.mbragg.playlister.controllers.viewControllers;

import com.mbragg.playlister.controllers.ApplicationController;
import com.mbragg.playlister.entitys.Track;
import com.mbragg.playlister.services.PlaylistService;
import com.mbragg.playlister.services.ScanService;
import com.mbragg.playlister.tools.externalApplicationLauncher.OperatingSystemDetector;
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

public class ViewController {

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

    private Stage stage;
    private File queryFile;
    @Autowired
    private HelpViewController helpViewController;

    public Node getView() {
        return view;
    }

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

    public void selectDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();

        if (OperatingSystemDetector.isMac()) {
            File defaultDirectory = new File(StringTools.defaultMacMusicDirectory());
            chooser.setInitialDirectory(defaultDirectory);
        }

        File selectedDirectory = chooser.showDialog(stage);

        if (selectedDirectory != null) {
            directoryTextField.setText(selectedDirectory.getAbsolutePath());
            directoryScanButton.setDisable(false);
        }

    }

    public void selectQuery() {
        playlistToolbar.setDisable(true);
        playlistTable.setDisable(true);

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("m4a files (*.m4a)", "*.m4a");
        fileChooser.getExtensionFilters().add(extFilter);

        if (OperatingSystemDetector.isMac()) {
            File defaultDirectory = new File(StringTools.defaultMacMusicDirectory());
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

    public void scanDirectory() {
        scanService.reset();

        scanService.setListOfFiles(applicationController.directoryBatchBuild(directoryTextField.getText()));
        scanService.setOnSucceeded(workerStateEvent -> {
            cancelButton.setDisable(true);
            directoryScanButton.setDisable(false);
        });

        startScan();
    }

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

    public void createPlaylist() {

//        try {
//            applicationController.testPlaylistQuality();
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }

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


    public void exportPlaylist() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("(*.m3u)", "*.m3u");
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

    public void cancelScan() {
        scanService.cancel();
    }

    public void launchPlayer() {
        applicationController.launchPlaylist();
    }

    public void showAboutInfo() {
        aboutViewController.show(stage);
    }

    public void deleteDatabase() {
        applicationController.deleteDB();
    }

    public void exitApplication() {
        stage.close();
    }

    public void showHelp() {
        helpViewController.show(stage);
    }
}