package main.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import main.callback.WindowCallback;
import main.entity.FlightInfo;
import main.entity.FlightUser;
import main.manager.AccountManager;
import main.utils.ResourceUtils;
import main.utils.StyleUtils;
import main.utils.TextUtils;
import main.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class UresWindow extends Alert {
    @FXML
    private TextField name;

    @FXML
    private TextField position;

    @FXML
    private TextField ticketNumber;

    @FXML
    private TextField toLocation;

    private Alert alert;

    private static VBox vBox;

    public UresWindow(AlertType alertType) {
        super(alertType);
    }

    public void closeWindow() {
        if (alert != null) {
            alert.close();
        }
    }

    public void showWindow(FlightInfo flightInfo, WindowCallback callback) {
        alert = new Alert(AlertType.CONFIRMATION);
        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        alert.setTitle(ResourceUtils.getString("plsChooseColumn"));
        alert.setHeaderText("");
        vBox = new VBox();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getXmlUrl("uresWindow"));
        fxmlLoader.setResources(ResourceBundle.getBundle("language"));
        fxmlLoader.setController(this);
        try {
            Parent parent = fxmlLoader.load();
            StyleUtils.addStyleNode(parent);
            alert.setGraphic(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ok.setText(ResourceUtils.getString("add"));
        ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(name,position,toLocation, ticketNumber));
        ok.addEventFilter(ActionEvent.ACTION, eventOk -> {
            for (TextField check : textFields) {
                if (TextUtils.isEmpty(check.getText())) {
                    ToastUtils.toast(ResourceUtils.getString("completeTip"));
                    return;
                }
            }
            FlightUser flightUser = new FlightUser();
            flightUser.setName(name.getText());
            flightUser.setCompany(AccountManager.getInstance().getCompany());
            flightUser.setPosition(position.getText());
            flightUser.setFlightNumber(flightInfo.getFlightNumber());
            flightUser.setTicketNumber(ticketNumber.getText());
            flightUser.setFlightDate(flightInfo.getDate());
            if (callback != null) {
                callback.ok(flightUser);
            }
        });
        alert.show();
    }
}
