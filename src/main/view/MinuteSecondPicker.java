package main.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MinuteSecondPicker extends VBox implements Initializable {
    private final DateTimePickerPopup parentContainer;
    @FXML
    private Slider slider;
    @FXML
    private Label label;

    MinuteSecondPicker(DateTimePickerPopup parentContainer) {
        this.parentContainer = parentContainer;
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("MinuteSecondPicker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.slider.setMin(0.0D);
        this.slider.setMax(59.0D);
        this.slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int newValueInt = newValue.intValue();
                label.setText(String.format("%02d", newValueInt));
            }
        });
        this.slider.onMouseReleasedProperty().set(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                parentContainer.restoreTimePanel();
            }
        });
    }

    public int getValue() {
        return Integer.parseInt(this.label.getText());
    }
}
