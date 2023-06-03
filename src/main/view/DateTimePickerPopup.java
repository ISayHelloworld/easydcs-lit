//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package main.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

class DateTimePickerPopup extends VBox implements Initializable {
    private final DateTimePicker parentControl;
    private int hour;
    private int minute;
    @FXML
    private DatePicker datePicker;
    @FXML
    private HBox timeButtonsPane;
    @FXML
    private TextField hoursTextFiled;
    @FXML
    private TextField minutesTextFiled;

    public DateTimePickerPopup(DateTimePicker parentControl) {
        this.hour = parentControl.dateTimeProperty().get().getHour();
        this.minute = ((LocalDateTime) parentControl.dateTimeProperty().get()).getMinute();
        this.parentControl = parentControl;
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("DateTimePickerPopup.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(ResourceBundle.getBundle("language"));

        try {
            fxmlLoader.load();
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }

        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            if (!change.isContentChange()) {
                return change;
            }
            String text = change.getControlNewText();
            if (!Constants.NUMBER_PATTERN.matcher(text).matches()) {
                return null;
            }
            if (text.length() == 1) {
                try {
                    Integer hour = Integer.parseInt(text);
                    if (hour < 0 || hour > 2) {
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            if (hoursTextFiled.getText().length() >= 2 && change.isAdded() && !change.isReplaced()) {
                minutesTextFiled.requestFocus();
                return null;
            }
            if (text.length() == 2) {
                try {
                    Integer hour = Integer.parseInt(text);
                    if (hour >= 24) {
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            return change;
        });
        TextFormatter<String> minFormatter = new TextFormatter<>(change -> {
            if (!change.isContentChange()) {
                return change;
            }
            String text = change.getControlNewText();
            if (!Constants.NUMBER_PATTERN.matcher(text).matches()) {
                return null;
            }
            if (text.length() == 1) {
                try {
                    Integer hour = Integer.parseInt(text);
                    if (hour > 5) {
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            if (minutesTextFiled.getText().length() >= 2 && change.isAdded() && !change.isReplaced()) {
                return null;
            }
            if (text.length() == 2) {
                try {
                    Integer min = Integer.parseInt(text);
                    if (min >= 60) {
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            return change;
        });
        hoursTextFiled.setTextFormatter(textFormatter);
        minutesTextFiled.setTextFormatter(minFormatter);
    }

    public void initialize(URL location, ResourceBundle resources) {
        this.setTimeButtonText();
        this.datePicker.valueProperty().set(((LocalDateTime) this.parentControl.dateTimeProperty().get()).toLocalDate());
    }

    void setDate(LocalDate date) {
        this.datePicker.setValue(date);
    }

    LocalDate getDate() {
        return (LocalDate) this.datePicker.getValue();
    }

    void setTime(LocalTime time) {
        this.hour = time.getHour();
        this.minute = time.getMinute();
        this.setTimeButtonText();
    }

    LocalTime getTime() {
        return LocalTime.of(this.hour, this.minute);
    }

    int getHour() {
        return this.hour;
    }

    void restoreTimePanel() {
        this.hour = getInteger(this.hoursTextFiled);
        this.minute = getInteger(this.minutesTextFiled);
        this.setTimeButtonText();
    }

    @FXML
    void handleOkButtonAction() {
        this.hour = getInteger(this.hoursTextFiled);
        this.minute = getInteger(this.minutesTextFiled);
        this.setTimeButtonText();
        this.parentControl.hidePopup();
    }

    private void setTimeButtonText() {
        this.hoursTextFiled.setText(String.format("%02d", this.hour));
        this.minutesTextFiled.setText(String.format("%02d", this.minute));
    }

    private int getInteger(TextField textField) {
        try {
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}