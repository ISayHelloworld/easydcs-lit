package main.utils;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Main;
import main.manager.PageRouter;
import main.manager.TabManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 换肤与CSS控制
public class StyleUtils {
    private static Scene mScene;

    private static List<Parent> pages = new ArrayList<>();

    private static String mStyle;

    public static void setScene(Scene scene) {
        mScene = scene;
    }

    public static Scene getScene() {
        return mScene;
    }

    public static String getStyle() {
        return mStyle;
    }

    static {
        mStyle = PropertyUtils.readProperty("style", "style");
        if (TextUtils.isEmpty(mStyle)) {
            mStyle = "light";
        }
    }

    public static void addStyleNode(Parent node) {
        if (!pages.contains(node) && node != null) {
            pages.add(node);
            node.getStylesheets().clear();
            node.getStylesheets().add("resources/css/" + mStyle + ".css");
        }
    }

    public static void changeStyle(String style, boolean isRestart) {
        mStyle = style;
        if (TextUtils.isEmpty(style)) {
            return;
        }
        mScene.getStylesheets().clear();
        mScene.getStylesheets().add("resources/css/" + style + ".css");
        for (Parent node : pages) {
            if (node == null) {
                continue;
            }
            node.getStylesheets().clear();
            node.getStylesheets().add("resources/css/" + style + ".css");
        }
        if (isRestart) {
            restart();
        }
    }

    private static void restart() {
        Main.getStage().close();
        Platform.runLater(() -> {
            try {
                PageRouter.clearCache();
                new Main().start(new Stage());
                PageRouter.jumpToPage(12);
                TabManager.getInstance().setSelected(6);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String getReverseColor() {
        if (TextUtils.equals(mStyle, "light")) {
            return "black";
        } else {
            return "white";
        }
    }

    public static boolean isDarkMode() {
        return TextUtils.equals(mStyle, "dark");
    }
}