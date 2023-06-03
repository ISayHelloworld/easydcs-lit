package main.page;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import main.utils.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogPage extends BasePageNode {
    private Label logContent;

    @Override
    String getFxmLPath() {
        setId("tab_log");
        return "logPage";
    }

    @Override
    void initView() {
        logContent = (Label) mRoot.lookup("#logContent");
    }

    @Override
    void initData() {
        // 获取数据并刷下去
        String res = Log.readGroupLog();
        logContent.setText(res);
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
