package com.mbragg.playlister.controllers.viewControllers;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AboutController
 * <p>
 * Logic for initiating and configuring the "About" popup window.
 *
 * @author Michael Bragg
 */
@Component
public class AboutViewController {

    public static final int DIALOG_WIDTH = 400;
    public static final int DIALOG_HEIGHT = 250;
    public static final int DIALOG_PADDING = 20;
    public static final String BOLD_FONT = "-fx-font-weight: bold";

    @Value("${applicationName}")
    private String applicationName;
    @Value("${author}")
    private String author;
    @Value("${supervisor}")
    private String supervisor;
    @Value("${projectName}")
    private String projectName;
    @Value("${collegeInfo}")
    private String collegeInfo;

    public void show(Stage parentStage) {

        final Stage dialog = new Stage();

        int textWrapWidth = DIALOG_WIDTH - 2 * DIALOG_PADDING;

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setResizable(false);
        dialog.setTitle("About");
        VBox dialogBox = new VBox(DIALOG_PADDING);
        dialogBox.setPadding(new Insets(DIALOG_PADDING));

        Text applicationNameText = new Text(applicationName);
        Text authorText          = new Text("Author: " + author);
        Text supervisorText      = new Text("Supervisor: " + supervisor);
        Text projectNameText     = new Text("Project name: " + projectName);
        Text collegeInfoText     = new Text(collegeInfo);

        applicationNameText.setWrappingWidth(textWrapWidth);
        applicationNameText.setStyle(BOLD_FONT);
        authorText.setWrappingWidth(textWrapWidth);
        supervisorText.setWrappingWidth(textWrapWidth);
        projectNameText.setWrappingWidth(textWrapWidth);
        collegeInfoText.setWrappingWidth(textWrapWidth);

        dialogBox.getChildren().addAll(applicationNameText, authorText, supervisorText, projectNameText, collegeInfoText);

        Scene dialogScene = new Scene(dialogBox, DIALOG_WIDTH, DIALOG_HEIGHT);
        dialog.setScene(dialogScene);
        dialog.show();
    }
}
