package com.mbragg.playlister.controllers.viewControllers;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Component
public class HelpViewController {
    private static final int DIALOG_WIDTH = 400;
    private static final int DIALOG_HEIGHT = 400;
    private static final int DIALOG_PADDING = 10;
    private static final double ACCORDION_WIDTH = DIALOG_WIDTH - 2 * DIALOG_PADDING;
    private static final double TEXT_WRAP_WIDTH = ACCORDION_WIDTH - 2 * DIALOG_PADDING;

    @Autowired
    private Logger logger;

    public void show(Stage parentStage) {
        final Stage dialog = new Stage();

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setResizable(false);
        dialog.setTitle("Help");

        Accordion accordion = new Accordion();
        VBox dialogBox = new VBox(DIALOG_PADDING);
        dialogBox.setPadding(new Insets(DIALOG_PADDING));

        Properties properties = new Properties();
        try {
            InputStream is = HelpViewController.class.getResourceAsStream("help.properties");
            properties.load(is);

            for (Map.Entry<Object, Object> x : properties.entrySet()) {

                String[] qAndA = StringUtils.split((String) x.getValue(), ";");

                accordion.getPanes().add(createTitledPane(qAndA[0].trim(), qAndA[1].trim()));
            }
        } catch (IOException e) {
            logger.log(Level.WARN, "Help properties file not found");
        }

        dialogBox.getChildren().add(accordion);

        Scene dialogScene = new Scene(dialogBox, DIALOG_WIDTH, DIALOG_HEIGHT);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private TitledPane createTitledPane(String title, String body) {
        TitledPane titledPane = new TitledPane();
        titledPane.setText(title);
        titledPane.setContent(createHBoxWithText(body));

        return titledPane;
    }

    private HBox createHBoxWithText(String body) {
        HBox hBox = new HBox();
        Text text = new Text();
        text.setWrappingWidth(TEXT_WRAP_WIDTH);
        text.setText(body);

        hBox.setPadding(new Insets(DIALOG_PADDING));
        hBox.getChildren().add(text);

        return hBox;
    }
}
