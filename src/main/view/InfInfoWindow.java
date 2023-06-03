package main.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import main.callback.CommonCallback;
import main.entity.FlightUser;
import main.utils.*;

import java.io.IOException;
import java.util.ResourceBundle;

public class InfInfoWindow extends Pane {
    private Parent mNode;

    private FlightUser flightUser;

    @FXML
    private TextField name;

    @FXML
    private TextField middleName;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField gender;

    @FXML
    private TextField country;

    @FXML
    private TextField birthday;

    @FXML
    private TextField passportNumber;

    @FXML
    private TextField issueDate;

    @FXML
    private TextField expireDate;

    @FXML
    private Button saveApi;

    public InfInfoWindow(FlightUser flightUser) {
        super();
        this.flightUser = flightUser;
        initView();
    }

    private void initView() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getXmlUrl("infInfoWindow"));
        fxmlLoader.setResources(ResourceBundle.getBundle("language"));
        fxmlLoader.setController(this);
        try {
            mNode = fxmlLoader.load();
            StyleUtils.addStyleNode(mNode);
            getChildren().add(mNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        refreshApi();
        saveApi.setOnAction(view -> {
            setSaveApi(flightUser);
        });
    }

    private void refreshApi() {
        // 清空数据
        if (flightUser == null) {
            return;
        }
        TextUtils.safeSetStr(passportNumber, flightUser.getInfNumber());
        TextUtils.safeSetStr(name, flightUser.getInfName());
        TextUtils.safeSetStr(middleName, flightUser.getInfMiddleName());
        if (flightUser.getInfName() != null && flightUser.getInfName().contains("/")) {
            String[] names = flightUser.getInfName().split("/");
            if (names.length >= 2) {
                firstName.setText(names[1].trim());
                lastName.setText(names[0].trim());
            }
        }
        TextUtils.safeSetStr(gender, flightUser.getInfGender());
        TextUtils.safeSetStr(country, flightUser.getInfCountry());
        TextUtils.safeSetStr(birthday, flightUser.getInfBirthDate());
        TextUtils.safeSetStr(passportNumber, flightUser.getInfNumber());
        TextUtils.safeSetStr(issueDate, flightUser.getInfIssueDate());
        TextUtils.safeSetStr(expireDate, flightUser.getInfValidityPeriod());
    }

    private void refreshView() {
    }

    public void showWindow() {
        Stage stage = new Stage();
        stage.initOwner(Main.getStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(this);
        stage.setScene(scene);
        stage.setTitle(ResourceUtils.getString("flightStatus"));
        stage.show();
        refreshView();
    }

    private boolean checkCondition() {
        TextField[] checkList = new TextField[]{
                lastName, firstName, gender, country, birthday, passportNumber};
        for (TextField textField : checkList) {
            if (textField == null) {
                ToastUtils.toast(ResourceUtils.getString("invalidFormat"));
                return false;
            }
            if (TextUtils.isEmpty(textField.getText())) {
                ToastUtils.toast(ResourceUtils.getString("completeTip"));
                return false;
            }
        }
        return true;
    }

    private void setSaveApi(FlightUser user) {
        if (!checkCondition()) {
            ToastUtils.toast(ResourceUtils.getString("completeTip"));
            return;
        }
        setGroupInfo(user);
        CloudApi.getInstance().uploadFlightUser(user, new CommonCallback() {
            @Override
            public void success(String success) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("saveSuccess"));
                });
            }

            @Override
            public void fail(String fail) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("saveFailed2") + fail);
                });
            }
        });
    }

    private void setGroupInfo(FlightUser user) {
        user.setInfMiddleName(middleName.getText());
        user.setInfName(lastName.getText() + "/" + firstName.getText());
        user.setInfGender(user.getGender());
        user.setInfValidityPeriod(expireDate.getText());
        user.setInfBirthDate(birthday.getText());
        user.setInfCountry(country.getText());
        user.setInfIssueDate(issueDate.getText());
    }
}
