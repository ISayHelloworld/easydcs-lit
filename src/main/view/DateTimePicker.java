//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package main.view;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.ResourceBundle;

public class DateTimePicker extends HBox implements Initializable {
    private ObjectProperty<LocalDateTime> dateTime;
    private final DateTimeFormatter formatter;
    private final Popup popupContainer;
    private final DateTimePickerPopup popup;

    @FXML
    private TextField textField;

    @FXML
    private Button button;

    public DateTimePicker() {
        this(LocalDateTime.now());
    }

    public DateTimePicker(LocalDateTime dateTime) {
        this(dateTime, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }

    public DateTimePicker(LocalDateTime dateTime, DateTimeFormatter formatter) {
        this.dateTime = new SimpleObjectProperty(dateTime);
        this.formatter = formatter;
        this.popupContainer = new Popup();
        this.popup = new DateTimePickerPopup(this);
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("DateTimePicker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.textField.setText("----/--/-- 00:00");
        this.dateTime.addListener((observable, oldValue, newValue) -> {
            this.popup.setDate(newValue.toLocalDate());
            this.popup.setTime(newValue.toLocalTime());
            this.textField.setText(this.formatter.format(newValue));
        });
        this.button.prefHeightProperty().bind(this.textField.heightProperty());
        this.popupContainer.getContent().add(this.popup);
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return this.dateTime;
    }

    void hidePopup() {
        LocalDate date = this.popup.getDate();
        LocalTime time = this.popup.getTime();
        this.dateTime.setValue(LocalDateTime.of(date, time));
        this.textField.setText(date + " " + time);
        this.popupContainer.hide();
    }

    @FXML
    void handleButtonAction(ActionEvent event) {
        if (this.popupContainer.isShowing()) {
            this.popupContainer.hide();
        } else {
            Window window = this.button.getScene().getWindow();
            double x = window.getX() + this.textField.localToScene(0.0D, 0.0D).getX() + this.textField.getScene().getX();
            double y = window.getY() + this.button.localToScene(0.0D, 0.0D).getY() + this.button.getScene().getY() + this.button.getHeight();
            this.popupContainer.show(this.getParent(), x, y);
        }
    }

    public String getDateTime() {
        return this.textField.getText();
    }

    public void clear() {
        this.textField.setText("----/--/-- 00:00");
    }

    public void setDateTime(String str) {
        this.textField.setText(str);
    }
}
