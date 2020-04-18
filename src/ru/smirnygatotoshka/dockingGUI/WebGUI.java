package ru.smirnygatotoshka.dockingGUI;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebGUI extends Tab {

    public WebView web;
    public Button upload;
    public TextField url;
    public WebEngine engine;

    public WebGUI(Stage stage) {
        super(stage);
    }

    @Override
    protected void init() {
        uploader = new EventHandler() {
            @Override
            public void handle(Event event) {
                engine.load(url.getText());
            }
        };
        web = (WebView) stage.getScene().lookup("#web");
        engine = web.getEngine();
        upload = (Button) stage.getScene().lookup("#upload");
        upload.setOnAction(uploader);
        url = (TextField) stage.getScene().lookup("#url");
        String hadoopServer = Main.getConfigurationGUI().getFromFields().getArguments()[Configuration.HOST].split(":")[0];
        url.setText("http://"+hadoopServer+":8088");
    }

    private EventHandler uploader;

}
