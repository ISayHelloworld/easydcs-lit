package main.manager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import main.utils.PropertyUtils;
import main.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class TabManager {
    private static final Object LOCK = new Object();
    private static TabManager sInstance;
    private String mCurrentLocation;

    private HBox currentTab;
    private List<HBox> tabs = new ArrayList<>();

    public static TabManager getInstance() {
        synchronized (LOCK) {
            if (sInstance == null) {
                synchronized (LOCK) {
                    sInstance = new TabManager();
                }
            }
        }
        return sInstance;
    }

    private TabManager() {
    }

    public void init(List<HBox> tabs) {
        this.tabs = tabs;
    }

    public void setSelected(int index) {
        currentTab = tabs.get(index);
        for (HBox tab : tabs) {
            if (tab.getId() == currentTab.getId()) {
                tab.styleProperty().unbind();
                tab.setStyle("-fx-background-color:#63b4ff");
            } else {
                resetHoverState(tab);
            }
        }
    }

    private void resetHoverState(Node node) {
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
}