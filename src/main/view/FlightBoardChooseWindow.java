package main.view;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.callback.WindowCallback;
import main.entity.FlightInfo;
import main.utils.ResourceUtils;

import java.util.List;

public class FlightBoardChooseWindow extends Alert {
    static ToggleGroup toggleGroup;
    public FlightBoardChooseWindow(AlertType alertType) {
        super(alertType);
    }

    public static void showWindow(List<FlightInfo> flightInfoList, FlightBoard flightBoard, WindowCallback callback) {
        toggleGroup = new ToggleGroup();
        int i = 0;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        alert.setTitle(ResourceUtils.getString("plsChooseFlight"));
        alert.setHeaderText("");
        VBox vBox = new VBox();
        for (FlightInfo info : flightInfoList) {
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            RadioButton radioButton = new RadioButton();
            radioButton.setUserData(info);
            radioButton.setToggleGroup(toggleGroup);
            if (i == 0) {
                radioButton.setSelected(true);
            }
            hBox.getChildren().add(radioButton);
            FlightBoard board = new FlightBoard();
            board.setOnMouseClicked(event -> {
                radioButton.setSelected(!radioButton.isSelected());
            });
            board.setFlightInfo(info);
            hBox.getChildren().add(board);
            vBox.getChildren().add(hBox);
            i++;
        }
        alert.setGraphic(vBox);
        ok.addEventFilter(ActionEvent.ACTION, eventOk -> {
            FlightInfo flightInfo = (FlightInfo) toggleGroup.getSelectedToggle().getUserData();
            flightBoard.setFlightInfo(flightInfo);
            if (callback != null) {
                callback.ok(flightInfo);
            }
        });
        alert.show();
    }
}
