package main.page;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.Main;
import main.manager.PageRouter;
import main.utils.PropertyUtils;
import main.utils.StyleUtils;
import main.utils.TextUtils;
import main.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingPage extends BasePageNode {
    private ToggleGroup toggleGroup;

    @FXML
    private RadioButton light;

    @FXML
    private RadioButton dark;

    @FXML
    private ComboBox<String> language;

    @Override
    String getFxmLPath() {
        setId("tab_setting");
        return "settingPage";
    }

    @Override
    void initView() {
        String lang = PropertyUtils.readProperty("lang", "lang");
        if (TextUtils.isEmpty(lang)) {
            lang = "English";
        }
        language.setPromptText(lang);
        language.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                String defaultLang = PropertyUtils.readProperty("lang", "lang");
                if (TextUtils.isEmpty(defaultLang)) {
                    defaultLang = "English";
                }
                language.setPromptText(defaultLang);
                return;
            }
            if (TextUtils.equals(newValue, "English")) {
                PropertyUtils.modifyProperty("lang", "lang", "English");
            } else if (TextUtils.equals(newValue, "Chinese")) {
                PropertyUtils.modifyProperty("lang", "lang", "Chinese");
            } else {
                PropertyUtils.modifyProperty("lang", "lang", "English");
            }
            if (!TextUtils.equals(oldValue, newValue)) {
                restart();
            }
        });
        toggleGroup = new ToggleGroup();
        light.setToggleGroup(toggleGroup);
        dark.setToggleGroup(toggleGroup);
        if (TextUtils.equals(StyleUtils.getStyle(), "light")) {
            light.setSelected(true);
            dark.setSelected(false);
        } else {
            light.setSelected(false);
            dark.setSelected(true);
        }
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            String id = ((RadioButton)newValue).getId();
            PropertyUtils.modifyProperty("style", "style", id);
            StyleUtils.changeStyle(id, true);
        });
    }

    @Override
    void initData() {
        initLanguage();
    }

    private static void restart() {
        Main.getStage().close();
        Platform.runLater(() -> {
            try {
                PageRouter.clearCache();
                new Main().start(new Stage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    List<Pane> getRasterizeWidthPanes() {
        return new ArrayList<>(Arrays.asList(mRoot));
    }

    @Override
    List<Pane> getRasterizeHeightPanes() {
        return new ArrayList<>(Arrays.asList(mRoot));
    }

    @Override
    public void refresh() {
        initData();
    }

    private void initLanguage() {
        language.getItems().clear();
        List<String> langs = new ArrayList<>();
        langs.add("English");
        langs.add("Chinese");
        language.getItems().addAll(langs);
    }

}
