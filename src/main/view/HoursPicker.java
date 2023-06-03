package main.view;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HoursPicker extends GridPane implements Initializable {
    private static final int NUM_BUTTONS = 12;
    private final List<ToggleButton> buttonList = new ArrayList(12);
    private final DateTimePickerPopup parentContainer;
    @FXML
    private ToggleGroup hoursToggleGroup;
    @FXML
    private ToggleButton zeroButton;
    @FXML
    private ToggleButton oneButton;
    @FXML
    private ToggleButton twoButton;
    @FXML
    private ToggleButton threeButton;
    @FXML
    private ToggleButton fourButton;
    @FXML
    private ToggleButton fiveButton;
    @FXML
    private ToggleButton sixButton;
    @FXML
    private ToggleButton sevenButton;
    @FXML
    private ToggleButton eightButton;
    @FXML
    private ToggleButton nineButton;
    @FXML
    private ToggleButton tenButton;
    @FXML
    private ToggleButton elevenButton;
    @FXML
    private ToggleButton amPmButton;

    public HoursPicker(DateTimePickerPopup parentContainer) {
        this.parentContainer = parentContainer;
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("HoursPicker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.buttonList.add(this.zeroButton);
        this.buttonList.add(this.oneButton);
        this.buttonList.add(this.twoButton);
        this.buttonList.add(this.threeButton);
        this.buttonList.add(this.fourButton);
        this.buttonList.add(this.fiveButton);
        this.buttonList.add(this.sixButton);
        this.buttonList.add(this.sevenButton);
        this.buttonList.add(this.eightButton);
        this.buttonList.add(this.nineButton);
        this.buttonList.add(this.tenButton);
        this.buttonList.add(this.elevenButton);
        this.amPmButton.prefHeightProperty().bind(this.zeroButton.heightProperty().multiply(3).add(this.getHgap() * 2.0D));
        this.amPmButton.prefWidthProperty().set(35.0D);
        this.amPmButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                int offset = 0;
                if (newValue) {
                    amPmButton.setText("上\n午");
                    offset = 12;
                } else {
                    amPmButton.setText("下\n午");
                }

                for(int i = 0; i < 12; ++i) {
                    ((ToggleButton) buttonList.get(i)).setText(String.format("%02d", i + offset));
                }

            }
        });
        int hour = this.parentContainer.getHour();
        int offset = 0;
        if (hour > 11) {
            this.amPmButton.setSelected(true);
            offset = -12;
        }

        this.hoursToggleGroup.selectToggle((Toggle)this.buttonList.get(hour + offset));
        this.hoursToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == null) {
                    hoursToggleGroup.selectToggle(oldValue);
                } else {
                    parentContainer.restoreTimePanel();
                }

            }
        });
    }

    public int getHour() {
        int hour = this.buttonList.indexOf(this.hoursToggleGroup.getSelectedToggle());
        if (this.amPmButton.isSelected()) {
            hour += 12;
        }

        return hour;
    }
}
