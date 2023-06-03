package main.view;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import main.callback.WindowCallback;
import main.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class ColumnChooseWindow extends Alert {
    ToggleGroup toggleGroup;

    Alert alert;

    private static VBox vBox;
    public ColumnChooseWindow(AlertType alertType) {
        super(alertType);
    }

    public void closeWindow() {
        if (alert != null) {
            alert.close();
        }
    }

    public void showWindow(List<TableColumn> columnList, WindowCallback callback) {
        toggleGroup = new ToggleGroup();
        alert = new Alert(AlertType.CONFIRMATION);
        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        alert.setTitle(ResourceUtils.getString("plsChooseColumn"));
        alert.setHeaderText("");
        vBox = new VBox();
        for (TableColumn column : columnList) {
            CheckBox checkBox = new CheckBox(column.getText());
            checkBox.setSelected(true);
            vBox.getChildren().add(checkBox);
        }
        alert.setGraphic(vBox);
        ok.addEventFilter(ActionEvent.ACTION, eventOk -> {
            List<String> chooseColumns = new ArrayList<>();
            for (Node node : vBox.getChildren()) {
                if (!(node instanceof CheckBox)) {
                    continue;
                }
                CheckBox checkBox = (CheckBox) node;
                if (checkBox.isSelected()) {
                    chooseColumns.add(checkBox.getText());
                }
            }
            if (callback != null) {
                callback.ok(chooseColumns);
            }
        });
        alert.show();
    }
}
