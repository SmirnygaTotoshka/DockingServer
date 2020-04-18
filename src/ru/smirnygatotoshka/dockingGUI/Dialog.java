package ru.smirnygatotoshka.dockingGUI;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

public class Dialog
{
    public static ButtonType getDialog(String title, String message, Alert.AlertType type)
    {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        Platform.runLater(() -> alert.showAndWait());
        return alert.getResult();
    }

}
