package main.page;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import main.Main;
import main.callback.CommonCallback;
import main.callback.ValueCallback;
import main.manager.DownloadManager;
import main.manager.PageRouter;
import main.utils.*;
import main.view.DownloadProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VersionPage extends BasePageNode {
    @FXML
    private Button checkVersion;

    @FXML
    private Label currentVersion;

    @FXML
    private Label latestVersion;

    @Override
    String getFxmLPath() {
        setId("tab_version");
        return "versionPage";
    }

    private void showUpdateWindow(String version) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        alert.setTitle(ResourceUtils.getString("versionUpdate"));
        alert.setHeaderText(ResourceUtils.getString("updateTip", version));
        ok.setOnAction(view -> {
            update(version);
        });
        alert.show();
    }

    private void close() {
        Main.getStage().close();
        Platform.exit();
    }

    private void showRestartWindow() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        Button cancel = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancel.setText(ResourceUtils.getString("restartLater"));
        alert.setTitle(ResourceUtils.getString("versionUpdate"));
        alert.setHeaderText(ResourceUtils.getString("needRestart"));
        ok.setOnAction(view -> {
            close();
        });
        alert.show();
    }

    private void update(String version) {
        String url = CloudApi.SERVER_HOST + "/easyDCS/easyDCS_" + version + ".zip";
        DownloadProgressBar downloadProgressBar = new DownloadProgressBar();
        Task task = new Task() {
            @Override
            protected Object call() {
                ValueCallback callback = value -> {
                    updateMessage(value + "%");
                    updateValue(value);
                    if (value == 100) {
                        ZipUtils.unzip("tmp/downloadTmp.zip", "../../EasyDCS");
                        PropertyUtils.modifyProperty("version", "version", version);
                        ZipUtils.deleteFile(new File("tmp"));
                        Platform.runLater(()->{
                            downloadProgressBar.closeWindow();
                            showRestartWindow();
                        });
                    }
                };
                if (!new File("tmp/downloadTmp.zip").exists()) {
                    DownloadManager.download(callback, url);
                } else {
                    ZipUtils.unzip("tmp/downloadTmp.zip", "../../EasyDCS");
                    ZipUtils.deleteFile(new File("tmp"));
                    downloadProgressBar.closeWindow();
                    // todo:这里好像会重复调用2次
//                    showRestartWindow();
                }
                return null;
            }
        };
        downloadProgressBar.setTask(task);
        downloadProgressBar.showWindow();
    }

    @Override
    void initView() {
        checkVersion.setOnAction(view -> {
            CloudApi.getInstance().queryLatestVersion(new CommonCallback() {
                @Override
                public void success(String version) {
                    Platform.runLater(() -> {
                        latestVersion.setText(version);
                        if (!TextUtils.equals(version, currentVersion.getText())) {
                            showUpdateWindow(version);
                        } else {
                            ToastUtils.toast(ResourceUtils.getString("currentIsLatest"));
                        }
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("queryFailed"));
                    });
                }
            });
        });
        KeyCodeCombination kc1 = new KeyCodeCombination(KeyCode.W, KeyCodeCombination.CONTROL_DOWN);
        StyleUtils.getScene().getAccelerators().put(kc1, new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(":sasdsa");
                alert.show();
            }
        });

    }

    @Override
    void initData() {
        String version = Constants.VERSION;
        currentVersion.setText(version);
        CloudApi.getInstance().queryLatestVersion(new CommonCallback() {
            @Override
            public void success(String version) {
                Platform.runLater(() -> {
                    latestVersion.setText(version);
                    if (!TextUtils.equals(version, currentVersion.getText())) {
                        showUpdateWindow(version);
                    }
                });
            }

            @Override
            public void fail(String fail) {
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
}
