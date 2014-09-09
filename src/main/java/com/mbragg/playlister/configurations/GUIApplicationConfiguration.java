package com.mbragg.playlister.configurations;

import com.mbragg.playlister.controllers.viewControllers.ViewController;
import javafx.fxml.FXMLLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Additional configuration class for GUI components
 *
 * @author Michael Bragg
 */
@Configuration
@PropertySource("classpath:about.properties")
public class GUIApplicationConfiguration {

    @Bean
    public ViewController viewController() throws IOException {
        return (ViewController) loadView("view.fxml");
    }

    protected Object loadView(String url) throws IOException {
        InputStream fxmlStream = null;
        try {
            fxmlStream = getClass().getResourceAsStream(url);
            FXMLLoader loader = new FXMLLoader();
            loader.load(fxmlStream);
            return loader.getController();
        } finally {
            if (fxmlStream != null) {
                fxmlStream.close();
            }
        }
    }

}