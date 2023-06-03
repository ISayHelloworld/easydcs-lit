package main.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import main.entity.FlightInfo;
import main.utils.Constants;
import main.utils.ResourceUtils;

import java.io.IOException;
import java.util.ResourceBundle;

public class FlightBoard extends Pane {
    private FlightInfo mFlightInfo;

    private Label mFlightNumber;

    private Parent mNode;

    private Label mFlightDate;

    private Label mFromTo;

    private Label mStatus;

    public FlightBoard() {
        super();
        initView();
    }

    private void initView() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getXmlUrl("flightBoard"));
        fxmlLoader.setResources(ResourceBundle.getBundle("language"));
        fxmlLoader.setController(this);
        try {
            mNode = fxmlLoader.load();
            mFlightNumber = (Label) mNode.lookup("#flightNumber");
            mFlightDate = (Label) mNode.lookup("#flightDate");
            mFromTo = (Label) mNode.lookup("#fromTo");
            mStatus = (Label) mNode.lookup("#status");
            getChildren().add(mNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getNode() {
        return mNode;
    }

    public void setFlightInfo(FlightInfo flightInfo) {
        mFlightInfo = flightInfo;
        refreshView();
    }

    public FlightInfo getFlightInfo() {
        return mFlightInfo;
    }

    public String getFlightNumber() {
        if (mFlightInfo == null) {
            return null;
        }
        return mFlightInfo.getFlightNumber();
    }

    private void refreshView() {
        mFlightNumber.setText(mFlightInfo.getFlightNumber());
        mFlightDate.setText(mFlightInfo.getDate().toString());
        mFromTo.setText(mFlightInfo.getFromLocation() + "-" + mFlightInfo.getToLocation());
        if (mFlightInfo.getCheckInStatus() == -1) {
            mStatus.setText("--");
        } else {
            mStatus.setText(ResourceUtils.getString(Constants.CHECKING_STATUS_MAP.get(mFlightInfo.getCheckInStatus())));
        }
    }

    public void clear() {
        mFlightInfo = null;
        mFlightNumber.setText("--");
        mFlightDate.setText("--");
        mFromTo.setText("--");
        mStatus.setText("--");
    }
}
