package ru.smirnygatotoshka.dockingGUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;


public class InformationListGUI extends Tab {

    private String[] nameProperties;
    private TableView<DockInfo> table;
    public InformationListGUI(Stage stage) {
        super(stage);
    }

    @Override
    protected void init() {
        table = (TableView) stage.getScene().lookup("#taskInfo");
        nameProperties = new String[]{"id", "slaveName", "path", "statusName"};
        ObservableList<TableColumn<DockInfo,?>> columns = table.getColumns();
        try {
            for (int i = 0; i < columns.size(); i++) {
                columns.get(i).setCellValueFactory(new PropertyValueFactory<>(nameProperties[i]));
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            Dialog.getDialog("Error","Properties length != number of columns", Alert.AlertType.ERROR);
        }
    }

    public /*synchronized*/ void update(ArrayList<DockInfo> dockInfo)
    {
        ObservableList<DockInfo> d = FXCollections.observableList(dockInfo);
        table.setItems(d);
    }
}
