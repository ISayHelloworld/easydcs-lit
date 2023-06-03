package main.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.callback.WindowCallback;
import main.entity.AirPackage;
import main.entity.FlightInfo;
import main.manager.UsersChooseManager;
import main.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class PackageAddWindow extends Pane {
    private Parent mNode;

    private ComboBox<String> mType;

    private VBox autoBox;

    private VBox manualBox;

    private Button okBtn;

    private WindowCallback mCallback;

    private TextField autoNum;
    private TextField autoWeight;
    private TextField autoLocation;
    private TextField manualWeight;
    private TextField manualLocation;
    private TextField manualNumber;
    private CheckBox manualPrint;

    private List<TextField> checkControlsAuto = new ArrayList<>();
    private List<TextField> checkControlsManual = new ArrayList<>();


    public PackageAddWindow() {
        super();
        initView();
    }

    private void initView() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(this);
        fxmlLoader.setResources(ResourceBundle.getBundle("language"));
        fxmlLoader.setLocation(ResourceUtils.getXmlUrl("packageAddWindow"));
        try {
            mNode = fxmlLoader.load();
            StyleUtils.addStyleNode(mNode);
            getChildren().add(mNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mType = (ComboBox<String>) mNode.lookup("#type");
        mType.getItems().add(ResourceUtils.getString("auto"));
        mType.getItems().add(ResourceUtils.getString("manual"));
        autoBox = (VBox) mNode.lookup("#auto");
        manualBox = (VBox) mNode.lookup("#manual");
        okBtn = (Button) mNode.lookup("#ok");
        autoNum = (TextField) mNode.lookup("#auto_num");
        autoWeight = (TextField) mNode.lookup("#auto_weight");
        autoLocation = (TextField) mNode.lookup("#auto_location");
        checkControlsAuto.addAll(Arrays.asList(autoNum, autoWeight, autoLocation));
        manualWeight = (TextField) mNode.lookup("#manual_weight");
        manualLocation = (TextField) mNode.lookup("#manual_location");
        manualNumber = (TextField) mNode.lookup("#manual_number");
        manualPrint = (CheckBox) mNode.lookup("#manual_print");
        checkControlsManual.addAll(Arrays.asList(manualWeight, manualNumber, manualLocation));
        manualBox.managedProperty().bind(manualBox.visibleProperty());
        autoBox.managedProperty().bind(autoBox.visibleProperty());
        initListener();
        mType.getSelectionModel().select(0);
    }

    private void initListener() {
        mType.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mType.getSelectionModel().getSelectedIndex() == Constants.AUTO) {
                autoBox.setVisible(true);
                manualBox.setVisible(false);
            } else if (mType.getSelectionModel().getSelectedIndex() == Constants.MANUAL) {
                autoBox.setVisible(false);
                manualBox.setVisible(true);
            }
        });
        okBtn.setOnAction(view -> {
            if (!checkCondition()) {
                ToastUtils.toastInWindow(ResourceUtils.getString("completeTip"));
                return;
            }
            int type = mType.getSelectionModel().getSelectedIndex();
            AirPackage airPackage = new AirPackage();
            airPackage.setType(type);
            try {
                if (type == Constants.AUTO) {
                    // 自动行李
                    airPackage.setNum(Integer.parseInt(autoNum.getText()));
                    airPackage.setWeight(Integer.parseInt(autoWeight.getText()));
                    airPackage.setNumber(ResourceUtils.getString("autoGenerate"));
                    airPackage.setToLocation(autoLocation.getText());
                } else {
                    // 手工行李
                    airPackage.setNum(1);
                    airPackage.setWeight(Integer.parseInt(manualWeight.getText()));
                    airPackage.setNumber(manualNumber.getText());
                    airPackage.setToLocation(manualLocation.getText());
                    airPackage.setPrint(manualPrint.isSelected());
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            mCallback.ok(airPackage);
        });
    }

    private boolean checkCondition() {
        int type = mType.getSelectionModel().getSelectedIndex();
        if (type == Constants.AUTO) {
            // 自动行李
            for (TextField textField : checkControlsAuto) {
                if (TextUtils.isEmpty(textField.getText())) {
                    return false;
                }
            }
        } else {
            // 手工行李
            for (TextField textField : checkControlsManual) {
                if (TextUtils.isEmpty(textField.getText())) {
                    return false;
                }
            }
        }
        return true;
    }

    public void clear(FlightInfo flightInfo) {
        autoWeight.setText("");
        autoNum.setText("1");
        autoLocation.setText(flightInfo.getToLocation());
        manualWeight.setText("");
        manualLocation.setText(flightInfo.getToLocation());
        manualNumber.setText("");
        manualPrint.setSelected(false);
    }

    public void setCallback(WindowCallback callback) {
        mCallback = callback;
    }
}
