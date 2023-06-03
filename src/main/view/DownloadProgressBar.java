package main.view;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import main.utils.ResourceUtils;
import main.utils.StyleUtils;

import java.io.IOException;
import java.util.ResourceBundle;

public class DownloadProgressBar extends Pane {
    @FXML
    private ProgressBar downloadProgress;

    @FXML
    private Label label;
    private Parent mNode;

    private Service service;

    private Stage stage;

    public void setTask(Task task) {
        initView();
        service = new Service<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                return task;
            }
        };
        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            label.setText(service.getMessage());
        });
        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            double percent = (int) (newValue) * 1.0 / 100;
            downloadProgress.setProgress(percent);
        });
    }

    public DownloadProgressBar() {
    }

    private void initView() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getXmlUrl("downloadProgressBar"));
        fxmlLoader.setResources(ResourceBundle.getBundle("language"));
        fxmlLoader.setController(this);
        try {
            mNode = fxmlLoader.load();
            StyleUtils.addStyleNode(mNode);
            getChildren().add(mNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeWindow() {
        if (stage != null){
            stage.close();
        }
    }

    public void showWindow() {
        stage = new Stage();
        stage.initOwner(Main.getStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(this, 400, 60);
        stage.setScene(scene);
        stage.setTitle(ResourceUtils.getString("versionUpdate"));
        stage.show();
        service.start();
    }
}
