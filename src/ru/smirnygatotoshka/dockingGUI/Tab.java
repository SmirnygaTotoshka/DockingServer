package ru.smirnygatotoshka.dockingGUI;

import javafx.stage.Stage;

public abstract class Tab
{
    public Tab(Stage stage) {
        this.stage = stage;
        init();
    }

    protected Stage stage;

    protected abstract void init();
}
