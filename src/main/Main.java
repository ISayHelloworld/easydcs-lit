package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.callback.CommonCallback;
import main.manager.AccountManager;
import main.manager.PageRouter;
import main.manager.TabManager;
import main.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Main extends Application {
    private static final int WIDTH = 1200;

    private static final int HEIGHT = 720;
    private static Stage mStage;
    private Scene mScene;

    private HBox mAccountTab;

    private HBox mCheckInTab;

    private HBox mManagerTab;

    private HBox mControlTab;

    private HBox mBoardTab;

    private HBox mDataTab;

    private static HBox mSettingTab;

    private HBox mVersionTab;

    private static HBox mCurrentTab;

    private static List<HBox> tabList;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 界面1:航班搜索区
        // 界面2:航班信息展示版，航班用户搜索+结果区，用户选定区
        // 界面3:航班信息展示板，用户信息登记区（可单独更新提交），选座（可单独更新提交）
        // 界面切换wizard，用堆栈维护一个界面切换栈
        mStage = primaryStage;
        mStage.setTitle("EASYDCS LiT");
        ResourceUtils.readLang();
        Parent parent = FXMLLoader.load(ResourceUtils.getXmlUrl("main"), ResourceBundle.getBundle("language"));
        VBox root = (VBox) parent.lookup("#mainRoot");
        ViewUtils.setInnerShadow(root);
        Pane rootPane = (Pane) parent.lookup("#rootPane");
        Pane tab = (Pane) parent.lookup("#tab");
        ViewUtils.setOutShadow(tab);
        mAccountTab = (HBox) parent.lookup("#tab_account");
        mCheckInTab = (HBox) parent.lookup("#tab_checking");
        mBoardTab = (HBox) parent.lookup("#tab_boarding");
        mVersionTab = (HBox) parent.lookup("#tab_version");
        mManagerTab = (HBox) parent.lookup("#tab_manager");
        mControlTab = (HBox) parent.lookup("#tab_control");
        mDataTab = (HBox) parent.lookup("#tab_data");
        mSettingTab = (HBox) parent.lookup("#tab_setting");
        tabList = new ArrayList<>(Arrays.asList(mAccountTab, mCheckInTab, mBoardTab, mManagerTab, mControlTab, mDataTab, mSettingTab, mVersionTab));
        TabManager.getInstance().init(tabList);
        AnchorPane content = (AnchorPane) parent.lookup("#content");
        PageRouter.init(content, tab);
        rootPane.prefWidthProperty().bind(root.widthProperty());
        rootPane.prefHeightProperty().bind(root.heightProperty());
        mScene = new Scene(parent, WIDTH, HEIGHT);
        mStage.setScene(mScene);
        StyleUtils.setScene(mScene);
        String style = PropertyUtils.readProperty("style", "style");
        if (TextUtils.isEmpty(style)) {
            style = "light";
        }
        StyleUtils.changeStyle(style, false);

        //设置窗体标题小图标和任务栏图标
        mStage.getIcons().add(ResourceUtils.getImg("flight.png"));
        mStage.show();

        // 防止线程泄露
        mStage.setOnCloseRequest(view -> Platform.exit());
        PageRouter.jumpToPage(4);
        mAccountTab.setOnMouseClicked(event -> {
            PageRouter.jumpToPage(4);
            setSelected(mAccountTab);
        });
        mCheckInTab.setOnMouseClicked(event -> {
            if (!AccountManager.getInstance().isLogin()) {
                ToastUtils.toast(ResourceUtils.getString("plsLogin"));
                return;
            }
            PageRouter.jumpToPage(1);
            setSelected(mCheckInTab);
        });
        mBoardTab.setOnMouseClicked(event -> {
            if (!AccountManager.getInstance().isLogin()) {
                ToastUtils.toast(ResourceUtils.getString("plsLogin"));
                return;
            }
            PageRouter.jumpToPage(9);
            setSelected(mBoardTab);
        });
        mVersionTab.setOnMouseClicked(event -> {
            PageRouter.jumpToPage(5);
            setSelected(mVersionTab);
        });
        mManagerTab.setOnMouseClicked(event -> {
            if (!AccountManager.getInstance().isLogin()) {
                ToastUtils.toast(ResourceUtils.getString("plsLogin"));
                return;
            }
            if (!AccountManager.getInstance().isManager()) {
                ToastUtils.toast(ResourceUtils.getString("noAuthority"));
                return;
            }
            PageRouter.jumpToPage(6);
            setSelected(mManagerTab);
        });
        mControlTab.setOnMouseClicked(event -> {
            if (!AccountManager.getInstance().isLogin()) {
                ToastUtils.toast(ResourceUtils.getString("plsLogin"));
                return;
            }
            if (!AccountManager.getInstance().isManager()) {
                ToastUtils.toast(ResourceUtils.getString("noAuthority"));
                return;
            }
            PageRouter.jumpToPage(7);
            setSelected(mControlTab);
        });
        mDataTab.setOnMouseClicked(event -> {
            if (!AccountManager.getInstance().isLogin()) {
                ToastUtils.toast(ResourceUtils.getString("plsLogin"));
                return;
            }
            if (!AccountManager.getInstance().isManager()) {
                ToastUtils.toast(ResourceUtils.getString("noAuthority"));
                return;
            }
            PageRouter.jumpToPage(11);
            setSelected(mDataTab);
        });
        mSettingTab.setOnMouseClicked(event -> {
            PageRouter.jumpToPage(12);
            setSelected(mSettingTab);
        });
        setSelected(mAccountTab);
        checkVersion();
    }

    private static void initAppBar(Pane root) {
        mStage.initStyle(StageStyle.TRANSPARENT);
        try {
            Parent appBar = FXMLLoader.load(ResourceUtils.getXmlUrl("appBar"), ResourceBundle.getBundle("language"));
            root.managedProperty().bind(root.visibleProperty());
            root.getChildren().add(0, appBar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkVersion() {
        String currentVersion = Constants.VERSION;
        CloudApi.getInstance().queryLatestVersion(new CommonCallback() {
            @Override
            public void success(String version) {
                Platform.runLater(() -> {
                    if (!TextUtils.equals(version, currentVersion)) {
                        PageRouter.jumpToPage(5);
                        setSelected(mVersionTab);
                    }
                });
            }

            @Override
            public void fail(String fail) {
            }
        });
    }

    public void setSelected(HBox hBox) {
        mCurrentTab = hBox;
        for (HBox tab : tabList) {
            if (tab.getId() == hBox.getId()) {
                tab.styleProperty().unbind();
                tab.setStyle("-fx-background-color:#63b4ff");
            } else {
                resetHoverState(tab);
            }
        }
    }

    private static void resetHoverState(Node node) {
        node.styleProperty().bind(Bindings
                .when(node.hoverProperty())
                .then(new SimpleStringProperty("-fx-background-color:#63b4ff"))
                .otherwise(new SimpleStringProperty("-fx-background-color:" + getTabColor()))
        );
    }

    private static String getTabColor() {
        String color;
        if (TextUtils.equals(PropertyUtils.readProperty("style", "style"), "light")) {
            color  = "white";
        } else {
            color = "#03375f";
        }
        return color;
    }

    public static Stage getStage() {
        return mStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
