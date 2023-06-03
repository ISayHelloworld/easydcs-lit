package main.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import main.utils.TextUtils;

import java.util.stream.IntStream;

/************************************************
 <函数名称>   TimeField
 <功    能>   时间输入控件
 <参数说明>   []
 <返回值>     void
 <作    者>   MUYI
 **************************************************/
public class TimeField extends HBox {
    private final int SEPARATORS_SIZE = 2;

    private final int TEXTFIELD_NUMBER = 2;

    private final Label[] separators  = {new Label(":"), new Label(":")};

    private final TextField[] textFields  = {new TextField(), new TextField()};

    private final StringProperty text = new SimpleStringProperty();

    public TimeField() {
        // 添加光标移动规则 //
        caretMoveRegular();

        // 文本格式规则 //
        formatRegular();

        IntStream.range(0, TEXTFIELD_NUMBER).forEach(index -> {
            if (index != 0) {
                getChildren().add(separators[index - 1]);
            }
            getChildren().add(textFields[index]);
        });

        disableProperty().addListener(observable -> {
            for (Label separator : separators) {
                separator.setVisible(!isDisable());
            }
        });

    }

    public TimeField(String time) {
        new TimeField();
        setTimeText(time);
    }

    private void formatRegular() {
        String[] regex = new String[3];
        regex[0] = "(^[0-1]?[0-9]|[2]?[0-3]$)|(-)|(--)";
        regex[1] = "(^[0-5]?[0-9]$)|(-)|(--)";
        IntStream.range(0, TEXTFIELD_NUMBER).forEach(index -> {
            textFields[index].textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.length() >= 3) {
                    if (index < TEXTFIELD_NUMBER - 1 && textFields[index].getCaretPosition() == SEPARATORS_SIZE) {
                        textFields[index + 1].requestFocus();
                        textFields[index + 1].setText(newValue);
                        textFields[index + 1].positionCaret(0);
                    }
                    textFields[index].setText(oldValue);
                }
                else if (!newValue.matches(regex[index]) && !TextUtils.isEmpty(newValue)) {
                    textFields[index].setText("--");
                } else {
                    textFields[index].setText(newValue);
                    text.set(textFields[0].getText() + ":" + textFields[1].getText());
                }
            });

            textFields[index].focusedProperty().addListener(observable -> {
                if (!isFocused() && !isDisable()) {
                    // 空白格置0 //
                    if (TextUtils.isEmpty(textFields[index].getText())) {
                        textFields[index].setText("00");
                    }
                    // 1位补0 //
                    if(textFields[index].getText().length() == 1){
                        textFields[index].setText("0" + textFields[index].getText());
                    }
                }
            });
        });
    }

    private void caretMoveRegular() {
        IntStream.range(0, TEXTFIELD_NUMBER).forEach(index -> {
            textFields[index].setPrefWidth(40);
            textFields[index].addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
            textFields[index].addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent keyEvent) -> {
                // 按下“->”键 //
                if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
                    if (index < TEXTFIELD_NUMBER - 1 && textFields[index].getCaretPosition() == textFields[index].getText().length()) {
                        textFields[index + 1].requestFocus();
                        textFields[index + 1].positionCaret(2);
                        keyEvent.consume();
                    }
                }
                // 按下“<-”键 //
                if (keyEvent.getCode().equals(KeyCode.LEFT) ) {
                    if (index > 0 && textFields[index].getCaretPosition() == 0) {
                        textFields[index - 1].requestFocus();
                        textFields[index - 1].positionCaret(textFields[index - 1].getText().length());
                        keyEvent.consume();
                    }
                }

                // 按下“BackSpace”键 //
                if (keyEvent.getCode().equals(KeyCode.BACK_SPACE)) {
                    // 在边界再按一次往前移动光标 //
                    if (index > 0 && textFields[index].getCaretPosition() == 0) {
                        textFields[index - 1].requestFocus();
                        textFields[index - 1].positionCaret(textFields[index - 1].getText().length());
                        keyEvent.consume();
                    }
                }
            });

        });
    }

    public final void setTimeText(String content) {
        String[] times = content.split(":");
        if (times.length != TEXTFIELD_NUMBER) {
            clearText();
            return;
        }
        for (int i = 0; i < times.length; i++) {
            textFields[i].setText(times[i]);
        }
        text.set(content);
        separators[0].setVisible(true);
        separators[1].setVisible(true);
    }

    public final StringProperty textProperty() { return text; }

    public final String getText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < TEXTFIELD_NUMBER; i++) {
            stringBuilder.append(textFields[i].getText()).append(":");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        text.set(stringBuilder.toString());
        String res = text.get();
        if (res.contains("-")) {
            return "";
        }
        return text.get();
    }

    public TextField[] getTextFields() {
        return textFields;
    }

    public void clearText() {
        IntStream.range(0,TEXTFIELD_NUMBER).forEach(index -> textFields[index].clear());
        separators[0].setVisible(false);
        separators[1].setVisible(false);
    }

    public void setSeparatorStyleClass(String style) {
        for (Label label :separators) {
            label.getStyleClass().clear();
            label.getStyleClass().add(style);
        }
    }

    public void setInputTextStyleClass(String style) {
        for (TextField textField :textFields) {
            textField.getStyleClass().clear();
            textField.getStyleClass().add(style);
        }
    }
}