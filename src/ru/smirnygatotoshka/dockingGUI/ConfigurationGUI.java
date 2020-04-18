package ru.smirnygatotoshka.dockingGUI;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ConfigurationGUI extends Tab
{

    public TextArea pathJar,inputPath,outputPath,name,mgltoolsPath,workspacePath,hadoopPath,numReducers,host;
    public CheckBox prepare_gpf;
    public Button chooseJar,chooseMGL,chooseWorkspace,chooseHadoop,save;


    private EventHandler file;

    private EventHandler dir;

    public ConfigurationGUI(Stage stage) {
        super(stage);
    }

    @Override
    protected void init()
    {
        pathJar = (TextArea) stage.getScene().lookup("#pathJar");
        inputPath = (TextArea) stage.getScene().lookup("#pathInput");
        outputPath = (TextArea) stage.getScene().lookup("#pathOutput");
        name = (TextArea) stage.getScene().lookup("#name");
        mgltoolsPath = (TextArea) stage.getScene().lookup("#pathMGL");
        workspacePath = (TextArea) stage.getScene().lookup("#pathWorkspace");
        hadoopPath = (TextArea) stage.getScene().lookup("#pathHadoop");
        prepare_gpf = (CheckBox) stage.getScene().lookup("#prepare_gpf");
        numReducers = (TextArea) stage.getScene().lookup("#numReducers");
        host = (TextArea) stage.getScene().lookup("#host");


        dir = new EventHandler() {
        @Override
        public void handle(Event event) {
            try {
                File d = FileChooserFactory.createChooseDirectory("Выберите папку");
                if (d != null) {
                    Button b = (Button) event.getSource();
                    TextArea t;

                    if (b.getId().equals("chooseMGL"))
                        t = mgltoolsPath;
                    else if (b.getId().equals("chooseWorkspace"))
                        t = workspacePath;
                    else if (b.getId().equals("chooseHadoop"))
                        t = hadoopPath;
                    else throw new NullPointerException("Event source not found.");

                    t.setText(d.getAbsolutePath());
                }
            }
            catch (IOException | NullPointerException e)
            {
                Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    };

        file = new EventHandler() {
        @Override
        public void handle(Event event) {
            try {
                File j = FileChooserFactory.createChooseOneFile("Выберите jar файл");
                if (j != null) {
                    String r = getFileExtension(j.getName());
                    if (!getFileExtension(j.getName()).contentEquals( "jar")) throw new IOException("Выберите файл формата jar");
                    pathJar.setText(j.getAbsolutePath());
                }
            } catch (IOException e) {
                Dialog.getDialog("Error",e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    };

        chooseJar = (Button) stage.getScene().lookup("#chooseJar");
        chooseJar.setOnAction(file);

        chooseMGL = (Button) stage.getScene().lookup("#chooseMGL");
        chooseMGL.setOnAction(dir);

        chooseWorkspace = (Button) stage.getScene().lookup("#chooseWorkspace");
        chooseWorkspace.setOnAction(dir);

        chooseHadoop = (Button) stage.getScene().lookup("#chooseHadoop");
        chooseHadoop.setOnAction(dir);

        save = (Button) stage.getScene().lookup("#save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Configuration configuration = getFromFields();
                try {
                    configuration.serialize();
                    Dialog.getDialog("Info","Сохранено", Alert.AlertType.INFORMATION);
                } catch (IOException e) {
                    Dialog.getDialog("Error","Cannot serialize config,because "+e.getMessage(), Alert.AlertType.ERROR);

                }
            }
        });

    }

    private String getFileExtension(String name) {
        int indexPoint = name.lastIndexOf('.');
        return indexPoint == -1 ? "" : name.substring(indexPoint + 1);
    }

    public Configuration getFromFields()
    {
        Configuration configuration = Configuration.getInstance();
        configuration.setJar(pathJar.getText());
        configuration.setHadoop(hadoopPath.getText());

        String[] args = new String[Configuration.PREPARE_GPF + 1];

        args[Configuration.INPUT] = inputPath.getText();
        args[Configuration.OUTPUT] = outputPath.getText();
        args[Configuration.NAME] = name.getText();
        args[Configuration.MGLTOOLS] = mgltoolsPath.getText();
        args[Configuration.WORKSPACE] = workspacePath.getText();
        args[Configuration.HOST] = host.getText();
        args[Configuration.REDUCERS] = numReducers.getText();

        if (prepare_gpf.isSelected())
            args[Configuration.PREPARE_GPF] = "prepare_gpf";
        else
            args[Configuration.PREPARE_GPF] = "";

        configuration.setArguments(args);

        return configuration;
    }

    public void write(Configuration conf)
    {
        pathJar.setText(conf.getJar());
        hadoopPath.setText(conf.getHadoop());
        inputPath.setText(conf.getArguments()[Configuration.INPUT]);
        outputPath.setText(conf.getArguments()[Configuration.OUTPUT]);
        name.setText(conf.getArguments()[Configuration.NAME]);
        mgltoolsPath.setText(conf.getArguments()[Configuration.MGLTOOLS]);
        workspacePath.setText(conf.getArguments()[Configuration.WORKSPACE]);
        host.setText(conf.getArguments()[Configuration.HOST]);
        numReducers.setText(conf.getArguments()[Configuration.REDUCERS]);
        prepare_gpf.setSelected(conf.getArguments()[Configuration.PREPARE_GPF].contentEquals("prepare_gpf"));
    }
}
